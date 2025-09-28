package com._p1m.portfolio.features.users.service;

import com._p1m.portfolio.security.OAuth2.Github.dto.response.GoogleOAuthResponse;
import com._p1m.portfolio.security.OAuth2.Google.dto.request.GoogleUserInfo;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    GoogleOAuthResponse processGoogleOAuth(GoogleUserInfo googleUserInfo);
}
