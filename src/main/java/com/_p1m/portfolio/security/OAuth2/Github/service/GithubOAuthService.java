package com._p1m.portfolio.security.OAuth2.Github.service;

import com._p1m.portfolio.security.OAuth2.Github.GitHubApiClient;
import com._p1m.portfolio.security.OAuth2.Github.dto.request.GithubUserInfo;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GithubOAuthService {

    private final GitHubApiClient githubApiClient;

    public GithubOAuthService(GitHubApiClient githubApiClient) {
        this.githubApiClient = githubApiClient;
    }

    public GithubUserInfo verifyAccessToken(String accessToken) throws IOException {
        return githubApiClient.getUserProfile(accessToken);
    }
}