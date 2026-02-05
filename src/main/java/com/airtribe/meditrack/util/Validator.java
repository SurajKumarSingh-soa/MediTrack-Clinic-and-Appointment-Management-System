package com.airtribe.meditrack.util;

import com.airtribe.meditrack.exception.InvalidDataException;

/**
 * Centralized validation utility
 * Demonstrates encapsulation of validation logic
 */
public class Validator {

    /**
     * Validate name
     */
    public static void validateName(String name) throws InvalidDataException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("name", "Name cannot be empty");
        }
        if (name.length() < 2) {
            throw new InvalidDataException("name", "Name must be at least 2 characters");
        }
        if (!name.matches("[a-zA-Z ]+")) {
            throw new InvalidDataException("name", "Name can only contain letters and spaces");
        }
    }

    /**
     * Validate age
     */
    public static void validateAge(int age) throws InvalidDataException {
        if (age < 0 || age > 150) {
            throw new InvalidDataException("age", "Age must be between 0 and 150");
        }
    }

    /**
     * Validate contact
     */
    public static void validateContact(String contact) throws InvalidDataException {
        if (contact == null || contact.trim().isEmpty()) {
            throw new InvalidDataException("contact", "Contact cannot be empty");
        }
        // Remove spaces and hyphens for validation
        String cleanContact = contact.replaceAll("[\\s-]", "");
        if (!cleanContact.matches("\\d{10}")) {
            throw new InvalidDataException("contact", "Contact must be a 10-digit number");
        }
    }

    /**
     * Validate fee
     */
    public static void validateFee(double fee) throws InvalidDataException {
        if (fee < 0) {
            throw new InvalidDataException("fee", "Fee cannot be negative");
        }
        if (fee > 100000) {
            throw new InvalidDataException("fee", "Fee seems unreasonably high");
        }
    }

    /**
     * Validate non-empty string
     */
    public static void validateNonEmpty(String value, String fieldName) throws InvalidDataException {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidDataException(fieldName, fieldName + " cannot be empty");
        }
    }

    /**
     * Validate positive number
     */
    public static void validatePositive(double value, String fieldName) throws InvalidDataException {
        if (value < 0) {
            throw new InvalidDataException(fieldName, fieldName + " cannot be negative");
        }
    }
}
