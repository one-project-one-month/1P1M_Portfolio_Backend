package com._p1m.portfolio.data.models;

import com._p1m.portfolio.data.models.common.Auditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
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
public class TeamMembers extends Auditable {
    @ManyToOne
    @JoinColumn(name = "dev_id")
    private DevProfile devProfile;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;
    
    @Column(nullable = false)
    private String roleInTeam;
}