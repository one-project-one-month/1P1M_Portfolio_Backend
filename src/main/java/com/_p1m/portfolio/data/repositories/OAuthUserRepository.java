package com._p1m.portfolio.data.repositories;

import com._p1m.portfolio.data.models.OAuthUser;
import com._p1m.portfolio.data.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuthUserRepository extends JpaRepository<OAuthUser , Long> {
    Optional<OAuthUser> findByProviderAndProviderUserId(String provider, String providerUserId);
    Optional<OAuthUser> findByUser(User user);

    Optional<OAuthUser> findByUserAndProvider(User user, String github);
}
