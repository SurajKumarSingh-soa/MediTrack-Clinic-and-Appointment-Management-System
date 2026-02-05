package com.airtribe.meditrack.constants;

/**
 * Enum representing appointment status
 */
public enum AppointmentStatus {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    NO_SHOW("No Show");
    
    private final String displayName;
    
    // Constructor
    AppointmentStatus(String displayName) {
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
