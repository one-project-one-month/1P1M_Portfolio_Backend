package com._p1m.portfolio.features.projectIdea.controller;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.utils.ResponseUtils;
import com._p1m.portfolio.features.opomRegister.dto.request.UserRegisterRequest;
import com._p1m.portfolio.features.projectIdea.dto.request.ProjectIdeaRequest;
import com._p1m.portfolio.features.projectIdea.service.ProjectIdeaService;
import com._p1m.portfolio.security.JWT.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/${api.base.path}/project-idea")
@RequiredArgsConstructor
@Tag(name = "Project Idea ", description = "Endpoints for managing Project Ideas")
public class ProjectIdeaController {

    private final ProjectIdeaService projectIdeaService;
    private final JWTUtil jwtUtil;

    @Operation(
            summary = "Create Project Idea",
            description = "Create Project Idea",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Create Project Idea Request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProjectIdeaRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "201",
                            description = "Project Idea Created successfully.",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    )
            }
    )
    @PostMapping
    public ResponseEntity<ApiResponse> createProjectIdea(@Valid @RequestBody ProjectIdeaRequest projectIdeaRequest , HttpServletRequest request){
        String token = jwtUtil.extractTokenFromRequest(request);
        final ApiResponse response = this.projectIdeaService.createNewProjectIdea(projectIdeaRequest , token);
        return ResponseUtils.buildResponse(request ,response);
    }

}
