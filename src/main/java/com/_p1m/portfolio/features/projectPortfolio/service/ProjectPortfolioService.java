package com._p1m.portfolio.features.projectPortfolio.service;

import org.springframework.data.domain.Pageable;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;
import com._p1m.portfolio.features.projectPortfolio.dto.request.CreateProjectPortfolioRequest;
import com._p1m.portfolio.features.projectPortfolio.dto.request.DeleteProjectPortfolioPictureRequest;
import com._p1m.portfolio.features.projectPortfolio.dto.request.UpdateProjectPortfolioRequest;
import com._p1m.portfolio.features.projectPortfolio.dto.request.UploadFileRequest;
import com._p1m.portfolio.features.projectPortfolio.dto.response.ProjectPortfolioResponse;

public interface ProjectPortfolioService {
    ApiResponse createProjectPortfolio(final CreateProjectPortfolioRequest createRequest,final String token);
    ApiResponse uploadFile(final UploadFileRequest fileRequest,final Long projectPortfolioId);
    ApiResponse retrieveSpecificProjectPortfolio(final Long projectPortfolioId);
    PaginatedApiResponse<ProjectPortfolioResponse> getAllpaginatedProjectProfiles(final String keyword,final Pageable pageable , String token);
    ApiResponse updateProjectPortfolio(final UpdateProjectPortfolioRequest updateRequest,final Long projectPortfolioId);
    ApiResponse deleteProjectPortfolio(final Long projectPortfolioId);
    ApiResponse deleteProjectPortfolioPicture(final DeleteProjectPortfolioPictureRequest deleteRequest,final Long projectPortfolioId);
    
    ApiResponse reactProjectPortfolio(Long projectPortfolioId,String token);
    ApiResponse unreactProjectPortfolio(Long projectPortfolioId, String token);
    ApiResponse getProjectPortfolioReactionCount(Long projectPortfolioId);
}
