package com._p1m.portfolio.features.projectIdea.dto.response;

import com._p1m.portfolio.data.enums.ProjectIdeaStatus;
import lombok.*;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectIdeaResponse {
    private long projectIdeaId;
    private String projectIdeaName;
    private ProjectIdeaStatus status;
    private String description;
    private long dev_id;  //(Owner Id)
}
