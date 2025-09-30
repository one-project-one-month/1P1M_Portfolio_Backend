package com._p1m.portfolio.security.OAuth2.Google.service.impl;

import com._p1m.portfolio.security.OAuth2.Google.service.ExchangeGoogleCodeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ExchangeGoogleCodeServiceImpl implements ExchangeGoogleCodeService {

    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.client-secret}")
    private String secretKey;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";

    @Override
    public String exchangeGoogleCodeForToken(String code) {

        RestTemplate restTemplate = new RestTemplate();

        // URL-decode the code just in case
        String decodedCode = java.net.URLDecoder.decode(code, java.nio.charset.StandardCharsets.UTF_8);

        // Prepare form parameters
        org.springframework.util.MultiValueMap<String, String> params = new org.springframework.util.LinkedMultiValueMap<>();
        params.add("code", decodedCode);
        params.add("client_id", clientId);
        params.add("client_secret", secretKey);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        // Set headers to x-www-form-urlencoded
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<org.springframework.util.MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // Call Google token endpoint
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
