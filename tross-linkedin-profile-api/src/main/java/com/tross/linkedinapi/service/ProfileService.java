package com.tross.linkedinapi.service;

import com.tross.linkedinapi.client.LinkedInClient;
import com.tross.linkedinapi.model.RawProfile;
import com.tross.linkedinapi.parser.ProfileParser;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProfileService {

    private final LinkedInClient client;
    private final ProfileParser parser;

    public ProfileService(LinkedInClient client, ProfileParser parser) {
        this.client = client;
        this.parser = parser;
    }

    public Map<String, Object> getProfile(String profileUrl) {
        String publicId = extractPublicId(profileUrl);
        RawProfile raw = client.fetchProfile(publicId, profileUrl);
        return parser.parse(raw.payload(), profileUrl);
    }

    public static String extractPublicId(String value) {
        try {
            java.net.URI uri = java.net.URI.create(value);
            if (!"http".equalsIgnoreCase(uri.getScheme())
                    && !"https".equalsIgnoreCase(uri.getScheme())) return null;

            String host = uri.getHost();
            if (host == null || !(host.equalsIgnoreCase("linkedin.com")
                    || host.equalsIgnoreCase("www.linkedin.com"))) return null;

            String[] parts = uri.getPath().split("/");
            if (parts.length < 3 || !"in".equalsIgnoreCase(parts[1]) || parts[2].isBlank()) return null;

            String publicId = java.net.URLDecoder.decode(parts[2], java.nio.charset.StandardCharsets.UTF_8);
            if (publicId.length() > 200) return null;
            return publicId;
        } catch (Exception e) {
            return null;
        }
    }
}
