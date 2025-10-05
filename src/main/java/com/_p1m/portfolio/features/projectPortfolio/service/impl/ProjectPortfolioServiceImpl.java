package com._p1m.portfolio.features.projectPortfolio.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com._p1m.portfolio.config.exceptions.EntityNotFoundException;
import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.data.models.DevProfile;
import com._p1m.portfolio.data.models.ProjectPortfolio;
import com._p1m.portfolio.data.models.lookup.LanguageAndTools;
import com._p1m.portfolio.data.repositories.DevProfileRepository;
import com._p1m.portfolio.data.repositories.LanguageAndToolsRepository;
import com._p1m.portfolio.data.repositories.ProjectPortfolioRepository;
import com._p1m.portfolio.features.projectPortfolio.dto.request.CreateProjectPortfolioRequest;
import com._p1m.portfolio.features.projectPortfolio.service.ProjectPortfolioService;
import com._p1m.portfolio.security.JWT.JWTUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectPortfolioServiceImpl implements ProjectPortfolioService {
	private final ProjectPortfolioRepository projectPortfolioRepository;
	private final DevProfileRepository devProfileRepository;
	private final LanguageAndToolsRepository languageAndToolsRepository;
	private final JWTUtil jwtUtil;
	@Override
	@Transactional
	public ApiResponse createProjectPortfolio(CreateProjectPortfolioRequest createRequest, String token) {
	    String email = jwtUtil.extractEmail(token);
	    System.out.println("Email is "+email);
	    DevProfile devProfile = devProfileRepository.findByUserEmail(email)
	        .orElseThrow(() -> new EntityNotFoundException("DevProfile not found for email: " + email));

	    Set<DevProfile> devProfiles = new HashSet<>();
	    devProfiles.add(devProfile);

	    List<String> languageAndTools = createRequest.languageAndTools();

	    Set<LanguageAndTools> savedLanguagesAndTools = new HashSet<>();
	    languageAndTools.stream()
	        .filter(l -> !l.isBlank())
	        .distinct()
	        .forEach(name -> {
	            LanguageAndTools languageTool = LanguageAndTools.builder().name(name).build();
	            savedLanguagesAndTools.add(this.languageAndToolsRepository.save(languageTool));
	        });

	    ProjectPortfolio projectPortfolio = ProjectPortfolio.builder()
	        .name(createRequest.name())
	        .description(createRequest.description())
	        .projectLink(createRequest.projectLink())
	        .repoLink(createRequest.repoLink())
	        .devProfiles(devProfiles)
	        .languageAndTools(savedLanguagesAndTools)
	        .build();

	    this.projectPortfolioRepository.save(projectPortfolio);

	    return ApiResponse.builder()
	        .success(1)
	        .code(HttpStatus.CREATED.value())
	        .data(true)
	        .message("Project portfolio created successfully.")
	        .build();
	}


}
