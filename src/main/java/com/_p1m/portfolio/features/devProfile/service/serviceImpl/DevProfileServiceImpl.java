package com._p1m.portfolio.features.devProfile.service.serviceImpl;

import com._p1m.portfolio.config.exceptions.EntityNotFoundException;
import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;
import com._p1m.portfolio.config.response.dto.PaginationMeta;
import com._p1m.portfolio.data.models.DevProfile;
import com._p1m.portfolio.data.models.OpomRegister;
import com._p1m.portfolio.data.models.ProjectPortfolio;
import com._p1m.portfolio.data.models.User;
import com._p1m.portfolio.data.models.lookup.TechStack;
import com._p1m.portfolio.data.repositories.UserRepository;
import com._p1m.portfolio.features.devProfile.dto.request.CreateDevProfileRequest;
import com._p1m.portfolio.features.devProfile.dto.request.UpdateDevProfileRequest;
import com._p1m.portfolio.features.devProfile.dto.response.DevProfileListResponse;
import com._p1m.portfolio.features.devProfile.dto.response.DevProfileResponse;
import com._p1m.portfolio.data.repositories.DevProfileRepository;
import com._p1m.portfolio.data.repositories.TechStackRepository;
import com._p1m.portfolio.features.devProfile.service.DevProfileService;
import com._p1m.portfolio.features.opomRegister.dto.response.OpomRegisterResponse;
import com._p1m.portfolio.security.JWT.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DevProfileServiceImpl implements DevProfileService {

    private final UserRepository userRepository;
    private final DevProfileRepository devProfileRepository;
    private final TechStackRepository techStackRepository;
    private final JWTUtil jwtUtil;

    @Override
    @Transactional
    public ApiResponse createDevProfile(CreateDevProfileRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (devProfileRepository.existsByUser(user)) {
            throw new IllegalStateException("A developer profile for this user already exists.");
        }

        List<TechStack> resolvedTechStacks = new ArrayList<>();
        if (request.getTechStacks() != null && !request.getTechStacks().isEmpty()) {
            for (String techStackName : request.getTechStacks()) {
                TechStack resolvedTechStack = findOrCreateTechStack(techStackName);
                resolvedTechStacks.add(resolvedTechStack);
            }
        }

        DevProfile devProfile = DevProfile.builder()
                .user(user)
                .name(request.getName())
                .profilePictureUrl(request.getProfilePictureUrl())
                .github(request.getGithub())
                .linkedIn(request.getLinkedIn())
                .aboutDev(request.getAboutDev())
                .techStacks(resolvedTechStacks)
                .role(request.getRole())
                .build();

        devProfileRepository.save(devProfile);
        DevProfileResponse responseDto = mapToDevProfileResponse(devProfile);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.CREATED.value())
                .message("Developer profile created successfully.")
                .data(responseDto)
                .build();
    }

    @Override
    public PaginatedApiResponse<DevProfileListResponse> getAllPaginatedDeveloperList(String keyword, Pageable pageable) {
        Specification<ProjectPortfolio> spec = (root, query, criteriaBuilder) -> {
            if (keyword != null && !keyword.trim().isEmpty()) {
                return criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + keyword.toLowerCase() + "%"
                );
            }
            return null;
        };

        Page<DevProfile> devProfileResponse = this.devProfileRepository.findAll(spec, pageable);
        List<DevProfileListResponse> devProfileListResponses = devProfileResponse.getContent().stream()
                .map(devProfile -> DevProfileListResponse.builder()
                        .userId(devProfile.getId())
                        .name(devProfile.getName())
                        .aboutDev(devProfile.getAboutDev())
                        .profilePictureUrl(devProfile.getProfilePictureUrl())
                        .github(devProfile.getGithub())
                        .linkedIn(devProfile.getLinkedIn())
                        .role(devProfile.getRole())
                        .build()
                ).toList();

        PaginationMeta meta = new PaginationMeta();
        meta.setTotalItems(devProfileResponse.getTotalElements());
        meta.setTotalPages(devProfileResponse.getTotalPages());
        meta.setCurrentPage(pageable.getPageNumber() + 1);

        return PaginatedApiResponse.<DevProfileListResponse>builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Fetched successfully")
                .meta(meta)
                .data(devProfileListResponses)
                .build();
    }

    @Override
    @jakarta.transaction.Transactional
    public ApiResponse updateDevProfile(UpdateDevProfileRequest updateRequest, Long id, String token) {
        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found from token"));

        if (!user.getRole().getName().equalsIgnoreCase("ADMIN")) {
            throw new SecurityException("Access Denied: Admin role required.");
        }

        DevProfile devProfile = devProfileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Developer Profile Not Found for Id: " + id));

        // Update basic profile info
        devProfile.setName(updateRequest.getName());
        devProfile.setProfilePictureUrl(updateRequest.getProfilePictureUrl());
        devProfile.setGithub(updateRequest.getGithub());
        devProfile.setLinkedIn(updateRequest.getLinkedIn());
        devProfile.setAboutDev(updateRequest.getAboutDev());
        devProfile.setRole(updateRequest.getRole());

        // Update TechStacks (clear old and add new)
        List<TechStack> resolvedTechStacks = new ArrayList<>();
        if (updateRequest.getTechStacks() != null && !updateRequest.getTechStacks().isEmpty()) {
            for (String techStackName : updateRequest.getTechStacks()) {
                TechStack resolvedTechStack = findOrCreateTechStack(techStackName);
                resolvedTechStacks.add(resolvedTechStack);
            }
        }
        devProfile.setTechStacks(resolvedTechStacks);
        DevProfile updatedProfile = devProfileRepository.save(devProfile);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Developer profile updated successfully.")
                .data(updatedProfile)
                .build();
    }


    private TechStack findOrCreateTechStack(String name) {
        return techStackRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> techStackRepository.save(TechStack.builder().name(name).build()));
    }

    private DevProfileResponse mapToDevProfileResponse(DevProfile profile) {
        List<String> techStackNames = profile.getTechStacks().stream()
                .map(TechStack::getName)
                .collect(Collectors.toList());

        return DevProfileResponse.builder()
                .userId(profile.getUser().getId())
                .name(profile.getName())
                .profilePictureUrl(profile.getProfilePictureUrl())
                .github(profile.getGithub())
                .linkedIn(profile.getLinkedIn())
                .aboutDev(profile.getAboutDev())
                .techStacks(techStackNames)
                .build();
    }
}
