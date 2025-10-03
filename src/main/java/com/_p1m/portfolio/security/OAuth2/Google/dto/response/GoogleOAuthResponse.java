package com._p1m.portfolio.security.OAuth2.Google.dto.response;

import lombok.Data;

@Data
public class GoogleOAuthResponse {
    private Object user;
    private String token;
    private boolean isNewUser;

    // Default constructor
    public GoogleOAuthResponse() {}

    // Constructor
    public GoogleOAuthResponse(Object user, String token, boolean isNewUser) {
        this.user = user;
        this.token = token;
        this.isNewUser = isNewUser;
    }

    // Getters and Setters
    public Object getUser() {
        return user;
    }

    public void setUser(Object user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isNewUser() {
        return isNewUser;
    }

    public void setNewUser(boolean newUser) {
        isNewUser = newUser;
    }
}
