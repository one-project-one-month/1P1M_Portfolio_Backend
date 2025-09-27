package com._p1m.portfolio.security.OAuth2.dto.service;

import com._p1m.portfolio.security.OAuth2.dto.request.GoogleUserInfo;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Component
public class GoogleIdTokenVerifierService {

    @Value("${google.client-id}")
    private String googleClientId;

    public GoogleUserInfo verify(String idToken) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance()
        )
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken token = verifier.verify(idToken);
        if (token == null) {
            throw new IllegalArgumentException("Invalid Google ID token");
        }

        GoogleIdToken.Payload payload = token.getPayload();

        return GoogleUserInfo.builder()
                .email(payload.getEmail())
                .name((String) payload.get("name"))
                .givenName((String) payload.get("given_name"))
                .familyName((String) payload.get("family_name"))
                .picture((String) payload.get("picture"))
                .emailVerified(payload.getEmailVerified())
                .googleId(payload.getSubject())
                .build();
    }
}
