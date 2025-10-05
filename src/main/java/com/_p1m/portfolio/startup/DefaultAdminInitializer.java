package com._p1m.portfolio.startup;

import com._p1m.portfolio.data.enums.ROLE;
import com._p1m.portfolio.data.models.Role;
import com._p1m.portfolio.data.models.User;
import com._p1m.portfolio.data.repositories.RoleRepository;
import com._p1m.portfolio.data.repositories.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class DefaultAdminInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    @Value("${default.admin.password}")
    private String defaultAdminPassword;

    private static final String ADMIN_FILE_PATH = "/jsonFiles/admins.json";

    @Override
    public void run(String... args) {
        try (InputStream inputStream = getClass().getResourceAsStream(ADMIN_FILE_PATH)) {
            if (inputStream == null) {
                log.warn("Admin JSON file not found at: {}", ADMIN_FILE_PATH);
                return;
            }

            JsonNode rootNode = objectMapper.readTree(inputStream);
            JsonNode adminsNode = rootNode.get("admins");

            if (adminsNode == null || !adminsNode.isArray()) {
                log.warn("Invalid admins.json structure.");
                return;
            }

            Role adminRole = roleRepository.findByName(ROLE.ADMIN.name())
                    .orElseThrow(() -> new IllegalStateException("Admin role not found. Please initialize roles first."));

            for (JsonNode adminNode : adminsNode) {
                String email = adminNode.asText().trim();
                if (email.isEmpty()) continue;

                userRepository.findByEmail(email).ifPresentOrElse(
                        user -> log.info("Admin already exists: {}", email),
                        () -> {
                            User admin = User.builder()
                                    .username(email.substring(0, email.indexOf('@')))
                                    .email(email)
                                    .password(passwordEncoder.encode(defaultAdminPassword))
                                    .role(adminRole)
                                    .build();
                            userRepository.save(admin);
                            log.info("Created default admin account: {}", email);
                        }
                );
            }
        } catch (Exception e) {
            log.error("Failed to initialize default admins: {}", e.getMessage(), e);
        }
    }
}
