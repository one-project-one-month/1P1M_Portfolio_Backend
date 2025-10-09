package com._p1m.portfolio.features.projectIdeaUD.controller;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.utils.ResponseUtils;
import com._p1m.portfolio.features.projectIdeaUD.dto.request.ProjectIdeaRequest;
import com._p1m.portfolio.features.projectIdeaUD.service.ProjectIdeaUDService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/projectIdeaUD")
@RestController
public class ProjectIdeaUDController {

    private final ProjectIdeaUDService projectIdeaUDService;

    // ========================= UPDATE =========================
    @PutMapping("/update/{ideaId}")
    @Operation(
            summary = "Update Project Idea",
            description = "Allows a user to update their own project idea, or an admin to update any idea (including status).",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Project idea update request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProjectIdeaRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project idea updated successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project idea not found"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Not authorized to update this project idea")
            }
    )
    public ResponseEntity<ApiResponse> updateProjectIdea(
            @Valid @PathVariable Long ideaId,
            @Valid @RequestBody ProjectIdeaRequest projectIdeaRequest,
            HttpServletRequest request) {
        ApiResponse response = projectIdeaUDService.updateProjectIdea(ideaId, projectIdeaRequest);
        return ResponseUtils.buildResponse(request, response);
    }

    // ========================= DELETE =========================
    @DeleteMapping("/delete/{ideaId}")
    @Operation(
            summary = "Delete Project Idea",
            description = "Allows a user to delete their own project idea, or an admin to delete any project idea.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project idea deleted successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project idea not found"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Not authorized to delete this project idea")
            }
    )
    public ResponseEntity<ApiResponse> deleteProjectIdea(
            @Valid @PathVariable Long ideaId,
            HttpServletRequest request) {
        ApiResponse response = projectIdeaUDService.deleteProjectIdea(ideaId);
        return ResponseUtils.buildResponse(request, response);
    }
}
