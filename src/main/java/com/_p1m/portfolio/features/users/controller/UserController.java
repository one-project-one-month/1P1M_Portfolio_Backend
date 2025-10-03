package com._p1m.portfolio.features.users.controller;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.utils.ResponseUtils;
import com._p1m.portfolio.features.users.dto.request.*;
import com._p1m.portfolio.features.users.service.UserService;

import com._p1m.portfolio.security.OAuth2.Github.dto.request.GithubOAuthRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base.path}/auth/users")
@Tag(name = "User API", description = "Endpoints for managing users")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Check Email Exists in the System or Not.",
            description = "Check Email Exists in the System or Not.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Check Email Exists in the System or Not. Request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CheckEmailRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Email Exists in the System."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Email does not Exist in the System.")
            }
    )
    @PostMapping("checkEmail")
    public ResponseEntity<ApiResponse> checkEmail(@Valid @RequestBody CheckEmailRequest checkEmailRequest ,
                                                  HttpServletRequest request){
        final ApiResponse response = userService.checkEmailExistOrNot(checkEmailRequest);
        return ResponseUtils.buildResponse(request , response);
    }

    @Operation(
            summary = "Login or Signup User Via Google",
            description = "Login Or SignUp User Via Google",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Login Or SignUp User Via Google Request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = GoogleOAuthRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Google OAuth2 successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    @PostMapping("googleOAuth2")
    public ResponseEntity<ApiResponse> googleOAuth2(@Valid @RequestBody GoogleOAuthRequest googleOAuthRequest,
                                                    HttpServletRequest request){
        final ApiResponse response = this.userService.googleOAuth2Service(googleOAuthRequest);
        return ResponseUtils.buildResponse(request , response);
    }

    @Operation(
            summary = "FOR LOCAL TESTING : Login or Signup User Via GitHub",
            description = "FOR LOCAL TESTING : Login Or SignUp User Via GitHub",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "FOR LOCAL TESTING : Login Or SignUp User Via GitHub Request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = com._p1m.portfolio.features.users.dto.request.GithubOAuthRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "GitHub OAuth2 successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    @PostMapping("githubOAuth2")
    public ResponseEntity<ApiResponse> githubOAuth2(@Valid @RequestBody GithubOAuthRequest githubOAuthRequest,
                                                    HttpServletRequest request){
        final ApiResponse response = this.userService.githubOAuth2Service(githubOAuthRequest);
        return ResponseUtils.buildResponse(request , response);
    }

    @Operation(
            summary = "PRODUCTION : Login or Signup User Via GitHub",
            description = "PRODUCTION : FOR LOCAL TESTING : Login Or SignUp User Via GitHub",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "PRODUCTION : Login Or SignUp User Via GitHub Request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = GithubCodeRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "GitHub OAuth2 successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    @PostMapping("exchangeGitHub")
    public ResponseEntity<ApiResponse> githubExchange(@RequestBody GithubCodeRequest codeRequest,
                                                      HttpServletRequest request){
        final ApiResponse response = this.userService.exchangeCodeAndProcessGitHubOAuth(codeRequest);
        return ResponseUtils.buildResponse(request , response);
    }

    @Operation(
            summary = "PRODUCTION : Login or Signup User Via Google",
            description = "PRODUCTION : FOR LOCAL TESTING : Login Or SignUp User Via Google",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "PRODUCTION : Login Or SignUp User Via Google Request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = GoogleCodeRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Google OAuth2 successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    @PostMapping("exchangeGoogle")
    public ResponseEntity<ApiResponse> googleExchange (@RequestBody GoogleCodeRequest googlecodeRequest,
                                                      HttpServletRequest request){
        final ApiResponse response = this.userService.exchangeCodeAndProcessGoogleOAuth(googlecodeRequest);
        return ResponseUtils.buildResponse(request , response);
    }

    @Operation(
            summary = "Login User",
            description = "Login User",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Login User Request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login User successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest loginRequest,
                                             HttpServletRequest request) {
        final ApiResponse response = this.userService.loginUser(loginRequest);
        return ResponseUtils.buildResponse(request , response);
    }

    @Operation(
            summary = "Signup User",
            description = "Signup User",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Signup User Request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SignupRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sign Up User successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@RequestBody SignupRequest signupRequest,
                                              HttpServletRequest request) {
        final ApiResponse response = userService.registerUser(signupRequest);
        return ResponseUtils.buildResponse(request , response);
    }

    @Operation(
            summary = "Send OTP Code to User",
            description = "Send OTP Code to User",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Send OTP Code to User Request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = OtpRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Send OTP Code successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    @PostMapping("send-otpCode")
    public ResponseEntity<ApiResponse> sendOTPCode(@RequestBody OtpRequest otpRequest,
                                                   HttpServletRequest request) throws MessagingException, IOException {
        ApiResponse response = userService.sendOtpCode(otpRequest);
        return ResponseUtils.buildResponse(request , response);
    }

    @Operation(
            summary = "Verify OTP Code",
            description = "Verify OTP Code",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Verify OTP Code Request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = VerifyOtpRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OTP verified successfully."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    @PostMapping("verify-otpCode")
    public ResponseEntity<ApiResponse> verifyOtpCode(@RequestBody VerifyOtpRequest verifyOtpRequest,
                                                     HttpServletRequest request){
        ApiResponse response = userService.verifyOtpCode(verifyOtpRequest);
        return ResponseUtils.buildResponse(request , response);
    }

}

