package com._p1m.portfolio.features.projectIdea.service;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;
import com._p1m.portfolio.features.projectIdea.dto.request.ProjectIdeaRequest;
import com._p1m.portfolio.features.projectIdea.dto.response.ProjectIdeaListResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

public interface ProjectIdeaService {
    ApiResponse createNewProjectIdea(@Valid ProjectIdeaRequest projectIdeaRequest, String token);

    ApiResponse approveProjectIdeaStatus(Long projectIdeaId, Long status , String token);

    PaginatedApiResponse<ProjectIdeaListResponse> getAllPaginatedProjectIdeaList(String keyword, Pageable pageable);
}
