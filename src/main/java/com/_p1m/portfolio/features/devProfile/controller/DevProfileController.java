package com._p1m.portfolio.features.devProfile.controller;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;
import com._p1m.portfolio.config.response.dto.PaginationMeta;
import com._p1m.portfolio.config.response.utils.ResponseUtils;
import com._p1m.portfolio.data.models.DevProfile;
import com._p1m.portfolio.data.models.UserDetail;
import com._p1m.portfolio.features.devProfile.dto.request.CreateDevProfileRequest;
import com._p1m.portfolio.features.devProfile.dto.request.UpdateDevProfileRequest;
import com._p1m.portfolio.features.devProfile.dto.response.DevProfileListResponse;
import com._p1m.portfolio.features.devProfile.service.DevProfileService;
import com._p1m.portfolio.features.opomRegister.dto.response.OpomRegisterResponse;
import com._p1m.portfolio.features.projectPortfolio.dto.request.UpdateProjectPortfolioRequest;
import com._p1m.portfolio.features.projectPortfolio.dto.request.UploadFileRequest;
import com._p1m.portfolio.security.JWT.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/${api.base.path}/profiles")
@RequiredArgsConstructor
@Tag(name = "Developer Profile", description = "Endpoints for managing developer profiles")
public class DevProfileController {

    private final DevProfileService devProfileService;
    private final JWTUtil jwtUtil;
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
            @Valid @RequestBody CreateDevProfileRequest request,
            HttpServletRequest httpServletRequest) {

        final ApiResponse response = devProfileService.createDevProfile(request, userId);
        return ResponseUtils.buildResponse(httpServletRequest, response);
    }

    @Operation(
            summary = "Upload a file (Dev Profile image)",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Multipart form with image file",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = UploadFileRequest.class)
                    )
            )
    )
    @PatchMapping(
            value = "/uploadFile",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse> uploadFile(
            @RequestParam("devProfileId") Long devProfileId,
            @Parameter(hidden = true)
            @ModelAttribute UploadFileRequest fileRequest,
            HttpServletRequest request
    ) {
        ApiResponse response = this.devProfileService.uploadFile(fileRequest, devProfileId);
        return ResponseUtils.buildResponse(request, response);
    }


    @Operation(summary = "Get all developer profiles",
            description = "Fetches a list of all developer profiles.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Developer profiles fetched successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @GetMapping
    public ResponseEntity<PaginatedApiResponse<DevProfileListResponse>>  getAllDevProfile(
            @Parameter(description = "Search keyword")
            @RequestParam(value = "keyword", required = false) String keyword,

            @Parameter(description = "Page number (starts from 0)")
            @RequestParam(value = "page", defaultValue = "0")
            @Min(value = 0, message = "Page number must be 0 or greater") int page,

            @Parameter(description = "Page size")
            @RequestParam(value = "size", defaultValue = "20")
            @Min(value = 1, message = "Page size must be 0 or greater")
            @Max(value = 100, message = "Page size can't be greater than 100") int size,

            @Parameter(description = "Field to sort by (name)")
            @RequestParam(value = "sortField", defaultValue = "name")String sortField,

            @Parameter(description = "Sort direction (asc or desc)")
            @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection,

            HttpServletRequest request
    ){
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        PaginatedApiResponse<DevProfileListResponse> response =
                this.devProfileService.getAllPaginatedDeveloperList(keyword, pageable);

        return ResponseUtils.buildPaginatedResponse(request , response);
    }


    @Operation(
            summary = "Partially update Dev Profile",
            description = "Allows partial updates to the Dev Profile",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UpdateDevProfileRequest.class)
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Dev Profile Updated successfully.",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    )
            }
    )
    @PatchMapping
    public ResponseEntity<ApiResponse> updateProjectPortfolio(
            @RequestParam("id") Long id,
            @RequestBody @Valid UpdateDevProfileRequest updateRequest,
            HttpServletRequest request
    ) {
        String token = jwtUtil.extractTokenFromRequest(request);
        final ApiResponse response = this.devProfileService.updateDevProfile(updateRequest,id , token);
        return ResponseUtils.buildResponse(request, response);
    }
}