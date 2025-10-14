package com._p1m.portfolio.features.opomRegister.service;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;
import com._p1m.portfolio.features.opomRegister.dto.request.UserRegisterRequest;
import com._p1m.portfolio.features.opomRegister.dto.response.OpomRegisterResponse;
import com._p1m.portfolio.features.projectPortfolio.dto.response.ProjectPortfolioResponse;
import org.springframework.data.domain.Pageable;

public interface OpomRegisterService {

    ApiResponse registerUser(UserRegisterRequest request, String token);

    ApiResponse updateOpomRegisterData(Long id, UserRegisterRequest request,String token);

    ApiResponse softDeleteOpomRegister(Long id);

    PaginatedApiResponse<OpomRegisterResponse> getAllpaginatedOpomRegisterList(String keyword, Pageable pageable);
}



