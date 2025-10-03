package com._p1m.portfolio.startup;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com._p1m.portfolio.data.enums.ROLE;
import com._p1m.portfolio.data.models.Role;
import com._p1m.portfolio.data.repositories.RoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        for (ROLE roleEnum : ROLE.values()) {
            String roleName = roleEnum.name();
            roleRepository.findByName(roleName).orElseGet(() -> {
                Role role = Role.builder()
                        .name(roleName)
                        .build();
                log.info("Initializing role: {} (code: {})", roleName, roleEnum.getValue());
                return roleRepository.save(role);
            });
        }
    }
}



