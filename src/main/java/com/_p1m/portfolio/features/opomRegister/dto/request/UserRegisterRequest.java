package com._p1m.portfolio.features.opomRegister.dto.request;

import com._p1m.portfolio.common.constant.Status;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Size(min = 8, max = 15, message = "Phone number must be between 8 and 15 digits")
    private String phone;

    private String github_url;
    private String telegram_username;

    @NotBlank(message = "Role is required")
    private String role;

    @NotNull(message = "Status is required")
    private Status status;

    @NotNull(message = "Platform ID is required")
    private Long platformId;

    @NotBlank(message = "Platform link is required")
    private String platformUrl;

    private Long devProfileId; // optional (if linked to a DevProfile)
}
