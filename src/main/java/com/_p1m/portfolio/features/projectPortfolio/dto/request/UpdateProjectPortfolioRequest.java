package com._p1m.portfolio.features.projectPortfolio.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
public record UpdateProjectPortfolioRequest(
	String name,
	
	@Size(max = 500, message = "Description cannot exceed 500 characters.")
	String description,
	
	@Pattern(regexp = "^(https?://)?(www\\.)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$", message = "Invalid project link format.")
	String projectLink,
	
	List<String> devEmails, 

	@Pattern(regexp = "^(https?://)?(www\\.)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$", message = "Invalid repository link format.")
	String repoLink) {}
