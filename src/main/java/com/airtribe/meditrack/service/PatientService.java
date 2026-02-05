package com.airtribe.meditrack.service;

import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.interfaces.Searchable;
import com.airtribe.meditrack.util.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Patient operations
 * Demonstrates CRUD and polymorphism
 */
public class PatientService implements Searchable<Patient> {

    private final DataStore<Patient> patientStore;
    private final IdGenerator idGenerator;

    public PatientService() {
        this.patientStore = new DataStore<>();
        this.idGenerator = IdGenerator.getInstance();
    }

    /**
     * Add a new patient
     */
    public Patient addPatient(String name, int age, String contact,
            String medicalHistory, List<String> allergies)
            throws InvalidDataException {
        // Validate
        Validator.validateName(name);
        Validator.validateAge(age);
        Validator.validateContact(contact);

        int id = idGenerator.generatePatientId();
        Patient patient = new Patient(id, name, age, contact, medicalHistory, allergies);
        patient.validate();

        patientStore.add(id, patient);
        System.out.println("[SUCCESS] Patient added: " + patient.getName() + " (ID: " + id + ")");
        return patient;
    }

    /**
     * Get patient by ID
     */
    public Patient getPatientById(int id) {
        return patientStore.getById(id);
    }

    /**
     * Get all patients
     */
    public List<Patient> getAllPatients() {
        return patientStore.getAll();
    }

    /**
     * Update patient
     */
    public void updatePatient(Patient patient) throws InvalidDataException {
        patient.validate();
        patientStore.update(patient.getId(), patient);
        System.out.println("[SUCCESS] Patient updated: " + patient.getName());
    }

    /**
     * Delete patient
     */
    public boolean deletePatient(int id) {
        boolean removed = patientStore.remove(id);
        if (removed) {
            System.out.println("[SUCCESS] Patient deleted (ID: " + id + ")");
        } else {
            System.out.println("[WARNING] Patient not found (ID: " + id + ")");
        }
        return removed;
    }

    // ========== POLYMORPHISM: Method Overloading ==========

    /**
     * Search by ID (Overloaded #1)
     */
    @Override
    public Patient searchById(int id) {
        return patientStore.getById(id);
    }

    /**
     * Search by name (Overloaded #2)
     */
    @Override
    public List<Patient> searchByName(String name) {
        return patientStore.search(p -> p.getName().toLowerCase().contains(name.toLowerCase()));
    }

    /**
     * Search by age (Overloaded #3)
     */
    public List<Patient> searchByAge(int age) {
        return patientStore.search(p -> p.getAge() == age);
    }

    /**
     * Search by age range (Overloaded #4)
     */
    public List<Patient> searchByAgeRange(int minAge, int maxAge) {
        return patientStore.search(p -> p.getAge() >= minAge && p.getAge() <= maxAge);
    }

    /**
     * Search by allergy (Overloaded #5)
     */
    public List<Patient> searchByAllergy(String allergy) {
        return patientStore.search(p -> p.getAllergies().stream()
                .anyMatch(a -> a.toLowerCase().contains(allergy.toLowerCase())));
    }

    // ========== JAVA 8 STREAMS ==========

    /**
     * Get patients with specific allergy
     */
    public List<Patient> getPatientsWithAllergy(String allergy) {
        return patientStore.getAll().stream()
                .filter(p -> p.getAllergies().contains(allergy))
                .collect(Collectors.toList());
    }

    /**
     * Calculate average age
     */
    public double calculateAverageAge() {
        return patientStore.getAll().stream()
                .mapToInt(Patient::getAge)
                .average()
                .orElse(0.0);
    }

    // ========== FILE I/O PERSISTENCE ==========

    /**
     * Save patients to CSV file
     */
    public void savePatientsToFile() throws IOException {
        List<String> lines = patientStore.getAll().stream()
                .map(Patient::toCSV)
                .collect(Collectors.toList());

        CSVUtil.writeCSV(Constants.PATIENTS_FILE, lines);
        System.out.println("[INFO] Saved " + lines.size() + " patients to file");
    }

    /**
     * Load patients from CSV file
     */
    public void loadPatientsFromFile() throws IOException {
        if (!CSVUtil.fileExists(Constants.PATIENTS_FILE)) {
            System.out.println("[INFO] No patients file found, starting fresh");
            return;
        }

        List<String> lines = CSVUtil.readCSV(Constants.PATIENTS_FILE);
        int count = 0;

        for (String line : lines) {
            try {
                Patient patient = Patient.fromCSV(line);
                patientStore.add(patient.getId(), patient);

                // Update ID generator
                idGenerator.setCounters(0, patient.getId(), 0, 0);
                count++;
            } catch (Exception e) {
                System.err.println("[ERROR] Failed to load patient: " + line);
            }
        }

        System.out.println("[INFO] Loaded " + count + " patients from file");
    }

    /**
     * Display statistics
     */
    public void displayStatistics() {
        System.out.println("\n===== Patient Statistics =====");
        System.out.println("Total Patients: " + patientStore.size());
        System.out.println("Average Age: " + String.format("%.1f", calculateAverageAge()));
    }
}
