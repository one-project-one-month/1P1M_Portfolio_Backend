package com._p1m.portfolio.security.OAuth2.Google.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleOAuthResponse {
    // Getters and Setters
    private Object user;
    private String token;
    private String profile_picture;
    private boolean isNewUser;

}
