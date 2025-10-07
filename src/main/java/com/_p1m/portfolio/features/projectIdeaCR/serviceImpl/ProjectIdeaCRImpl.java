package com._p1m.portfolio.features.projectIdeaCR.serviceImpl;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.data.models.ProjectIdea;
import com._p1m.portfolio.features.projectIdeaCR.dto.request.ProjectIdeaCRRequest;
import com._p1m.portfolio.features.projectIdeaCR.service.ProjectIdeaCRService;
import org.springframework.stereotype.Service;

@Service
public class ProjectIdeaCRImpl implements ProjectIdeaCRService {

    @Override
    public ApiResponse createProjectIdea(ProjectIdeaCRRequest projectIdea) {
        return null;
    }
}
