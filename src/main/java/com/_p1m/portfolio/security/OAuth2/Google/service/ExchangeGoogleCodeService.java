package com._p1m.portfolio.security.OAuth2.Google.service;

import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

@Service
public interface ExchangeGoogleCodeService {
    String exchangeGoogleCodeForToken(final String code);
}
