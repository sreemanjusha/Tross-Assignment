package com.tross.linkedinapi.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProfileParserTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private final ProfileParser parser = new ProfileParser();

    @Test
    void parsesNormalizedProfile() throws Exception {
        String json = """
        {
          "data": {"*elements": ["urn:li:fsd_profile:p1"]},
          "included": [
            {
              "$type": "com.linkedin.voyager.dash.identity.profile.Profile",
              "entityUrn": "urn:li:fsd_profile:p1",
              "publicIdentifier": "jane-doe",
              "firstName": {"text": "Jane", "attributes": []},
              "lastName": {"text": "Doe", "attributes": []},
              "headline": {"text": "Engineer", "attributes": []},
              "summary": {"text": "About Jane", "attributes": []},
              "locationName": "Bengaluru",
              "*profilePositionGroups": "urn:li:collection:c1",
              "*profileEducations": "urn:li:collection:e1"
            },
            {
              "entityUrn": "urn:li:collection:c1",
              "*elements": ["urn:li:group:g1"]
            },
            {
              "entityUrn": "urn:li:group:g1",
              "*profilePositionInPositionGroup": "urn:li:collection:p1"
            },
            {
              "entityUrn": "urn:li:collection:p1",
              "*elements": ["urn:li:position:x1"]
            },
            {
              "entityUrn": "urn:li:position:x1",
              "title": {"text": "Software Engineer", "attributes": []},
              "companyName": "Acme",
              "dateRange": {"start": {"year": 2024, "month": 1}}
            },
            {
              "entityUrn": "urn:li:collection:e1",
              "*elements": ["urn:li:education:y1"]
            },
            {
              "entityUrn": "urn:li:education:y1",
              "schoolName": "Example University",
              "degreeName": "B.Tech"
            }
          ]
        }
        """;

        JsonNode payload = mapper.readTree(json);
        Map<String, Object> out =
                parser.parse(payload, "https://www.linkedin.com/in/jane-doe");

        assertEquals("Jane Doe", out.get("name"));
        assertEquals("Engineer", out.get("headline"));

        var experience = (java.util.List<?>) out.get("experience");
        var firstExperience = (Map<?, ?>) experience.get(0);
        assertEquals("Software Engineer", firstExperience.get("title"));

        var education = (java.util.List<?>) out.get("education");
        var firstEducation = (Map<?, ?>) education.get(0);
        assertEquals("Example University", firstEducation.get("school"));
    }
}
