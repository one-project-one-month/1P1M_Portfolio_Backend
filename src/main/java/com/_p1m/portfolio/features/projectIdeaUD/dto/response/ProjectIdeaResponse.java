package com._p1m.portfolio.features.projectIdeaUD.dto.response;

import com._p1m.portfolio.data.models.DevProfile;
import com._p1m.portfolio.data.models.User;
import com._p1m.portfolio.data.models.lookup.ProjectType;

import java.util.Set;

public class ProjectIdeaResponse {
    private String name;
    private String description;
    private boolean approveStatus;
    private DevProfile devProfile;
    private Set<ProjectType> projectType;
    private Set<User> reactedUsers;
}