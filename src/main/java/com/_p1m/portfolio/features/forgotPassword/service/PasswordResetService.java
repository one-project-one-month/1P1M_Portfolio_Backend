package com._p1m.portfolio.features.forgotPassword.service;

import com._p1m.portfolio.data.models.PasswordResetToken;
import com._p1m.portfolio.data.models.User;
import com._p1m.portfolio.data.repositories.UserRepository;
import com._p1m.portfolio.features.forgotPassword.dto.request.ResetPasswordRequest;
import com._p1m.portfolio.features.forgotPassword.repository.PasswordResetTokenRepository;
import com._p1m.portfolio.features.forgotPassword.repository.UserFinder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserFinder userFinder;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void handleForgotPasswordRequest(String email) {
        User user = userFinder.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email " + email + " not found."));

        tokenRepository.deleteByUser(user);

        String tokenValue = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(tokenValue, user);

        tokenRepository.save(resetToken);

        // Printing it to the console for testing
        System.out.println("---- PASSWORD RESET ----");
        System.out.println("Sending reset link to: " + email);
        System.out.println("Link: http://localhost:3000/reset-password?token=" + tokenValue);
        System.out.println("----------------------");
    }

    @Transactional
    public void handlePasswordReset(ResetPasswordRequest request) {
        PasswordResetToken token = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid password reset token."));

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Password reset token has expired.");
        }

        User user = token.getUser();

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        tokenRepository.delete(token);
    }
}
