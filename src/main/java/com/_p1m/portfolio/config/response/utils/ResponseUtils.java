package com._p1m.portfolio.config.response.utils;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.config.response.dto.PaginatedApiResponse;

import jakarta.servlet.http.HttpServletRequest;

public class ResponseUtils {

    public static ResponseEntity<ApiResponse> buildResponse(final HttpServletRequest request, final Object response) {
        ApiResponse apiResponse = (ApiResponse) response;
        final HttpStatus status = HttpStatus.valueOf(apiResponse.getCode());

        if (apiResponse.getMeta() == null) {
            final String method = request.getMethod();
            final String endpoint = request.getRequestURI();
            apiResponse.setMeta(new HashMap<>());
            apiResponse.getMeta().put("method", method);
            apiResponse.getMeta().put("endpoint", endpoint);
        }

        return new ResponseEntity<ApiResponse>( apiResponse, status);
    }
    
    public static <T> ResponseEntity<PaginatedApiResponse<T>> buildPaginatedResponse(
            final HttpServletRequest request,
            PaginatedApiResponse<T> paginatedResponse) {

        final HttpStatus status = HttpStatus.valueOf(paginatedResponse.getCode());
        if (paginatedResponse.getMeta().getMethod() == null && paginatedResponse.getMeta().getEndpoint() == null) {
            final String method = request.getMethod();
            final String endpoint = request.getRequestURI();
            paginatedResponse.getMeta().setMethod(method);
            paginatedResponse.getMeta().setEndpoint(endpoint);
        }
        return new ResponseEntity<>(paginatedResponse, status);
    }



    /*
     * 
     *  Can uncomment out for later use
     * 
     */
//    public static ResponseEntity<Resource> buildFileResponse(Resource resource, boolean asAttachment, double requestStartTime) {
//        try {
//            Path filePath = Path.of(resource.getURI());
//            String contentType = Files.probeContentType(filePath);
//            if (contentType == null) {
//                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
//            }
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.parseMediaType(contentType));
//
//            String dispositionType = asAttachment ? "attachment" : "inline";
//            headers.setContentDisposition(ContentDisposition
//                    .builder(dispositionType)
//                    .filename(resource.getFilename())
//                    .build());
//
//            long duration = Instant.now().getEpochSecond() - (long) requestStartTime;
//            headers.add("X-Response-Duration", duration + "s");
//
//            return ResponseEntity.ok()
//                    .headers(headers)
//                    .body(resource);
//
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to build file response for: " + resource.getFilename(), e);
//        }
//    }

}