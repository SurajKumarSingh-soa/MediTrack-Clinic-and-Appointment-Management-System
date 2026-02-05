package com.airtribe.meditrack.service;

import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.constants.Specialization;
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.interfaces.Searchable;
import com.airtribe.meditrack.util.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Doctor operations
 * Demonstrates:
 * - CRUD operations
 * - Method overloading (polymorphism)
 * - Java 8 Streams
 * - Implements Searchable interface
 */
public class DoctorService implements Searchable<Doctor> {

    private final DataStore<Doctor> doctorStore;
    private final IdGenerator idGenerator;

    public DoctorService() {
        this.doctorStore = new DataStore<>();
        this.idGenerator = IdGenerator.getInstance();
    }

    /**
     * Add a new doctor
     */
    public Doctor addDoctor(String name, int age, String contact,
            Specialization specialization, double consultationFee,
            String qualification) throws InvalidDataException {
        // Validate using Validator
        Validator.validateName(name);
        Validator.validateAge(age);
        Validator.validateContact(contact);
        Validator.validateFee(consultationFee);
        Validator.validateNonEmpty(qualification, "qualification");

        int id = idGenerator.generateDoctorId();
        Doctor doctor = new Doctor(id, name, age, contact, specialization,
                consultationFee, qualification);
        doctor.validate();

        doctorStore.add(id, doctor);
        System.out.println("[SUCCESS] Doctor added: " + doctor.getName() + " (ID: " + id + ")");
        return doctor;
    }

    /**
     * Get doctor by ID
     */
    public Doctor getDoctorById(int id) {
        return doctorStore.getById(id);
    }

    /**
     * Get all doctors
     */
    public List<Doctor> getAllDoctors() {
        return doctorStore.getAll();
    }

    /**
     * Update doctor
     */
    public void updateDoctor(Doctor doctor) throws InvalidDataException {
        doctor.validate();
        doctorStore.update(doctor.getId(), doctor);
        System.out.println("[SUCCESS] Doctor updated: " + doctor.getName());
    }

    /**
     * Delete doctor
     */
    public boolean deleteDoctor(int id) {
        boolean removed = doctorStore.remove(id);
        if (removed) {
            System.out.println("[SUCCESS] Doctor deleted (ID: " + id + ")");
        } else {
            System.out.println("[WARNING] Doctor not found (ID: " + id + ")");
        }
        return removed;
    }

    // ========== POLYMORPHISM: Method Overloading ==========

    /**
     * Search by ID (Overloaded method #1)
     */
    @Override
    public Doctor searchById(int id) {
        return doctorStore.getById(id);
    }

    /**
     * Search by name (Overloaded method #2)
     */
    @Override
    public List<Doctor> searchByName(String name) {
        return doctorStore.search(d -> d.getName().toLowerCase().contains(name.toLowerCase()));
    }

    /**
     * Search by specialization (Overloaded method #3)
     */
    public List<Doctor> searchBySpecialization(Specialization specialization) {
        return doctorStore.search(d -> d.getSpecialization() == specialization);
    }

    /**
     * Search by fee range (Overloaded method #4)
     */
    public List<Doctor> searchByFeeRange(double minFee, double maxFee) {
        return doctorStore.search(d -> d.getConsultationFee() >= minFee && d.getConsultationFee() <= maxFee);
    }

    // ========== JAVA 8 STREAMS & LAMBDAS ==========

    /**
     * Get doctors by specialization using streams
     */
    public List<Doctor> getDoctorsBySpecialization(Specialization specialization) {
        return doctorStore.getAll().stream()
                .filter(d -> d.getSpecialization() == specialization)
                .collect(Collectors.toList());
    }

    /**
     * Calculate average consultation fee (Streams)
     */
    public double calculateAverageFee() {
        return doctorStore.getAll().stream()
                .mapToDouble(Doctor::getConsultationFee)
                .average()
                .orElse(0.0);
    }

    /**
     * Get doctors sorted by fee (Streams with Comparator)
     */
    public List<Doctor> getDoctorsSortedByFee() {
        return doctorStore.getAll().stream()
                .sorted(Comparator.comparingDouble(Doctor::getConsultationFee))
                .collect(Collectors.toList());
    }

    /**
     * Get top N doctors by fee
     */
    public List<Doctor> getTopDoctorsByFee(int n) {
        return doctorStore.getAll().stream()
                .sorted(Comparator.comparingDouble(Doctor::getConsultationFee).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    // ========== FILE I/O PERSISTENCE ==========

    /**
     * Save doctors to CSV file
     */
    public void saveDoctorsToFile() throws IOException {
        List<String> lines = doctorStore.getAll().stream()
                .map(Doctor::toCSV)
                .collect(Collectors.toList());

        CSVUtil.writeCSV(Constants.DOCTORS_FILE, lines);
        System.out.println("[INFO] Saved " + lines.size() + " doctors to file");
    }

    /**
     * Load doctors from CSV file
     */
    public void loadDoctorsFromFile() throws IOException {
        if (!CSVUtil.fileExists(Constants.DOCTORS_FILE)) {
            System.out.println("[INFO] No doctors file found, starting fresh");
            return;
        }

        List<String> lines = CSVUtil.readCSV(Constants.DOCTORS_FILE);
        int count = 0;

        for (String line : lines) {
            try {
                Doctor doctor = Doctor.fromCSV(line);
                doctorStore.add(doctor.getId(), doctor);

                // Update ID generator to avoid conflicts
                idGenerator.setCounters(doctor.getId(), 0, 0, 0);
                count++;
            } catch (Exception e) {
                System.err.println("[ERROR] Failed to load doctor: " + line);
            }
        }

        System.out.println("[INFO] Loaded " + count + " doctors from file");
    }

    /**
     * Get statistics
     */
    public void displayStatistics() {
        System.out.println("\n===== Doctor Statistics =====");
        System.out.println("Total Doctors: " + doctorStore.size());
        System.out.println("Average Fee: ₹" + String.format("%.2f", calculateAverageFee()));

        // Group by specialization
        System.out.println("\nDoctors by Specialization:");
        for (Specialization spec : Specialization.values()) {
            long count = doctorStore.getAll().stream()
                    .filter(d -> d.getSpecialization() == spec)
                    .count();
            if (count > 0) {
                System.out.println("  " + spec + ": " + count);
            }
        }
    }
}
