package com._p1m.portfolio.features.opomRegister.service;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.features.opomRegister.dto.request.UserRegisterRequest;

public interface OpomRegisterService {
    ApiResponse registerUser(UserRegisterRequest userRegisterRequest);
}
