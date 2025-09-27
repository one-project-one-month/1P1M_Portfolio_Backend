package com._p1m.portfolio.security.OAuth2.Github;

import com._p1m.portfolio.security.OAuth2.Github.dto.request.GithubUserInfo;
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

    private static final String USER_URL = "https://api.github.com/user";
    private static final String EMAILS_URL = "https://api.github.com/user/emails";

    public GithubUserInfo getUserProfile(String accessToken) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("Accept", "application/vnd.github.v3+json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Get main user profile
        ResponseEntity<String> response = restTemplate.exchange(
                USER_URL,
                HttpMethod.GET,
                entity,
                String.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new IOException("Failed to fetch GitHub user profile");
        }

        Map<String, Object> profile = objectMapper.readValue(response.getBody(), Map.class);

        // Fetch emails (GitHub sometimes doesn’t include email in /user)
        ResponseEntity<String> emailResponse = restTemplate.exchange(
                EMAILS_URL,
                HttpMethod.GET,
                entity,
                String.class
        );

        String email = null;
        if (emailResponse.getStatusCode() == HttpStatus.OK) {
            var emails = objectMapper.readValue(emailResponse.getBody(), java.util.List.class);
            if (!emails.isEmpty()) {
                // Find primary email
                Map<String, Object> primaryEmail = (Map<String, Object>) emails.stream()
                        .filter(e -> Boolean.TRUE.equals(((Map<?, ?>) e).get("primary")))
                        .findFirst()
                        .orElse(emails.get(0));

                email = (String) primaryEmail.get("email");
            }
        }

        return GithubUserInfo.builder()
                .id(String.valueOf(profile.get("id")))
                .login((String) profile.get("login"))
                .name((String) profile.get("name"))
                .avatarUrl((String) profile.get("avatar_url"))
                .email(email)
                .build();
    }
}
