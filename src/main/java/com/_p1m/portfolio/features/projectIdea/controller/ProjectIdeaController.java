package com._p1m.portfolio.features.projectIdea.controller;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;
import com._p1m.portfolio.config.response.utils.ResponseUtils;
import com._p1m.portfolio.features.opomRegister.dto.request.UserRegisterRequest;
import com._p1m.portfolio.features.opomRegister.dto.response.OpomRegisterResponse;
import com._p1m.portfolio.features.projectIdea.dto.request.ProjectIdeaRequest;
import com._p1m.portfolio.features.projectIdea.dto.response.ProjectIdeaListResponse;
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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
            description = "Update Project Idea Status " +
                    "1 = Approve , 0 = Reject , 2 = In Progress , 3 = COMPLETED , 4 = DELETED",
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

    @Operation(
            summary = "Fetching all Project Ideas.",
            description = "Fetching all Project Ideas.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project profiles are fetched successfully"),
            }
    )
    @Validated
    @GetMapping("/getAllProjectIdeas")
    public ResponseEntity<PaginatedApiResponse<ProjectIdeaListResponse>> getAllProjectIdeaList(
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
    ) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        String token = jwtUtil.extractTokenFromRequest(request);
        PaginatedApiResponse<ProjectIdeaListResponse> response =
                this.projectIdeaService.getAllPaginatedProjectIdeaList(keyword, pageable, token);

        return ResponseUtils.buildPaginatedResponse(request, response);
    }

    @Operation(
            summary = "Delete project idea",
            description = "Delete all data of project idea",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Project Idea deleted successfully.",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    )
            }
    )
    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteProjectIdea(
            @RequestParam("projectIdeaId") Long projectIdeaId,
            HttpServletRequest request
    ) {
        ApiResponse response = this.projectIdeaService.deleteProjectIdea(projectIdeaId);
        return ResponseUtils.buildResponse(request, response);
    }
    
    @Operation(
            summary = "React Project Idea",
            description = "Reaction Project Idea",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Reacted",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    )
            }
    )
    @PostMapping("/react")
    public ResponseEntity<ApiResponse> reactProjectIdea(@RequestParam("projectIdeaId") Long projectIdeaId,HttpServletRequest request){
        String token = jwtUtil.extractTokenFromRequest(request);
        final ApiResponse response = this.projectIdeaService.reactProjectIdea(projectIdeaId,token);
        return ResponseUtils.buildResponse(request ,response);
    }
    
    @Operation(
            summary = "Unreact Project Idea",
            description = "Remove a user's reaction from a project idea",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Reaction removed successfully",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    )
            }
    )
    @DeleteMapping("/unreact")
    public ResponseEntity<ApiResponse> unreactProjectIdea(@RequestParam("projectIdeaId") Long projectIdeaId,
                                                          HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromRequest(request);
        ApiResponse response = projectIdeaService.unreactProjectIdea(projectIdeaId, token);
        return ResponseUtils.buildResponse(request, response);
    }

    @Operation(
            summary = "Get Project Idea Reaction Count",
            description = "Returns the total number of reactions for a project idea",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Reaction count fetched successfully",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    )
            }
    )
    @GetMapping("/react/count")
    public ResponseEntity<ApiResponse> getReactionCount(@RequestParam("projectIdeaId") Long projectIdeaId,
                                                        HttpServletRequest request) {
        ApiResponse response = projectIdeaService.getProjectIdeaReactionCount(projectIdeaId);
        return ResponseUtils.buildResponse(request, response);
    }
}
