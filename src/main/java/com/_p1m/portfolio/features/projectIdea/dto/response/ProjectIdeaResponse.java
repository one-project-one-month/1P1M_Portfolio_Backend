package com._p1m.portfolio.features.projectIdea.dto.response;

import com._p1m.portfolio.data.models.DevProfile;
import com._p1m.portfolio.data.models.User;
import com._p1m.portfolio.data.models.lookup.ProjectType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class ProjectIdeaResponse {
    private String name;
    private String description;
    private boolean approveStatus;
    private DevProfile devProfile;
    private Set<ProjectType> projectType;
    private Set<User> reactedUsers;
}
