package com._p1m.portfolio.features.opomRegister.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpomRegisterResponse {
    Long id;
    String name;
    String email;
    String phone;
    String telegram_username;
    private String role;
}
