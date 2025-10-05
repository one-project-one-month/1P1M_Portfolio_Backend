package com._p1m.portfolio.features.projectPortfolio.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DeleteProjectPortfolioPictureRequest(
		@NotBlank(message = "ImageUrl cannot be empty or null.")
		String imageUrl
) {}
