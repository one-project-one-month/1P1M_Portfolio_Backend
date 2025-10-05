package com._p1m.portfolio.features.devProfile.controller;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.utils.ResponseUtils;
import com._p1m.portfolio.data.models.UserDetail;
import com._p1m.portfolio.features.devProfile.dto.request.CreateDevProfileRequest;
import com._p1m.portfolio.features.devProfile.service.DevProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createDevProfile(
            @Valid @RequestBody CreateDevProfileRequest request,
            UserDetail userDetail,
            HttpServletRequest httpServletRequest) {

        //Long userId = userDetail.getUser().getId();
        Long userId = 1L;

        final ApiResponse response = devProfileService.createDevProfile(request, userId);

        return ResponseUtils.buildResponse(httpServletRequest, response);
    }
}