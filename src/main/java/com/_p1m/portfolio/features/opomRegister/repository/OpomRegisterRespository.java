package com._p1m.portfolio.features.opomRegister.repository;

import com._p1m.portfolio.model.OpomRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface OpomRegisterRespository extends JpaRepository<OpomRegister , Long> {
    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}
