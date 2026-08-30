# LinkedIn Profile API — Java Spring Boot

This is the Java/Spring Boot conversion of the supplied Python project.

## Stack

- Java 21
- Spring Boot 3.5.5
- Spring Web
- Jackson
- Maven

## Endpoint

`POST /v1/profile`

Also available as:

`POST /api/v1/profile`

Request:

```json
{
  "url": "https://www.linkedin.com/in/example"
}
```

For compatibility with the earlier Java/Postman setup, `profileUrl` is also accepted:

```json
{
  "profileUrl": "https://www.linkedin.com/in/example"
}
```

## Environment

Set:

```text
LINKEDIN_LI_AT=...
LINKEDIN_JSESSIONID=...
```

Optional:

```text
LINKEDIN_PROFILE_DECORATION=com.linkedin.voyager.dash.deco.identity.profile.FullProfileWithEntities-101
UPSTREAM_TIMEOUT_SECONDS=20
API_KEY=...
```

Do not commit credentials.

## Run

```bash
mvn clean test
mvn spring-boot:run
```

Health:

```text
GET http://localhost:8080/v1/health
```

Profile:

```text
POST http://localhost:8080/api/v1/profile
Content-Type: application/json
```

```json
{
  "profileUrl": "https://www.linkedin.com/in/veeramalla-sree-manjusha/"
}
```

The implementation directly calls LinkedIn's Voyager profile endpoint and parses the normalized `data` / `included` graph. It does not use browser automation.
