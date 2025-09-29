package com._p1m.portfolio.features.createDevProfile.service;

import com._p1m.portfolio.data.models.DevProfile;
import com._p1m.portfolio.data.models.User;
import com._p1m.portfolio.data.models.lookup.TechStack;
import com._p1m.portfolio.data.repositories.DevProfileRepository;
import com._p1m.portfolio.data.repositories.UserRepository;
import com._p1m.portfolio.features.createDevProfile.dto.request.CreateDevProfileRequest;
import com._p1m.portfolio.features.createDevProfile.repository.TechStackFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DevProfileService {

    private final DevProfileRepository devProfileRepository;
    private final UserRepository userRepository;
    private final TechStackFinder techStackFinder;

    @Transactional
    public DevProfile createDevProfile(CreateDevProfileRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Set<TechStack> resolvedTechStacks = request.getTechStacks().stream()
                .map(techStackFinder::findOrCreateByName)
                .collect(Collectors.toSet());

        DevProfile devProfile = DevProfile.builder()
                .user(user)
                .name(request.getName())
                .profilePictureUrl(request.getProfilePictureUrl())
                .github(request.getGithub())
                .linkedIn(request.getLinkedIn())
                .techStacks(resolvedTechStacks)
                .build();

        return devProfileRepository.save(devProfile);
    }
}