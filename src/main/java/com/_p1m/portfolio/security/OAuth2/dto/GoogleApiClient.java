package com._p1m.portfolio.security.OAuth2.dto;

import com._p1m.portfolio.security.OAuth2.dto.request.GoogleUserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@Component
public class GoogleApiClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String TOKEN_INFO_URL = "https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=";
    private static final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";

    public Map<String, Object> getTokenInfo(String accessToken) throws IOException {
        ResponseEntity<String> response = restTemplate.exchange(
                TOKEN_INFO_URL + accessToken,
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                String.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new IllegalArgumentException("Invalid Google access token");
        }

        return objectMapper.readValue(response.getBody(), Map.class);
    }

    public GoogleUserInfo getUserProfile(String accessToken) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        ResponseEntity<String> response = restTemplate.exchange(
                USER_INFO_URL,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new IOException("Failed to fetch Google user profile");
        }

        Map<String, Object> userInfo = objectMapper.readValue(response.getBody(), Map.class);

        return GoogleUserInfo.builder()
                .email((String) userInfo.get("email"))
                .name((String) userInfo.get("name"))
                .givenName((String) userInfo.get("given_name"))
                .familyName((String) userInfo.get("family_name"))
                .picture((String) userInfo.get("picture"))
                .emailVerified((Boolean) userInfo.get("verified_email"))
                .googleId((String) userInfo.get("id"))
                .build();
    }
}