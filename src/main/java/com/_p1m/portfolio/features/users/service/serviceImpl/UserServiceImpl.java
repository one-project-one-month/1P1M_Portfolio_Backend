package com._p1m.portfolio.features.users.service.serviceImpl;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.data.models.User;
import com._p1m.portfolio.data.models.Role;
import com._p1m.portfolio.data.repositories.UserRepository;
import com._p1m.portfolio.features.users.dto.request.GoogleOAuthRequest;
import com._p1m.portfolio.features.users.dto.request.LoginRequest;
import com._p1m.portfolio.features.users.dto.request.SignupRequest;
import com._p1m.portfolio.features.users.dto.response.AuthResponse;
import com._p1m.portfolio.features.users.service.AuthService;
import com._p1m.portfolio.features.users.service.UserService;
import com._p1m.portfolio.security.JWT.JWTUtil;

import com._p1m.portfolio.security.OAuth2.Github.dto.request.GithubOAuthRequest;
import com._p1m.portfolio.security.OAuth2.Github.dto.response.GoogleOAuthResponse;
import com._p1m.portfolio.security.OAuth2.Google.dto.request.GoogleUserInfo;
import com._p1m.portfolio.security.OAuth2.Google.service.GoogleOAuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final GoogleOAuthService googleOAuthService;
    private final AuthService authService;

    @Override
    public ApiResponse googleOAuth2Service(GoogleOAuthRequest googleOAuthRequest) {
        // Add validation for token
        if (googleOAuthRequest.getToken() == null || googleOAuthRequest.getToken().trim().isEmpty()) {
            return ApiResponse.builder()
                    .success(0)
                    .code(400)
                    .message("Token is required")
                    .data(null)
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }

        try {
            GoogleUserInfo googleUserInfo = new GoogleUserInfo();
            try{
                googleUserInfo = googleOAuthService.verifyIdToken(googleOAuthRequest.getToken());
            }catch (Exception e){
                googleUserInfo = googleOAuthService.verifyAccessToken(googleOAuthRequest.getToken());
            }
            // Process Google OAuth and Save User
            GoogleOAuthResponse googleOAuthResponse = authService.processGoogleOAuth(googleUserInfo);

            return ApiResponse.builder()
                    .success(1)
                    .code(200)
                    .message("Google OAuth2 Successfully.")
                    .data(googleOAuthResponse)
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();

        } catch (Exception e) {
            return ApiResponse.builder()
                    .success(0)
                    .code(500)
                    .message("Internal server error: " + e.getMessage())
                    .data(null)
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }
    }


    @Override
    public ApiResponse githubOAuth2Service(GithubOAuthRequest githubOAuthRequest) {
        return null;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());

        if (user.isEmpty() || !passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        String token = jwtUtil.generateToken(user.get().getEmail());
        return new AuthResponse(token, user.get().getEmail());
    }

    @Override
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.builder().id(request.getRoleId()).build())
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail());
    }
}

