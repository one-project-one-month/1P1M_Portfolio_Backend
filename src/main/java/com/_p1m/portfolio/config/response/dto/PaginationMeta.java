package com._p1m.portfolio.config.response.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class PaginationMeta {
    private long totalItems;
    private int totalPages;
    private int currentPage;
    private String method;
    private String endpoint;
}
