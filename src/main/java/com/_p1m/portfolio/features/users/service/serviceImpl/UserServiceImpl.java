package com._p1m.portfolio.features.users.service.impl;

import com._p1m.portfolio.data.models.User;
import com._p1m.portfolio.data.models.Role;
import com._p1m.portfolio.data.repositories.UserRepository;
import com._p1m.portfolio.features.users.dto.request.LoginRequest;
import com._p1m.portfolio.features.users.dto.request.SignupRequest;
import com._p1m.portfolio.features.users.dto.response.AuthResponse;
import com._p1m.portfolio.features.users.service.UserService;
import com._p1m.portfolio.security.JWT.JWTUtil;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail());
    }

    @Override
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()) != null) {
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

