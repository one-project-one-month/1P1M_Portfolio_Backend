package com._p1m.portfolio.features.projectIdea.controller;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.utils.ResponseUtils;
import com._p1m.portfolio.features.opomRegister.dto.request.UserRegisterRequest;
import com._p1m.portfolio.features.projectIdea.dto.request.ProjectIdeaRequest;
import com._p1m.portfolio.features.projectIdea.service.ProjectIdeaService;
import com._p1m.portfolio.features.projectPortfolio.dto.request.UpdateProjectPortfolioRequest;
import com._p1m.portfolio.security.JWT.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "409",
                            description = "Project Name Already Exists.",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "Unexpected server error.",
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

    @Operation(
            summary = "Update Project Idea Status",
            description = "Update Project Idea Status",
            parameters = {
                    @Parameter(
                            name = "projectIdeaId",
                            description = "The ID of the project idea to update",
                            required = true,
                            example = "101"
                    ),
                    @Parameter(
                            name = "status",
                            description = "The new status (1 = APPROVED, 0 = REJECTED)",
                            required = true,
                            example = "1"
                    )
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Project Idea Status Updated successfully.",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    )
            }
    )
    @PatchMapping
    public ResponseEntity<ApiResponse> approveProjectIdea(
            @RequestParam("projectIdeaId") Long projectIdeaId,
            @RequestParam("status") Long status,
            HttpServletRequest request
    ){
        String token = jwtUtil.extractTokenFromRequest(request);
        final ApiResponse response = this.projectIdeaService.approveProjectIdeaStatus(projectIdeaId , status , token);
        return ResponseUtils.buildResponse(request , response);
    }

}
