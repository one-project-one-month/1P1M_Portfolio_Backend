package com._p1m.portfolio.features.projectIdea.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com._p1m.portfolio.config.exceptions.EntityNotFoundException;
import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;
import com._p1m.portfolio.config.response.dto.PaginationMeta;
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
import com._p1m.portfolio.features.projectIdea.dto.response.ProjectIdeaListResponse;
import com._p1m.portfolio.features.projectIdea.service.ProjectIdeaService;
import com._p1m.portfolio.security.JWT.JWTUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

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
    public PaginatedApiResponse<ProjectIdeaListResponse> getAllPaginatedProjectIdeaList(String keyword, Pageable pageable , String token) {
        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found from token"));

        Specification<ProjectIdea> spec = (root, query, criteriaBuilder) -> {
            if (keyword != null && !keyword.trim().isEmpty()) {
                return criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + keyword.toLowerCase() + "%"
                );
            }
            return null;
        };

        Set<Long> reactedProjectIds = user.getReactedProjectIdeas()
                .stream()
                .map(ProjectIdea::getId)
                .collect(Collectors.toSet());

        Page<ProjectIdea> projectIdeas = this.projectIdeaRepository.findAll(spec, pageable);
        List<ProjectIdeaListResponse> projectIdeaListResponses = projectIdeas.getContent().stream()
                .map(projectIdea -> ProjectIdeaListResponse.builder()
                        .id(projectIdea.getId())
                        .projectName(projectIdea.getName())
                        .description(projectIdea.getDescription())
                        .dev_id(projectIdea.getDevProfile().getId())
                        .devName(projectIdea.getDevProfile().getName())
                        .profilePictureUrl(projectIdea.getDevProfile().getProfilePictureUrl())
                        .status(projectIdea.getStatus())
                        .reaction_count(projectIdea.getReactedUsers() != null
                                ? projectIdea.getReactedUsers().size()
                                : 0)
                        .projectTypes(projectIdea.getProjectTypes().stream()
                                .map(ProjectType::getName)
                                .toList())
                        .reactedProjects(
                                user.getReactedProjectIdeas().stream()
                                        .map(ProjectIdea::getId)
                                        .toList()
                        )
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

    @Override
    public ApiResponse deleteProjectIdea(Long projectIdeaId) {
        ProjectIdea projectIdea = projectIdeaRepository.findById(projectIdeaId)
                .orElseThrow(() -> new EntityNotFoundException("Project Idea not found for id: " + projectIdeaId));
        projectIdeaRepository.delete(projectIdea);
        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(true)
                .message("Project Idea deleted successfully.")
                .build();
    }


    @Override
    @Transactional
    public ApiResponse reactProjectIdea(Long projectIdeaId, String token) {
        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found from token"));

        ProjectIdea projectIdea = projectIdeaRepository.findById(projectIdeaId)
                .orElseThrow(() -> new com._p1m.portfolio.config.exceptions.EntityNotFoundException("Project Idea Does not Exist."));

        if (projectIdea.getReactedUsers().contains(user)) {
            return ApiResponse.builder()
                    .success(0)
                    .code(HttpStatus.CONFLICT.value()) 
                    .message("You have already reacted to this project.")
                    .data(false)
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }

        projectIdea.getReactedUsers().add(user);
        user.getReactedProjectIdeas().add(projectIdea);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.CREATED.value())
                .message("Reacted successfully.")
                .data(true)
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }
	
	@Override
	@Transactional
	public ApiResponse unreactProjectIdea(Long projectIdeaId, String token) {
	    String email = jwtUtil.extractEmail(token);
	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new EntityNotFoundException("User not found from token"));

	    ProjectIdea projectIdea = projectIdeaRepository.findById(projectIdeaId)
	            .orElseThrow(() -> new com._p1m.portfolio.config.exceptions.EntityNotFoundException("Project Idea Does not Exist."));

	    boolean removed = projectIdea.getReactedUsers().remove(user);
	    user.getReactedProjectIdeas().remove(projectIdea);
	    if (removed) {
	        return ApiResponse.builder()
	                .success(1)
	                .code(HttpStatus.OK.value())
	                .message("Reaction removed successfully.")
	                .data(true)
	                .meta(Map.of("timestamp", System.currentTimeMillis()))
	                .build();
	    } else {
	        return ApiResponse.builder()
	                .success(0)
	                .code(HttpStatus.NOT_FOUND.value())
	                .message("User had not reacted to this project idea.")
	                .data(false)
	                .meta(Map.of("timestamp", System.currentTimeMillis()))
	                .build();
	    }
	}
	
	@Override
	public ApiResponse getProjectIdeaReactionCount(Long projectIdeaId) {
	    ProjectIdea projectIdea = projectIdeaRepository.findById(projectIdeaId)
	            .orElseThrow(() -> new com._p1m.portfolio.config.exceptions.EntityNotFoundException("Project Idea Does not Exist."));

	    int reactionCount = projectIdea.getReactedUsers().size();

	    return ApiResponse.builder()
	            .success(1)
	            .code(HttpStatus.OK.value())
	            .message("Reaction count fetched successfully.")
	            .data(Map.of(
	                "projectIdeaId", projectIdeaId,
	                "reactionCount", reactionCount
	            ))
	            .meta(Map.of("timestamp", System.currentTimeMillis()))
	            .build();
	}


}
