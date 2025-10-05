package com._p1m.portfolio.features.devProfile.service.serviceImpl;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DevProfileServiceImpl implements DevProfileService {

    private final UserRepository userRepository;
    private final DevProfileRepository devProfileRepository;
    private final TechStackRepository techStackRepository;

    @Override
    @Transactional
    public DevProfileResponse createDevProfile(CreateDevProfileRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (devProfileRepository.existsByUser(user)) {
            throw new IllegalStateException("A developer profile for this user already exists.");
        }

        Set<TechStack> resolvedTechStacks = new HashSet<>();
        if (request.getTechStacks() != null && !request.getTechStacks().isEmpty()) {
            resolvedTechStacks = request.getTechStacks().stream()
                    .map(this::findOrCreateTechStack)
                    .collect(Collectors.toSet());
        }

        DevProfile devProfile = DevProfile.builder()
                .user(user)
                .name(request.getName())
                .profilePictureUrl(request.getProfilePictureUrl())
                .github(request.getGithub())
                .linkedIn(request.getLinkedIn())
                .techStacks(resolvedTechStacks)
                .build();

        DevProfile savedProfile = devProfileRepository.save(devProfile);
        return mapToDevProfileResponse(savedProfile);
    }

    private TechStack findOrCreateTechStack(String name) {
        return techStackRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> techStackRepository.save(TechStack.builder().name(name).build()));
    }

    private DevProfileResponse mapToDevProfileResponse(DevProfile profile) {
        Set<String> techStackNames = profile.getTechStacks().stream()
                .map(TechStack::getName)
                .collect(Collectors.toSet());

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
