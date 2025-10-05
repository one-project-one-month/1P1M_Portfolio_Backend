package com._p1m.portfolio.config.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Getter
@Component
public class AdminEmailConfig {
    private List<String> adminEmails;

    @PostConstruct
    public void loadAdminEmails() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = getClass().getResourceAsStream("/jsonFiles/admins.json")) {
            AdminEmailsWrapper wrapper = objectMapper.readValue(inputStream, AdminEmailsWrapper.class);
            this.adminEmails = wrapper.getAdmins();
        }
    }

    public boolean isAdmin(String email) {
        return adminEmails != null && adminEmails.contains(email);
    }

    // Wrapper class for JSON
    @Setter
    @Getter
    public static class AdminEmailsWrapper {
        private List<String> admins;

    }
}
