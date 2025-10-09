package com._p1m.portfolio.features.approvedProjectIdeas.service.serviceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._p1m.portfolio.config.exceptions.EntityNotFoundException;
import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;
import com._p1m.portfolio.config.response.dto.PaginationMeta;
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
    public PaginatedApiResponse<ApprovedIdeaResponse> listApprovedIdeas(String keyword, Pageable pageable) {
        Specification<ProjectIdea> spec = (root, query, criteriaBuilder) -> {
            var approvedPredicate = criteriaBuilder.isTrue(root.get("approveStatus"));
            if (keyword != null && !keyword.trim().isEmpty()) {
                var keywordPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + keyword.toLowerCase() + "%");
                return criteriaBuilder.and(approvedPredicate, keywordPredicate);
            }
            return approvedPredicate;
        };

        Page<ProjectIdea> ideaPage = projectIdeaRepository.findAll(spec, pageable);
        List<ApprovedIdeaResponse> responses = ideaPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

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

        Optional.ofNullable(request.name()).ifPresent(approvedIdea::setName);
        Optional.ofNullable(request.description()).ifPresent(approvedIdea::setDescription);

        projectIdeaRepository.save(approvedIdea);

        return ApiResponse.builder()
                .success(1).code(HttpStatus.OK.value()).message("Approved Project Idea updated successfully.")
                .build();
    }

    @Override
    @Transactional
    public ApiResponse deleteApprovedIdea(Long ideaId, String token) {
        checkAdminRole(token);
        if (!projectIdeaRepository.existsByIdAndApproveStatus(ideaId, true)) {
            throw new EntityNotFoundException("Approved Project Idea not found with id: " + ideaId);
        }
        projectIdeaRepository.deleteById(ideaId);

        return ApiResponse.builder()
                .success(1).code(HttpStatus.OK.value()).message("Approved Project Idea deleted successfully.")
                .build();
    }

    private ApprovedIdeaResponse mapToResponse(ProjectIdea idea) {
        return new ApprovedIdeaResponse(
                idea.getId(),
                idea.getName(),
                idea.getDescription(),
                idea.getDevProfile() != null ? idea.getDevProfile().getName() : "Anonymous",
                idea.getProjectTypes().stream()
                        .map(pt -> pt.getName())
                        .collect(Collectors.toList())
        );
    }

    private void checkAdminRole(String token) {
        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found from token"));

        if (!user.getRole().getName().equalsIgnoreCase("ADMIN")) {
            throw new SecurityException("Access Denied: Admin role required.");
        }
    }
}