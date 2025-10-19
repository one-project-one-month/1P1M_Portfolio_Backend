package com._p1m.portfolio.features.projectIdea.dto.response;

import com._p1m.portfolio.data.enums.ProjectIdeaStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectIdeaListResponse {
    private long id;
    private long dev_id;
    private String projectName;
    private String description;
    private String profilePictureUrl;
    private String devName;
    private long reaction_count;
    private List<String> projectTypes;
    private ProjectIdeaStatus status;
}
