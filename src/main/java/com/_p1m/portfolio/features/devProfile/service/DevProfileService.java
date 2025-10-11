package com._p1m.portfolio.features.devProfile.service;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;
import com._p1m.portfolio.data.models.DevProfile;
import com._p1m.portfolio.features.devProfile.dto.request.DevProfileRequest;
import com._p1m.portfolio.features.devProfile.dto.response.DevProfileResponse;

public interface DevProfileService {
    ApiResponse createDevProfile(DevProfileRequest request, Long userId);
    PaginatedApiResponse<DevProfileResponse> findAllDevPf();
    ApiResponse findDevById(Long id);
    ApiResponse findDevByName(String name);

    ApiResponse deleteDevProfile(Long id);

    PaginatedApiResponse<DevProfileResponse> findByTechStack(String techStack);
}