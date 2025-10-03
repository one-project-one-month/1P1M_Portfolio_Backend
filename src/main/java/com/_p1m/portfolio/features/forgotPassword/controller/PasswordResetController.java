package com._p1m.portfolio.features.forgotPassword.controller;

import com._p1m.portfolio.features.forgotPassword.dto.request.ForgotPasswordRequest;
import com._p1m.portfolio.features.forgotPassword.dto.request.ResetPasswordRequest;
import com._p1m.portfolio.features.forgotPassword.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.handleForgotPasswordRequest(request.getEmail());
        return ResponseEntity.ok("If an account with that email exists, a password reset link has been sent.");
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.handlePasswordReset(request);
        return ResponseEntity.ok("Your password has been successfully reset.");
    }
}