package com._p1m.portfolio.features.projectIdea.serviceImpl;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;
import com._p1m.portfolio.config.response.dto.PaginationMeta;
import com._p1m.portfolio.data.models.DevProfile;
import com._p1m.portfolio.data.models.ProjectIdea;
import com._p1m.portfolio.data.repositories.DevProfileRepository;
import com._p1m.portfolio.data.repositories.ProjectIdeaRepository;
import com._p1m.portfolio.features.projectIdea.dto.request.ProjectIdeaRequest;
import com._p1m.portfolio.features.projectIdea.dto.response.ProjectIdeaResponse;
import com._p1m.portfolio.features.projectIdea.service.ProjectIdeaCRService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProjectIdeaCRImpl implements ProjectIdeaCRService {
    private final ProjectIdeaRepository projectIdeaRepository;
    private final DevProfileRepository devProfileRepository;
    @Override
    public ApiResponse createProjectIdea(ProjectIdeaRequest projectIdea, Long devId) {
        if(devProfileRepository.findById(devId).isEmpty()){
            return ApiResponse.builder()
                    .success(0)
                    .code(404)
                    .message("Developer profile not found")
                    .build();
        }
        if(projectIdeaRepository.existsByName(projectIdea.getName())  &&
        projectIdeaRepository.existsByDescription(projectIdea.getDescription()) ){
            return ApiResponse.builder()
                    .success(0)
                    .code(409)
                    .message("Project Idea with the same name and description already exists")
                    .build();
        }
        ProjectIdea newProject = ProjectIdea.builder()
                .name(projectIdea.getName())
                .description(projectIdea.getDescription())
                .approveStatus(projectIdea.isApproveStatus())
                .devProfile(devProfileRepository.findById(devId).get())
                .projectTypes(projectIdea.getProjectTypes())
                .reactedUsers(projectIdea.getReactedUsers())
                .build();
        projectIdeaRepository.save(newProject);

        ProjectIdeaResponse response = ProjectIdeaResponse.builder()
                .name(newProject.getName())
                .description(newProject.getDescription())
                .approveStatus(newProject.isApproveStatus())
                .devProfile(newProject.getDevProfile())
                .projectType(newProject.getProjectTypes())
                .reactedUsers(newProject.getReactedUsers())
                .build();

        return ApiResponse.builder()
                .success(1)
                .code(201)
                .message("Project Idea created successfully")
                .data(response)
                .build();
    }

    @Override
    public PaginatedApiResponse<ProjectIdea> getAllProjectIdeas(HttpServletRequest request) {
        List<ProjectIdea> projectIdeaList = projectIdeaRepository.findAll();

        if(!projectIdeaList.isEmpty()){
            return PaginatedApiResponse.<ProjectIdea>builder()
                    .success(1)
                    .code(200)
                    .message("Project Ideas retrieved successfully")
                    .data(projectIdeaList)
                    .meta(PaginationMeta.builder()
                            .totalItems(projectIdeaList.size())
                            .totalPages(1)
                            .currentPage(1)
                            .method(request.getMethod())
                            .endpoint(request.getRequestURI())
                            .build())
                    .build();
        }
        return PaginatedApiResponse.<ProjectIdea>builder()
                .success(1)
                .code(200)
                .message("No Project Ideas found")
                .data(List.of())
                .meta(PaginationMeta.builder()
                        .totalItems(0)
                        .totalPages(0)
                        .currentPage(0)
                        .method(request.getMethod())
                        .endpoint(request.getRequestURI())
                        .build())
                .build();
    }

    @Override
    public ApiResponse getProjectIdeasByDevProfileId(Long devProfileId) {
        DevProfile developer = devProfileRepository.findById(devProfileId).orElseThrow(() -> new RuntimeException("devProfile not found with id: " + devProfileId));
        if(developer != null){
            List<ProjectIdea> projectIdeas = projectIdeaRepository.findByDevProfile(developer);
            if(!projectIdeas.isEmpty()){
                return ApiResponse.builder()
                        .success(1)
                        .code(200)
                        .message("Project Ideas retrieved successfully")
                        .data(projectIdeas)
                        .build();
            }else{
                return ApiResponse.builder()
                        .success(0)
                        .code(404)
                        .message("No Project Ideas found for the given developer profile ID")
                        .data(List.of())
                        .build();
            }
        }
        return ApiResponse.builder()
                .success(0)
                .code(404)
                .message("Developer profile not found")
                .build();
    }

    @Override
    public ApiResponse getProjectIdeaByName(String name) {
        List<ProjectIdea> projectIdeas = projectIdeaRepository.findByName(name);
        if(!projectIdeas.isEmpty()){
            return ApiResponse.builder()
                    .success(1)
                    .code(200)
                    .message("Project Ideas retrieved successfully")
                    .data(projectIdeas)
                    .build();
        }else {
            return ApiResponse.builder()
                    .success(0)
                    .code(404)
                    .message("No Project Ideas found for the given name ")
                    .data(List.of())
                    .build();
        }

    }
}
