package com._p1m.portfolio.features.projectIdea.service.impl;

import com._p1m.portfolio.config.exceptions.EntityNotFoundException;
import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.data.enums.ProjectIdeaStatus;
import com._p1m.portfolio.data.models.DevProfile;
import com._p1m.portfolio.data.models.ProjectIdea;
import com._p1m.portfolio.data.models.User;
import com._p1m.portfolio.data.models.lookup.ProjectType;
import com._p1m.portfolio.data.repositories.DevProfileRepository;
import com._p1m.portfolio.data.repositories.ProjectIdeaRepository;
import com._p1m.portfolio.data.repositories.ProjectTypeRepository;
import com._p1m.portfolio.data.repositories.UserRepository;
import com._p1m.portfolio.features.projectIdea.dto.request.ProjectIdeaRequest;
import com._p1m.portfolio.features.projectIdea.service.ProjectIdeaService;
import com._p1m.portfolio.security.JWT.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
}
