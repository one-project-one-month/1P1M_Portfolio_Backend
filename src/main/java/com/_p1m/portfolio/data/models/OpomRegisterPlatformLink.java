package com._p1m.portfolio.data.models;

import com._p1m.portfolio.data.models.common.Auditable;
import com._p1m.portfolio.data.models.lookup.Platform;

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
public class OpomRegisterPlatformLink extends Auditable {
    @ManyToOne
    @JoinColumn(name = "opom_register_id")
    private OpomRegister opomRegister;

    @ManyToOne
    @JoinColumn(name = "platform_id")
    private Platform platform;
    
    @Column(nullable = false)
    private String link;
}
