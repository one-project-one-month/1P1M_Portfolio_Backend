package com._p1m.portfolio.security;

public class SecurityConstants {

    public static final String[] WHITELIST = {
            // Authentication & OAuth
            "/api/v1/auth/**",
            "/auth/user/**",
            "/oauth2/**",
            "/login/oauth2/**",
            "/portfolio/api/v1/auth/users/githubOAuth2/**",

            // Swagger & API Docs
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-resources"
    };

    private SecurityConstants(){

    }
}
