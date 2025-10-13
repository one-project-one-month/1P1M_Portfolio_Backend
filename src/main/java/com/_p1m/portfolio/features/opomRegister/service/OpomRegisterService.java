package com._p1m.portfolio.features.opomRegister.service;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;
import com._p1m.portfolio.features.opomRegister.dto.request.UserRegisterRequest;

public interface OpomRegisterService {

    ApiResponse registerUser(UserRegisterRequest request, String token);

    PaginatedApiResponse getAllOpomRegisters(int page, int size, String dateOrder, String role);

    ApiResponse updateOpomRegisterData(Long id, UserRegisterRequest request);

    ApiResponse softDeleteOpomRegister(Long id);
}



