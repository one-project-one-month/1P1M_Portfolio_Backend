package com._p1m.portfolio.security;

public class SecurityConstants {

    public static final String[] WHITELIST = {
            // Authentication & OAuth
            "/portfolio/api/v1/auth/users/**",

            // List APIs for Public Views
            "/portfolio/api/v1/project-idea/getAllProjectIdeas",
            "/portfolio/api/v1/project-idea/getAllProjectProfiles",
            "/portfolio/api/v1/project-portfolio/getAllProjectProfiles",
            "/portfolio/api/v1/approved-ideas",

            // Swagger & API Docs
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-resources"
    };

    private SecurityConstants(){

    }
}
