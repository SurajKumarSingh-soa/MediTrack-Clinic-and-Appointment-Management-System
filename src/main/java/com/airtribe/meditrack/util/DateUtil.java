package com.airtribe.meditrack.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Date utility class for date operations
 */
public class DateUtil {

    public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Parse date from string
     */
    public static LocalDateTime parseDate(String dateStr) throws DateTimeParseException {
        return LocalDateTime.parse(dateStr, DEFAULT_FORMATTER);
    }

    /**
     * Format date to string
     */
    public static String formatDate(LocalDateTime date) {
        return date.format(DEFAULT_FORMATTER);
    }

    /**
     * Check if date is in the future
     */
    public static boolean isFuture(LocalDateTime date) {
        return date.isAfter(LocalDateTime.now());
    }

    /**
     * Check if date is today
     */
    public static boolean isToday(LocalDateTime date) {
        LocalDateTime now = LocalDateTime.now();
        return date.toLocalDate().equals(now.toLocalDate());
    }

    /**
     * Get current date-time
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    // Private constructor
    private DateUtil() {
        throw new AssertionError("Cannot instantiate DateUtil");
    }
}
