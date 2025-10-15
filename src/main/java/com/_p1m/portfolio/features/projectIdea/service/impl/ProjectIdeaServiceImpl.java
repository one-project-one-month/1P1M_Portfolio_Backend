package com._p1m.portfolio.features.projectIdea.service.impl;

import com._p1m.portfolio.config.exceptions.EntityNotFoundException;
import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;
import com._p1m.portfolio.config.response.dto.PaginationMeta;
import com._p1m.portfolio.data.enums.ProjectIdeaStatus;
import com._p1m.portfolio.data.models.*;
import com._p1m.portfolio.data.models.lookup.ProjectType;
import com._p1m.portfolio.data.repositories.DevProfileRepository;
import com._p1m.portfolio.data.repositories.ProjectIdeaRepository;
import com._p1m.portfolio.data.repositories.ProjectTypeRepository;
import com._p1m.portfolio.data.repositories.UserRepository;
import com._p1m.portfolio.features.opomRegister.dto.response.OpomRegisterResponse;
import com._p1m.portfolio.features.projectIdea.dto.request.ProjectIdeaRequest;
import com._p1m.portfolio.features.projectIdea.dto.response.ProjectIdeaListResponse;
import com._p1m.portfolio.features.projectIdea.service.ProjectIdeaService;
import com._p1m.portfolio.security.JWT.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProjectIdeaServiceImpl implements ProjectIdeaService {
    private final JWTUtil jwtUtil;
    private final ProjectIdeaRepository projectIdeaRepository;
    private final DevProfileRepository devProfileRepository;
    private final ProjectTypeRepository projectTypeRepository;
    private final UserRepository userRepository;

    @Override
    public ApiResponse createNewProjectIdea(ProjectIdeaRequest projectIdeaRequest, String token) {
        String email = this.jwtUtil.extractEmail(token);
        DevProfile devProfile = devProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new com._p1m.portfolio.config.exceptions.EntityNotFoundException("DevProfile not found for email: " + email));


        if (projectIdeaRepository.existsByName(projectIdeaRequest.getProjectName())) {
            return ApiResponse.builder()
                    .success(0)
                    .code(HttpStatus.CONFLICT.value())
                    .message("Project Name Already Exists.")
                    .data(Map.of("projectName", projectIdeaRequest.getProjectName()))
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }


        ProjectIdea projectIdea = new ProjectIdea();
        projectIdea.setName(projectIdeaRequest.getProjectName());
        projectIdea.setDescription(projectIdeaRequest.getDescription());
        projectIdea.setApproveStatus(false);
        projectIdea.setStatus(ProjectIdeaStatus.PENDING);
        projectIdea.setDevProfile(devProfile);

        Set<ProjectType> resolvedTypes = new HashSet<>();
        for (String typeName : projectIdeaRequest.getProjectType()) {
            ProjectType projectType = projectTypeRepository.findByName(typeName)
                    .orElseGet(() -> {
                        ProjectType newType = new ProjectType();
                        newType.setName(typeName);
                        return projectTypeRepository.save(newType);
                    });
            resolvedTypes.add(projectType);
        }
        projectIdea.setProjectTypes(resolvedTypes);

        projectIdeaRepository.save(projectIdea);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.CREATED.value())
                .message("Project Idea Created successfully.")
                .data(Map.of(
                        "Project Idea Name: ", projectIdea.getName(),
                        "Project Idea Approve Status : ", projectIdea.getStatus()
                ))
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }


    @Override
    public ApiResponse approveProjectIdeaStatus(Long projectIdeaId, Long status , String token) {
        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found from token"));

        if (!user.getRole().getName().equalsIgnoreCase("ADMIN")) {
            throw new SecurityException("Access Denied: Admin role required.");
        }

        ProjectIdea projectIdea = projectIdeaRepository.findById(projectIdeaId)
                .orElseThrow(() -> new com._p1m.portfolio.config.exceptions.EntityNotFoundException("Project Idea Does not Exist."));

        projectIdea.setApproveStatus(status == 1);
        projectIdea.setStatus(projectIdea.isApproveStatus() ? ProjectIdeaStatus.APPROVED : ProjectIdeaStatus.REJECTED);
        projectIdeaRepository.save(projectIdea);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Project Idea Status Updated successfully.")
                .data(Map.of(
                        "Project Idea Name: ", projectIdea.getName(),
                        "Project Idea Approve Status : ", projectIdea.getStatus()
                ))
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    @Override
    public PaginatedApiResponse<ProjectIdeaListResponse> getAllPaginatedProjectIdeaList(String keyword, Pageable pageable) {
        Specification<ProjectIdea> spec = (root, query, criteriaBuilder) -> {
            if (keyword != null && !keyword.trim().isEmpty()) {
                return criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + keyword.toLowerCase() + "%"
                );
            }
            return null;
        };

        Page<ProjectIdea> projectIdeas = this.projectIdeaRepository.findAll(spec, pageable);
        List<ProjectIdeaListResponse> projectIdeaListResponses = projectIdeas.getContent().stream()
                .map(projectIdea -> ProjectIdeaListResponse.builder()
                        .id(projectIdea.getId())
                        .projectName(projectIdea.getName())
                        .description(projectIdea.getDescription())
                        .dev_id(projectIdea.getDevProfile().getId())
                        .devName(projectIdea.getDevProfile().getName())
                        .profilePictureUrl(projectIdea.getDevProfile().getProfilePictureUrl())
                        .reaction_count(projectIdea.getReactedUsers() != null
                                ? projectIdea.getReactedUsers().size()
                                : 0)
                        .projectTypes(projectIdea.getProjectTypes().stream()
                                .map(ProjectType::getName)
                                .toList())
                        .build()
                ).toList();

        PaginationMeta meta = new PaginationMeta();
        meta.setTotalItems(projectIdeas.getTotalElements());
        meta.setTotalPages(projectIdeas.getTotalPages());
        meta.setCurrentPage(pageable.getPageNumber() + 1);

        return PaginatedApiResponse.<ProjectIdeaListResponse>builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Fetched successfully")
                .meta(meta)
                .data(projectIdeaListResponses)
                .build();
    }
}
