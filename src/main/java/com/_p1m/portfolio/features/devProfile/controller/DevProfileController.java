package com._p1m.portfolio.features.devProfile.controller;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;
import com._p1m.portfolio.config.response.utils.ResponseUtils;
import com._p1m.portfolio.data.models.DevProfile;
import com._p1m.portfolio.features.devProfile.dto.request.DevProfileRequest;
import com._p1m.portfolio.features.devProfile.service.DevProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portfolio/api/v1/auth/devProfile")
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
    @PostMapping("/create/{id}")
    public ResponseEntity<ApiResponse> createDevProfile(
            @Valid @PathVariable(name = "id") Long userId,
            @Valid @RequestBody DevProfileRequest request,
            HttpServletRequest httpServletRequest) {

        final ApiResponse response = devProfileService.createDevProfile(request, userId);
        return ResponseUtils.buildResponse(httpServletRequest, response);
    }


    @Operation(summary = "Get all developer profiles",
            description = "Fetches a list of all developer profiles.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Developer profiles fetched successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @GetMapping("")
    public PaginatedApiResponse<DevProfileRequest> getAllDevProfiles(HttpServletRequest request) {
        PaginatedApiResponse<DevProfileRequest> response = devProfileService.findAllDevPf();
        return ResponseUtils.buildPaginatedResponse(request, response).getBody();
    }
    @Operation(summary = "Get developer profile by id",
            description = "Fetches a developer profile based on the provided id.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Developer profiles fetched successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getDevProfileById(@PathVariable @Valid Long id, HttpServletRequest request) {
        ApiResponse response = devProfileService.findDevById(id);
        return ResponseUtils.buildResponse(request, response);
    }

    @Operation(summary = "Get developer profile by name",
            description = "Fetches a developer profile based on the provided name.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Developer profiles fetched successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse> getDevProfileByName(@PathVariable @Valid String name, HttpServletRequest request) {
        ApiResponse response = devProfileService.findDevByName(name);
        return ResponseUtils.buildResponse(request, response);
    }

    @Operation(summary = "Delete developer profile by ID",
            description = "Deletes a developer profile based on the provided ID.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Developer profile deleted successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @DeleteMapping("delete/{id}")
    public ResponseEntity<ApiResponse> deleteDevProfile(@PathVariable @Valid Long id, HttpServletRequest request) {
        ApiResponse response = devProfileService.deleteDevProfile(id);
        return ResponseUtils.buildResponse(request, response);
    }
}