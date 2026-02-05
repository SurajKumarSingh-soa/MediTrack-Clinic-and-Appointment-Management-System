package com.airtribe.meditrack.constants;

/**
 * Enum representing doctor specializations
 */
public enum Specialization {
    GENERAL("General Medicine"),
    CARDIOLOGY("Cardiology"),
    DERMATOLOGY("Dermatology"),
    ORTHOPEDICS("Orthopedics"),
    PEDIATRICS("Pediatrics"),
    NEUROLOGY("Neurology"),
    PSYCHIATRY("Psychiatry"),
    OPHTHALMOLOGY("Ophthalmology"),
    ENT("ENT (Ear, Nose, Throat)"),
    GYNECOLOGY("Gynecology");
    
    private final String displayName;
    
    // Constructor
    Specialization(String displayName) {
        this.displayName = displayName;
    }
    
    // Getter
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
