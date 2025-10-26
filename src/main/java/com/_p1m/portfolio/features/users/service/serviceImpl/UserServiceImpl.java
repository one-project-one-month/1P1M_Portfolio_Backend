package com._p1m.portfolio.features.users.service.serviceImpl;

import com._p1m.portfolio.common.component.OtpStore;
import com._p1m.portfolio.common.util.ServerUtil;
import com._p1m.portfolio.config.beans.AdminEmailConfig;
import com._p1m.portfolio.config.exceptions.EntityNotFoundException;
import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.data.enums.ROLE;
import com._p1m.portfolio.data.models.*;
import com._p1m.portfolio.data.models.lookup.ProjectType;
import com._p1m.portfolio.data.repositories.*;
import com._p1m.portfolio.features.devProfile.dto.response.DevProfileResponse;
import com._p1m.portfolio.features.devProfile.service.serviceImpl.DevProfileServiceImpl;
import com._p1m.portfolio.features.projectIdea.dto.response.ProjectIdeaResponse;
import com._p1m.portfolio.features.projectPortfolio.dto.response.ProjectPortfolioResponse;
import com._p1m.portfolio.features.projectPortfolio.dto.response.ProjectPortfolioResponseForProfileData;
import com._p1m.portfolio.features.users.dto.request.*;
import com._p1m.portfolio.features.users.dto.response.AuthResponse;
import com._p1m.portfolio.features.users.service.AuthService;
import com._p1m.portfolio.features.users.service.UserService;
import com._p1m.portfolio.security.JWT.JWTUtil;

import com._p1m.portfolio.security.OAuth2.Github.dto.request.GithubOAuthRequest;
import com._p1m.portfolio.security.OAuth2.Github.dto.request.GithubUserInfo;
import com._p1m.portfolio.security.OAuth2.Github.dto.response.GithubOAuthResponse;
import com._p1m.portfolio.security.OAuth2.Github.service.ExchangeGitHubCodeService;
import com._p1m.portfolio.security.OAuth2.Github.service.GithubOAuthService;
import com._p1m.portfolio.security.OAuth2.Google.dto.request.GoogleUserInfo;
import com._p1m.portfolio.security.OAuth2.Google.dto.response.GoogleOAuthResponse;
import com._p1m.portfolio.security.OAuth2.Google.service.ExchangeGoogleCodeService;
import com._p1m.portfolio.security.OAuth2.Google.service.GoogleOAuthService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DevProfileRepository devProfileRepository;
    private final ProjectIdeaRepository projectIdeaRepository;
    private final ProjectPortfolioRepository projectPortfolioRepository;
    private final DevProfileServiceImpl devProfileServiceImpl;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final GoogleOAuthService googleOAuthService;
    private final GithubOAuthService githubOAuthService;
    private final AuthService authService;
    private final ExchangeGitHubCodeService exchangeGitHubCodeService;
    private final ExchangeGoogleCodeService exchangeGoogleCodeService;
    private final OAuthUserRepository oAuthUserRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authManager;
    private final AdminEmailConfig adminEmailConfig;
    private final ServerUtil serverUtil;
    private final OtpStore otpStore;

    @Override
    public ApiResponse googleOAuth2Service(GoogleOAuthRequest googleOAuthRequest) {
        // Add validation for token
        if (googleOAuthRequest.getToken() == null || googleOAuthRequest.getToken().trim().isEmpty()) {
            return ApiResponse.builder()
                    .success(0)
                    .code(400)
                    .message("Token is required")
                    .data(null)
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }

        try {
            GoogleUserInfo googleUserInfo = new GoogleUserInfo();
            try{
                googleUserInfo = googleOAuthService.verifyIdToken(googleOAuthRequest.getToken());
            }catch (Exception e){
                googleUserInfo = googleOAuthService.verifyAccessToken(googleOAuthRequest.getToken());
            }
            // Process Google OAuth and Save User
            GoogleOAuthResponse googleOAuthResponse = authService.processGoogleOAuth(googleUserInfo);

            return ApiResponse.builder()
                    .success(1)
                    .code(200)
                    .message("Google OAuth2 Successfully.")
                    .data(googleOAuthResponse)
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();

        } catch (Exception e) {
            return ApiResponse.builder()
                    .success(0)
                    .code(500)
                    .message("Internal server error: " + e.getMessage())
                    .data(null)
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }
    }


    @Override
    public ApiResponse githubOAuth2Service(GithubOAuthRequest githubOAuthRequest) {
        // Add validation for token
        if (githubOAuthRequest.getToken() == null || githubOAuthRequest.getToken().trim().isEmpty()) {
            return ApiResponse.builder()
                    .success(0)
                    .code(400)
                    .message("Token is required")
                    .data(null)
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }

        try {
            // Verify GitHub access token and get user info
            GithubUserInfo githubUserInfo = githubOAuthService.verifyAccessToken(githubOAuthRequest.getToken());

            // Process GitHub OAuth and Save User
            GithubOAuthResponse githubOAuthResponse = authService.processGithubOAuth(githubUserInfo);

            return ApiResponse.builder()
                    .success(1)
                    .code(200)
                    .message("GitHub OAuth2 Successfully.")
                    .data(githubOAuthResponse)
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();

        } catch (IllegalArgumentException e) {
            return ApiResponse.builder()
                    .success(0)
                    .code(400)
                    .message("Invalid GitHub token: " + e.getMessage())
                    .data(null)
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        } catch (Exception e) {
            return ApiResponse.builder()
                    .success(0)
                    .code(500)
                    .message("Internal server error: " + e.getMessage())
                    .data(null)
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }
    }

    @Override
    public ApiResponse exchangeCodeAndProcessGoogleOAuth(GoogleCodeRequest googlecodeRequest) {
        String accessToken = exchangeGoogleCodeService.exchangeGoogleCodeForToken(googlecodeRequest.getCode());

        try {
            GoogleUserInfo googleUserInfo = new GoogleUserInfo();
            try{
                googleUserInfo = googleOAuthService.verifyIdToken(accessToken);
            }catch (Exception e){
                googleUserInfo = googleOAuthService.verifyAccessToken(accessToken);
            }

            // Process Google OAuth and Save User
            GoogleOAuthResponse googleOAuthResponse = authService.processGoogleOAuth(googleUserInfo);

            return ApiResponse.builder()
                    .success(1)
                    .code(200)
                    .message("Google OAuth2 Successfully.")
                    .data(googleOAuthResponse)
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();

        } catch (Exception e) {
            return ApiResponse.builder()
                    .success(0)
                    .code(500)
                    .message("Internal server error: " + e.getMessage())
                    .data(null)
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }
    }

    @Override
    public ApiResponse registerUser(SignupRequest signupRequest) {
        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        String userName = signupRequest.getEmail().split("@")[0];
        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setUsername(userName);
        // Fetch Role entity from DB
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("USER role not found"));
        user.setRole(userRole);
        userRepository.save(user);

        return ApiResponse.builder()
                .success(1)
                .code(200)
                .message("Sign Up Successfully.")
                .data(Map.of(
                        "userId",user.getId(),
                        "username", user.getUsername(),
                        "email", user.getEmail()
                ))
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    @Override
    public ApiResponse loginUser(LoginRequest loginRequest) {
        boolean isNewUser = false;
        Optional<User> existingUser = userRepository.findByEmail(loginRequest.getEmail());
        if(existingUser.isEmpty()){
            return ApiResponse.builder()
                    .success(0)
                    .code(404)
                    .message("User does not Exist in the System.")
                    .data(loginRequest.getEmail())
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }
        User user = existingUser.get();
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ApiResponse.builder()
                    .success(0)
                    .code(401)
                    .message("Incorrect Credentials.")
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }

        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail() , loginRequest.getPassword()));

        String token = jwtUtil.generateToken(loginRequest.getEmail());

        // To check If the user Login For the Very First time with Dev Profile.
        Optional<DevProfile> isDevProfileExist = devProfileRepository.findByUserId(user.getId());
        if(isDevProfileExist.isEmpty() && user.getRole().getId() == 1){ // Only for User ( Not including Admins )
            isNewUser = true;
        }

        // Check if the Email is Admin or Not
        long roleId = user.getRole().getId();
        String roleName =user.getRole().getName();
//        if(adminEmailConfig.isAdmin(user.getEmail())){
//            roleId = 2L;
//            roleName = "ADMIN";
//        }

        return ApiResponse.builder()
                .success(1)
                .code(200)
                .message("Login Successfully.")
                .data(Map.of(
                        "userId",user.getId(),
                        "username", user.getUsername(),
                        "email", user.getEmail(),
                        "roleId", roleId,
                        "role",roleName,
                        "token", token,
                        "isNewUserLogin", isNewUser
                ))
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    @Override
    public ApiResponse sendOtpCode(OtpRequest otpRequest) throws IOException, MessagingException {
        String email = otpRequest.getEmail();
        String otpCode = serverUtil.generateNumericCode(6);
        Optional<User>optionalUser = userRepository.findByEmail(otpRequest.getEmail());
        if (optionalUser.isPresent()) {
                return ApiResponse.builder()
                        .success(0)
                        .code(200)
                        .message("Email is already registered in the system.")
                        .meta(Map.of("timestamp", System.currentTimeMillis()))
                        .build();
        }
        serverUtil.sendOtpCode(otpRequest.getEmail() , otpCode);
        otpStore.saveOtp(email , otpCode , 15);

        return ApiResponse.builder()
                .success(1)
                .code(200)
                .message("Send OTP Code Successfully.")
                .data(otpRequest.getEmail())
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    @Override
    public ApiResponse verifyOtpCode(VerifyOtpRequest verifyOtpRequest) {
        boolean valid = otpStore.verifyOtp(verifyOtpRequest.getEmail(), verifyOtpRequest.getOtpCode());

        if (!valid) {
            return ApiResponse.builder()
                    .success(0)
                    .code(400)
                    .message("Invalid Request.")
                    .data(null)
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }
        return ApiResponse.builder()
                .success(1)
                .code(200)
                .message("OTP verified successfully.")
                .data(null)
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    @Override
    public ApiResponse checkEmailExistOrNot(CheckEmailRequest checkEmailRequest) {
        Optional<User> existingUser = userRepository.findByEmail(checkEmailRequest.getEmail());
        if(existingUser.isPresent()){
            return ApiResponse.builder()
                    .success(1)
                    .code(200)
                        .message("Email Exists in the System.")
                    .data(checkEmailRequest.getEmail())
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }
        return ApiResponse.builder()
                .success(0)
                .code(200)
                .message("Email does not Exist in the System.")
                .data(null)
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    @Override
    public ApiResponse exchangeCodeAndProcessGitHubOAuth(GithubCodeRequest githubCodeRequest) {

        String accessToken = exchangeGitHubCodeService.exchangeGithubCodeForToken(githubCodeRequest.getCode());

        try {
            // Verify GitHub access token and get user info
            GithubUserInfo githubUserInfo = githubOAuthService.verifyAccessToken(accessToken);
            // Process GitHub OAuth and Save User
            GithubOAuthResponse githubOAuthResponse = authService.processGithubOAuth(githubUserInfo);

            return ApiResponse.builder()
                    .success(1)
                    .code(200)
                    .message("GitHub OAuth2 Successfully.")
                    .data(githubOAuthResponse)
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();

        } catch (IllegalArgumentException e) {
            return ApiResponse.builder()
                    .success(0)
                    .code(400)
                    .message("Invalid GitHub token: " + e.getMessage())
                    .data(null)
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        } catch (Exception e) {
            return ApiResponse.builder()
                    .success(0)
                    .code(500)
                    .message("Internal server error: " + e.getMessage())
                    .data(null)
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse getProfileData(Long userId, String token) {
        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User Not found."));

        DevProfile devProfile = devProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Dev Profile Not Found."));

        ProjectIdea projectIdea = projectIdeaRepository.findByDevProfile_Id(devProfile.getId())
                .orElseThrow(() -> new EntityNotFoundException("Project Idea Not Found."));

        List<ProjectPortfolio> projectPortfolios =
                projectPortfolioRepository.findByDevProfiles_Id(devProfile.getId());

        if (projectPortfolios.isEmpty()) {
            throw new EntityNotFoundException("No Project Portfolios found.");
        }

        // Dev Profile
        DevProfileResponse devProfileResponse = devProfileServiceImpl.mapToDevProfileResponse(devProfile);

        // Project Idea
        DevProfile currentUserDevProfile = devProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Current Dev Profile Not Found."));
        ProjectIdeaResponse projectIdeaResponse = ProjectIdeaResponse.builder()
                .projectIdeaId(projectIdea.getId())
                .projectIdeaName(projectIdea.getName())
                .description(projectIdea.getDescription())
                .status(projectIdea.getStatus())
                .dev_id(projectIdea.getDevProfile().getId()) // Owner Id
                .build();

        // Reacted Project IDs for this user
        Set<Long> reactedProjectPortfolioIds = user.getReactedProjectPortfolios()
                .stream()
                .map(ProjectPortfolio::getId)
                .collect(Collectors.toSet());

        // Project Portfolio List
        List<ProjectPortfolioResponseForProfileData> projectPortfolioResponses = projectPortfolios.stream()
                .map(p -> ProjectPortfolioResponseForProfileData.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .projectPicUrl(p.getProjectPicUrl())
                        .repoLink(p.getRepoLink())
                        .projectLink(p.getProjectLink())
                        .description(p.getDescription())
                        .reaction_count(
                                p.getReactedUsers() != null ? p.getReactedUsers().size() : 0
                        )
                        .reactedProjectPortfolios(
                                user.getReactedProjectPortfolios().stream() .map(ProjectPortfolio::getId) .toList()
                        )
                        .isOwner(
                                p.getOwner() != null &&
                                        p.getOwner().getId().equals(currentUserDevProfile.getId())
                        )
                        .build())
                .toList();

        return ApiResponse.builder()
                .success(1)
                .code(200)
                .message("Profile data fetched successfully.")
                .data(Map.of(
                        "devProfile", devProfileResponse,
                        "projectIdea", projectIdeaResponse,
                        "projectPortfolios", projectPortfolioResponses
                ))
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

}

