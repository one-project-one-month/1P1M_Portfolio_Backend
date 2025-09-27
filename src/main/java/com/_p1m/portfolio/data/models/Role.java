package com._p1m.portfolio.data.models;

import com._p1m.portfolio.data.models.common.Auditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Role extends Auditable {
	@Column(nullable = false)
    private String name;
}
