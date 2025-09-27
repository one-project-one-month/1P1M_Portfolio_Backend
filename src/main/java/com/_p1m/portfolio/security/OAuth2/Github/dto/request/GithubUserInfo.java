package com._p1m.portfolio.security.OAuth2.Github.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GithubUserInfo {
    private String id;
    private String login;
    private String name;
    private String email;
    private String avatarUrl;
}