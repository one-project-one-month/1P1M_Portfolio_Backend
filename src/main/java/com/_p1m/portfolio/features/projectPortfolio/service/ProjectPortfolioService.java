package com._p1m.portfolio.features.projectPortfolio.service;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.features.projectPortfolio.dto.request.CreateProjectPortfolioRequest;

public interface ProjectPortfolioService {
    ApiResponse createProjectPortfolio(final CreateProjectPortfolioRequest createRequest,final String token);
}
