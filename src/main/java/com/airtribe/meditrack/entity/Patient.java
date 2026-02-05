package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.exception.InvalidDataException;
import java.util.ArrayList;
import java.util.List;

/**
 * Patient entity extending Person
 * Demonstrates:
 * - Inheritance
 * - Cloneable interface (Deep Copy)
 */
public class Patient extends Person implements Cloneable {

    // Patient-specific fields
    private String medicalHistory;
    private List<String> allergies; // Mutable object for deep copy demonstration

    // Default constructor
    public Patient() {
        super();
        this.medicalHistory = "";
        this.allergies = new ArrayList<>();
    }

    // Parameterized constructor
    public Patient(int id, String name, int age, String contact,
            String medicalHistory, List<String> allergies) {
        super(id, name, age, contact);
        this.medicalHistory = medicalHistory;
        this.allergies = allergies != null ? new ArrayList<>(allergies) : new ArrayList<>();
    }

    // Getters and Setters
    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public List<String> getAllergies() {
        return allergies;
    }

    public void setAllergies(List<String> allergies) {
        this.allergies = allergies != null ? new ArrayList<>(allergies) : new ArrayList<>();
    }

    public void addAllergy(String allergy) {
        this.allergies.add(allergy);
    }

    // Implement abstract method
    @Override
    public String getRole() {
        return "Patient";
    }

    // Override validate
    @Override
    public void validate() throws InvalidDataException {
        super.validate();
        // Additional patient-specific validation can go here
    }

    /**
     * Deep copy implementation using Cloneable
     * Demonstrates deep vs shallow copy
     */
    @Override
    public Patient clone() {
        try {
            // Shallow copy first
            Patient cloned = (Patient) super.clone();

            // Deep copy of mutable fields
            cloned.allergies = new ArrayList<>(this.allergies);

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Clone not supported", e);
        }
    }

    // Method overriding
    @Override
    public String toString() {
        return super.toString() +
                String.format(", Role: %s, Medical History: %s, Allergies: %s",
                        getRole(), medicalHistory, allergies);
    }

    // CSV export helper
    public String toCSV() {
        String allergiesStr = String.join(";", allergies);
        return String.format("%d,%s,%d,%s,%s,%s",
                getId(), getName(), getAge(), getContact(),
                medicalHistory.replace(",", "~"), allergiesStr);
    }

    // CSV import helper
    public static Patient fromCSV(String csvLine) {
        String[] parts = csvLine.split(",");
        List<String> allergies = new ArrayList<>();
        if (parts.length > 5 && !parts[5].isEmpty()) {
            String[] allergyArray = parts[5].split(";");
            for (String allergy : allergyArray) {
                allergies.add(allergy);
            }
        }
        return new Patient(
                Integer.parseInt(parts[0]),
                parts[1],
                Integer.parseInt(parts[2]),
                parts[3],
                parts[4].replace("~", ","),
                allergies);
    }
}
