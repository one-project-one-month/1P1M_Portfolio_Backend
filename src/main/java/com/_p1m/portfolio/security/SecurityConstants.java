package com._p1m.portfolio.security;

public class SecurityConstants {

    public static final String[] WHITELIST = {
            // Authentication & OAuth
            "/api/v1/auth/**",
            "/auth/user/**",
            "/oauth2/**",
            "/login/oauth2/**",
            "/portfolio/api/v1/auth/users/githubOAuth2/**",
            "/portfolio/api/v1/auth/users/exchangeGitHub",
            "/portfolio/api/v1/auth/users/exchangeGoogle",
            "/portfolio/api/v1/auth/users/send-otpCode",
            "/portfolio/api/v1/auth/users/verify-otpCode",
            "/portfolio/api/v1/auth/devProfile",
            "/portfolio/api/v1/auth/devProfile/create/{id}",
            "/portfolio/api/v1/auth/devProfile/{id}",
            "/portfolio/api/v1/auth/devProfile/name/{name}",
            "/portfolio/api/v1/auth/devProfile/techStack/{techStack}",
            "/portfolio/api/v1/auth/devProfile/delete/{id}",
            // Swagger & API Docs
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-resources"
    };

    private SecurityConstants(){

    }
}
