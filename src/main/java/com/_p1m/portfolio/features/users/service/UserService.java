package com._p1m.portfolio.features.users.service;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.features.users.dto.request.GoogleOAuthRequest;
import com._p1m.portfolio.features.users.dto.request.LoginRequest;
import com._p1m.portfolio.features.users.dto.request.SignupRequest;
import com._p1m.portfolio.features.users.dto.response.AuthResponse;
import com._p1m.portfolio.security.OAuth2.Github.dto.request.GithubOAuthRequest;
import jakarta.validation.Valid;

public interface UserService {
    AuthResponse login(LoginRequest request);
    AuthResponse signup(SignupRequest request);

    ApiResponse googleOAuth2Service(@Valid GoogleOAuthRequest googleOAuthRequest);

    ApiResponse githubOAuth2Service(@Valid GithubOAuthRequest githubOAuthRequest);
}
