package com._p1m.portfolio.data.models;

import java.util.HashSet;
import java.util.Set;

import com._p1m.portfolio.data.models.common.Auditable;
import com._p1m.portfolio.data.models.lookup.TechStack;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
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
public class DevProfile extends Auditable {
	@Column(nullable = false)
    private String name;
    
    private String profilePictureUrl;
    
    private String github;
    
    private String linkedId;
    
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    
    @ManyToMany
    @JoinTable(
        name = "dev_techstack",
        joinColumns = @JoinColumn(name = "dev_id"),
        inverseJoinColumns = @JoinColumn(name = "techstack_id")
    )
    private Set<TechStack> techStacks = new HashSet<>();
}

