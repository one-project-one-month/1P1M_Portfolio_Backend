package com._p1m.portfolio.features.projectIdea.dto.request;

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
public class ProjectIdeaRequest {

    @NotBlank(message = "Project Name is required.")
    private String projectName;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters.")
    private String description;

    @NotEmpty(message = "At least One Project Type is required.")
    private List<String> projectType;
}
