package com._p1m.portfolio.features.ManageDevProfile.controller;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;
import com._p1m.portfolio.config.response.dto.PaginationMeta;
import com._p1m.portfolio.config.response.utils.ResponseUtils;
import com._p1m.portfolio.data.models.DevProfile;
import com._p1m.portfolio.features.ManageDevProfile.service.ManageDevProfileService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("portfolio/api/v1/auth/devProfile")
public class ManageDevProfileController {
    private final ManageDevProfileService manageDevProfileService;

    @Operation(summary = "Get all developer profiles",
            description = "Fetches a list of all developer profiles.",
    responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Developer profiles fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("")
    public PaginatedApiResponse<DevProfile> getAllDevProfile(HttpServletRequest request){
        List<DevProfile> devProfiles = manageDevProfileService.findAllDevPf();
        PaginatedApiResponse<DevProfile> response = PaginatedApiResponse.<DevProfile>builder()
                .success(1)
                .code(200)
                .message("Developer profiles fetched successfully")
                .meta(PaginationMeta.builder()
                        .totalItems(devProfiles.size())
                        .totalPages(1)
                        .currentPage(1)
                        .method(request.getMethod())
                        .endpoint(request.getRequestURI())
                        .build())
                .data(devProfiles)
                .build();
        return ResponseUtils.buildPaginatedResponse(request,response).getBody();
    }
    @Operation(summary = "Get developer profile by name",
            description = "Fetches a developer profile based on the provided name.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Developer profiles fetched successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @GetMapping("/{name}")
    public ResponseEntity<ApiResponse> getDevPfByName(@PathVariable @Valid String name,HttpServletRequest request){
       DevProfile developer = manageDevProfileService.findDevByName(name);
       ApiResponse response = ApiResponse.builder()
                       .success(1)
                       .code(200)
                       .message("Developer profile fetched successfully")
                       .data(developer)
                       .build();
        return ResponseUtils.buildResponse(request,response);
    }

    @Operation(summary = "Get developer profile by LinkedIn",
            description = "Fetches a developer profile based on the provided LinkedIn URL.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Developer profiles fetched successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @GetMapping("/linkedin/{linkedIn}")
    public ResponseEntity<ApiResponse> getDevPfByLinkedIn(@PathVariable @Valid String linkedIn,HttpServletRequest request){
        DevProfile developer = manageDevProfileService.findByLinkedIn(linkedIn);
        ApiResponse response = ApiResponse.builder()
                .success(1)
                .code(200)
                .message("Developer profile fetched successfully")
                .data(developer)
                .build();
        return ResponseUtils.buildResponse(request,response);
    }

    @Operation(summary = "Get developer profile by GitHub",
            description = "Fetches a developer profile based on the provided GitHub username.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Developer profiles fetched successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @GetMapping("/github/{github}")
    public ResponseEntity<ApiResponse> getDevPfByGitHub(@PathVariable @Valid String github,HttpServletRequest request){
        DevProfile developer = manageDevProfileService.findDevByGithub(github);
        ApiResponse response = ApiResponse.builder()
                .success(1)
                .code(200)
                .message("Developer profile fetched successfully")
                .data(developer)
                .build();
        return ResponseUtils.buildResponse(request,response);
    }

    @Operation(summary = "Update developer profile by ID",
            description = "Updates an existing developer profile based on the provided ID and new data.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Developer profile updated successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Developer profile not found"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @PutMapping("/update/{id}")
    public ApiResponse updateDevProfile(
            @PathVariable Long id,
            @RequestBody @Valid DevProfile devProfileDetails,
            HttpServletRequest request) {

        DevProfile updatedProfile = manageDevProfileService.updateDevProfile(id, devProfileDetails);

        ApiResponse response = ApiResponse.builder()
                .success(1)
                .code(200)
                .message("Developer profile updated successfully")
                .data(updatedProfile)
                .build();

        return ResponseUtils.buildResponse(request, response).getBody();
    }

    @Operation(summary = "Delete developer profile by ID",
            description = "Deletes a developer profile based on the provided ID.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Developer profile deleted successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteDevPf(@PathVariable Long id,HttpServletRequest request) {
        manageDevProfileService.deleteDevPf(id);

        return  ResponseEntity.ok( "Developer profile deleted successfully");
    }


}
