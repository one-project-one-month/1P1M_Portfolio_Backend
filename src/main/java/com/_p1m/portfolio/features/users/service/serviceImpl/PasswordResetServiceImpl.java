package com._p1m.portfolio.features.users.service.serviceImpl;

import com._p1m.portfolio.common.component.OtpStore;
import com._p1m.portfolio.common.util.ServerUtil;
import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.data.models.User;
import com._p1m.portfolio.data.repositories.UserRepository;
import com._p1m.portfolio.features.users.dto.request.ForgotPasswordRequest;
import com._p1m.portfolio.features.users.dto.request.ResetPasswordRequest;
import com._p1m.portfolio.features.users.dto.request.VerifyOtpRequest;
import com._p1m.portfolio.features.users.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final OtpStore otpStore;
    private final ServerUtil serverUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ApiResponse sendResetOtp(ForgotPasswordRequest request) throws Exception {
        if (userRepository.findByEmail(request.getEmail()).isEmpty()) {
            return ApiResponse.builder()
                    .success(1).code(HttpStatus.OK.value()).message("If a user exists, an OTP has been sent.")
                    .build();
        }

        String email = request.getEmail();
        String otp = serverUtil.generateNumericCode(6);
        serverUtil.sendOtpCode(email, otp);
        otpStore.saveOtp(email, otp, 15);

        return ApiResponse.builder()
                .success(1).code(HttpStatus.OK.value()).message("If a user exists, an OTP has been sent.")
                .build();
    }

    @Override
    public ApiResponse verifyResetOtp(VerifyOtpRequest request) {
        if (!otpStore.verifyOtp(request.getEmail(), request.getOtpCode())) {
            return ApiResponse.builder()
                    .success(0).code(HttpStatus.BAD_REQUEST.value()).message("Invalid or expired OTP.")
                    .build();
        }

        return ApiResponse.builder()
                .success(1).code(HttpStatus.OK.value()).message("OTP verified successfully.")
                .build();
    }

    @Override
    @Transactional
    public ApiResponse resetPassword(ResetPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found."));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ApiResponse.builder()
                .success(1).code(HttpStatus.OK.value()).message("Password has been reset successfully.")
                .build();
    }
}