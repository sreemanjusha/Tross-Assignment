package com.tross.linkedinapi.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tross.linkedinapi.exception.LinkedInExceptions.AuthException;
import com.tross.linkedinapi.exception.LinkedInExceptions.RateLimitException;
import com.tross.linkedinapi.exception.LinkedInExceptions.UpstreamException;
import com.tross.linkedinapi.model.RawProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class LinkedInClient {

    private static final String PROFILE_PATH = "/voyager/api/identity/dash/profiles";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String liAt;
    private final String jsessionId;
    private final String decoration;
    private final String userAgent;
    private final int timeoutSeconds;

    public LinkedInClient(
            RestClient restClient,
            ObjectMapper objectMapper,
            @Value("${linkedin.li-at:}") String liAt,
            @Value("${linkedin.jsessionid:}") String jsessionId,
            @Value("${linkedin.profile-decoration:}") String decoration,
            @Value("${linkedin.user-agent:}") String userAgent,
            @Value("${linkedin.timeout-seconds:20}") int timeoutSeconds
    ) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.liAt = liAt == null ? "" : liAt.trim();
        this.jsessionId = jsessionId == null ? "" : jsessionId.trim();
        this.decoration = decoration;
        this.userAgent = userAgent;
        this.timeoutSeconds = timeoutSeconds;
    }

    public RawProfile fetchProfile(String publicId, String sourceUrl) {
        if (liAt.isBlank() || jsessionId.isBlank()) {
            throw new AuthException(
                    "LINKEDIN_LI_AT and LINKEDIN_JSESSIONID must be configured."
            );
        }

        try {
            String body = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(PROFILE_PATH)
                            .queryParam("q", "memberIdentity")
                            .queryParam("memberIdentity", publicId)
                            .queryParam("decorationId", decoration)
                            .build())
                    .header(HttpHeaders.ACCEPT, "application/vnd.linkedin.normalized+json+2.1")
                    .header("x-restli-protocol-version", "2.0.0")
                    .header("csrf-token", jsessionId)
                    .header(HttpHeaders.USER_AGENT, userAgent)
                    .header(HttpHeaders.COOKIE,
                            "li_at=" + liAt + "; JSESSIONID=" + jsessionId)
                    .retrieve()
                    .onStatus(status -> status.value() == 401 || status.value() == 403,
                            (request, response) -> {
                                throw new AuthException(
                                        "LinkedIn rejected the session (401/403). Refresh the li_at/JSESSIONID session."
                                );
                            })
                    .onStatus(status -> status.value() == 429,
                            (request, response) -> {
                                throw new RateLimitException(
                                        "LinkedIn rate-limited the session. Stop sending requests and retry later."
                                );
                            })
                    .onStatus(HttpStatusCode::is5xxServerError,
                            (request, response) -> {
                                throw new UpstreamException(
                                        "LinkedIn returned HTTP " + response.getStatusCode().value() + "."
                                );
                            })
                    .body(String.class);

            if (body == null || body.isBlank()) {
                throw new UpstreamException("LinkedIn returned an empty response.");
            }

            JsonNode json;
            try {
                json = objectMapper.readTree(body);
            } catch (Exception e) {
                throw new UpstreamException("LinkedIn returned non-JSON data.", e);
            }

            if (json == null || !json.isObject()) {
                throw new UpstreamException("LinkedIn returned an unexpected JSON envelope.");
            }

            return new RawProfile(publicId, sourceUrl, json);

        } catch (AuthException | RateLimitException | UpstreamException e) {
            throw e;
        } catch (RestClientException e) {
            throw new UpstreamException("LinkedIn request failed: " + e.getMessage(), e);
        }
    }
}
