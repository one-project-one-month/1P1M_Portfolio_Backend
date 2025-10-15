package com._p1m.portfolio.features.approvedProjectIdeas.dto.response;

import java.util.List;

public record ApprovedIdeaResponse(
        Long id,
        String name,
        String description,
        String devName,
        int reactionCount,
        String status,
        List<String> projectTypes
) {}