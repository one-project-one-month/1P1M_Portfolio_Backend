package com._p1m.portfolio.features.projectIdea.service;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.features.projectIdea.dto.request.ProjectIdeaRequest;

public interface ProjectIdeaCRService {
    ApiResponse createProjectIdea(ProjectIdeaRequest projectIdea) ;
}
