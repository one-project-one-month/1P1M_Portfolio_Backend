package com._p1m.portfolio.features.approvedProjectIdeas.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateApprovedIdeaRequest(
        String name,

        @Size(max = 500, message = "Description cannot exceed 500 characters.")
        String description
) {}