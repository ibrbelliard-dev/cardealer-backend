package com.cardealer.iotproject.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("data")
    private T data;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    @JsonProperty("path")
    private String path;
    
    @JsonProperty("statusCode")
    private Integer statusCode;
    
    @JsonProperty("errors")
    private Map<String, String> errors;
    
    @JsonProperty("totalCount")
    private Long totalCount;
    
    @JsonProperty("page")
    private Integer page;
    
    @JsonProperty("size")
    private Integer size;
    
    @JsonProperty("totalPages")
    private Integer totalPages;
    
    // Default constructor
    public ApiResponse() {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.success = true;
    }
    
    // Constructor with success flag
    public ApiResponse(boolean success) {
        this.success = success;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
    
    // Constructor with success and message
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
    
    // Constructor with success, message, and data
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
    
    // Full constructor
    public ApiResponse(boolean success, String message, T data, String path, Integer statusCode) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.path = path;
        this.statusCode = statusCode;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public Integer getStatusCode() {
        return statusCode;
    }
    
    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
    
    public Map<String, String> getErrors() {
        return errors;
    }
    
    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
    
    public Long getTotalCount() {
        return totalCount;
    }
    
    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
    
    public Integer getPage() {
        return page;
    }
    
    public void setPage(Integer page) {
        this.page = page;
    }
    
    public Integer getSize() {
        return size;
    }
    
    public void setSize(Integer size) {
        this.size = size;
    }
    
    public Integer getTotalPages() {
        return totalPages;
    }
    
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
    
    // Helper methods for adding errors
    public void addError(String field, String errorMessage) {
        if (this.errors == null) {
            this.errors = new HashMap<>();
        }
        this.errors.put(field, errorMessage);
    }
    
    // Static factory methods
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, "Operation completed successfully");
    }
    
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message);
    }
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Operation completed successfully", data);
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }
    
    public static <T> ApiResponse<T> success(String message, T data, Long totalCount) {
        ApiResponse<T> response = new ApiResponse<>(true, message, data);
        response.setTotalCount(totalCount);
        return response;
    }
    
    public static <T> ApiResponse<T> success(String message, T data, Integer page, Integer size, Long totalCount, Integer totalPages) {
        ApiResponse<T> response = new ApiResponse<>(true, message, data);
        response.setPage(page);
        response.setSize(size);
        response.setTotalCount(totalCount);
        response.setTotalPages(totalPages);
        return response;
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message);
    }
    
    public static <T> ApiResponse<T> error(String message, Integer statusCode) {
        ApiResponse<T> response = new ApiResponse<>(false, message);
        response.setStatusCode(statusCode);
        return response;
    }
    
    public static <T> ApiResponse<T> error(String message, String path, Integer statusCode) {
        ApiResponse<T> response = new ApiResponse<>(false, message);
        response.setPath(path);
        response.setStatusCode(statusCode);
        return response;
    }
    
    public static <T> ApiResponse<T> validationError(Map<String, String> errors) {
        ApiResponse<T> response = new ApiResponse<>(false, "Validation failed");
        response.setErrors(errors);
        response.setStatusCode(400);
        return response;
    }
    
    public static <T> ApiResponse<T> notFound(String message) {
        ApiResponse<T> response = new ApiResponse<>(false, message);
        response.setStatusCode(404);
        return response;
    }
    
    public static <T> ApiResponse<T> unauthorized(String message) {
        ApiResponse<T> response = new ApiResponse<>(false, message);
        response.setStatusCode(401);
        return response;
    }
    
    public static <T> ApiResponse<T> forbidden(String message) {
        ApiResponse<T> response = new ApiResponse<>(false, message);
        response.setStatusCode(403);
        return response;
    }
    
    // Builder pattern for complex responses
    public static class Builder<T> {
        private boolean success;
        private String message;
        private T data;
        private String path;
        private Integer statusCode;
        private Map<String, String> errors;
        private Long totalCount;
        private Integer page;
        private Integer size;
        private Integer totalPages;
        
        public Builder<T> success(boolean success) {
            this.success = success;
            return this;
        }
        
        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }
        
        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }
        
        public Builder<T> path(String path) {
            this.path = path;
            return this;
        }
        
        public Builder<T> statusCode(Integer statusCode) {
            this.statusCode = statusCode;
            return this;
        }
        
        public Builder<T> errors(Map<String, String> errors) {
            this.errors = errors;
            return this;
        }
        
        public Builder<T> totalCount(Long totalCount) {
            this.totalCount = totalCount;
            return this;
        }
        
        public Builder<T> pagination(Integer page, Integer size, Long totalCount, Integer totalPages) {
            this.page = page;
            this.size = size;
            this.totalCount = totalCount;
            this.totalPages = totalPages;
            return this;
        }
        
        public ApiResponse<T> build() {
            ApiResponse<T> response = new ApiResponse<>(success, message, data);
            response.setPath(path);
            response.setStatusCode(statusCode);
            response.setErrors(errors);
            response.setTotalCount(totalCount);
            response.setPage(page);
            response.setSize(size);
            response.setTotalPages(totalPages);
            return response;
        }
    }
    
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }
    
    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp='" + timestamp + '\'' +
                ", path='" + path + '\'' +
                ", statusCode=" + statusCode +
                ", errors=" + errors +
                ", totalCount=" + totalCount +
                ", page=" + page +
                ", size=" + size +
                ", totalPages=" + totalPages +
                '}';
    }
}