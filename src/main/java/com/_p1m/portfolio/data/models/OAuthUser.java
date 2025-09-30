package com._p1m.portfolio.data.models;

import com._p1m.portfolio.data.models.common.Auditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class OAuthUser extends Auditable {
//	@Column(nullable = false)
//	private String username;
//
//	@Column(nullable = false)
//	private String email;
//
//	@Column(nullable = false)
//	private String password;

    private String provider;
    private String providerUserId;
    private String profilePicture;
    private boolean emailVerified;
	
	@OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}