package com._p1m.portfolio.features.ManageDevProfile.controller;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;
import com._p1m.portfolio.config.response.dto.PaginationMeta;
import com._p1m.portfolio.data.models.DevProfile;
import com._p1m.portfolio.features.ManageDevProfile.service.ManageDevProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("portfolio/api/v1/auth/devProfile")
public class ManageDevProfileController {
    private final ManageDevProfileService manageDevProfileService;

    @GetMapping("")
    public ResponseEntity<List<DevProfile>> getAllDevProfile(
    ){
        List<DevProfile> devProfiles = manageDevProfileService.findAllDevPf(Pageable.unpaged()).getContent();
        return ResponseEntity.ok(devProfiles);
    }
    @GetMapping("/{id}")
    public ResponseEntity<DevProfile> getDevPfByName(@PathVariable @Valid String name){
        DevProfile devPF = manageDevProfileService.findDevByName(name);
        return ResponseEntity.ok(devPF);
    }

    @GetMapping("/{linkedInURL}")
    public ResponseEntity<DevProfile> getDevPfByLinkedIn(@PathVariable @Valid String linkedInURL){
        DevProfile devPF = manageDevProfileService.findByLinkedIn(linkedInURL);
        return ResponseEntity.ok(devPF);
    }

    @GetMapping("/{githubURL}")
    public ResponseEntity<DevProfile> getDevPfByGitHub(@PathVariable @Valid String githubURL){
        DevProfile devPF = manageDevProfileService.findDevByGithub(githubURL);
        return ResponseEntity.ok(devPF);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDevPf(@PathVariable Long id) {
        manageDevProfileService.deleteDevPf(id);

        return ResponseEntity.ok("Deleted Successfully");
    }


}
