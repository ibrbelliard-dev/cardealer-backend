package com.cardealer.iotproject.model.dto;

import java.time.LocalDate;

public class CommissionFilterRequest {
    private Long salesRepId;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer page = 0;
    private Integer size = 20;
    private String sortBy = "createdAt";
    private String sortDir = "DESC";
    
    public CommissionFilterRequest() {}
    
    public Long getSalesRepId() { return salesRepId; }
    public void setSalesRepId(Long salesRepId) { this.salesRepId = salesRepId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
    
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    
    public String getSortDir() { return sortDir; }
    public void setSortDir(String sortDir) { this.sortDir = sortDir; }
}