package com._p1m.portfolio.features.devProfile.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class DevProfileResponse {

    private Long userId;
    private String name;
    private String email;
    private String profilePictureUrl;
    private String github;
    private String linkedIn;
    private List<String> techStacks;
}