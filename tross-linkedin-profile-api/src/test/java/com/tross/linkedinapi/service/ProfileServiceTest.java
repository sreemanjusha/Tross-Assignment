package com.tross.linkedinapi.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProfileServiceTest {

    @Test
    void extractsPublicId() {
        assertEquals(
                "jane-doe-123",
                ProfileService.extractPublicId(
                        "https://www.linkedin.com/in/jane-doe-123/?trk=x"
                )
        );
    }

    @Test
    void rejectsNonProfile() {
        assertNull(ProfileService.extractPublicId("https://www.linkedin.com/company/acme/"));
        assertNull(ProfileService.extractPublicId("https://example.com/in/jane"));
    }
}
