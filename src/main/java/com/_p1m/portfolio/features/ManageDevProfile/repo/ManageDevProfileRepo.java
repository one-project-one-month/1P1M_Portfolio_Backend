    package com._p1m.portfolio.features.ManageDevProfile.repo;
    
    import com._p1m.portfolio.data.models.DevProfile;
    import jakarta.transaction.Transactional;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Modifying;
    import org.springframework.data.jpa.repository.Query;

    public interface ManageDevProfileRepo extends JpaRepository<DevProfile,Long> {
        @Query("SELECT d FROM DevProfile d WHERE d.name = ?1")
         DevProfile findByName(String name);
        @Query("SELECT d FROM DevProfile d WHERE d.linkedIn = ?1")
         DevProfile findByLinkedIn(String linkedIn);
        @Query("SELECT d FROM DevProfile d WHERE d.github = ?1")
         DevProfile findByGithub(String url);
    //    public DevProfile findByTechStack(String tech);
    }
