package com._p1m.portfolio.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com._p1m.portfolio.data.models.DevProfile;

@Repository
public interface DevProfileRepository extends JpaRepository<DevProfile, Long> {
}
