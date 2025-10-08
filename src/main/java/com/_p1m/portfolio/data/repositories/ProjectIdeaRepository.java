package com._p1m.portfolio.data.repositories;

import com._p1m.portfolio.data.models.DevProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com._p1m.portfolio.data.models.ProjectIdea;

import java.util.List;

@Repository
public interface ProjectIdeaRepository extends JpaRepository<ProjectIdea, Long> {
    Boolean existsByName(String name);
    Boolean existsByDescription(String description);

    @Query("SELECT pi FROM ProjectIdea pi WHERE pi.devProfile = :developer")
    List<ProjectIdea> findByDevProfile(@Param("developer") DevProfile developer);

    List<ProjectIdea> findByName(String name);
}
