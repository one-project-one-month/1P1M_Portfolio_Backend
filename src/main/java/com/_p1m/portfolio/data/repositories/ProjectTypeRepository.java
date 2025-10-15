package com._p1m.portfolio.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com._p1m.portfolio.data.models.lookup.ProjectType;

import java.util.Optional;

@Repository
public interface ProjectTypeRepository extends JpaRepository<ProjectType, Long> {
    Optional<ProjectType> findByName(String typeName);
}
