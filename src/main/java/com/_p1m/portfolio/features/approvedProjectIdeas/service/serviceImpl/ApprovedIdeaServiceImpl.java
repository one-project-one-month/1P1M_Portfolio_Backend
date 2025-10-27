package com._p1m.portfolio.features.approvedProjectIdeas.service.serviceImpl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com._p1m.portfolio.config.response.dto.PaginationMeta;
import com._p1m.portfolio.data.enums.ProjectIdeaStatus;
import com._p1m.portfolio.data.models.lookup.ProjectType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._p1m.portfolio.config.exceptions.EntityNotFoundException;
import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;
import com._p1m.portfolio.data.models.ProjectIdea;
import com._p1m.portfolio.data.models.User;
import com._p1m.portfolio.data.repositories.ProjectIdeaRepository;
import com._p1m.portfolio.data.repositories.UserRepository;
import com._p1m.portfolio.features.approvedProjectIdeas.dto.request.UpdateApprovedIdeaRequest;
import com._p1m.portfolio.features.approvedProjectIdeas.dto.response.ApprovedIdeaResponse;
import com._p1m.portfolio.features.approvedProjectIdeas.service.ApprovedIdeaService;
import com._p1m.portfolio.security.JWT.JWTUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApprovedIdeaServiceImpl implements ApprovedIdeaService {

    private final ProjectIdeaRepository projectIdeaRepository;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    @Override
    @Transactional(readOnly = true)
    public PaginatedApiResponse<ApprovedIdeaResponse> listApprovedIdeas(String sortBy, Pageable pageable ,String token) {
        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found from token"));

        Page<ProjectIdea> ideaPage;

        if ("popular".equalsIgnoreCase(sortBy)) {
            ideaPage = projectIdeaRepository.findPopularApproved(pageable);
        } else {
            ideaPage = projectIdeaRepository.findByApproveStatus(true, pageable);
        }

        Set<Long> reactedProjectIds = user.getReactedProjectIdeas()
                .stream()
                .map(ProjectIdea::getId)
                .collect(Collectors.toSet());

        List<ApprovedIdeaResponse> responses = ideaPage.getContent().stream()
                .map(idea -> ApprovedIdeaResponse.builder()
                        .id(idea.getId())
                        .name(idea.getName())
                        .description(idea.getDescription())
                        .dev_id(idea.getDevProfile() != null ? idea.getDevProfile().getId() : null)
                        .devName(idea.getDevProfile() != null ? idea.getDevProfile().getName() : "Anonymous")
                        .profilePictureUrl(idea.getDevProfile() != null ? idea.getDevProfile().getProfilePictureUrl() : null)
                        .status(idea.getStatus())
                        .reactionCount(idea.getReactedUsers() != null ? idea.getReactedUsers().size() : 0)
                        .projectTypes(idea.getProjectTypes().stream()
                                .map(ProjectType::getName)
                                .toList())
                        .reactedProjects(
                                user.getReactedProjectIdeas().stream()
                                        .map(ProjectIdea::getId)
                                        .toList()
                        )
                        .build())
                .toList();

        PaginationMeta meta = new PaginationMeta();
        meta.setTotalItems(ideaPage.getTotalElements());
        meta.setTotalPages(ideaPage.getTotalPages());
        meta.setCurrentPage(pageable.getPageNumber() + 1);

        return PaginatedApiResponse.<ApprovedIdeaResponse>builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Approved project ideas fetched successfully.")
                .meta(meta)
                .data(responses)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse updateApprovedIdea(Long ideaId, UpdateApprovedIdeaRequest request, String token) {
        checkAdminRole(token);
        ProjectIdea approvedIdea = projectIdeaRepository.findByIdAndApproveStatus(ideaId, true)
                .orElseThrow(() -> new EntityNotFoundException("Approved Project Idea not found with id: " + ideaId));

        if (request.name() != null && !request.name().isBlank()) {
            approvedIdea.setName(request.name());
        }
        if (request.description() != null) {
            approvedIdea.setDescription(request.description());
        }
        if (request.status() != null) {
            ProjectIdeaStatus newStatus = switch (request.status().intValue()) {
                case 0 -> ProjectIdeaStatus.REJECTED;
                case 1 -> ProjectIdeaStatus.APPROVED;
                case 2 -> ProjectIdeaStatus.IN_PROGRESS;
                case 3 -> ProjectIdeaStatus.COMPLETED;
                case 4 -> ProjectIdeaStatus.DELETED;
                default -> throw new IllegalArgumentException("Invalid status code: " + request.status());
            };
            approvedIdea.setStatus(newStatus);
        }
        projectIdeaRepository.save(approvedIdea);

        return ApiResponse.builder()
                .success(1).code(HttpStatus.OK.value()).message("Approved Project Idea updated successfully.")
                .build();
    }

    @Override
    @Transactional
    public ApiResponse deleteApprovedIdea(Long ideaId, String token) {
        checkAdminRole(token);
        ProjectIdea approvedIdea = projectIdeaRepository.findByIdAndApproveStatus(ideaId, true)
                .orElseThrow(() -> new EntityNotFoundException("Approved Project Idea not found with id: " + ideaId));

        approvedIdea.setStatus(ProjectIdeaStatus.DELETED);
        projectIdeaRepository.save(approvedIdea);

        return ApiResponse.builder()
                .success(1).code(HttpStatus.OK.value()).message("Approved Project Idea deleted successfully.")
                .build();
    }

//    private ApprovedIdeaResponse mapToResponse(ProjectIdea idea) {
//        return new ApprovedIdeaResponse(
//                idea.getId(),
//                idea.getName(),
//                idea.getDescription(),
//                idea.getDevProfile() != null ? idea.getDevProfile().getName() : "Anonymous",
//                idea.getReactedUsers().size(),
//                idea.getStatus().toString(),
//                idea.getDevProfile().getProfilePictureUrl(),
//                idea.getProjectTypes().stream()
//                        .map(pt -> pt.getName())
//                        .collect(Collectors.toList())
//        );
//    }

    private void checkAdminRole(String token) {
        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found from token"));

        if (!user.getRole().getName().equalsIgnoreCase("ADMIN")) {
            throw new SecurityException("Access Denied: Admin role required.");
        }
    }
}
