package com._p1m.portfolio.config.response.dto;

import lombok.Data;

@Data
public class PaginationMeta {
    private long totalItems;
    private int totalPages;
    private int currentPage;
    private String method;
    private String endpoint;
}
