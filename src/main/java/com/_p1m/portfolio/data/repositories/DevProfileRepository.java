package com._p1m.portfolio.data.repositories;

import java.util.List;
import java.util.Optional;
import com._p1m.portfolio.data.models.User;
import com._p1m.portfolio.data.models.lookup.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com._p1m.portfolio.data.models.DevProfile;

@Repository
public interface DevProfileRepository extends JpaRepository<DevProfile, Long> {
	Optional<DevProfile> findByUserEmail(String email);
    boolean existsByUser(User user);
    Optional<DevProfile> findByName(String name);
    @Query("SELECT d FROM DevProfile d JOIN d.techStacks t WHERE t = :stack")
    List<DevProfile> findByTechStacks(Optional<TechStack> stack);

//    Optional<DevProfile> findByTechStacks(String techStackName);
}
