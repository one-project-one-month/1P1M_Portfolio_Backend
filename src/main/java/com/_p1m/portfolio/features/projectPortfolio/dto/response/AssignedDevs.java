package com._p1m.portfolio.features.projectPortfolio.dto.response;

import java.util.List;

public record AssignedDevs(List<DevProfileResponse> developers) {
    public record DevProfileResponse(
        Long id,
        String name,
        String profilePictureUrl,
        String github,
        String linkedIn,
        String aboutDev
    ) {}
}
