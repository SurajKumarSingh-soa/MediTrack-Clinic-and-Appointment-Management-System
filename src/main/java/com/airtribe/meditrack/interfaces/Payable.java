package com.airtribe.meditrack.interfaces;

/**
 * Interface for entities that can be billed
 */
public interface Payable {

    /**
     * Calculate the total amount
     */
    double calculateTotal();

    /**
     * Calculate tax on the amount
     */
    double calculateTax();

    /**
     * Default method to calculate final payable amount (amount + tax)
     */
    default double calculateFinalAmount() {
        return calculateTotal() + calculateTax();
    }

    /**
     * Default method to generate payment summary
     */
    default String generatePaymentSummary() {
        return String.format("Subtotal: ₹%.2f | Tax: ₹%.2f | Total: ₹%.2f",
                calculateTotal(), calculateTax(), calculateFinalAmount());
    }
}
