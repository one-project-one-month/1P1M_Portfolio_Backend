package com._p1m.portfolio.data.models;

import java.time.LocalDate;

import com._p1m.portfolio.data.enums.ProjectStatus;
import com._p1m.portfolio.data.models.common.Auditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.PastOrPresent;
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
public class Team extends Auditable {
	@Column(nullable = false)
    private String teamName;

    @Enumerated(EnumType.STRING)
    private ProjectStatus projectStatus;
    
    @PastOrPresent
    private LocalDate startDate;

    @FutureOrPresent
    private LocalDate endDate;
    
    @Column(nullable = false)
    private String contactLink;

    @Lob
    private String description;
    
    @OneToOne
    @JoinColumn(name = "project_idea_id", referencedColumnName = "id")
    private ProjectIdea projectIdea;
}

