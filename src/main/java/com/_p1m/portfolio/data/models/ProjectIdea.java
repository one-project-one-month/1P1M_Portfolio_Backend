package com._p1m.portfolio.data.models;

import java.util.HashSet;
import java.util.Set;

import com._p1m.portfolio.data.models.common.Auditable;
import com._p1m.portfolio.data.models.lookup.ProjectType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
public class ProjectIdea extends Auditable {
	@Column(nullable = false)
    private String name;
    
    @Lob
    private String description;

    private boolean approveStatus;

    @ManyToOne
    @JoinColumn(name = "dev_id", referencedColumnName = "id")
    private DevProfile devProfile;

    @ManyToMany
    @JoinTable(
        name = "project_platform",
        joinColumns = @JoinColumn(name = "project_idea_id"),
        inverseJoinColumns = @JoinColumn(name = "project_type_id")
    )
    private Set<ProjectType> projectTypes = new HashSet<>();

    @ManyToMany(mappedBy = "reactedProjectIdeas")
    private Set<User> reactedUsers = new HashSet<>();
}

