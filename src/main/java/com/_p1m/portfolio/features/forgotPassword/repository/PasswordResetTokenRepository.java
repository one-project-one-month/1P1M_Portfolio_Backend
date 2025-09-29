package com._p1m.portfolio.features.forgotPassword.repository;

import com._p1m.portfolio.data.models.PasswordResetToken;
import com._p1m.portfolio.data.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUser(User user);
}
