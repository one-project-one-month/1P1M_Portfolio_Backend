package com._p1m.portfolio.features.opomRegister.service.impl;

import com._p1m.portfolio.common.constant.Status;
import com._p1m.portfolio.data.models.DevProfile;
import com._p1m.portfolio.data.models.OpomRegister;
import com._p1m.portfolio.data.models.OpomRegisterPlatformLink;
import com._p1m.portfolio.data.models.lookup.Platform;
import com._p1m.portfolio.features.opomRegister.dto.request.UserRegisterRequest;
import com._p1m.portfolio.features.opomRegister.dto.response.UserRegisterResponse;
import com._p1m.portfolio.data.repositories.OpomRegisterRepository;
import com._p1m.portfolio.features.opomRegister.service.OpomRegisterService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OpomRegisterServiceImpl implements OpomRegisterService {

    private final OpomRegisterRepository opomRegisterRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public UserRegisterResponse registerUser(UserRegisterRequest request) {
        Platform platform = entityManager.find(Platform.class, request.getPlatformId());
        if (platform == null) throw new EntityNotFoundException("Platform not found");

        DevProfile devProfile = null;
        if (request.getDevProfileId() != null) {
            devProfile = entityManager.find(DevProfile.class, request.getDevProfileId());
        }

        OpomRegister register = OpomRegister.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .github_url(request.getGithub_url())
                .telegram_username(request.getTelegram_username())
                .role(request.getRole())
                .status(request.getStatus())
                .devProfile(devProfile)
                .build();

        opomRegisterRepository.save(register);

        // Create platform link
        OpomRegisterPlatformLink link = OpomRegisterPlatformLink.builder()
                .opomRegister(register)
                .platform(platform)
                .link(request.getPlatformUrl())
                .build();
        entityManager.persist(link);

        return toResponse(register);
    }

    @Override
    public Page<UserRegisterResponse> getAllOpomRegisters(int page, int size, String dateOrder, String role) {
        Sort.Direction sortDir = "oldest".equalsIgnoreCase(dateOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDir, "createdAt"));

        Page<OpomRegister> pageResult = (role != null && !role.isBlank())
                ? opomRegisterRepository.findByRoleIgnoreCase(role, pageable)
                : opomRegisterRepository.findAll(pageable);

        return pageResult.map(this::toResponse);
    }

    @Override
    @Transactional
    public UserRegisterResponse updateOpomRegisterData(Long id, UserRegisterRequest request) {
        OpomRegister register = opomRegisterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Register not found"));

        register.setName(request.getName());
        register.setEmail(request.getEmail());
        register.setPhone(request.getPhone());
        register.setGithub_url(request.getGithub_url());
        register.setTelegram_username(request.getTelegram_username());
        register.setRole(request.getRole());
        register.setStatus(request.getStatus());

        if (request.getDevProfileId() != null) {
            DevProfile devProfile = entityManager.find(DevProfile.class, request.getDevProfileId());
            register.setDevProfile(devProfile);
        }

        Platform platform = entityManager.find(Platform.class, request.getPlatformId());
        if (platform == null) throw new EntityNotFoundException("Platform not found");

        // Remove existing links
        List<OpomRegisterPlatformLink> links = entityManager.createQuery(
                        "SELECT l FROM OpomRegisterPlatformLink l WHERE l.opomRegister.id = :id",
                        OpomRegisterPlatformLink.class)
                .setParameter("id", id)
                .getResultList();
        links.forEach(entityManager::remove);

        // Add new link
        OpomRegisterPlatformLink newLink = OpomRegisterPlatformLink.builder()
                .opomRegister(register)
                .platform(platform)
                .link(request.getPlatformUrl())
                .build();
        entityManager.persist(newLink);

        return toResponse(register);
    }

    @Override
    @Transactional
    public void softDeleteOpomRegister(Long id) {
        OpomRegister register = opomRegisterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Register not found"));
        register.setStatus(Status.INACTIVE);
        register.setDeleted(true);
        opomRegisterRepository.save(register);
    }

    private UserRegisterResponse toResponse(OpomRegister entity) {
        UserRegisterResponse response = new UserRegisterResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setEmail(entity.getEmail());
        response.setPhone(entity.getPhone());
        response.setGithub_url(entity.getGithub_url());
        response.setTelegram_username(entity.getTelegram_username());
        response.setRole(entity.getRole());
        response.setStatus(entity.getStatus());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());

        List<UserRegisterResponse.PlatformInfo> platforms = entityManager.createQuery(
                        "SELECT l FROM OpomRegisterPlatformLink l WHERE l.opomRegister.id = :id",
                        OpomRegisterPlatformLink.class)
                .setParameter("id", entity.getId())
                .getResultList()
                .stream()
                .map(l -> {
                    UserRegisterResponse.PlatformInfo pi = new UserRegisterResponse.PlatformInfo();
                    pi.setPlatformId(l.getPlatform().getId());
                    pi.setPlatformName(l.getPlatform().getName());
                    pi.setLink(l.getLink());
                    return pi;
                }).collect(Collectors.toList());

        response.setPlatforms(platforms);
        return response;
    }
}
