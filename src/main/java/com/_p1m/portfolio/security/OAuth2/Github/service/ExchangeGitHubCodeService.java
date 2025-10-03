package com._p1m.portfolio.security.OAuth2.Github.service;

import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

@Service
public interface ExchangeGitHubCodeService {
    String exchangeGithubCodeForToken(final String code);
}
