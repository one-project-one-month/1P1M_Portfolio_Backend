package com._p1m.portfolio.security.OAuth2.Github.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GithubUserInfo {
    private String email;
    private String name;
    private String login; // GitHub username
    private String avatarUrl;
    private String bio;
    private String location;
    private String company;
    private String blog;
    private Long githubId; // GitHub user ID
    private Boolean emailVerified; // We'll assume verified since it comes from GitHub API
}