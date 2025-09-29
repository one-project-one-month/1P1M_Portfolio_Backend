package com._p1m.portfolio.security.OAuth2.Github;

import com._p1m.portfolio.security.OAuth2.Github.dto.request.GithubUserInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@Component
public class GitHubApiClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String USER_INFO_URL = "https://api.github.com/user";
    private static final String USER_EMAILS_URL = "https://api.github.com/user/emails";

    public GithubUserInfo getUserProfile(String accessToken) throws IOException {
        // Get user basic info
        JsonNode userInfo = getUserInfo(accessToken);

        // Get user emails (GitHub may not include email in basic profile)
        String primaryEmail = getPrimaryEmail(accessToken);

        return GithubUserInfo.builder()
                .githubId(userInfo.get("id").asLong())
                .login(userInfo.get("login").asText())
                .name(userInfo.has("name") && !userInfo.get("name").isNull()
                        ? userInfo.get("name").asText()
                        : userInfo.get("login").asText())
                .email(primaryEmail)
                .avatarUrl(userInfo.get("avatar_url").asText())
                .bio(userInfo.has("bio") && !userInfo.get("bio").isNull()
                        ? userInfo.get("bio").asText()
                        : null)
                .location(userInfo.has("location") && !userInfo.get("location").isNull()
                        ? userInfo.get("location").asText()
                        : null)
                .company(userInfo.has("company") && !userInfo.get("company").isNull()
                        ? userInfo.get("company").asText()
                        : null)
                .blog(userInfo.has("blog") && !userInfo.get("blog").isNull()
                        ? userInfo.get("blog").asText()
                        : null)
                .emailVerified(true) // Assume verified since it comes from GitHub API
                .build();
    }

    private JsonNode getUserInfo(String accessToken) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("Accept", "application/vnd.github.v3+json");

        ResponseEntity<String> response = restTemplate.exchange(
                USER_INFO_URL,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new IOException("Failed to fetch GitHub user profile: " + response.getStatusCode());
        }

        return objectMapper.readTree(response.getBody());
    }

    private String getPrimaryEmail(String accessToken) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("Accept", "application/vnd.github.v3+json");

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    USER_EMAILS_URL,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode emails = objectMapper.readTree(response.getBody());

                // Find primary email
                for (JsonNode emailNode : emails) {
                    if (emailNode.get("primary").asBoolean()) {
                        return emailNode.get("email").asText();
                    }
                }

                // If no primary email found, return first email
                if (emails.size() > 0) {
                    return emails.get(0).get("email").asText();
                }
            }
        } catch (Exception e) {
            // Email endpoint might not be accessible, continue without email
            System.err.println("Could not fetch GitHub user emails: " + e.getMessage());
        }

        return null; // No email available
    }

    public boolean validateToken(String accessToken) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("Accept", "application/vnd.github.v3+json");

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    USER_INFO_URL,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );

            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }
    }
}
