package com._p1m.portfolio.features.opomRegister.repository;

import com._p1m.portfolio.common.constant.Status;
import com._p1m.portfolio.data.models.OpomRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface OpomRegisterRespository extends JpaRepository<OpomRegister , Long> {
    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Optional<OpomRegister> findByIdAndStatus(Long id, Status status);
}
