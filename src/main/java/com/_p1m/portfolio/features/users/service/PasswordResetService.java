package com._p1m.portfolio.features.users.service;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.features.users.dto.request.ForgotPasswordRequest;
import com._p1m.portfolio.features.users.dto.request.ResetPasswordRequest;
import com._p1m.portfolio.features.users.dto.request.VerifyOtpRequest;

public interface PasswordResetService {
    ApiResponse sendResetOtp(ForgotPasswordRequest request) throws Exception;
    ApiResponse verifyResetOtp(VerifyOtpRequest request);
    ApiResponse resetPassword(ResetPasswordRequest request);
}