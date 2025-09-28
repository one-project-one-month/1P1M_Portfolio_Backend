package com._p1m.portfolio.features.users.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GoogleOAuthRequest {

    @NotNull(message = "Token is required")
    @NotBlank(message = "Token cannot be empty")
    private String token;

    // Default constructor
    public GoogleOAuthRequest() {}

    // Constructor with token
    public GoogleOAuthRequest(String token) {
        this.token = token;
    }

    // Getter and Setter
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "GoogleOAuthRequest{" +
                "token='" + (token != null ? token.substring(0, Math.min(token.length(), 20)) + "..." : "null") + '\'' +
                '}';
    }
}