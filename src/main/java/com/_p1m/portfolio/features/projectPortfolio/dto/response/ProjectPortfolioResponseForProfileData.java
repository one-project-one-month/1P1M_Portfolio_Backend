package com._p1m.portfolio.features.projectPortfolio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectPortfolioResponseForProfileData {
    Long id;
    String name;
    String projectPicUrl;
    String description;
    String projectLink;
    String repoLink;
    private long reaction_count;
    AssignedDevs assignedDevs;
    private List<Long> reactedProjectPortfolios;
    ProjectPortfolioDetails projectPortfolioDetails;
    private List<String> projectTypes;
    private boolean isOwner;
}
