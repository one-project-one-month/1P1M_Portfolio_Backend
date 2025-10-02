    package com._p1m.portfolio.features.ManageDevProfile.repo;
    
    import com._p1m.portfolio.data.models.DevProfile;
    import jakarta.transaction.Transactional;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Modifying;
    import org.springframework.data.jpa.repository.Query;

    public interface ManageDevProfileRepo extends JpaRepository<DevProfile,Long> {
         DevProfile findByName(String name);
         DevProfile findByLinkedIn(String linkedIn);
         DevProfile findByGithub(String url);
    //    public DevProfile findByTechStack(String tech);
    }
