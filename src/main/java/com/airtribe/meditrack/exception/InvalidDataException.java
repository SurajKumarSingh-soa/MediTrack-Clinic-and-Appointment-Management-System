package com.airtribe.meditrack.exception;

/**
 * Custom exception for data validation errors
 */
public class InvalidDataException extends Exception {

    private final String fieldName;

    public InvalidDataException(String message) {
        super(message);
        this.fieldName = null;
    }

    public InvalidDataException(String fieldName, String message) {
        super(fieldName + ": " + message);
        this.fieldName = fieldName;
    }

    // Constructor with chaining
    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
        this.fieldName = null;
    }

    public String getFieldName() {
        return fieldName;
    }
}
