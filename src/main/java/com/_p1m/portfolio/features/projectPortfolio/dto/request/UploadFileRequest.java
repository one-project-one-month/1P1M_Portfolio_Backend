package com._p1m.portfolio.features.projectPortfolio.dto.request;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;

public record UploadFileRequest(
	    @Schema(type = "string", format = "binary", description = "The image file to upload")
	    MultipartFile file
	) {}
