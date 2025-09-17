package com._p1m.portfolio.config.response.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ApiResponse {
    private int success;
    private int code;
    private Map<String, Object> meta;
    private Object data;
    private String message;
}