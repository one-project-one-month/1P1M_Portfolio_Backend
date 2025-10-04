package com._p1m.portfolio.data.models;

import java.util.HashSet;
import java.util.Set;

import com._p1m.portfolio.data.models.common.Auditable;

import jakarta.persistence.*;
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
@Table(name="users")
public class User extends Auditable {
	@Column(nullable = false)
    private String username;
    
	@Column(nullable = false)
    private String email;
    
	@Column(nullable = false)
    private String password;
    
    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;
    
    @ManyToMany
    @JoinTable(
        name = "project_idea_reaction",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "project_idea_id")
    )
    private Set<ProjectIdea> reactedProjectIdeas = new HashSet<>();
}

