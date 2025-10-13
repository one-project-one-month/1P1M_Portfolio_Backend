package com._p1m.portfolio.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com._p1m.portfolio.data.models.OpomRegisterPlatformLink;

import java.util.List;

@Repository
public interface OpomRegisterPlatformLinkRepository extends JpaRepository<OpomRegisterPlatformLink, Long> {
    List<OpomRegisterPlatformLink> findByOpomRegisterId(Long id);
}
