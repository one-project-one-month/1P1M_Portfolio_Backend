package com._p1m.portfolio.features.projectIdeaCR.controller;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.utils.ResponseUtils;
import com._p1m.portfolio.data.models.ProjectIdea;
import com._p1m.portfolio.features.projectIdeaCR.dto.request.ProjectIdeaCRRequest;
import com._p1m.portfolio.features.projectIdeaCR.service.ProjectIdeaCRService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/v1/projectIdeaCR")
@RestController
public class ProjectIdeaCRController {

    private final ProjectIdeaCRService projectIdeaCRService;


    @PostMapping
    @Operation(
            summary = "Create Project Idea",
            description = "Creates a new project idea in the system.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Project idea creation request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProjectIdeaCRRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Project idea created successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data")
            }
    )

    public ResponseEntity<ApiResponse> createProjectIdea(@Valid @RequestBody ProjectIdeaCRRequest projectIdea, HttpServletRequest request) {
        final ApiResponse response = projectIdeaCRService.createProjectIdea(projectIdea);
        return ResponseUtils.buildResponse(request, response);
    }
}
