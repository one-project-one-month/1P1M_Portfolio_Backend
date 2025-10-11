package com._p1m.portfolio.features.devProfile.dto.response;

import com._p1m.portfolio.data.models.User;
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
    private String profilePictureUrl;
    private String github;
    private String linkedIn;
    private User user;
    private List<String> techStacks;
}