package com.airtribe.meditrack.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Singleton ID Generator
 * Demonstrates:
 * - Singleton Pattern (Eager and Lazy initialization)
 * - Thread-safety with AtomicInteger
 * - Static initialization block
 */
public class IdGenerator {

    // Eager initialization - instance created at class loading
    private static final IdGenerator INSTANCE = new IdGenerator();

    // Thread-safe counters using AtomicInteger
    private final AtomicInteger doctorIdCounter;
    private final AtomicInteger patientIdCounter;
    private final AtomicInteger appointmentIdCounter;
    private final AtomicInteger billIdCounter;

    // Static initialization block
    static {
        System.out.println("[INFO] IdGenerator initialized (Singleton Pattern)");
    }

    // Private constructor prevents external instantiation
    private IdGenerator() {
        this.doctorIdCounter = new AtomicInteger(1000);
        this.patientIdCounter = new AtomicInteger(2000);
        this.appointmentIdCounter = new AtomicInteger(3000);
        this.billIdCounter = new AtomicInteger(4000);
    }

    /**
     * Get singleton instance
     */
    public static IdGenerator getInstance() {
        return INSTANCE;
    }

    /**
     * Generate next doctor ID (thread-safe)
     */
    public int generateDoctorId() {
        return doctorIdCounter.incrementAndGet();
    }

    /**
     * Generate next patient ID (thread-safe)
     */
    public int generatePatientId() {
        return patientIdCounter.incrementAndGet();
    }

    /**
     * Generate next appointment ID (thread-safe)
     */
    public int generateAppointmentId() {
        return appointmentIdCounter.incrementAndGet();
    }

    /**
     * Generate next bill ID (thread-safe)
     */
    public int generateBillId() {
        return billIdCounter.incrementAndGet();
    }

    /**
     * Reset counters (for testing purposes)
     */
    public void resetCounters() {
        doctorIdCounter.set(1000);
        patientIdCounter.set(2000);
        appointmentIdCounter.set(3000);
        billIdCounter.set(4000);
    }

    /**
     * Set initial counter values (for loading from file)
     */
    public void setCounters(int doctorId, int patientId, int appointmentId, int billId) {
        if (doctorId > doctorIdCounter.get()) {
            doctorIdCounter.set(doctorId);
        }
        if (patientId > patientIdCounter.get()) {
            patientIdCounter.set(patientId);
        }
        if (appointmentId > appointmentIdCounter.get()) {
            appointmentIdCounter.set(appointmentId);
        }
        if (billId > billIdCounter.get()) {
            billIdCounter.set(billId);
        }
    }
}
