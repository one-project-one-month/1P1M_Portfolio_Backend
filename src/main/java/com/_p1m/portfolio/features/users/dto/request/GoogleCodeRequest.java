package com._p1m.portfolio.features.users.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleCodeRequest {

    @NotBlank(message = "Code cannot be empty")
    String code;
}
