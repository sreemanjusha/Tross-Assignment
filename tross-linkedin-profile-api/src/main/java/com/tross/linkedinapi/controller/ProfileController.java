package com.tross.linkedinapi.controller;

import com.tross.linkedinapi.exception.LinkedInExceptions.AuthException;
import com.tross.linkedinapi.exception.LinkedInExceptions.RateLimitException;
import com.tross.linkedinapi.exception.LinkedInExceptions.UpstreamException;
import com.tross.linkedinapi.model.ProfileRequest;
import com.tross.linkedinapi.model.ProfileResponse;
import com.tross.linkedinapi.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping({"/v1", "/api/v1"})
public class ProfileController {

    private final ProfileService service;
    private final String apiKey;

    public ProfileController(
            ProfileService service,
            @Value("${api.key:}") String apiKey
    ) {
        this.service = service;
        this.apiKey = apiKey == null ? "" : apiKey;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    @PostMapping("/profile")
    public ResponseEntity<?> profile(
            @Valid @RequestBody ProfileRequest request,
            @RequestHeader(value = "X-API-Key", required = false) String requestApiKey
    ) {
        if (!apiKey.isBlank() && !apiKey.equals(requestApiKey)) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Invalid API key"));
        }

        String url = request.effectiveUrl();
        String publicId = ProfileService.extractPublicId(url);

        if (publicId == null) {
            return ResponseEntity.unprocessableEntity().body(Map.of(
                    "error", "Only LinkedIn profile URLs in the form https://www.linkedin.com/in/<public-id> are supported."
            ));
        }

        try {
            Map<String, Object> profile = service.getProfile(url);
            return ResponseEntity.ok(new ProfileResponse(url, profile));
        } catch (AuthException e) {
            return ResponseEntity.status(502).body(Map.of("error", e.getMessage()));
        } catch (RateLimitException e) {
            return ResponseEntity.status(429).body(Map.of("error", e.getMessage()));
        } catch (UpstreamException e) {
            return ResponseEntity.status(502).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(502).body(Map.of(
                    "error", "Unexpected LinkedIn response: " + e.getMessage()
            ));
        }
    }
}
