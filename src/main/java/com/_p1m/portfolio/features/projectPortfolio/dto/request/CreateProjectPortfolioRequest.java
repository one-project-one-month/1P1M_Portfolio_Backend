package com._p1m.portfolio.features.projectPortfolio.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateProjectPortfolioRequest(
    
    @NotBlank(message = "Project name cannot be empty or null.")
    String name,

    @NotBlank(message = "Project description cannot be empty or null.")
    @Size(max = 500, message = "Description cannot exceed 500 characters.")
    String description,

    @NotBlank(message = "Project link cannot be empty or null.")
    @Pattern(regexp = "^(https?://)?(www\\.)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$", message = "Invalid project link format.")
    String projectLink,

    @NotBlank(message = "Repository link cannot be empty or null.")
    @Pattern(regexp = "^(https?://)?(www\\.)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$", message = "Invalid repository link format.")
    String repoLink,

    @NotNull(message = "Language and tools list cannot be null.")
    @Size(min = 1, message = "At least one language or tool must be provided.")
    List<String> languageAndTools,

    @NotNull(message = "Developer emails cannot be null.")
    @Size(min = 1, message = "At least one developer email must be provided.")
    List<String> developerEmails
) {}

