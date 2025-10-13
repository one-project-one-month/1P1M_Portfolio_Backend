package com._p1m.portfolio.features.opomRegister.controller;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;
import com._p1m.portfolio.config.response.utils.ResponseUtils;
import com._p1m.portfolio.features.opomRegister.dto.request.UserRegisterRequest;
import com._p1m.portfolio.features.opomRegister.service.OpomRegisterService;
import com._p1m.portfolio.security.JWT.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/${api.base.path}")
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
            summary = "get all opom registers",
            description = "get all opom registers",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Get all Opom registers Request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserRegisterRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request Success"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )

    @GetMapping("/admin/list")
    public ResponseEntity<PaginatedApiResponse> getAllOpomRegisters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "newest") String dateOrder,
            @RequestParam(required = false) String role,
            HttpServletRequest request
    ) {
        PaginatedApiResponse response= this.opomRegisterService.getAllOpomRegisters(page, size, dateOrder, role);
        return ResponseUtils.buildPaginatedResponse(request,response);
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
        ApiResponse response=this.opomRegisterService.updateOpomRegisterData(id,userRegisterRequest);
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
