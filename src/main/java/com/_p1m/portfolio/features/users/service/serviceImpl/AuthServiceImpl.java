package com._p1m.portfolio.features.users.service.serviceImpl;

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

        try{
            Optional<OAuthUser> existingOAuthUser =
                    oAuthUserRepository.findByProviderAndProviderUserId("GOOGLE" , googleUserInfo.getGoogleId());

            User user;
            boolean isNewUser = false;

            // Check If OAuthUser Present or Not
            if(existingOAuthUser.isPresent()){
                user =existingOAuthUser.get().getUser();
                updateUserOAuth(existingOAuthUser.get() , googleUserInfo);
            } else {
                Optional<User> existingUser = userRepository.findByEmail(googleUserInfo.getEmail());
                if(existingUser.isPresent()){
                    user = existingUser.get();
                } else {
                    user = createUserFromGoogle(googleUserInfo);
                    isNewUser = true;
                }
                createUserOAuth(user , googleUserInfo);
            }

            // Generate JWT Token
            String jwtToken =jwtUtil.generateToken(user.getEmail() , TOKEN_VALID_TIME_MILLIS);

            // Building Response
            GoogleOAuthResponse respons = new GoogleOAuthResponse();
            respons.setUser(mapUserToResponse(user));
            respons.setToken(jwtToken);
            respons.setNewUser(isNewUser);

            return respons;

        }catch (Exception e) {
            throw new RuntimeException("Failed to process Google OAuth: " + e.getMessage(), e);
        }
    }

    @Override
    public GithubOAuthResponse processGithubOAuth(GithubUserInfo githubUserInfo) {
        try {
            Optional<OAuthUser> existingOAuthUser =
                    oAuthUserRepository.findByProviderAndProviderUserId("GITHUB", githubUserInfo.getGithubId().toString());

            User user;
            boolean isNewUser = false;

            // Check If OAuthUser Present or Not
            if (existingOAuthUser.isPresent()) {
                user = existingOAuthUser.get().getUser();
                updateGithubOAuth(existingOAuthUser.get(), githubUserInfo);
            } else {
                // Check if user exists by email (if email is available)
                if (githubUserInfo.getEmail() != null) {
                    Optional<User> existingUser = userRepository.findByEmail(githubUserInfo.getEmail());
                    if (existingUser.isPresent()) {
                        user = existingUser.get();
                    } else {
                        user = createUserFromGithub(githubUserInfo);
                        isNewUser = true;
                    }
                } else {
                    // No email available, create new user with GitHub username
                    user = createUserFromGithub(githubUserInfo);
                    isNewUser = true;
                }
                createGithubOAuth(user, githubUserInfo);
            }

            // Generate JWT Token
            String jwtToken = jwtUtil.generateToken(
                    user.getEmail() != null ? user.getEmail() : user.getUsername(),
                    TOKEN_VALID_TIME_MILLIS
            );

            // Building Response
            return GithubOAuthResponse.builder()
                    .user(mapUserToResponse(user))
                    .token(jwtToken)
                    .newUser(isNewUser)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to process GitHub OAuth: " + e.getMessage(), e);
        }
    }

    private void updateGithubOAuth(OAuthUser oAuthUser, GithubUserInfo githubUser) {
        oAuthUser.setProfilePicture(githubUser.getAvatarUrl());
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

    private void createUserOAuth(User user, GoogleUserInfo googleUser) {
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
