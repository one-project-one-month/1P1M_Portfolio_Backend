package com._p1m.portfolio.security.OAuth2.Github.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GithubOAuthRequest {

    @NotBlank(message = "Token cannot be empty")
    private String token;
}
