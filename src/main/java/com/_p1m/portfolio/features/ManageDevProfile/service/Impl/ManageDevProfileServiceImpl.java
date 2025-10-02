package com._p1m.portfolio.features.ManageDevProfile.service.Impl;

import com._p1m.portfolio.data.models.DevProfile;
import com._p1m.portfolio.features.ManageDevProfile.repo.ManageDevProfileRepo;
import com._p1m.portfolio.features.ManageDevProfile.service.ManageDevProfileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManageDevProfileServiceImpl implements ManageDevProfileService {

    private final ManageDevProfileRepo manageDevProfileRepo;

    public ManageDevProfileServiceImpl(ManageDevProfileRepo manageDevProfileRepo) {
        this.manageDevProfileRepo = manageDevProfileRepo;
    }


    @Override
    public Page<DevProfile> findAllDevPf(Pageable pageable) {
        return manageDevProfileRepo.findAll(pageable);
    }

    @Override
    public DevProfile findDevByName(String name) {
        return manageDevProfileRepo.findByName(name);
    }

    @Override
    public DevProfile findByLinkedIn(String url) {
        return manageDevProfileRepo.findByLinkedIn(url);
    }

    @Override
    public DevProfile findDevByGithub(String githubURL) {
        return manageDevProfileRepo.findByGithub(githubURL);
    }

    @Override
    public void deleteDevPf(Long id) {
        manageDevProfileRepo.deleteById(id);
    }
}
