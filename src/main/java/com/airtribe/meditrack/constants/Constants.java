package com.airtribe.meditrack.constants;

/**
 * Application-wide constants and configuration
 */
public final class Constants {
    
    // Tax and billing constants
    public static final double TAX_RATE = 0.18; // 18% GST
    public static final double EMERGENCY_SURCHARGE = 500.0;
    
    // File paths for persistence
    public static final String DATA_DIR = "data/";
    public static final String DOCTORS_FILE = DATA_DIR + "doctors.csv";
    public static final String PATIENTS_FILE = DATA_DIR + "patients.csv";
    public static final String APPOINTMENTS_FILE = DATA_DIR + "appointments.csv";
    
    // Application settings  
    public static final int MAX_APPOINTMENTS_PER_DAY = 20;
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
    
    // Static initialization block
    static {
        System.out.println("[INFO] Loading MediTrack configuration...");
        System.out.println("[INFO] Tax Rate: " + (TAX_RATE * 100) + "%");
        System.out.println("[INFO] Data Directory: " + DATA_DIR);
    }
    
    // Private constructor to prevent instantiation
    private Constants() {
        throw new AssertionError("Cannot instantiate Constants class");
    }
}
