package com._p1m.portfolio.features.devProfile.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDevProfileRequest {

    @NotBlank(message = "Name is required.")
    private String name;

    @URL(message = "Please provide a valid URL for the profile picture.")
    private String profilePictureUrl;

    @URL(message = "Please provide a valid GitHub profile URL.")
    private String github;

    @URL(message = "Please provide a valid LinkedIn profile URL.")
    private String linkedIn;

    @NotEmpty(message = "At least one tech stack is required.")
    private Set<String> techStacks;
}