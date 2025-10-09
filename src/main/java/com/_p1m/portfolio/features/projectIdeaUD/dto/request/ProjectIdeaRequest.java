package com._p1m.portfolio.features.projectIdeaUD.dto.request;

import com._p1m.portfolio.data.models.DevProfile;
import com._p1m.portfolio.data.models.User;
import com._p1m.portfolio.data.models.lookup.ProjectType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class ProjectIdeaRequest {

    @NotBlank(message = "Name is required and cannot be blank.")
    private String name;

    @NotBlank(message = "Description is required and cannot be blank.")
    private String description;


    @NotNull(message = "Approval status is required.")
    private boolean approveStatus;


    @NotNull(message = "DevProfile is required.")
    @Valid
    private DevProfile devProfile;

    @NotEmpty(message = "At least one ProjectType is required.")
    private Set<ProjectType> projectTypes = new HashSet<>();

    private Set<User> reactedUsers = new HashSet<>();


}