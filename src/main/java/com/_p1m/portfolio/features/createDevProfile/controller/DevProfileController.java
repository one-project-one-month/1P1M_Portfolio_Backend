package com._p1m.portfolio.features.createDevProfile.controller;

import com._p1m.portfolio.data.models.DevProfile;
import com._p1m.portfolio.features.createDevProfile.dto.request.CreateDevProfileRequest;
import com._p1m.portfolio.features.createDevProfile.service.DevProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class DevProfileController {

    private final DevProfileService devProfileService;

    @PostMapping
    public ResponseEntity<DevProfile> createDevProfile(@Valid @RequestBody CreateDevProfileRequest request) {
        // TODO: Replace with ID from authenticated user once Spring Security is implemented
        Long currentUserId = 1L;

        DevProfile createdProfile = devProfileService.createDevProfile(request, currentUserId);

        return new ResponseEntity<>(createdProfile, HttpStatus.CREATED);
    }
}