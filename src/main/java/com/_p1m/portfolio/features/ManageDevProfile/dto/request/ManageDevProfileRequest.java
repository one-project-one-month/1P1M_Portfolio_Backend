package com._p1m.portfolio.features.ManageDevProfile.dto.request;

import com._p1m.portfolio.data.models.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import java.util.Set;

public class ManageDevProfileRequest {
    @NotBlank(message = "Name is required.")
    private String name;

    @URL(message = "Please provide a valid URL for the profile picture.")
    private String profilePictureUrl;

    @URL(message = "Please provide a valid GitHub profile URL.")
    private String github;

    @URL(message = "Please provide a valid LinkedIn profile URL.")
    private String linkedIn;

    @NotEmpty(message = "User Required")
    private User user;

    @NotEmpty(message = "At least one tech stack is required.")
    private Set<String> techStacks;
}
