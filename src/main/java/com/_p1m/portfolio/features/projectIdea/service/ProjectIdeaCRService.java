package com._p1m.portfolio.features.projectIdeaCR.service;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.data.models.ProjectIdea;
import com._p1m.portfolio.features.projectIdeaCR.dto.request.ProjectIdeaCRRequest;

public interface ProjectIdeaCRService {
    ApiResponse createProjectIdea(ProjectIdeaCRRequest projectIdea) ;
}
