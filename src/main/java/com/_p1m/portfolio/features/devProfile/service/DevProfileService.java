package com._p1m.portfolio.features.devProfile.service;

import com._p1m.portfolio.features.devProfile.dto.request.CreateDevProfileRequest;
import com._p1m.portfolio.features.devProfile.dto.response.DevProfileResponse;

public interface DevProfileService {
    DevProfileResponse createDevProfile(CreateDevProfileRequest request, Long userId);
}