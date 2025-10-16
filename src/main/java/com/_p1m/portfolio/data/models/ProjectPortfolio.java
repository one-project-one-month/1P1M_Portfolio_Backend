package com._p1m.portfolio.data.models;

import java.util.HashSet;
import java.util.Set;

import com._p1m.portfolio.data.models.common.Auditable;
import com._p1m.portfolio.data.models.lookup.LanguageAndTools;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProjectPortfolio extends Auditable {
	@Column(nullable = false)
    private String name;
    
    private String projectPicUrl;
    
    @Lob
    private String description;
    
    private String projectLink;
    
    @Column(nullable = false)
    private String repoLink;
    
    @ManyToMany(mappedBy = "reactedProjectPortfolios")
    private Set<User> reactedUsers = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "project_assignment",
        joinColumns = @JoinColumn(name = "project_portfolio_id"),
        inverseJoinColumns = @JoinColumn(name = "dev_profiles_id")
    )
    private Set<DevProfile> devProfiles = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "project_portfolio_details",
        joinColumns = @JoinColumn(name = "portfolio_id"),
        inverseJoinColumns = @JoinColumn(name = "language_and_tools_id")
    )
    private Set<LanguageAndTools> languageAndTools = new HashSet<>();
}