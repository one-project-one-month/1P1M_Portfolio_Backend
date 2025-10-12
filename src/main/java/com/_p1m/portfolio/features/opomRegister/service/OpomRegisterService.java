package com._p1m.portfolio.features.opomRegister.service;

import com._p1m.portfolio.features.opomRegister.dto.request.UserRegisterRequest;
import com._p1m.portfolio.features.opomRegister.dto.response.UserRegisterResponse;
import org.springframework.data.domain.Page;

public interface OpomRegisterService {

    UserRegisterResponse registerUser(UserRegisterRequest request);

    Page<UserRegisterResponse> getAllOpomRegisters(int page, int size, String dateOrder, String role);

    UserRegisterResponse updateOpomRegisterData(Long id, UserRegisterRequest request);

    void softDeleteOpomRegister(Long id);
}



