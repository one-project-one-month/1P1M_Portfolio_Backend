package com._p1m.portfolio.features.devProfile.service.serviceImpl;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.data.models.DevProfile;
import com._p1m.portfolio.data.models.User;
import com._p1m.portfolio.data.models.lookup.TechStack;
import com._p1m.portfolio.data.repositories.UserRepository;
import com._p1m.portfolio.features.devProfile.dto.request.CreateDevProfileRequest;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DevProfileServiceImpl implements DevProfileService {

    private final UserRepository userRepository;
    private final DevProfileRepository devProfileRepository;
    private final TechStackRepository techStackRepository;

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
