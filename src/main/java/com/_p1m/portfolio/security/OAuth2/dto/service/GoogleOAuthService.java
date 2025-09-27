package com._p1m.portfolio.security.OAuth2.dto.service;

import com._p1m.portfolio.security.OAuth2.dto.GoogleApiClient;
import com._p1m.portfolio.security.OAuth2.dto.request.GoogleUserInfo;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

@Service
public class GoogleOAuthService {

    private final GoogleApiClient googleApiClient;
    private final GoogleIdTokenVerifierService idTokenVerifier;

    public GoogleOAuthService(GoogleApiClient googleApiClient, GoogleIdTokenVerifierService idTokenVerifier) {
        this.googleApiClient = googleApiClient;
        this.idTokenVerifier = idTokenVerifier;
    }

    public GoogleUserInfo verifyIdToken(String idToken) throws GeneralSecurityException, IOException {
        return idTokenVerifier.verify(idToken);
    }

    public GoogleUserInfo verifyAccessToken(String accessToken) throws IOException {
        Map<String, Object> tokenInfo = googleApiClient.getTokenInfo(accessToken);

        String scope = (String) tokenInfo.get("scope");
        if (scope == null || (!scope.contains("email") && !scope.contains("profile"))) {
            throw new IllegalArgumentException("Token doesn't have required scopes");
        }

        return googleApiClient.getUserProfile(accessToken);
    }
}
