package com._p1m.portfolio.features.projectIdea.controller;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;
import com._p1m.portfolio.config.response.utils.ResponseUtils;
import com._p1m.portfolio.data.models.ProjectIdea;
import com._p1m.portfolio.features.projectIdea.dto.request.ProjectIdeaRequest;
import com._p1m.portfolio.features.projectIdea.service.ProjectIdeaCRService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/projectIdeaCR")
@RestController
public class ProjectIdeaCRController {

    private final ProjectIdeaCRService projectIdeaCRService;


    @PostMapping("/create")
    @Operation(
            summary = "Create Project Idea",
            description = "Creates a new project idea in the system.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Project idea creation request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProjectIdeaRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Project idea created successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data")
            }
    )
    public ResponseEntity<ApiResponse> createProjectIdea(
            @Valid @PathVariable(name = "devId") Long devId,
            @Valid @RequestBody ProjectIdeaRequest projectIdea,
            HttpServletRequest request) {
        final ApiResponse response = projectIdeaCRService.createProjectIdea(projectIdea, devId);
        return ResponseUtils.buildResponse(request, response);
    }

    @GetMapping("")
    public ResponseEntity<PaginatedApiResponse<ProjectIdea>> getAllProjectIdeas(HttpServletRequest request) {
        PaginatedApiResponse<ProjectIdea> response = projectIdeaCRService.getAllProjectIdeas();
        return ResponseUtils.buildPaginatedResponse(request, response);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse> getProjectIdeaByName(@Valid @PathVariable String name, HttpServletRequest request) {
        ApiResponse response = projectIdeaCRService.getProjectIdeaByName(name);
        return ResponseUtils.buildResponse(request, response);
    }

    @GetMapping("/devPFId/{devProfileId}")
    public ResponseEntity<ApiResponse> getProjectIdeasByDevProfileId(@Valid @PathVariable Long devProfileId, HttpServletRequest request) {
        ApiResponse response = projectIdeaCRService.getProjectIdeasByDevProfileId(devProfileId);
        return ResponseUtils.buildResponse(request, response);
    }

}
