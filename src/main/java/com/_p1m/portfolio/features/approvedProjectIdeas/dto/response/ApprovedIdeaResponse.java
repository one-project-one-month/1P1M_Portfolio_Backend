package com._p1m.portfolio.features.approvedProjectIdeas.dto.response;

import com._p1m.portfolio.common.constant.Status;
import com._p1m.portfolio.data.enums.ProjectIdeaStatus;
import lombok.Builder;

import java.util.List;

@Builder
public record ApprovedIdeaResponse(
        Long id,
        String name,
        String description,
        String devName,
        Long dev_id,
        int reactionCount,
        ProjectIdeaStatus status,
        String profilePictureUrl,
        List<String> projectTypes,
        List<Long> reactedProjects
) {}