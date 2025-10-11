package com._p1m.portfolio.features.devProfile.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Getter
@Setter
@Data
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

    @Size(max = 1000, message = "About section cannot exceed 1000 characters.")
    private String aboutDev;

    @NotEmpty(message = "At least one tech stack is required.")
    private List<String> techStacks;
}