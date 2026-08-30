package com.tross.linkedinapi.model;

import com.fasterxml.jackson.databind.JsonNode;

public record RawProfile(
        String publicId,
        String sourceUrl,
        JsonNode payload
) {}
