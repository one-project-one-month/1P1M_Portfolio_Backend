package com._p1m.portfolio.features.users.service.serviceImpl;

import com._p1m.portfolio.data.models.OAuthUser;
import com._p1m.portfolio.data.models.Role;
import com._p1m.portfolio.data.models.User;
import com._p1m.portfolio.data.repositories.OAuthUserRepository;
import com._p1m.portfolio.data.repositories.RoleRepository;
import com._p1m.portfolio.data.repositories.UserRepository;
import com._p1m.portfolio.features.users.service.AuthService;
import com._p1m.portfolio.security.JWT.JWTUtil;
import com._p1m.portfolio.security.OAuth2.Github.dto.response.GoogleOAuthResponse;
import com._p1m.portfolio.security.OAuth2.Google.dto.request.GoogleUserInfo;
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
