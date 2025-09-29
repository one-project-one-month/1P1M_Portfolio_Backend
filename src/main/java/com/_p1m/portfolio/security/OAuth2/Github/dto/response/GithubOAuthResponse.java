package com._p1m.portfolio.security.OAuth2.Github.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GithubOAuthResponse {
    private Object user;
    private String token; // JWT token
    private boolean newUser;
}
