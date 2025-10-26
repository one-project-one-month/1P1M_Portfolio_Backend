package com._p1m.portfolio.features.users.service;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.features.users.dto.request.*;
import com._p1m.portfolio.features.users.dto.response.AuthResponse;
import com._p1m.portfolio.security.OAuth2.Github.dto.request.GithubOAuthRequest;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface UserService {

    ApiResponse googleOAuth2Service(@Valid GoogleOAuthRequest googleOAuthRequest);

    ApiResponse githubOAuth2Service(@Valid GithubOAuthRequest githubOAuthRequest);

    ApiResponse exchangeCodeAndProcessGitHubOAuth(GithubCodeRequest githubCodeRequest);

    ApiResponse exchangeCodeAndProcessGoogleOAuth(GoogleCodeRequest googlecodeRequest);

    ApiResponse registerUser(SignupRequest signupRequest);

    ApiResponse loginUser(LoginRequest request);

    ApiResponse sendOtpCode(OtpRequest otpRequest) throws IOException, MessagingException;

    ApiResponse verifyOtpCode(VerifyOtpRequest verifyOtpRequest);

    ApiResponse checkEmailExistOrNot(@Valid CheckEmailRequest checkEmailRequest);

    ApiResponse getProfileData(Long userId, String token);
}
