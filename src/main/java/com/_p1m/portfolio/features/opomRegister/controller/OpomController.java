package com._p1m.portfolio.features.opomRegister.controller;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.utils.ResponseUtils;
import com._p1m.portfolio.features.opomRegister.dto.request.UserRegisterRequest;
import com._p1m.portfolio.features.opomRegister.service.OpomRegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base.path}/auth/opomRegister")
public class OpomController {

    private final OpomRegisterService opomRegisterService;

    @PostMapping
    @Operation(
            summary = "Register for OPOM Project",
            description = "Registers new user in the system.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User creation request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserRegisterRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User Register successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    public ResponseEntity<ApiResponse> opomRegister(
            @RequestBody final UserRegisterRequest userRegisterRequest,
            HttpServletRequest request){
        final ApiResponse response = this.opomRegisterService.registerUser(userRegisterRequest);
        return ResponseUtils.buildResponse(request ,response);
    }


    @PutMapping("/update/{id}")
    @Operation(
            summary = "Update User Register for OPOM Project",
            description = "Update user datat in the system.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User Update request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserRegisterRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Updated User Register successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    public ResponseEntity<ApiResponse> updateOpomRegister(
            @Valid @RequestBody final UserRegisterRequest userRegisterRequest,
            @PathVariable(name = "id") Long id,
            HttpServletRequest request
    ){
        final ApiResponse response = this.opomRegisterService.updateOpomRegisterData(id ,userRegisterRequest);
        return ResponseUtils.buildResponse(request ,response);
    }

}
