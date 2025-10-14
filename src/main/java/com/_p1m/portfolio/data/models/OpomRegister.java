package com._p1m.portfolio.data.models;

import com._p1m.portfolio.common.constant.Status;
import com._p1m.portfolio.data.models.common.Auditable;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OpomRegister extends Auditable {
	@Column(nullable = false)
    private String name;
    
	@Column(nullable = false)
    private String email;
    
	@Column(nullable = false)
    private String phone;
    
    private String telegram_username;
    
    @Column(nullable = false)
    private String role;
    
    @ManyToOne
    @JoinColumn(name = "dev_profiles_id", referencedColumnName = "id")
    private DevProfile devProfile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private boolean isDeleted = false;
    }
