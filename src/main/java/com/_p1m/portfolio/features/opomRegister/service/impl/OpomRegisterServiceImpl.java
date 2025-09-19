package com._p1m.portfolio.features.opomRegister.service.impl;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.features.opomRegister.dto.request.UserRegisterRequest;
import com._p1m.portfolio.features.opomRegister.service.OpomRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpomRegisterServiceImpl implements OpomRegisterService {
    @Override
    public ApiResponse registerUser(UserRegisterRequest userRegisterRequest) {
        return null;
    }
}
