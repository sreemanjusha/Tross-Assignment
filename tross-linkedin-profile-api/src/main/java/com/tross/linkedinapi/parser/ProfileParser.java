package com.tross.linkedinapi.parser;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ProfileParser {

    public Map<String, Object> parse(JsonNode payload, String sourceUrl) {
        Map<String, JsonNode> index = indexIncluded(payload);
        JsonNode data = payload.path("data");

        JsonNode elements = data.get("*elements");
        if (elements == null) elements = data.get("elements");

        if (elements == null || !elements.isArray() || elements.isEmpty()
                || !elements.get(0).isTextual()) {
            throw new IllegalArgumentException("No profile reference in LinkedIn response.");
        }

        String targetUrn = elements.get(0).asText();
        JsonNode profile = index.get(targetUrn);

        if (profile == null) {
            throw new IllegalArgumentException("Profile entity was not present in LinkedIn response.");
        }

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("public_id", text(profile, "publicIdentifier"));
        out.put("profile_urn", targetUrn);

        String first = firstText(profile, "firstName");
        String last = firstText(profile, "lastName");
        String name = String.join(" ", nonBlank(first, last));

        out.put("name", name.isBlank() ? null : name);
        out.put("first_name", first);
        out.put("last_name", last);
        out.put("headline", firstText(profile, "headline"));

        JsonNode geo = null;
        JsonNode geoLocation = profile.get("geoLocation");
        if (geoLocation != null) {
            geo = index.get(text(geoLocation, "geoUrn"));
        }

        out.put("location",
                firstText(profile, "locationName") != null
                        ? firstText(profile, "locationName")
                        : firstText(geo, "defaultLocalizedName", "name"));

        out.put("about", firstText(profile, "summary", "about"));

        JsonNode picture = firstNode(profile,
                "displayImage", "profilePicture", "picture", "displayPicture");

        String image = imageUrl(picture);
        if (image == null) image = imageUrl(profile.get("displayPictureUrl"));
        out.put("profile_image", image);

        out.put("experience", parseExperience(profile, index));
        out.put("education", parseEducation(profile, index));
        out.put("skills", parseSkills(profile, index));
        out.put("certifications", parseCertifications(profile, index));
        out.put("languages", parseLanguages(profile, index));

        Map<String, Object> available = new LinkedHashMap<>();
        JsonNode included = payload.get("included");
        available.put("included_entities", included != null && included.isArray() ? included.size() : 0);
        available.put("has_position_groups", profile.has("*profilePositionGroups"));
        available.put("has_educations", profile.has("*profileEducations"));
        available.put("has_skills",
                profile.has("*profileSkills") || profile.has("*skills"));
        available.put("has_certifications",
                profile.has("*profileCertifications") || profile.has("*certifications"));
        available.put("has_languages",
                profile.has("*profileLanguages") || profile.has("*languages"));
        out.put("raw_available_sections", available);

        Map<String, Object> source = new LinkedHashMap<>();
        source.put("url", sourceUrl);
        source.put("endpoint", "/voyager/api/identity/dash/profiles");
        source.put("decoration_id", "FullProfileWithEntities-101");
        out.put("source", source);

        return out;
    }

    private Map<String, JsonNode> indexIncluded(JsonNode payload) {
        Map<String, JsonNode> index = new LinkedHashMap<>();
        JsonNode included = payload.get("included");
        if (included != null && included.isArray()) {
            for (JsonNode item : included) {
                String urn = text(item, "entityUrn");
                if (urn != null) index.put(urn, item);
            }
        }
        return index;
    }

    private List<Map<String, Object>> parseExperience(JsonNode profile, Map<String, JsonNode> index) {
        List<Map<String, Object>> result = new ArrayList<>();

        JsonNode groupsCollection = resolve(index, profile.get("*profilePositionGroups"));
        for (String groupUrn : refs(groupsCollection)) {
            JsonNode group = index.get(groupUrn);
            JsonNode positionsCollection =
                    resolve(index, group == null ? null : group.get("*profilePositionInPositionGroup"));

            for (String positionUrn : refs(positionsCollection)) {
                JsonNode p = index.get(positionUrn);
                if (p == null) continue;

                JsonNode company = resolve(index, p.get("*company"));
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("title", firstText(p, "title"));
                String companyName = firstText(p, "companyName");
                if (companyName == null) companyName = firstText(company, "name");
                item.put("company", companyName);
                item.put("company_url", text(company, "url"));
                item.put("location", firstText(p, "locationName"));
                item.put("description", firstText(p, "description"));
                item.put("from", dateString(child(p, "dateRange", "start")));
                String to = dateString(child(p, "dateRange", "end"));
                item.put("to", to == null ? "present" : to);
                result.add(item);
            }
        }

        if (result.isEmpty()) {
            for (JsonNode p : collectByType(index, "position")) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("title", firstText(p, "title"));
                item.put("company", firstText(p, "companyName"));
                item.put("company_url", null);
                item.put("location", firstText(p, "locationName"));
                item.put("description", firstText(p, "description"));
                item.put("from", dateString(child(p, "dateRange", "start")));
                String to = dateString(child(p, "dateRange", "end"));
                item.put("to", to == null ? "present" : to);
                result.add(item);
            }
        }

        return dedupe(result);
    }

    private List<Map<String, Object>> parseEducation(JsonNode profile, Map<String, JsonNode> index) {
        List<Map<String, Object>> result = new ArrayList<>();
        JsonNode collection = resolve(index, profile.get("*profileEducations"));

        for (String urn : refs(collection)) {
            JsonNode edu = index.get(urn);
            if (edu == null) continue;
            JsonNode school = resolve(index, edu.get("*school"));

            Map<String, Object> item = new LinkedHashMap<>();
            String schoolName = firstText(edu, "schoolName", "name");
            if (schoolName == null) schoolName = firstText(school, "name");

            item.put("school", schoolName);
            item.put("school_url", text(school, "url"));
            item.put("degree", firstText(edu, "degreeName", "degree"));
            item.put("field_of_study", firstText(edu, "fieldOfStudy"));
            item.put("description", firstText(edu, "description"));
            item.put("from", dateString(child(edu, "dateRange", "start")));
            item.put("to", dateString(child(edu, "dateRange", "end")));
            result.add(item);
        }
        return dedupe(result);
    }

    private List<Map<String, Object>> parseSkills(JsonNode profile, Map<String, JsonNode> index) {
        List<JsonNode> candidates = new ArrayList<>();
        for (String key : List.of("*profileSkills", "*skills")) {
            if (profile.has(key)) {
                for (String urn : refs(resolve(index, profile.get(key)))) {
                    JsonNode n = index.get(urn);
                    if (n != null) candidates.add(n);
                }
            }
        }
        if (candidates.isEmpty()) candidates = collectByType(index, "skill");

        List<Map<String, Object>> result = new ArrayList<>();
        for (JsonNode s : candidates) {
            String name = firstText(s, "name", "standardizedName", "skillName");
            if (name != null) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("name", name);
                item.put("endorsements", s.has("endorsementCount") ? s.get("endorsementCount").asInt() : null);
                result.add(item);
            }
        }
        return dedupe(result);
    }

    private List<Map<String, Object>> parseCertifications(JsonNode profile, Map<String, JsonNode> index) {
        List<JsonNode> candidates = new ArrayList<>();
        for (String key : List.of("*profileCertifications", "*certifications")) {
            if (profile.has(key)) {
                for (String urn : refs(resolve(index, profile.get(key)))) {
                    JsonNode n = index.get(urn);
                    if (n != null) candidates.add(n);
                }
            }
        }
        if (candidates.isEmpty()) candidates = collectByType(index, "certification");

        List<Map<String, Object>> result = new ArrayList<>();
        for (JsonNode c : candidates) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", firstText(c, "name", "title"));
            item.put("issuer", firstText(c, "authority", "issuer", "companyName"));
            item.put("issue_date", dateString(child(c, "dateRange", "start")));
            item.put("credential_id", firstText(c, "licenseNumber", "credentialId"));
            item.put("url", text(c, "url"));
            result.add(item);
        }
        return dedupe(result);
    }

    private List<Map<String, Object>> parseLanguages(JsonNode profile, Map<String, JsonNode> index) {
        List<JsonNode> candidates = new ArrayList<>();
        for (String key : List.of("*profileLanguages", "*languages")) {
            if (profile.has(key)) {
                for (String urn : refs(resolve(index, profile.get(key)))) {
                    JsonNode n = index.get(urn);
                    if (n != null) candidates.add(n);
                }
            }
        }
        if (candidates.isEmpty()) candidates = collectByType(index, "language");

        List<Map<String, Object>> result = new ArrayList<>();
        for (JsonNode lang : candidates) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", firstText(lang, "name", "languageName"));
            item.put("proficiency", firstText(lang, "proficiency", "proficiencyLevel"));
            result.add(item);
        }
        return dedupe(result);
    }

    private List<JsonNode> collectByType(Map<String, JsonNode> index, String needle) {
        List<JsonNode> result = new ArrayList<>();
        for (JsonNode item : index.values()) {
            String type = text(item, "$type");
            if (type != null && type.toLowerCase(Locale.ROOT).contains(needle.toLowerCase(Locale.ROOT))) {
                result.add(item);
            }
        }
        return result;
    }

    private JsonNode resolve(Map<String, JsonNode> index, JsonNode urnNode) {
        String urn = unwrapText(urnNode);
        return urn == null ? null : index.get(urn);
    }

    private List<String> refs(JsonNode value) {
        if (value == null || value.isNull()) return List.of();
        if (value.isArray()) {
            List<String> result = new ArrayList<>();
            for (JsonNode n : value) {
                if (n.isTextual()) result.add(n.asText());
            }
            return result;
        }
        if (value.isTextual()) return List.of(value.asText());
        if (value.isObject()) {
            for (String key : List.of("*elements", "elements")) {
                if (value.has(key)) return refs(value.get(key));
            }
        }
        return List.of();
    }

    private String firstText(JsonNode obj, String... keys) {
        if (obj == null || obj.isMissingNode() || obj.isNull()) return null;
        for (String key : keys) {
            String value = unwrapText(obj.get(key));
            if (value != null && !value.isBlank()) return value.trim();
        }
        return null;
    }

    private String unwrapText(JsonNode value) {
        if (value == null || value.isNull()) return null;
        if (value.isTextual()) return value.asText();
        if (value.isObject()) {
            JsonNode text = value.get("text");
            if (text != null && text.isTextual()) return text.asText();
        }
        return null;
    }

    private String text(JsonNode obj, String key) {
        if (obj == null || !obj.has(key)) return null;
        JsonNode n = obj.get(key);
        return n.isTextual() ? n.asText() : null;
    }

    private JsonNode firstNode(JsonNode obj, String... keys) {
        if (obj == null) return null;
        for (String key : keys) {
            if (obj.has(key) && !obj.get(key).isNull()) return obj.get(key);
        }
        return null;
    }

    private JsonNode child(JsonNode obj, String... keys) {
        JsonNode current = obj;
        for (String key : keys) {
            if (current == null || !current.has(key)) return null;
            current = current.get(key);
        }
        return current;
    }

    private String dateString(JsonNode value) {
        if (value == null || !value.isObject() || !value.has("year")) return null;
        int year = value.get("year").asInt();
        if (value.has("month") && !value.get("month").isNull()) {
            return String.format("%04d-%02d", year, value.get("month").asInt());
        }
        return String.valueOf(year);
    }

    private String imageUrl(JsonNode value) {
        if (value == null || value.isNull()) return null;
        if (value.isTextual()) {
            String s = value.asText();
            return s.startsWith("http://") || s.startsWith("https://") ? s : null;
        }
        if (!value.isObject()) return null;

        for (String key : List.of("displayImageUrl", "url", "rootUrl")) {
            String candidate = text(value, key);
            if (candidate != null && (candidate.startsWith("http://") || candidate.startsWith("https://"))) {
                return candidate;
            }
        }

        for (String key : List.of("com.linkedin.common.VectorImage", "vectorImage", "vector")) {
            JsonNode vi = value.get(key);
            if (vi != null && vi.isObject()) {
                String root = text(vi, "rootUrl");
                if (root != null) {
                    JsonNode artifacts = vi.get("artifacts");
                    if (artifacts != null && artifacts.isArray() && !artifacts.isEmpty()) {
                        JsonNode last = artifacts.get(artifacts.size() - 1);
                        String segment = text(last, "fileIdentifyingUrlPathSegment");
                        return root + (segment == null ? "" : segment);
                    }
                    return root;
                }
            }
        }
        return null;
    }

    private List<Map<String, Object>> dedupe(List<Map<String, Object>> items) {
        Set<String> seen = new HashSet<>();
        List<Map<String, Object>> out = new ArrayList<>();
        for (Map<String, Object> item : items) {
            boolean hasValue = item.values().stream()
                    .anyMatch(v -> v != null && !String.valueOf(v).isBlank() && !(v instanceof Collection<?> c && c.isEmpty()));
            if (!hasValue) continue;
            String key = item.toString();
            if (seen.add(key)) out.add(item);
        }
        return out;
    }

    private List<String> nonBlank(String... values) {
        List<String> out = new ArrayList<>();
        for (String value : values) if (value != null && !value.isBlank()) out.add(value);
        return out;
    }
}
