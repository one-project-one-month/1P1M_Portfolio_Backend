package com._p1m.portfolio.features.projectPortfolio.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.utils.ResponseUtils;
import com._p1m.portfolio.features.projectPortfolio.dto.request.CreateProjectPortfolioRequest;
import com._p1m.portfolio.features.projectPortfolio.service.ProjectPortfolioService;
import com._p1m.portfolio.security.JWT.JWTUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Tag(name = "Project portfolio Module", description = "Endpoints for project portfolio")
@RestController
@RequestMapping("/${api.base.path}/project-portfolio")
@RequiredArgsConstructor
@Slf4j
public class ProjectPortfolioController {
    private final ProjectPortfolioService projectPortfolioService;
    private final JWTUtil jwtUtil;
    @Operation(
    	    summary = "Create project portfolio.",
    	    description = "This API endpoint allows the creation of a project portfolio by providing necessary information.",
    	    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
    	        description = "Creating project portfolio for users by providing required details.",
    	        required = true,
    	        content = @Content(
    	            schema = @Schema(implementation = CreateProjectPortfolioRequest.class),
    	            examples = {
    	                @ExampleObject(
    	                    name = "Project Portfolio Example",
    	                    value = """
    	                            {
    	                              "name": "My Awesome Project",
    	                              "description": "A brief description of the project.",
    	                              "projectLink": "http://example.com/project",
    	                              "repoLink": "http://github.com/project-repo",
    	                              "languageAndTools": ["Java", "Spring Boot", "React"]
    	                            }
    	                            """
    	                )
    	            }
    	        )
    	    ),
    	    responses = {
    	        @io.swagger.v3.oas.annotations.responses.ApiResponse(
    	            responseCode = "201",
    	            description = "Project portfolio created successfully.",
    	            content = @Content(
    	                schema = @Schema(implementation = ApiResponse.class)
    	            )
    	        )
    	    }
    	)
    @PostMapping
    public ResponseEntity<ApiResponse> createProjectPortfolio(
        @Valid @RequestBody CreateProjectPortfolioRequest createRequest,
        HttpServletRequest request
    )  {
    	String token = jwtUtil.extractTokenFromRequest(request);
	    final ApiResponse response = this.projectPortfolioService.createProjectPortfolio(createRequest,"");
	    return ResponseUtils.buildResponse(request, response);
	}
}