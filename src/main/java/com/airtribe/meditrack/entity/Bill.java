package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.interfaces.Payable;

/**
 * Bill entity demonstrating Payable interface
 */
public class Bill implements Payable {

    private int id;
    private Appointment appointment;
    private double consultationFee;
    private double additionalCharges;
    private boolean isEmergency;

    // Constructor
    public Bill() {
        this.consultationFee = 0.0;
        this.additionalCharges = 0.0;
        this.isEmergency = false;
    }

    public Bill(int id, Appointment appointment, double consultationFee,
            double additionalCharges, boolean isEmergency) {
        this.id = id;
        this.appointment = appointment;
        this.consultationFee = consultationFee;
        this.additionalCharges = additionalCharges;
        this.isEmergency = isEmergency;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public double getAdditionalCharges() {
        return additionalCharges;
    }

    public void setAdditionalCharges(double additionalCharges) {
        this.additionalCharges = additionalCharges;
    }

    public boolean isEmergency() {
        return isEmergency;
    }

    public void setEmergency(boolean emergency) {
        isEmergency = emergency;
    }

    // Implement Payable interface
    @Override
    public double calculateTotal() {
        double total = consultationFee + additionalCharges;
        if (isEmergency) {
            total += Constants.EMERGENCY_SURCHARGE;
        }
        return total;
    }

    @Override
    public double calculateTax() {
        return calculateTotal() * Constants.TAX_RATE;
    }

    // Use default method from interface
    // calculateFinalAmount() and generatePaymentSummary() inherited

    @Override
    public String toString() {
        return String.format("Bill ID: %d | Appointment: %d | %s | Final Amount: ₹%.2f",
                id, appointment.getId(), generatePaymentSummary(), calculateFinalAmount());
    }

    /**
     * Generate detailed bill
     */
    public String generateDetailedBill() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n========================================\n");
        sb.append("           MEDITRACK CLINIC             \n");
        sb.append("========================================\n");
        sb.append(String.format("Bill ID: %d\n", id));
        sb.append(String.format("Date: %s\n", java.time.LocalDateTime.now()));
        sb.append("----------------------------------------\n");
        sb.append(String.format("Patient: %s\n", appointment.getPatient().getName()));
        sb.append(String.format("Doctor: %s\n", appointment.getDoctor().getName()));
        sb.append(String.format("Specialization: %s\n", appointment.getDoctor().getSpecialization()));
        sb.append("----------------------------------------\n");
        sb.append(String.format("Consultation Fee: ₹%.2f\n", consultationFee));
        sb.append(String.format("Additional Charges: ₹%.2f\n", additionalCharges));
        if (isEmergency) {
            sb.append(String.format("Emergency Surcharge: ₹%.2f\n", Constants.EMERGENCY_SURCHARGE));
        }
        sb.append("----------------------------------------\n");
        sb.append(String.format("Subtotal: ₹%.2f\n", calculateTotal()));
        sb.append(String.format("Tax (%.0f%%): ₹%.2f\n", Constants.TAX_RATE * 100, calculateTax()));
        sb.append("========================================\n");
        sb.append(String.format("TOTAL AMOUNT: ₹%.2f\n", calculateFinalAmount()));
        sb.append("========================================\n");
        return sb.toString();
    }
}
