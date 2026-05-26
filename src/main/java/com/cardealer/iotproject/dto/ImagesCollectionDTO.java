package com.cardealer.iotproject.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ImagesCollectionDTO {
    private Integer id;
    private String collectionTitle;
    private LocalDateTime dateCreated;
    private Integer userId;
    private String collectionImage;
    private List<CollectionItemDTO> collectionItems;
    
    // Constructors
    public ImagesCollectionDTO() {}
    
    public ImagesCollectionDTO(Integer id, String collectionTitle, LocalDateTime dateCreated, 
                               Integer userId, String collectionImage) {
        this.id = id;
        this.collectionTitle = collectionTitle;
        this.dateCreated = dateCreated;
        this.userId = userId;
        this.collectionImage = collectionImage;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getCollectionTitle() {
        return collectionTitle;
    }
    
    public void setCollectionTitle(String collectionTitle) {
        this.collectionTitle = collectionTitle;
    }
    
    public LocalDateTime getDateCreated() {
        return dateCreated;
    }
    
    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public String getCollectionImage() {
        return collectionImage;
    }
    
    public void setCollectionImage(String collectionImage) {
        this.collectionImage = collectionImage;
    }
    
    public List<CollectionItemDTO> getCollectionItems() {
        return collectionItems;
    }
    
    public void setCollectionItems(List<CollectionItemDTO> collectionItems) {
        this.collectionItems = collectionItems;
    }
}