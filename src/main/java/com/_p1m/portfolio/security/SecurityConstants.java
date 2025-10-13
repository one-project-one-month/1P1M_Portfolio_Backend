package com._p1m.portfolio.security;

public class SecurityConstants {

    public static final String[] WHITELIST = {
            // Authentication & OAuth
            "/portfolio/api/v1/auth/users/**",
            "/portfolio/api/v1/**",
            "http://localhost:8080/portfolio/api/v1/register",
            // Swagger & API Docs
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-resources"
    };

    private SecurityConstants(){

    }
}
