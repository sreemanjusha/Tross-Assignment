package com.tross.linkedinapi.model;

public record ProfileRequest(
        String url,
        String profileUrl
) {

    public String effectiveUrl() {
        if (url != null && !url.isBlank()) {
            return url.trim();
        }

        if (profileUrl != null && !profileUrl.isBlank()) {
            return profileUrl.trim();
        }

        return null;
    }
}