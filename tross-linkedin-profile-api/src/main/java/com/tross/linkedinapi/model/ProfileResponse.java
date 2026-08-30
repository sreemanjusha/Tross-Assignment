package com.tross.linkedinapi.model;

import java.util.Map;

public record ProfileResponse(
        String source_url,
        Map<String, Object> profile
) {}
