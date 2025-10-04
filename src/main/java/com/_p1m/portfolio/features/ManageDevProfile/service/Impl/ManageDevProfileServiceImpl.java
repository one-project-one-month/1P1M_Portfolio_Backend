package com._p1m.portfolio.features.ManageDevProfile.service.Impl;

import com._p1m.portfolio.data.models.DevProfile;
import com._p1m.portfolio.features.ManageDevProfile.repo.ManageDevProfileRepo;
import com._p1m.portfolio.features.ManageDevProfile.service.ManageDevProfileService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManageDevProfileServiceImpl implements ManageDevProfileService {

    private final ManageDevProfileRepo manageDevProfileRepo;

    public ManageDevProfileServiceImpl(ManageDevProfileRepo manageDevProfileRepo) {
        this.manageDevProfileRepo = manageDevProfileRepo;
    }


    @Override
    public List<DevProfile> findAllDevPf() {
        return manageDevProfileRepo.findAll();
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
    public DevProfile updateDevProfile(Long id, DevProfile devProfileDetails) {
        return manageDevProfileRepo.findById(id).map(devProfile -> {
            devProfile.setName(devProfileDetails.getName());
            devProfile.setProfilePictureUrl(devProfileDetails.getProfilePictureUrl());
            devProfile.setGithub(devProfileDetails.getGithub());
            devProfile.setLinkedIn(devProfileDetails.getLinkedIn());
            devProfile.setTechStacks(devProfileDetails.getTechStacks());
            return manageDevProfileRepo.save(devProfile);
        }).orElseThrow(() -> new RuntimeException("Developer profile not found with id: " + id));
    }

    @Override
    public void deleteDevPf(Long id) {
        manageDevProfileRepo.deleteById(id);
    }


}
