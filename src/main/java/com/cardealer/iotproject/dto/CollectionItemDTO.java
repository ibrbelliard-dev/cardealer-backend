package com.cardealer.iotproject.dto;

public class CollectionItemDTO {
    private Integer id;
    private Integer collectionId;
    private Integer imageId;
    private String imageUrl;
    private Integer mediaType;
    
    // Constructors
    public CollectionItemDTO() {}
    
    public CollectionItemDTO(Integer id, Integer collectionId, Integer imageId, 
                            String imageUrl, Integer mediaType) {
        this.id = id;
        this.collectionId = collectionId;
        this.imageId = imageId;
        this.imageUrl = imageUrl;
        this.mediaType = mediaType;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getCollectionId() {
        return collectionId;
    }
    
    public void setCollectionId(Integer collectionId) {
        this.collectionId = collectionId;
    }
    
    public Integer getImageId() {
        return imageId;
    }
    
    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public Integer getMediaType() {
        return mediaType;
    }
    
    public void setMediaType(Integer mediaType) {
        this.mediaType = mediaType;
    }
}