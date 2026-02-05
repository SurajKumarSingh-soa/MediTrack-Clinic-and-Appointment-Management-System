package com.airtribe.meditrack.entity;

import java.time.LocalDateTime;

/**
 * Immutable BillSummary class
 * Demonstrates:
 * - Immutability (final class, final fields, no setters)
 * - Thread-safety
 */
public final class BillSummary {

    // All fields are final
    private final int billId;
    private final String patientName;
    private final String doctorName;
    private final LocalDateTime billDate;
    private final double totalAmount;
    private final double taxAmount;
    private final double finalAmount;

    // Constructor - only way to set values
    public BillSummary(int billId, String patientName, String doctorName,
            LocalDateTime billDate, double totalAmount,
            double taxAmount, double finalAmount) {
        this.billId = billId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.billDate = billDate;
        this.totalAmount = totalAmount;
        this.taxAmount = taxAmount;
        this.finalAmount = finalAmount;
    }

    // Only getters, no setters (Immutability)
    public int getBillId() {
        return billId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public LocalDateTime getBillDate() {
        // Return defensive copy of mutable object
        return billDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    // Factory method to create from Bill
    public static BillSummary fromBill(Bill bill) {
        return new BillSummary(
                bill.getId(),
                bill.getAppointment().getPatient().getName(),
                bill.getAppointment().getDoctor().getName(),
                LocalDateTime.now(),
                bill.calculateTotal(),
                bill.calculateTax(),
                bill.calculateFinalAmount());
    }

    @Override
    public String toString() {
        return String.format("BillSummary[ID=%d, Patient=%s, Doctor=%s, Amount=₹%.2f]",
                billId, patientName, doctorName, finalAmount);
    }

    // equals and hashCode for immutable objects
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        BillSummary that = (BillSummary) obj;
        return billId == that.billId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(billId);
    }
}
