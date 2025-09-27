package com._p1m.portfolio.features.users.service;

import com._p1m.portfolio.features.users.dto.request.LoginRequest;
import com._p1m.portfolio.features.users.dto.request.SignupRequest;
import com._p1m.portfolio.features.users.dto.response.AuthResponse;

public interface UserService {
    AuthResponse login(LoginRequest request);
    AuthResponse signup(SignupRequest request);
}
