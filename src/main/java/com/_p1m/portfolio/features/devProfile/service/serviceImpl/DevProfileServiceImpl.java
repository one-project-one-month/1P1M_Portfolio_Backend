package com._p1m.portfolio.features.devProfile.service.serviceImpl;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;
import com._p1m.portfolio.config.response.dto.PaginationMeta;
import com._p1m.portfolio.data.models.DevProfile;
import com._p1m.portfolio.data.models.User;
import com._p1m.portfolio.data.models.lookup.TechStack;
import com._p1m.portfolio.data.repositories.UserRepository;
import com._p1m.portfolio.features.devProfile.dto.request.DevProfileRequest;
import com._p1m.portfolio.features.devProfile.dto.response.DevProfileResponse;
import com._p1m.portfolio.data.repositories.DevProfileRepository;
import com._p1m.portfolio.data.repositories.TechStackRepository;
import com._p1m.portfolio.features.devProfile.service.DevProfileService;
import lombok.RequiredArgsConstructor;
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

    @Override
    @Transactional
    public ApiResponse createDevProfile(DevProfileRequest request, Long userId) {
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
                .techStacks(resolvedTechStacks)
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
    public PaginatedApiResponse<DevProfileRequest> findAllDevPf() {

        List<DevProfile> developers = devProfileRepository.findAll();
        List<DevProfileRequest> developerDtos = new ArrayList<>();

        PaginationMeta emptyMeta = PaginationMeta.builder()
                .method("GET")
                .endpoint("/portfolio/api/v1/auth/devProfile")
                .totalItems(0)
                .totalPages(0)
                .currentPage(0)
                .build();

        if (!developers.isEmpty()) {
            developerDtos = developers.stream()
                    .map(dev -> {
                        DevProfileRequest requestDto = new DevProfileRequest();
                        requestDto.setName(dev.getName());
                        requestDto.setProfilePictureUrl(dev.getProfilePictureUrl());
                        requestDto.setGithub(dev.getGithub());
                        requestDto.setLinkedIn(dev.getLinkedIn());

                        List<String> techStackNames = dev.getTechStacks().stream()
                                .map(TechStack::getName)
                                .collect(Collectors.toList());
                        requestDto.setTechStacks(techStackNames);
                        return requestDto;
                    })
                    .collect(Collectors.toList());


            PaginationMeta populatedMeta = emptyMeta.toBuilder()
                    .totalItems(developers.size())
                    .build();

            return PaginatedApiResponse.<DevProfileRequest>builder()
                    .success(1)
                    .code(HttpStatus.OK.value())
                    .message("Developer profiles fetched successfully. Total: " + developers.size())
                    .data(developerDtos)
                    .meta(populatedMeta)
                    .build();
            }
        return PaginatedApiResponse.<DevProfileRequest>builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("No developer profiles found.")
                .data(developerDtos)
                .meta(emptyMeta)
                .build();


    }

    @Override
    public ApiResponse findDevById(Long id) {
        Optional<DevProfile> newDev = devProfileRepository.findById(id);
        if (newDev.isEmpty()) {
            return ApiResponse.builder()
                    .success(0)
                    .code(HttpStatus.NOT_FOUND.value())
                    .message("Developer profile not found with id: " + id)
                    .data(null)
                    .build();
        } else {
            DevProfileResponse responseDto = mapToDevProfileResponse(newDev.get());
            return ApiResponse.builder()
                    .success(1)
                    .code(HttpStatus.OK.value())
                    .message("Developer profile fetched successfully.")
                    .data(responseDto)
                    .build();
        }

    }

    @Override
    public ApiResponse findDevByName(String name) {
        Optional<DevProfile> developer = devProfileRepository.findByName(name);
        if (developer.isEmpty()) {
            return ApiResponse.builder()
                    .success(0)
                    .code(HttpStatus.NOT_FOUND.value())
                    .message("Developer profile not found with name: " + name)
                    .data(null)
                    .build();
        } else {
            DevProfileResponse responseDto = mapToDevProfileResponse(developer.get());
            return ApiResponse.builder()
                    .success(1)
                    .code(HttpStatus.OK.value())
                    .message("Developer profile fetched successfully.")
                    .data(responseDto)
                    .build();
        }
    }

    @Override
    public ApiResponse deleteDevProfile(Long id) {
        devProfileRepository.deleteById(id);
        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Developer profile deleted successfully.")
                .data(null)
                .build();

    }

    @Override
    public PaginatedApiResponse<DevProfile> findByTechStack(String techStack) {
        Optional<TechStack> stack = techStackRepository.findByNameIgnoreCase(techStack);
        if(stack.isPresent()){
            List<DevProfile> developers = devProfileRepository.findByTechStacks(stack);
            if (!developers.isEmpty()) {
                return PaginatedApiResponse.<DevProfile>builder()
                        .success(1)
                        .code(HttpStatus.OK.value())
                        .message("Developer profiles fetched successfully. Total: " + developers.size())
                        .data(developers)
                        .meta(PaginationMeta.builder()
                                .method("GET")
                                .endpoint("/portfolio/api/v1/auth/devProfile/techStack/" + techStack)
                                .totalItems(developers.size())
                                .totalPages(1)
                                .currentPage(1)
                                .build())
                        .build();
            } else {
                return PaginatedApiResponse.<DevProfile>builder()
                        .success(1)
                        .code(HttpStatus.OK.value())
                        .message("No developer profiles found for tech stack: " + techStack)
                        .data(new ArrayList<>())
                        .meta(PaginationMeta.builder()
                                .method("GET")
                                .endpoint("/portfolio/api/v1/auth/devProfile/techStack/" + techStack)
                                .totalItems(0)
                                .totalPages(0)
                                .currentPage(0)
                                .build())
                        .build();
            }

        }
        return PaginatedApiResponse.<DevProfile>builder()
                .success(0)
                .code(HttpStatus.NOT_FOUND.value())
                .message("Tech stack not found: " + techStack)
                .data(new ArrayList<>())
                .meta(PaginationMeta.builder()
                        .method("GET")
                        .endpoint("/portfolio/api/v1/auth/devProfile/techStack/" + techStack)
                        .totalItems(0)
                        .totalPages(0)
                        .currentPage(0)
                        .build())
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
                .email(profile.getUser().getEmail())
                .profilePictureUrl(profile.getProfilePictureUrl())
                .github(profile.getGithub())
                .linkedIn(profile.getLinkedIn())
                .techStacks(techStackNames)
                .build();
    }
}
