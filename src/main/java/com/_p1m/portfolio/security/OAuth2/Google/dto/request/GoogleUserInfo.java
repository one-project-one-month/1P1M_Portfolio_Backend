package com._p1m.portfolio.security.OAuth2.Google.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoogleUserInfo {
    private String email;
    private String name;
    private String givenName;
    private String familyName;
    private String picture;
    private Boolean emailVerified;
    private String googleId;
}