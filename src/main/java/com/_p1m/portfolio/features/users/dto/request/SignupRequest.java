package com._p1m.portfolio.features.users.dto.request;

import lombok.Data;

@Data
public class SignupRequest {
    private String username;
    private String email;
    private String password;
}
