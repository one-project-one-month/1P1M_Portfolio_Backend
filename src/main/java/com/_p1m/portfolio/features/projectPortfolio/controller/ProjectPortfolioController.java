package com._p1m.portfolio.features.projectPortfolio.controller;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;
import com._p1m.portfolio.config.response.utils.ResponseUtils;
import com._p1m.portfolio.features.projectPortfolio.dto.request.CreateProjectPortfolioRequest;
import com._p1m.portfolio.features.projectPortfolio.dto.request.DeleteProjectPortfolioPictureRequest;
import com._p1m.portfolio.features.projectPortfolio.dto.request.UpdateProjectPortfolioRequest;
import com._p1m.portfolio.features.projectPortfolio.dto.request.UploadFileRequest;
import com._p1m.portfolio.features.projectPortfolio.dto.response.ProjectPortfolioResponse;
import com._p1m.portfolio.features.projectPortfolio.service.ProjectPortfolioService;
import com._p1m.portfolio.security.JWT.JWTUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
	    final ApiResponse response = this.projectPortfolioService.createProjectPortfolio(createRequest,token);
	    return ResponseUtils.buildResponse(request, response);
	}
    
    @PatchMapping(
    	    value = "/uploadFile",
    	    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    	)
    	@Operation(
    	    summary = "Upload a file (project potrfolio image)",
    	    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
    	        description = "Multipart form with image file",
    	        required = true,
    	        content = @Content(
    	            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
    	            schema = @Schema(implementation = UploadFileRequest.class)
    	        )
    	    )
    	)
    	public ResponseEntity<ApiResponse> uploadFile(
    	    @RequestParam("projectPortfolioId") Long projectPortfolioId,
    	    @Parameter(hidden = true)
    	    @ModelAttribute UploadFileRequest fileRequest,
    	    HttpServletRequest request
    	) {
    	    ApiResponse response = this.projectPortfolioService.uploadFile(fileRequest, projectPortfolioId);
    	    return ResponseUtils.buildResponse(request, response);
    	}
    
    @Operation(
    	    summary = "Retrieve project portfolio data",
    	    description = "Retrieve project portfolio data for specified project.",
    	    responses = {
    	    	@io.swagger.v3.oas.annotations.responses.ApiResponse(
    	            responseCode = "200",
    	            description = "Project portfolio retrieve successfully.",
    	            content = @Content(schema = @Schema(implementation = ApiResponse.class))
    	        )
    	    }
    )
	@GetMapping
	public ResponseEntity<ApiResponse> retrieveSpecificProjectPortfolio(
		@RequestParam("projectPortfolioId") Long projectPortfolioId,
	    HttpServletRequest request
	) {
	    ApiResponse response = this.projectPortfolioService.retrieveSpecificProjectPortfolio(projectPortfolioId);
	    return ResponseUtils.buildResponse(request, response);
	}
    
    @Operation(
            summary = "Fetching all project profiles.",
            description = "Fetching project profiles with keywords - name, returning pagination",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project profiles are fetched successfully"),
            }
    )
    @Validated
	@GetMapping("/getAllProjectProfiles")
	public ResponseEntity<PaginatedApiResponse<ProjectPortfolioResponse>> retrieveAllProjectPortfolio(
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

	    PaginatedApiResponse<ProjectPortfolioResponse> response =
	    		this.projectPortfolioService.getAllpaginatedProjectProfiles(keyword, pageable);

	    return ResponseUtils.buildPaginatedResponse(request, response);
	}
    
    @Operation(
    	    summary = "Partially update project portfolio",
    	    description = "Allows partial updates to the project portfolio",
    	    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
    	        required = true,
    	        content = @Content(
    	            schema = @Schema(implementation = UpdateProjectPortfolioRequest.class),
    	            examples = @ExampleObject(
    	                name = "UpdateProjectPortfolioRequest Example",
    	                value = """
                        {
                          "name": "My Cool Project",
                          "description": "Description goes here",
                          "projectLink": "http://example.com/project",
                          "repoLink": "http://github.com/project-repo"
                        }
                        """
    	            )
    	        )
    	    ),
    	    responses = {
    	    	@io.swagger.v3.oas.annotations.responses.ApiResponse(
    	            responseCode = "200",
    	            description = "Project portfolio updated successfully.",
    	            content = @Content(schema = @Schema(implementation = ApiResponse.class))
    	        )
    	    }
    )
	@PatchMapping
	public ResponseEntity<ApiResponse> updateProjectPortfolio(
		@RequestParam("projectPortfolioId") Long projectPortfolioId,
	    @RequestBody @Valid UpdateProjectPortfolioRequest updateRequest,
	    HttpServletRequest request
	) {
	    ApiResponse response = this.projectPortfolioService.updateProjectPortfolio(updateRequest,projectPortfolioId);
	    return ResponseUtils.buildResponse(request, response);
	}
    
    @Operation(
    	    summary = "Delete project portfolio",
    	    description = "Delete all data of project portfolio",
    	    responses = {
    	    	@io.swagger.v3.oas.annotations.responses.ApiResponse(
    	            responseCode = "200",
    	            description = "Project portfolio deleted successfully.",
    	            content = @Content(schema = @Schema(implementation = ApiResponse.class))
    	        )
    	    }
    )
	@DeleteMapping
	public ResponseEntity<ApiResponse> deleteProjectPortfolio(
		@RequestParam("projectPortfolioId") Long projectPortfolioId,
	    HttpServletRequest request
	) {
	    ApiResponse response = this.projectPortfolioService.deleteProjectPortfolio(projectPortfolioId);
	    return ResponseUtils.buildResponse(request, response);
	}
    
    @Operation(
    	    summary = "Delete project portfolio picture",
    	    description = "Delete partially data of project portfolio,i.e picture",
    		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
        	        required = true,
        	        content = @Content(
        	            schema = @Schema(implementation = DeleteProjectPortfolioPictureRequest.class),
        	            examples = @ExampleObject(
        	                name = "DeleteProjectPortfolioPictureRequest Example",
        	                value = """
                            {
                              "imageUrl" : "http://cloudinary image url goes here"
                            }
                            """
        	            )
        	        )
        	    ),
    	    responses = {
    	    	@io.swagger.v3.oas.annotations.responses.ApiResponse(
    	            responseCode = "200",
    	            description = "Project portfolio picture deleted successfully.",
    	            content = @Content(schema = @Schema(implementation = ApiResponse.class))
    	        )
    	    }
    )
	@DeleteMapping("/picture")
	public ResponseEntity<ApiResponse> deleteProjectPortfolioPicture(
		@RequestParam("projectPortfolioId") Long projectPortfolioId,
		@RequestBody @Valid DeleteProjectPortfolioPictureRequest deleteRequest,
	    HttpServletRequest request
	) {
	    ApiResponse response = this.projectPortfolioService.deleteProjectPortfolioPicture(deleteRequest,projectPortfolioId);
	    return ResponseUtils.buildResponse(request, response);
	}
    
}