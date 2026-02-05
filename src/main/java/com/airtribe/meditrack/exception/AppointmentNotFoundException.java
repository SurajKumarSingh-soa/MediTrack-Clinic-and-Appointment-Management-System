package com.airtribe.meditrack.exception;

/**
 * Custom exception for appointment-related errors
 * Demonstrates exception handling and chaining
 */
public class AppointmentNotFoundException extends Exception {
    
    private final int appointmentId;
    
    public AppointmentNotFoundException(int appointmentId) {
        super("Appointment not found with ID: " + appointmentId);
        this.appointmentId = appointmentId;
    }
    
    public AppointmentNotFoundException(String message) {
        super(message);
        this.appointmentId = -1;
    }
    
    // Constructor with chaining
    public AppointmentNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.appointmentId = -1;
    }
    
    public int getAppointmentId() {
        return appointmentId;
    }
}
