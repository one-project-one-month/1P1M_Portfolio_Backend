package com._p1m.portfolio.features.devProfile.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class DevProfileListResponse {
    private Long dev_id;
    private String email;
    private String name;
    private String profilePictureUrl;
    private String github;
    private String linkedIn;
    private String aboutDev;
    private List<String> tech_stack;
}
