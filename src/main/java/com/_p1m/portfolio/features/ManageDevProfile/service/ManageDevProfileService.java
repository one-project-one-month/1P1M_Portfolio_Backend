package com._p1m.portfolio.features.ManageDevProfile.service;

import com._p1m.portfolio.data.models.DevProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ManageDevProfileService {
   Page<DevProfile> findAllDevPf(Pageable pageable);

   DevProfile findDevByName(String name);

   DevProfile findByLinkedIn(String url);

   DevProfile findDevByGithub(String githubURL);

   void deleteDevPf(Long id);
}
