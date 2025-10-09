package com._p1m.portfolio.features.projectIdeaUD.service;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.features.projectIdeaUD.dto.request.ProjectIdeaRequest;
import jakarta.validation.Valid;

public interface ProjectIdeaUDService {

    ApiResponse updateProjectIdea(Long ideaId, @Valid ProjectIdeaRequest projectIdeaRequest);

    ApiResponse deleteProjectIdea(Long ideaId);
}
