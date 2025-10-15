package com._p1m.portfolio.features.opomRegister.controller;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;
import com._p1m.portfolio.config.response.utils.ResponseUtils;
import com._p1m.portfolio.features.opomRegister.dto.request.UserRegisterRequest;
import com._p1m.portfolio.features.opomRegister.dto.response.OpomRegisterResponse;
import com._p1m.portfolio.features.opomRegister.service.OpomRegisterService;
import com._p1m.portfolio.features.projectPortfolio.dto.response.ProjectPortfolioResponse;
import com._p1m.portfolio.security.JWT.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/${api.base.path}/auth")
@RequiredArgsConstructor
public class OpomController {

    private final OpomRegisterService opomRegisterService;
    private final JWTUtil jwtUtil;

    @Operation(
            summary = "Register User",
            description = "Register User",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Register User Request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserRegisterRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Register User Sucessfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody UserRegisterRequest userRegisterRequest, HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromRequest(request);

        final ApiResponse response=this.opomRegisterService.registerUser(userRegisterRequest,token);
        return ResponseUtils.buildResponse(request,response);
    }

    @Operation(
            summary = "Fetching all Opom Register.",
            description = "Fetching all Opom Register.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project profiles are fetched successfully"),
            }
    )
    @Validated
    @GetMapping("/getAllOpomRegister")
    public ResponseEntity<PaginatedApiResponse<OpomRegisterResponse>> getAllOpomRegisterList(
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

        PaginatedApiResponse<OpomRegisterResponse> response =
                this.opomRegisterService.getAllpaginatedOpomRegisterList(keyword, pageable);

        return ResponseUtils.buildPaginatedResponse(request, response);
    }

    @Operation(
            summary = "update opom registers",
            description = "Allows to update opom registers",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = " Update opom register request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserRegisterRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Updated Successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    @PutMapping("/admin/{id}")
    public ResponseEntity<ApiResponse> updateOpomRegisterData(
            @PathVariable Long id,
            @Valid @RequestBody UserRegisterRequest userRegisterRequest,
            HttpServletRequest request
    ) {
        String token = jwtUtil.extractTokenFromRequest(request);

        ApiResponse response=this.opomRegisterService.updateOpomRegisterData(id,userRegisterRequest,token);
        return ResponseUtils.buildResponse(request,response);
    }

    @Operation(
            summary = "delete opom registers",
            description = "Allows to delete opom registers",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = " delete opom register request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserRegisterRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Deleted Successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<ApiResponse> softDeleteOpomRegister(@PathVariable Long id, HttpServletRequest request) {
        ApiResponse response=this.opomRegisterService.softDeleteOpomRegister(id);
        return ResponseUtils.buildResponse(request,response);
    }
}
