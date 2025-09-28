package com._p1m.portfolio.features.opomRegister.dto.request;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UserRegisterRequest {
    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    private String github_username;

    @Column(unique = true)
    private String telegram_username;

    @Column(nullable = false)
    private String role;
}
