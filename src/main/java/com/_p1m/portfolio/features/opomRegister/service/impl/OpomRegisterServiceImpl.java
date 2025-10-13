package com._p1m.portfolio.features.opomRegister.service.impl;

import com._p1m.portfolio.common.constant.Status;
import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;
import com._p1m.portfolio.config.response.dto.PaginationMeta;
import com._p1m.portfolio.data.models.DevProfile;
import com._p1m.portfolio.data.models.OpomRegister;
import com._p1m.portfolio.data.models.OpomRegisterPlatformLink;
import com._p1m.portfolio.data.models.lookup.Platform;
import com._p1m.portfolio.data.repositories.DevProfileRepository;
import com._p1m.portfolio.data.storage.CloudStorageService;
import com._p1m.portfolio.features.opomRegister.dto.request.UserRegisterRequest;
import com._p1m.portfolio.features.opomRegister.dto.response.UserRegisterResponse;
import com._p1m.portfolio.data.repositories.OpomRegisterRepository;
import com._p1m.portfolio.features.opomRegister.service.OpomRegisterService;
import com._p1m.portfolio.security.JWT.JWTUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class OpomRegisterServiceImpl implements OpomRegisterService {

    private final OpomRegisterRepository opomRegisterRepository;
    private final DevProfileRepository devProfileRepository;
    private final EntityManager entityManager;
    private final CloudStorageService cloudStorageService;
    private final JWTUtil jwtUtil;

    @Override
    @Transactional
    public ApiResponse registerUser(UserRegisterRequest userRegisterRequest,String token) {
        String email = jwtUtil.extractEmail(token);
        Platform platform = entityManager.find(Platform.class, userRegisterRequest.getPlatformId());
        if (platform == null) throw new EntityNotFoundException("Platform not found");

        DevProfile devProfile = devProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new com._p1m.portfolio.config.exceptions.EntityNotFoundException("DevProfile not found for email: " + email));


        OpomRegister register = OpomRegister.builder()
                .name(userRegisterRequest.getName())
                .email(userRegisterRequest.getEmail())
                .phone(userRegisterRequest.getPhone())
                .github_url(userRegisterRequest.getGithub_url())
                .telegram_username(userRegisterRequest.getTelegram_username())
                .role(userRegisterRequest.getRole())
                .status(userRegisterRequest.getStatus())
                .devProfile(devProfile)
                .build();

        opomRegisterRepository.save(register);

        // Create platform link
        OpomRegisterPlatformLink link = OpomRegisterPlatformLink.builder()
                .opomRegister(register)
                .platform(platform)
                .link(userRegisterRequest.getPlatformUrl())
                .build();
        entityManager.persist(link);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.CREATED.value())
                .data(true)
                .message("Opom registered successfully.")
                .build();
    }


    @Override
    public PaginatedApiResponse<UserRegisterResponse> getAllOpomRegisters(int page, int size, String dateOrder, String role) {
        Sort.Direction sortDir = "oldest".equalsIgnoreCase(dateOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDir, "createdAt"));

        Page<OpomRegister> pageResult = (role != null && !role.isBlank())
                ? opomRegisterRepository.findByRoleIgnoreCase(role, pageable)
                : opomRegisterRepository.findAll(pageable);

        PaginationMeta meta = new PaginationMeta();

         return PaginatedApiResponse.<UserRegisterResponse>builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Fetched successfully")
                .meta(meta)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse updateOpomRegisterData(Long id, UserRegisterRequest userRegisterRequest,String token) {
        OpomRegister register = opomRegisterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Register not found"));
        String email = jwtUtil.extractEmail(token);

        DevProfile devProfile = devProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new com._p1m.portfolio.config.exceptions.EntityNotFoundException("DevProfile not found for email: " + email));


        register.setName(userRegisterRequest.getName());
        register.setEmail(userRegisterRequest.getEmail());
        register.setPhone(userRegisterRequest.getPhone());
        register.setGithub_url(userRegisterRequest.getGithub_url());
        register.setTelegram_username(userRegisterRequest.getTelegram_username());
        register.setRole(userRegisterRequest.getRole());
        register.setStatus(userRegisterRequest.getStatus());


        Platform platform = entityManager.find(Platform.class, userRegisterRequest.getPlatformId());
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
                .link(userRegisterRequest.getPlatformUrl())
                .build();
        entityManager.persist(newLink);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(true)
                .message("Opom register updated successfully.")
                .build();
    }

    @Override
    @Transactional
    public ApiResponse softDeleteOpomRegister(Long id) {
        OpomRegister register = opomRegisterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Register not found"));
        register.setStatus(Status.INACTIVE);
        register.setDeleted(true);
        opomRegisterRepository.save(register);
        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(true)
                .message("Opom register deleted successfully.")
                .build();
    }
}
