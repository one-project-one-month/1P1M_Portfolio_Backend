package com._p1m.portfolio.features.users.service.serviceImpl;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.data.models.OAuthUser;
import com._p1m.portfolio.data.models.Role;
import com._p1m.portfolio.data.models.User;
import com._p1m.portfolio.data.repositories.OAuthUserRepository;
import com._p1m.portfolio.data.repositories.RoleRepository;
import com._p1m.portfolio.data.repositories.UserRepository;
import com._p1m.portfolio.features.users.dto.request.GoogleOAuthRequest;
import com._p1m.portfolio.features.users.service.AuthService;
import com._p1m.portfolio.security.JWT.JWTUtil;
import com._p1m.portfolio.security.OAuth2.Github.dto.request.GithubUserInfo;
import com._p1m.portfolio.security.OAuth2.Github.dto.response.GithubOAuthResponse;
import com._p1m.portfolio.security.OAuth2.Google.dto.request.GoogleUserInfo;
import com._p1m.portfolio.security.OAuth2.Google.dto.response.GoogleOAuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final OAuthUserRepository oAuthUserRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    private static final long TOKEN_VALID_TIME_MILLIS = 12 * 60 * 60 * 1000L;

    @Override
    public GoogleOAuthResponse processGoogleOAuth(GoogleUserInfo googleUserInfo) {

        Optional<User> existingUser = userRepository.findByEmail(googleUserInfo.getEmail());
        if(existingUser.isPresent()){
            User user = existingUser.get();

            // Check if the User has already registered via Google
            Optional<OAuthUser> existingOAuthUser = oAuthUserRepository.findByUserAndProvider(user, "GOOGLE");
            if(existingOAuthUser.isPresent()){
                // User exists and was registered via Google - allow login
                String jwtToken = jwtUtil.generateToken(
                        user.getEmail() != null ? user.getEmail() : user.getUsername(),
                        TOKEN_VALID_TIME_MILLIS
                );
                GoogleOAuthResponse googleOAuthResponse= new GoogleOAuthResponse();
                googleOAuthResponse.setUser(mapUserToResponse(user));
                googleOAuthResponse.setProfile_picture(googleUserInfo.getPicture());
                googleOAuthResponse.setToken(jwtToken);
                googleOAuthResponse.setNewUser(false);
                return googleOAuthResponse;
            } else {
                // User exists but registered via different provider (GitHub)
                throw new IllegalArgumentException("We have an account that is already registered with different Provider.");
            }
        }

        // Create a new user (no existing-user checks here)
        User user = createUserFromGoogle(googleUserInfo);

        // Create OAuthUser linked to this new user
        createGoogleOAuth(user, googleUserInfo);

        // Generate JWT Token
        String jwtToken =jwtUtil.generateToken(user.getEmail() , TOKEN_VALID_TIME_MILLIS);

        // Building Response
        GoogleOAuthResponse response= new GoogleOAuthResponse();
        response.setUser(mapUserToResponse(user));
        response.setToken(jwtToken);
        response.setNewUser(true);

        return response;
    }

    @Override
    public GithubOAuthResponse processGithubOAuth(GithubUserInfo githubUserInfo) {

        Optional<User> existingUser = userRepository.findByEmail(githubUserInfo.getEmail());

        if(existingUser.isPresent()){
            User user = existingUser.get();

            // Check if the User has already registered via Github
            Optional<OAuthUser> existingOAuthUser = oAuthUserRepository.findByUserAndProvider(user, "GITHUB");

            if(existingOAuthUser.isPresent()){
                // User exists and was registered via GitHub - allow login
                String jwtToken = jwtUtil.generateToken(
                        user.getEmail() != null ? user.getEmail() : user.getUsername(),
                        TOKEN_VALID_TIME_MILLIS
                );

                GithubOAuthResponse githubOAuthResponse = new GithubOAuthResponse();
                githubOAuthResponse.setUser(mapUserToResponse(user));
                githubOAuthResponse.setProfile_picture(githubUserInfo.getAvatarUrl());
                githubOAuthResponse.setToken(jwtToken);
                githubOAuthResponse.setNewUser(false);

                return githubOAuthResponse;
            } else {
                // User exists but registered via different provider (Google)
                throw new IllegalArgumentException("We have an account that is already registered with different Provider.");
            }
        } else {
            // Create a new user (only reaches here if no existing user)
            User user = createUserFromGithub(githubUserInfo);

            // Create OAuthUser linked to this new user
            createGithubOAuth(user, githubUserInfo);

            // Generate JWT token
            String jwtToken = jwtUtil.generateToken(
                    user.getEmail() != null ? user.getEmail() : user.getUsername(),
                    TOKEN_VALID_TIME_MILLIS
            );

            // Build response
            GithubOAuthResponse response = new GithubOAuthResponse();
            response.setUser(mapUserToResponse(user));
            response.setToken(jwtToken);
            response.setNewUser(true);

            return response;
        }
    }

    private void updateGithubOAuth(OAuthUser oAuthUser, GithubUserInfo githubUser) {
        oAuthUser.setProfilePicture(githubUser.getAvatarUrl());
        oAuthUser.setProvider("GITHUB");
        oAuthUser.setEmailVerified(githubUser.getEmailVerified());
        oAuthUserRepository.save(oAuthUser);

        // Also update user info if available
        User user = oAuthUser.getUser();
        if (githubUser.getName() != null && !githubUser.getName().equals(user.getUsername())) {
            // You might want to update user's display name or other fields
        }
        userRepository.save(user);
    }

    private void createGithubOAuth(User user, GithubUserInfo githubUser) {
        OAuthUser oAuthUser = new OAuthUser();
        oAuthUser.setProvider("GITHUB");
        oAuthUser.setProviderUserId(githubUser.getGithubId().toString());
        oAuthUser.setProfilePicture(githubUser.getAvatarUrl());
        oAuthUser.setEmailVerified(githubUser.getEmailVerified());
        oAuthUser.setUser(user);
        oAuthUserRepository.save(oAuthUser);
    }

    private User createUserFromGithub(GithubUserInfo githubUserInfo) {
        User user = new User();

        // Use email if available, otherwise use GitHub username + @github.local
        if (githubUserInfo.getEmail() != null) {
            user.setEmail(githubUserInfo.getEmail());
            user.setUsername(githubUserInfo.getEmail().split("@")[0]);
        } else {
            // No email available, use GitHub username
            user.setEmail(githubUserInfo.getLogin() + "@github.local"); // Temporary email
            user.setUsername(githubUserInfo.getLogin());
        }

        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));

        // Fetch Role entity from DB
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("USER role not found"));
        user.setRole(userRole);

        return userRepository.save(user);
    }

    private void updateUserOAuth(OAuthUser oAuthUser, GoogleUserInfo googleUser) {
        oAuthUser.setProfilePicture(googleUser.getPicture());
        if (Boolean.TRUE.equals(googleUser.getEmailVerified())) {
            oAuthUser.setEmailVerified(true);
        }
        oAuthUserRepository.save(oAuthUser);
    }

    private void createGoogleOAuth(User user, GoogleUserInfo googleUser) {
        OAuthUser oAuthUser = new OAuthUser();
        oAuthUser.setProvider("GOOGLE");
        oAuthUser.setProviderUserId(googleUser.getGoogleId());
        oAuthUser.setProfilePicture(googleUser.getPicture());
        oAuthUser.setEmailVerified(Boolean.TRUE.equals(googleUser.getEmailVerified()));
        oAuthUser.setUser(user);
        oAuthUserRepository.save(oAuthUser);
    }

    private User createUserFromGoogle(GoogleUserInfo googleUserInfo) {
        User user = new User();
        user.setEmail(googleUserInfo.getEmail());
        user.setUsername(googleUserInfo.getEmail().split("@")[0]);
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));

        // Fetch Role entity from DB
        // In createUserFromGoogle method, change:
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("USER role not found"));
        user.setRole(userRole);
        return userRepository.save(user);
    }

    private Object mapUserToResponse(User user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("email", user.getEmail());
        userResponse.put("username", user.getUsername());
        userResponse.put("role", user.getRole());
        return userResponse;
    }
}
