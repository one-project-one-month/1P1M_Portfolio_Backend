package com._p1m.portfolio.features.users.controller;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.utils.ResponseUtils;
import com._p1m.portfolio.features.users.dto.request.GoogleOAuthRequest;
import com._p1m.portfolio.features.users.dto.request.LoginRequest;
import com._p1m.portfolio.features.users.dto.request.SignupRequest;
import com._p1m.portfolio.features.users.dto.response.AuthResponse;
import com._p1m.portfolio.features.users.service.UserService;

import com._p1m.portfolio.security.OAuth2.Github.dto.request.GithubOAuthRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base.path}/auth/users")
@Tag(name = "User API", description = "Endpoints for managing users")
public class UserController {

    private final UserService userService;

    @PostMapping("googleOAuth2")
    public ResponseEntity<ApiResponse> googleOAuth2(@Valid @RequestBody GoogleOAuthRequest googleOAuthRequest,
                                                    HttpServletRequest request){
        final ApiResponse response = this.userService.googleOAuth2Service(googleOAuthRequest);
        return ResponseUtils.buildResponse(request , response);
    }

    @PostMapping("githubOAuth2")
    public ResponseEntity<ApiResponse> githubOAuth2(@Valid @RequestBody GithubOAuthRequest githubOAuthRequest,
                                                    HttpServletRequest request){
        final ApiResponse response = this.userService.githubOAuth2Service(githubOAuthRequest);
        return ResponseUtils.buildResponse(request , response);
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(userService.signup(request));
    }
}

