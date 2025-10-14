package com._p1m.portfolio.features.devProfile.service;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;
import com._p1m.portfolio.data.models.DevProfile;
import com._p1m.portfolio.features.devProfile.dto.request.CreateDevProfileRequest;
import com._p1m.portfolio.features.devProfile.dto.request.UpdateDevProfileRequest;
import com._p1m.portfolio.features.devProfile.dto.response.DevProfileListResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DevProfileService {
    ApiResponse createDevProfile(CreateDevProfileRequest request, Long userId);

    PaginatedApiResponse<DevProfileListResponse> getAllPaginatedDeveloperList(String keyword, Pageable pageable);

    ApiResponse updateDevProfile(@Valid UpdateDevProfileRequest updateRequest, Long id , String token);
}