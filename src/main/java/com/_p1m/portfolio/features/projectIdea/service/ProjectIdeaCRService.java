package com._p1m.portfolio.features.projectIdea.service;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;
import com._p1m.portfolio.data.models.ProjectIdea;
import com._p1m.portfolio.features.projectIdea.dto.request.ProjectIdeaRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface ProjectIdeaCRService {
    ApiResponse createProjectIdea(ProjectIdeaRequest projectIdea,Long devId);

    PaginatedApiResponse<ProjectIdea> getAllProjectIdeas(HttpServletRequest request);

    ApiResponse getProjectIdeasByDevProfileId(Long devProfileId);

    ApiResponse getProjectIdeaByName(String name);

    ;
}
