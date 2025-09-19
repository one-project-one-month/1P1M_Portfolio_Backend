package com._p1m.portfolio.features.opomRegister.dto.request;

import lombok.Data;

@Data
public class UserRegisterRequest {
    private String name;
    private String email;
    private String phone;
    private String github_username;
    private String telegram_username;
    private String role;
}
