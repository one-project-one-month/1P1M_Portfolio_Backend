package com._p1m.portfolio.features.projectIdeaUD.serviceImpl;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.data.models.ProjectIdea;
import com._p1m.portfolio.data.repositories.ProjectIdeaRepository;
import com._p1m.portfolio.features.projectIdeaUD.dto.request.ProjectIdeaRequest;
import com._p1m.portfolio.features.projectIdeaUD.service.ProjectIdeaUDService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectIdeaUDServiceImpl implements ProjectIdeaUDService {

    private final ProjectIdeaRepository projectIdeaRepository;

    @Override
    public ApiResponse updateProjectIdea(Long ideaId, ProjectIdeaRequest projectIdeaRequest) {
        Optional<ProjectIdea> optionalIdea = projectIdeaRepository.findById(ideaId);

        if (optionalIdea.isEmpty()) {
            return ApiResponse.builder()
                    .success(0)
                    .code(404)
                    .message("Project Idea not found with id: " + ideaId)
                    .build();
        }

        ProjectIdea existingIdea = optionalIdea.get();

        // Update editable fields
        existingIdea.setName(projectIdeaRequest.getName());
        existingIdea.setDescription(projectIdeaRequest.getDescription());
        existingIdea.setApproveStatus(projectIdeaRequest.isApproveStatus());
        existingIdea.setDevProfile(projectIdeaRequest.getDevProfile());
        existingIdea.setProjectTypes(projectIdeaRequest.getProjectTypes());
        existingIdea.setReactedUsers(projectIdeaRequest.getReactedUsers());

        ProjectIdea updatedIdea = projectIdeaRepository.save(existingIdea);

        return ApiResponse.builder()
                .success(1)
                .code(200)
                .message("Project Idea updated successfully")
                .data(updatedIdea)
                .build();
    }

    @Override
    public ApiResponse deleteProjectIdea(Long ideaId) {
        Optional<ProjectIdea> optionalIdea = projectIdeaRepository.findById(ideaId);

        if (optionalIdea.isEmpty()) {
            return ApiResponse.builder()
                    .success(0)
                    .code(404)
                    .message("Project Idea not found with id: " + ideaId)
                    .build();
        }

        ProjectIdea idea = optionalIdea.get();

        projectIdeaRepository.delete(idea);

        return ApiResponse.builder()
                .success(1)
                .code(200)
                .message("Project Idea deleted successfully")
                .build();
    }
}
