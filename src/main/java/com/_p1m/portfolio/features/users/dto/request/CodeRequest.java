package com._p1m.portfolio.features.users.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CodeRequest {

    @NotBlank(message = "Code cannot be empty")
    private String code;
}
