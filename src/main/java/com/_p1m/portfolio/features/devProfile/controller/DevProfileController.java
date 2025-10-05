package com._p1m.portfolio.features.devProfile.controller;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.utils.ResponseUtils;
import com._p1m.portfolio.data.models.UserDetail;
import com._p1m.portfolio.features.devProfile.dto.request.CreateDevProfileRequest;
import com._p1m.portfolio.features.devProfile.dto.response.DevProfileResponse;
import com._p1m.portfolio.features.devProfile.service.DevProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portfolio/api/v1/profiles")
@RequiredArgsConstructor
@Tag(name = "Developer Profile", description = "Endpoints for managing developer profiles")
public class DevProfileController {

    private final DevProfileService devProfileService;

    @PostMapping
    @Operation(
            summary = "Create a Developer Profile",
            description = "Creates a new developer profile for the currently authenticated user. A user can only have one profile.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Profile created successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data or if a profile already exists for the user"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "User is not authenticated")
            }
    )
    public ResponseEntity<ApiResponse> createDevProfile(
            @Valid @RequestBody CreateDevProfileRequest request,
            HttpServletRequest httpServletRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof UserDetail userDetail)) {
            throw new SecurityException("Authentication principal is not of expected type UserDetail");
        }

        Long userId = userDetail.getUser().getId();

        DevProfileResponse createdProfileResponse = devProfileService.createDevProfile(request, userId);

        final ApiResponse apiResponse = ApiResponse.builder()
                .success(1)
                .code(HttpStatus.CREATED.value())
                .message("Developer profile created successfully.")
                .data(createdProfileResponse)
                .build();

        return ResponseUtils.buildResponse(httpServletRequest, apiResponse);
    }
}