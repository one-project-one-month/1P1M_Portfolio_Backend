package com._p1m.portfolio.features.projectIdea.serviceImpl;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;
import com._p1m.portfolio.config.response.dto.PaginationMeta;
import com._p1m.portfolio.data.models.DevProfile;
import com._p1m.portfolio.data.models.ProjectIdea;
import com._p1m.portfolio.data.repositories.DevProfileRepository;
import com._p1m.portfolio.data.repositories.ProjectIdeaRepository;
import com._p1m.portfolio.features.projectIdea.dto.request.ProjectIdeaRequest;
import com._p1m.portfolio.features.projectIdea.service.ProjectIdeaCRService;
import lombok.RequiredArgsConstructor;
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
                .approveStatus(true)
                .devProfile(devProfileRepository.findById(devId).get())
                .projectTypes(projectIdea.getProjectTypes())
                .reactedUsers(projectIdea.getReactedUsers())
                .build();
        ProjectIdea response = projectIdeaRepository.save(newProject);

        return ApiResponse.builder()
                .success(1)
                .code(201)
                .message("Project Idea created successfully")
                .data(response) // in case i do have dto response i will use it here
                .build();
    }

    @Override
    public PaginatedApiResponse<ProjectIdea> getAllProjectIdeas() {
        List<ProjectIdea> projectIdeaList = projectIdeaRepository.findAll();
        PaginationMeta metaData = new PaginationMeta();
        metaData.setTotalItems(projectIdeaList.size());
        metaData.setTotalPages(1);     // Since all data is retrieved at once
        metaData.setCurrentPage(1);    // Since all data is retrieved at once
        metaData.setMethod("GET");     // Appropriate HTTP method
        metaData.setEndpoint("/project-ideas");
        if(!projectIdeaList.isEmpty()){
            return PaginatedApiResponse.<ProjectIdea>builder()
                    .success(1)
                    .code(200)
                    .message("Project Ideas retrieved successfully")
                    .data(projectIdeaList)
                    .meta(metaData)
                    .build();
        }
        return PaginatedApiResponse.<ProjectIdea>builder()
                .success(1)
                .code(200)
                .message("No Project Ideas found")
                .data(List.of())
                .meta(metaData)
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
