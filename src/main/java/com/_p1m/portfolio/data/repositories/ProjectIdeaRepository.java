package com._p1m.portfolio.data.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com._p1m.portfolio.data.models.ProjectIdea;
import java.util.Optional;

@Repository
public interface ProjectIdeaRepository extends JpaRepository<ProjectIdea, Long>, JpaSpecificationExecutor<ProjectIdea> {
    Optional<ProjectIdea> findByIdAndApproveStatus(Long id, boolean approveStatus);
    Page<ProjectIdea> findByApproveStatus(boolean approveStatus, Pageable pageable);
}
