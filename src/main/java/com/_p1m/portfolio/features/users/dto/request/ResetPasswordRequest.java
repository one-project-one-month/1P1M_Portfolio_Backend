package com._p1m.portfolio.features.users.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank(message = "Email is required.")
    private String email;

    @NotBlank(message = "OTP code is required.")
    private String otpCode;

    @NotBlank(message = "New password is required.")
    @Size(min = 8, message = "Password must be at least 8 characters long.")
    private String newPassword;
}
