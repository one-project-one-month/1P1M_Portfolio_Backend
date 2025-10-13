package com._p1m.portfolio.config.response.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public class PaginatedApiResponse<T> {
	private int success;
    private int code;
    private String message;
    private PaginationMeta meta;
    private List<T> data;
}
