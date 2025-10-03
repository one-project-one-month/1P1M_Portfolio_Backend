package com._p1m.portfolio.security.OAuth2.Github.service.impl;

import com._p1m.portfolio.security.OAuth2.Github.service.ExchangeGitHubCodeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ExchangeGitHubCodeServiceImpl implements ExchangeGitHubCodeService {

    @Value("${github.client-id}")
    private String clientId;

    @Value("${github.client-secret}")
    private String secretKey;

    private static final String TOKEN_URL = "https://github.com/login/oauth/access_token";

    @Override
    public String exchangeGithubCodeForToken(final String code) {

        RestTemplate restTemplate = new RestTemplate();

        Map<String , String > params = new HashMap<>();
        params.put("client_id" , clientId);
        params.put("client_secret", secretKey);
        params.put("code" , code);

        // Set headers to accept JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);

        // Call GitHub API
        ResponseEntity<Map> response = restTemplate.exchange(
                TOKEN_URL,
                HttpMethod.POST,
                request,
                Map.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return (String) response.getBody().get("access_token");
        } else {
            throw new RuntimeException("Failed to exchange code for access token: " + response.getStatusCode());
        }
    }
}
