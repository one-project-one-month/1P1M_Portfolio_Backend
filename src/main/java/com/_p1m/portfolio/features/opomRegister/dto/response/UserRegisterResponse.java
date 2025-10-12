package com._p1m.portfolio.features.opomRegister.dto.response;

import com._p1m.portfolio.common.constant.Status;
import lombok.Data;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserRegisterResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String github_url;
    private String telegram_username;
    private String role;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<PlatformInfo> platforms;

    @Data
    public static class PlatformInfo {
        private Long platformId;
        private String platformName;
        private String link;
    }
}


