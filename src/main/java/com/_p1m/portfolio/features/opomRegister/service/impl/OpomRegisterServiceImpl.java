package com._p1m.portfolio.features.opomRegister.service.impl;

import com._p1m.portfolio.common.constant.Status;
import com._p1m.portfolio.config.exceptions.DuplicateEntityException;
import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;
import com._p1m.portfolio.config.response.dto.PaginationMeta;
import com._p1m.portfolio.data.models.DevProfile;
import com._p1m.portfolio.data.models.OpomRegister;
import com._p1m.portfolio.data.models.OpomRegisterPlatformLink;
import com._p1m.portfolio.data.models.ProjectPortfolio;
import com._p1m.portfolio.data.models.lookup.LanguageAndTools;
import com._p1m.portfolio.data.models.lookup.Platform;
import com._p1m.portfolio.data.repositories.DevProfileRepository;
import com._p1m.portfolio.data.repositories.OpomRegisterPlatformLinkRepository;
import com._p1m.portfolio.data.repositories.PlatformRepository;
import com._p1m.portfolio.data.storage.CloudStorageService;
import com._p1m.portfolio.features.opomRegister.dto.request.PlatformLinkDto;
import com._p1m.portfolio.features.opomRegister.dto.request.UserRegisterRequest;
import com._p1m.portfolio.features.opomRegister.dto.response.OpomRegisterResponse;
import com._p1m.portfolio.features.opomRegister.dto.response.UserRegisterResponse;
import com._p1m.portfolio.data.repositories.OpomRegisterRepository;
import com._p1m.portfolio.features.opomRegister.service.OpomRegisterService;
import com._p1m.portfolio.features.projectPortfolio.dto.response.AssignedDevs;
import com._p1m.portfolio.features.projectPortfolio.dto.response.ProjectPortfolioDetails;
import com._p1m.portfolio.features.projectPortfolio.dto.response.ProjectPortfolioResponse;
import com._p1m.portfolio.security.JWT.JWTUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OpomRegisterServiceImpl implements OpomRegisterService {

    private final OpomRegisterRepository opomRegisterRepository;
    private final DevProfileRepository devProfileRepository;
    private final OpomRegisterPlatformLinkRepository opomRegisterPlatformLinkRepository;
    private final PlatformRepository platformRepository;
    private final EntityManager entityManager;
    private final CloudStorageService cloudStorageService;
    private final JWTUtil jwtUtil;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public ApiResponse registerUser(UserRegisterRequest userRegisterRequest,String token) {
        String email = jwtUtil.extractEmail(token);
        DevProfile devProfile = devProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new com._p1m.portfolio.config.exceptions.EntityNotFoundException("DevProfile not found for email: " + email));

        if (opomRegisterRepository.existsByEmail(userRegisterRequest.getEmail())) {
            throw new DuplicateEntityException("Email is Already in Use.");
        }
        if (opomRegisterRepository.existsByPhone(userRegisterRequest.getPhone())) {
            throw new DuplicateEntityException("Phone Number is Already in Use.");
        }

        // 1️⃣ Save main user
        OpomRegister opomRegister = new OpomRegister();
        opomRegister.setName(userRegisterRequest.getName());
        opomRegister.setEmail(userRegisterRequest.getEmail());
        opomRegister.setPhone(userRegisterRequest.getPhone());
        opomRegister.setTelegram_username(userRegisterRequest.getTelegram_username());
        opomRegister.setRole(userRegisterRequest.getRole());
        opomRegister.setDevProfile(devProfile);
        opomRegister.setStatus(Status.ACTIVE);
        opomRegisterRepository.save(opomRegister);

        // 2️⃣ Save platform links
        for (PlatformLinkDto dto : userRegisterRequest.getPlatformLinks()) {
            Platform platform = platformRepository.findById(dto.getPlatformId())
                    .orElseThrow(() -> new EntityNotFoundException("Platform not found"));

            OpomRegisterPlatformLink link = new OpomRegisterPlatformLink();
            link.setOpomRegister(opomRegister);
            link.setPlatform(platform);
            link.setLink(dto.getLink());
            opomRegisterPlatformLinkRepository.save(link);
        }

        UserRegisterResponse response = modelMapper.map(opomRegister, UserRegisterResponse.class);
        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.CREATED.value())
                .data(Map.of("Register User Info", response))
                .message("Opom registered successfully.")
                .build();
    }

    @Override
    @Transactional
    public ApiResponse updateOpomRegisterData(Long id, UserRegisterRequest userRegisterRequest, String token) {
        // Find the existing user
        OpomRegister register = opomRegisterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Register not found"));

        // Validate DevProfile (for token-based ownership)
        String email = jwtUtil.extractEmail(token);
        DevProfile devProfile = devProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new com._p1m.portfolio.config.exceptions.EntityNotFoundException("DevProfile not found for email: " + email));

        // Update main info
        register.setName(userRegisterRequest.getName());
        register.setEmail(userRegisterRequest.getEmail());
        register.setPhone(userRegisterRequest.getPhone());
        register.setTelegram_username(userRegisterRequest.getTelegram_username());
        register.setRole(userRegisterRequest.getRole());
        register.setStatus(Status.ACTIVE); // or userRegisterRequest.getStatus() if editable
        opomRegisterRepository.save(register);

        // Remove existing platform links
        List<OpomRegisterPlatformLink> existingLinks = opomRegisterPlatformLinkRepository.findByOpomRegisterId(id);
        if (!existingLinks.isEmpty()) {
            opomRegisterPlatformLinkRepository.deleteAll(existingLinks);
        }

        // Add new platform links
        for (PlatformLinkDto dto : userRegisterRequest.getPlatformLinks()) {
            Platform platform = platformRepository.findById(dto.getPlatformId())
                    .orElseThrow(() -> new EntityNotFoundException("Platform not found"));

            OpomRegisterPlatformLink newLink = new OpomRegisterPlatformLink();
            newLink.setOpomRegister(register);
            newLink.setPlatform(platform);
            newLink.setLink(dto.getLink());
            opomRegisterPlatformLinkRepository.save(newLink);
        }

        // 6 Return response
        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .data(Map.of("updatedId", register.getId()))
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

    @Override
    public PaginatedApiResponse<OpomRegisterResponse> getAllpaginatedOpomRegisterList(String keyword, Pageable pageable) {
        Specification<ProjectPortfolio> spec = (root, query, criteriaBuilder) -> {
            if (keyword != null && !keyword.trim().isEmpty()) {
                return criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + keyword.toLowerCase() + "%"
                );
            }
            return null;
        };

        Page<OpomRegister> opomRegisterPage = this.opomRegisterRepository.findAll(spec, pageable);

        List<OpomRegisterResponse> opomRegisterResponses = opomRegisterPage.getContent().stream()
                .map(opomRegister -> OpomRegisterResponse.builder()
                        .id(opomRegister.getId())
                        .name(opomRegister.getName())
                        .telegram_username(opomRegister.getTelegram_username())
                        .email(opomRegister.getEmail())
                        .phone(opomRegister.getPhone())
                        .role(opomRegister.getRole())
                        .build()
                ).toList();

        PaginationMeta meta = new PaginationMeta();
        meta.setTotalItems(opomRegisterPage.getTotalElements());
        meta.setTotalPages(opomRegisterPage.getTotalPages());
        meta.setCurrentPage(pageable.getPageNumber() + 1);

        return PaginatedApiResponse.<OpomRegisterResponse>builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Fetched successfully")
                .meta(meta)
                .data(opomRegisterResponses)
                .build();
    }
}
