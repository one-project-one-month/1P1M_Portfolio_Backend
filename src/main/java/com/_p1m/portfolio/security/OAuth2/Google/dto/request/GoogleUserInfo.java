package com._p1m.portfolio.security.OAuth2.Google.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleUserInfo {
    private String email;
    private String name;
    private String givenName;
    private String familyName;
    private String picture;
    private Boolean emailVerified;
    private String googleId;
}