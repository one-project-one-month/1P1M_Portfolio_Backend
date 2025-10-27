package com._p1m.portfolio.features.approvedProjectIdeas.service;

import org.springframework.data.domain.Pageable;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;
import com._p1m.portfolio.features.approvedProjectIdeas.dto.request.UpdateApprovedIdeaRequest;
import com._p1m.portfolio.features.approvedProjectIdeas.dto.response.ApprovedIdeaResponse;

public interface ApprovedIdeaService {
    PaginatedApiResponse<ApprovedIdeaResponse> listApprovedIdeas(String sortBy, Pageable pageable , String token);
    ApiResponse updateApprovedIdea(Long ideaId, UpdateApprovedIdeaRequest request, String token);
    ApiResponse deleteApprovedIdea(Long ideaId, String token);
}