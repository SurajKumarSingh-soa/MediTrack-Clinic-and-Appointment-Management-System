package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.constants.Specialization;
import com.airtribe.meditrack.exception.InvalidDataException;

/**
 * Doctor entity extending Person
 * Demonstrates: Inheritance with super(), constructor chaining
 */
public class Doctor extends Person {

    // Doctor-specific fields
    private Specialization specialization;
    private double consultationFee;
    private String qualification;

    // Default constructor
    public Doctor() {
        super(); // Call parent constructor
        this.specialization = Specialization.GENERAL;
        this.consultationFee = 0.0;
        this.qualification = "";
    }

    // Parameterized constructor with super()
    public Doctor(int id, String name, int age, String contact,
            Specialization specialization, double consultationFee, String qualification) {
        super(id, name, age, contact); // Constructor chaining
        this.specialization = specialization;
        this.consultationFee = consultationFee;
        this.qualification = qualification;
    }

    // Getters and Setters
    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    // Implement abstract method
    @Override
    public String getRole() {
        return "Doctor";
    }

    // Override validate with additional checks
    @Override
    public void validate() throws InvalidDataException {
        super.validate(); // Call parent validation
        if (consultationFee < 0) {
            throw new InvalidDataException("consultationFee", "Fee cannot be negative");
        }
        if (qualification == null || qualification.trim().isEmpty()) {
            throw new InvalidDataException("qualification", "Qualification cannot be empty");
        }
    }

    // Method overriding
    @Override
    public String toString() {
        return super.toString() +
                String.format(", Role: %s, Specialization: %s, Fee: ₹%.2f, Qualification: %s",
                        getRole(), specialization, consultationFee, qualification);
    }

    // CSV export helper
    public String toCSV() {
        return String.format("%d,%s,%d,%s,%s,%.2f,%s",
                getId(), getName(), getAge(), getContact(),
                specialization.name(), consultationFee, qualification);
    }

    // CSV import helper
    public static Doctor fromCSV(String csvLine) {
        String[] parts = csvLine.split(",");
        return new Doctor(
                Integer.parseInt(parts[0]),
                parts[1],
                Integer.parseInt(parts[2]),
                parts[3],
                Specialization.valueOf(parts[4]),
                Double.parseDouble(parts[5]),
                parts[6]);
    }
}
