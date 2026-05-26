package com.cardealer.iotproject.model.enums;

public enum ImageType {
    EXTERIOR_FRONT("Exterior - Front"),
    EXTERIOR_REAR("Exterior - Rear"),
    EXTERIOR_SIDE("Exterior - Side"),
    EXTERIOR_TOP("Exterior - Top"),
    INTERIOR("Interior"),
    INTERIOR_DASHBOARD("Interior - Dashboard"),
    INTERIOR_SEATS("Interior - Seats"),
    ENGINE("Engine"),
    ENGINE_BAY("Engine Bay"),
    TRUNK("Trunk"),
    WHEELS("Wheels"),
    DAMAGE("Damage/Defects"),
    DOCUMENT("Document"),
    CERTIFICATE("Certificate"),
    WARRANTY("Warranty Document"),
    TITLE("Title Document"),
    INVOICE("Invoice"),
    MAINTENANCE("Maintenance Record"),
    ACCIDENT("Accident Report"),
    OTHER("Other");
    
    private final String displayName;
    
    ImageType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static ImageType fromDisplayName(String displayName) {
        for (ImageType type : ImageType.values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        return OTHER;
    }
}