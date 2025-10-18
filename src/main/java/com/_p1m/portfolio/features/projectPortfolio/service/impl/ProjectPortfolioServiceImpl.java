package com._p1m.portfolio.features.projectPortfolio.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com._p1m.portfolio.config.exceptions.BadRequestException;
import com._p1m.portfolio.config.exceptions.EntityNotFoundException;
import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;
import com._p1m.portfolio.config.response.dto.PaginationMeta;
import com._p1m.portfolio.data.models.DevProfile;
import com._p1m.portfolio.data.models.ProjectIdea;
import com._p1m.portfolio.data.models.ProjectPortfolio;
import com._p1m.portfolio.data.models.User;
import com._p1m.portfolio.data.models.lookup.LanguageAndTools;
import com._p1m.portfolio.data.repositories.DevProfileRepository;
import com._p1m.portfolio.data.repositories.LanguageAndToolsRepository;
import com._p1m.portfolio.data.repositories.ProjectPortfolioRepository;
import com._p1m.portfolio.data.repositories.UserRepository;
import com._p1m.portfolio.data.storage.CloudStorageService;
import com._p1m.portfolio.features.projectPortfolio.dto.request.CreateProjectPortfolioRequest;
import com._p1m.portfolio.features.projectPortfolio.dto.request.DeleteProjectPortfolioPictureRequest;
import com._p1m.portfolio.features.projectPortfolio.dto.request.UpdateProjectPortfolioRequest;
import com._p1m.portfolio.features.projectPortfolio.dto.request.UploadFileRequest;
import com._p1m.portfolio.features.projectPortfolio.dto.response.AssignedDevs;
import com._p1m.portfolio.features.projectPortfolio.dto.response.ProjectPortfolioDetails;
import com._p1m.portfolio.features.projectPortfolio.dto.response.ProjectPortfolioResponse;
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

	private final CloudStorageService cloudStorageService;
	private final UserRepository userRepository;
	
	private final JWTUtil jwtUtil;
	@Override
	@Transactional
	public ApiResponse createProjectPortfolio(CreateProjectPortfolioRequest createRequest, String token) {
		String email = jwtUtil.extractEmail(token);
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

	@Override
	@Transactional
	public ApiResponse uploadFile(UploadFileRequest fileRequest,Long projectPortfolioId) {
		String publicId = cloudStorageService.upload(fileRequest.file());
		ProjectPortfolio projectPortfolio = projectPortfolioRepository.findById(projectPortfolioId)
				.orElseThrow(() -> new EntityNotFoundException("Project portfolio not found for id: " + projectPortfolioId));
		projectPortfolio.setProjectPicUrl(publicId);
		projectPortfolioRepository.save(projectPortfolio);
		return ApiResponse.builder()
				.success(1)
				.code(HttpStatus.OK.value())
				.data(Map.of("imageUrl", publicId))
				.message("File uploaded successfully.")
				.build();
	}

	@Override
	public ApiResponse retrieveSpecificProjectPortfolio(Long projectPortfolioId) {
		ProjectPortfolio projectPortfolio = projectPortfolioRepository.findById(projectPortfolioId)
				.orElseThrow(() -> new EntityNotFoundException("Project portfolio not found for id: " + projectPortfolioId));

		ProjectPortfolioResponse response = ProjectPortfolioResponse.builder()
				.id(projectPortfolio.getId())
				.name(projectPortfolio.getName())
				.projectPicUrl(projectPortfolio.getProjectPicUrl())
				.description(projectPortfolio.getDescription())
				.projectLink(projectPortfolio.getProjectLink())
				.repoLink(projectPortfolio.getRepoLink())
				.assignedDevs(new AssignedDevs(
						projectPortfolio.getDevProfiles().stream()
								.map(DevProfile::getId)
								.collect(Collectors.toList())
				))
				.projectPortfolioDetails(new ProjectPortfolioDetails(
						projectPortfolio.getLanguageAndTools().stream()
								.map(LanguageAndTools::getName)
								.collect(Collectors.toList())
				))
				.build();

		return ApiResponse.builder()
				.success(1)
				.code(HttpStatus.CREATED.value())
				.data(response)
				.message("Project portfolio retrieved successfully.")
				.build();
	}

	@Override
	public PaginatedApiResponse<ProjectPortfolioResponse> getAllpaginatedProjectProfiles(
			String keyword, Pageable pageable) {

		Specification<ProjectPortfolio> spec = (root, query, criteriaBuilder) -> {
			if (keyword != null && !keyword.trim().isEmpty()) {
				return criteriaBuilder.like(
						criteriaBuilder.lower(root.get("name")),
						"%" + keyword.toLowerCase() + "%"
				);
			}
			return null;
		};

		Page<ProjectPortfolio> projectPage = this.projectPortfolioRepository.findAll(spec, pageable);

		List<ProjectPortfolioResponse> projectResponses = projectPage.getContent().stream()
				.map(project -> ProjectPortfolioResponse.builder()
						.id(project.getId())
						.name(project.getName())
						.projectPicUrl(project.getProjectPicUrl())
						.description(project.getDescription())
						.projectLink(project.getProjectLink())
						.repoLink(project.getRepoLink())
						.reaction_count(project.getReactedUsers() != null
                                ? project.getReactedUsers().size()
                                : 0)
						.assignedDevs(new AssignedDevs(
								project.getDevProfiles().stream()
										.map(DevProfile::getId)
										.collect(Collectors.toList())
						))
						.projectPortfolioDetails(new ProjectPortfolioDetails(
								project.getLanguageAndTools().stream()
										.map(LanguageAndTools::getName)
										.collect(Collectors.toList())
						))
						.build()
				).collect(Collectors.toList());

		PaginationMeta meta = new PaginationMeta();
		meta.setTotalItems(projectPage.getTotalElements());
		meta.setTotalPages(projectPage.getTotalPages());
		meta.setCurrentPage(pageable.getPageNumber() + 1);

		return PaginatedApiResponse.<ProjectPortfolioResponse>builder()
				.success(1)
				.code(HttpStatus.OK.value())
				.message("Fetched successfully")
				.meta(meta)
				.data(projectResponses)
				.build();
	}

	@Override
	@Transactional
	public ApiResponse updateProjectPortfolio(UpdateProjectPortfolioRequest updateRequest, Long projectPortfolioId) {
		ProjectPortfolio projectPortfolio = projectPortfolioRepository.findById(projectPortfolioId)
				.orElseThrow(() -> new EntityNotFoundException("Project portfolio not found for id: " + projectPortfolioId));

		Optional.ofNullable(updateRequest.name()).ifPresent(projectPortfolio::setName);
		Optional.ofNullable(updateRequest.description()).ifPresent(projectPortfolio::setDescription);
		Optional.ofNullable(updateRequest.projectLink()).ifPresent(projectPortfolio::setProjectLink);
		Optional.ofNullable(updateRequest.repoLink()).ifPresent(projectPortfolio::setRepoLink);

		projectPortfolioRepository.save(projectPortfolio);

		return ApiResponse.builder()
				.success(1)
				.code(HttpStatus.OK.value())
				.data(true)
				.message("Project portfolio updated successfully.")
				.build();
	}

	@Override
	public ApiResponse deleteProjectPortfolio(Long projectPortfolioId) {
		ProjectPortfolio projectPortfolio = projectPortfolioRepository.findById(projectPortfolioId)
				.orElseThrow(() -> new EntityNotFoundException("Project portfolio not found for id: " + projectPortfolioId));
		cloudStorageService.delete(projectPortfolio.getProjectPicUrl());
		projectPortfolioRepository.delete(projectPortfolio);
		return ApiResponse.builder()
				.success(1)
				.code(HttpStatus.OK.value())
				.data(true)
				.message("Project portfolio deleted successfully.")
				.build();
	}

	@Override
	public ApiResponse deleteProjectPortfolioPicture(DeleteProjectPortfolioPictureRequest deleteRequest,
													 Long projectPortfolioId) {
		ProjectPortfolio projectPortfolio = projectPortfolioRepository.findById(projectPortfolioId)
				.orElseThrow(() -> new EntityNotFoundException("Project portfolio not found for id: " + projectPortfolioId));
		if(!projectPortfolio.getProjectPicUrl().equals(deleteRequest.imageUrl()))throw new BadRequestException("Url mismatch.");
		cloudStorageService.delete(projectPortfolio.getProjectPicUrl());
		projectPortfolio.setProjectPicUrl(null);
		projectPortfolioRepository.save(projectPortfolio);
		return ApiResponse.builder()
				.success(1)
				.code(HttpStatus.OK.value())
				.data(true)
				.message("Project portfolio picture deleted successfully.")
				.build();
	}

	@Override
	@Transactional
	public ApiResponse reactProjectPortfolio(Long projectPortfolioId, String token) {
	    String email = jwtUtil.extractEmail(token);

	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new EntityNotFoundException("User not found from token"));

	    ProjectPortfolio portfolio = projectPortfolioRepository.findById(projectPortfolioId)
	            .orElseThrow(() -> new com._p1m.portfolio.config.exceptions.EntityNotFoundException("Project Portfolio Does not Exist."));

	    if (portfolio.getReactedUsers().contains(user)) {
	        return ApiResponse.builder()
	                .success(0)
	                .code(HttpStatus.CONFLICT.value())
	                .message("You have already reacted to this project.")
	                .data(false)
	                .meta(Map.of("timestamp", System.currentTimeMillis()))
	                .build();
	    }

	    portfolio.getReactedUsers().add(user);
	    user.getReactedProjectPortfolios().add(portfolio);

	    projectPortfolioRepository.save(portfolio);

	    return ApiResponse.builder()
	            .success(1)
	            .code(HttpStatus.CREATED.value())
	            .message("Reacted successfully.")
	            .data(true)
	            .meta(Map.of("timestamp", System.currentTimeMillis()))
	            .build();
	}

	
	@Override
	@Transactional
	public ApiResponse unreactProjectPortfolio(Long projectPortfolioId, String token) {
	    String email = jwtUtil.extractEmail(token);
	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new EntityNotFoundException("User not found from token"));

	    ProjectPortfolio projectPortfolio = projectPortfolioRepository.findById(projectPortfolioId)
	            .orElseThrow(() -> new com._p1m.portfolio.config.exceptions.EntityNotFoundException("Project Portfolio Does not Exist."));

	    boolean removed = projectPortfolio.getReactedUsers().remove(user);
	    user.getReactedProjectPortfolios().remove(projectPortfolio);
	    projectPortfolioRepository.save(projectPortfolio);
	    
	    if (removed) {
	        return ApiResponse.builder()
	                .success(1)
	                .code(HttpStatus.OK.value())
	                .message("Reaction removed successfully.")
	                .data(true)
	                .meta(Map.of("timestamp", System.currentTimeMillis()))
	                .build();
	    } else {
	        return ApiResponse.builder()
	                .success(0)
	                .code(HttpStatus.NOT_FOUND.value())
	                .message("User had not reacted to this project idea.")
	                .data(false)
	                .meta(Map.of("timestamp", System.currentTimeMillis()))
	                .build();
	    }
	}
	
	@Override
	public ApiResponse getProjectPortfolioReactionCount(Long projectPortfolioId) {
		ProjectPortfolio projectIdea = projectPortfolioRepository.findById(projectPortfolioId)
	            .orElseThrow(() -> new com._p1m.portfolio.config.exceptions.EntityNotFoundException("Project Portfolio Does not Exist."));

	    int reactionCount = projectIdea.getReactedUsers().size();

	    return ApiResponse.builder()
	            .success(1)
	            .code(HttpStatus.OK.value())
	            .message("Reaction count fetched successfully.")
	            .data(Map.of(
	                "projectPortfolioId", projectPortfolioId,
	                "reactionCount", reactionCount
	            ))
	            .meta(Map.of("timestamp", System.currentTimeMillis()))
	            .build();
	}
}
