package com._p1m.portfolio.startup;

import com._p1m.portfolio.data.enums.Platform;
import com._p1m.portfolio.data.enums.ROLE;
import com._p1m.portfolio.data.models.Role;
import com._p1m.portfolio.data.repositories.PlatformRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class PlatformInitializer implements CommandLineRunner {

    private final PlatformRepository platformRepository;


    @Override
    public void run(String... args) throws Exception {
        for (Platform platformEnum : Platform.values()) {
            String platformName = platformEnum.name();
            platformRepository.findByName(platformName).orElseGet(() -> {
                com._p1m.portfolio.data.models.lookup.Platform platform = com._p1m.portfolio.data.models.lookup.Platform.builder()
                        .name(platformName)
                        .build();
                log.info("Initializing platform: {} (code: {})", platformName, platformEnum.getValue());
                return platformRepository.save(platform);
            });
        }
    }
}
