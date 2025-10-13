package com._p1m.portfolio.data.repositories;

import com._p1m.portfolio.data.models.OpomRegister;
import com._p1m.portfolio.data.models.ProjectPortfolio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OpomRegisterRepository extends JpaRepository<OpomRegister, Long> {

    // For duplicate checks
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    Page<OpomRegister> findByRoleIgnoreCase(String role, Pageable pageable);
    // For paginated admin list
    Page<OpomRegister> findAllByIsDeletedFalse(Pageable pageable);

    // For update / soft delete
    Optional<OpomRegister> findByIdAndIsDeletedFalse(Long id);

    Page<OpomRegister> findAll(Specification<ProjectPortfolio> spec, Pageable pageable);
}


