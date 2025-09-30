package com._p1m.portfolio.features.users.service;

import com._p1m.portfolio.security.OAuth2.Github.dto.request.GithubUserInfo;
import com._p1m.portfolio.security.OAuth2.Github.dto.response.GithubOAuthResponse;
import com._p1m.portfolio.security.OAuth2.Google.dto.request.GoogleUserInfo;
import com._p1m.portfolio.security.OAuth2.Google.dto.response.GoogleOAuthResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    GoogleOAuthResponse processGoogleOAuth(GoogleUserInfo googleUserInfo);
    GithubOAuthResponse processGithubOAuth(GithubUserInfo githubUserInfo);

}
