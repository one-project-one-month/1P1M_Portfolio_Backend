package com._p1m.portfolio.data.repositories;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com._p1m.portfolio.data.models.ProjectPortfolio;

@Repository
public interface ProjectPortfolioRepository extends JpaRepository<ProjectPortfolio, Long>, JpaSpecificationExecutor<ProjectPortfolio> {
    boolean existsByName(@NotBlank(message = "Project name cannot be empty or null.") String name);
}
