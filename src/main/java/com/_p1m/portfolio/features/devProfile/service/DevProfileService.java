package com._p1m.portfolio.features.devProfile.service;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.features.devProfile.dto.request.CreateDevProfileRequest;

public interface DevProfileService {
    ApiResponse createDevProfile(CreateDevProfileRequest request, Long userId);
}