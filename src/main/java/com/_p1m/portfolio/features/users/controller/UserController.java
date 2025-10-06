package com._p1m.portfolio.features.users.controller;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.utils.ResponseUtils;
import com._p1m.portfolio.features.users.dto.request.*;
import com._p1m.portfolio.features.users.service.PasswordResetService;
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
    private final PasswordResetService passwordResetService;

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

    @Operation(
            summary = "Initiate Password Reset",
            description = "Takes a user's email address. If the user exists, it generates a 6-digit OTP and sends it to their email. For security, it always returns a generic success message, regardless of whether the user was found.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Request containing the user's email address.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ForgotPasswordRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request processed successfully. A generic success message is always returned."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid email format.")
            }
    )
    @PostMapping("/password/forgot")
    public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request,
                                                      HttpServletRequest httpServletRequest) throws Exception {
        final ApiResponse response = passwordResetService.sendResetOtp(request);
        return ResponseUtils.buildResponse(httpServletRequest, response);
    }

    @Operation(
            summary = "Verify Password Reset OTP",
            description = "Takes the user's email and the 6-digit OTP they received. It verifies if the OTP is correct and has not expired.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Request containing the user's email and the OTP code.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = VerifyOtpRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OTP verified successfully."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid or expired OTP code.")
            }
    )
    @PostMapping("/password/verify-otp")
    public ResponseEntity<ApiResponse> verifyPasswordOtp(@Valid @RequestBody VerifyOtpRequest request,
                                                         HttpServletRequest httpServletRequest) {
        final ApiResponse response = passwordResetService.verifyResetOtp(request);
        return ResponseUtils.buildResponse(httpServletRequest, response);
    }

    @Operation(
            summary = "Reset User's Password",
            description = "Takes the user's email and their new password. This endpoint should only be called after a successful OTP verification. It sets and securely hashes the new password for the user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Request containing the user's email and their new password.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ResetPasswordRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password has been reset successfully."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found (should not happen in a normal flow).")
            }
    )
    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request,
                                                     HttpServletRequest httpServletRequest) {
        final ApiResponse response = passwordResetService.resetPassword(request);
        return ResponseUtils.buildResponse(httpServletRequest, response);
    }

}

