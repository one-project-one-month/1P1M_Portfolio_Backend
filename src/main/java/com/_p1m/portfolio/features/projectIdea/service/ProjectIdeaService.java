package com._p1m.portfolio.features.projectIdea.service;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.features.projectIdea.dto.request.ProjectIdeaRequest;
import jakarta.validation.Valid;

public interface ProjectIdeaService {
    ApiResponse createNewProjectIdea(@Valid ProjectIdeaRequest projectIdeaRequest, String token);
}
