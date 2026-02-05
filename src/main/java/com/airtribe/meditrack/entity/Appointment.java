package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.constants.AppointmentStatus;
import com.airtribe.meditrack.exception.InvalidDataException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Appointment entity
 * Demonstrates: Cloneable interface (Deep Copy)
 */
public class Appointment implements Cloneable {

    private int id;
    private Patient patient; // Mutable object for deep copy
    private Doctor doctor; // Mutable object for deep copy
    private LocalDateTime appointmentDate;
    private AppointmentStatus status;
    private String notes;

    // Constructor
    public Appointment() {
        this.status = AppointmentStatus.PENDING;
        this.notes = "";
    }

    public Appointment(int id, Patient patient, Doctor doctor,
            LocalDateTime appointmentDate, AppointmentStatus status, String notes) {
        this.id = id;
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentDate = appointmentDate;
        this.status = status;
        this.notes = notes;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Validation
    public void validate() throws InvalidDataException {
        if (patient == null) {
            throw new InvalidDataException("patient", "Patient cannot be null");
        }
        if (doctor == null) {
            throw new InvalidDataException("doctor", "Doctor cannot be null");
        }
        if (appointmentDate == null) {
            throw new InvalidDataException("appointmentDate", "Appointment date cannot be null");
        }
        if (appointmentDate.isBefore(LocalDateTime.now())) {
            throw new InvalidDataException("appointmentDate", "Appointment date cannot be in the past");
        }
    }

    /**
     * Deep copy implementation
     */
    @Override
    public Appointment clone() {
        try {
            Appointment cloned = (Appointment) super.clone();

            // Deep copy of mutable fields
            if (this.patient != null) {
                cloned.patient = this.patient.clone();
            }
            // Note: Doctor is not cloned as it's typically a reference
            // But in a full implementation, you could clone it too

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Clone not supported", e);
        }
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("Appointment ID: %d | Patient: %s | Doctor: %s | Date: %s | Status: %s",
                id, patient.getName(), doctor.getName(),
                appointmentDate.format(formatter), status);
    }

    // CSV export helper
    public String toCSV() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("%d,%d,%d,%s,%s,%s",
                id, patient.getId(), doctor.getId(),
                appointmentDate.format(formatter), status.name(),
                notes.replace(",", "~"));
    }

    // Note: fromCSV will need services to resolve patient and doctor by ID
    // This is handled in AppointmentService
}
