package com._p1m.portfolio.features.approvedProjectIdeas.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;
import com._p1m.portfolio.config.response.utils.ResponseUtils;
import com._p1m.portfolio.features.approvedProjectIdeas.dto.request.UpdateApprovedIdeaRequest;
import com._p1m.portfolio.features.approvedProjectIdeas.dto.response.ApprovedIdeaResponse;
import com._p1m.portfolio.features.approvedProjectIdeas.service.ApprovedIdeaService;
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
import lombok.RequiredArgsConstructor;

@Tag(name = "Approved Project Ideas", description = "Endpoints for viewing and managing approved project ideas")
@RestController
@RequestMapping("/${api.base.path}/approved-ideas")
@RequiredArgsConstructor
@Validated
public class ApprovedIdeaController {

    private final ApprovedIdeaService approvedIdeaService;
    private final JWTUtil jwtUtil;

    @Operation(
            summary = "List all approved project ideas",
            description = "Fetches a paginated list of all project ideas that have been marked as approved. This endpoint is public.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved list of approved ideas.")
            }
    )
    @GetMapping
    public ResponseEntity<PaginatedApiResponse<ApprovedIdeaResponse>> listApprovedIdeas(
            @Parameter(description = "Optional sort method. Use 'popular' to sort by reaction count.")
            @RequestParam(value = "sortBy", required = false) String sortBy,

            @Parameter(description = "Page number (starts from 0)")
            @RequestParam(value = "page", defaultValue = "0") @Min(0) int page,

            @Parameter(description = "Page size")
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(50) int size,

            HttpServletRequest request) {

        String token = jwtUtil.extractTokenFromRequest(request);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        final PaginatedApiResponse<ApprovedIdeaResponse> response = approvedIdeaService.listApprovedIdeas(sortBy, pageable, token);
        return ResponseUtils.buildPaginatedResponse(request, response);
    }

    @Operation(
            summary = "Update an approved project idea (Admin only)",
            description = "Allows an admin to update the details of an existing approved project idea. " +
                    "1 = Approve , 0 = Reject , 2 = In Progress , 3 = COMPLETED , 4 = DELETED",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UpdateApprovedIdeaRequest.class),
                            examples = @ExampleObject(
                                    name = "UpdateApprovedIdeaRequest Example",
                                    value = """
                        {
                          "name": "Updated Idea Name",
                          "description": "This is an updated description.",
                          "status":1
                        }
                        """
                            )
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project idea updated successfully."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - a valid admin token is required."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Approved project idea not found.")
            }
    )
    @PatchMapping("/{ideaId}")
    public ResponseEntity<ApiResponse> updateApprovedIdea(
            @PathVariable Long ideaId,
            @Valid @RequestBody UpdateApprovedIdeaRequest updateRequest,
            HttpServletRequest request) {

        String token = jwtUtil.extractTokenFromRequest(request);
        final ApiResponse response = approvedIdeaService.updateApprovedIdea(ideaId, updateRequest, token);
        return ResponseUtils.buildResponse(request, response);
    }

    @Operation(
            summary = "Delete an approved project idea (Admin only)",
            description = "Allows an admin to permanently delete an existing approved project idea.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project idea deleted successfully."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - a valid admin token is required."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Approved project idea not found.")
            }
    )
    @DeleteMapping("/{ideaId}")
    public ResponseEntity<ApiResponse> deleteApprovedIdea(
            @PathVariable Long ideaId,
            HttpServletRequest request) {

        String token = jwtUtil.extractTokenFromRequest(request);
        final ApiResponse response = approvedIdeaService.deleteApprovedIdea(ideaId, token);
        return ResponseUtils.buildResponse(request, response);
    }
}