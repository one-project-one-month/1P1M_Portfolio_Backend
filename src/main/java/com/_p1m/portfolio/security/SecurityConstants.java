package com._p1m.portfolio.security;

public class SecurityConstants {

    public static final String[] WHITELIST = {
            // Authentication & OAuth
            "/portfolio/api/v1/auth/**",
            "/portfolio/api/v1/**",//this is so wrong, need to fix immediately :::::-0
            // Swagger & API Docs
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-resources"
    };

    private SecurityConstants(){

    }
}
