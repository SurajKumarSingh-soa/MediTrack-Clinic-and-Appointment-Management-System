package com.airtribe.meditrack.util;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV Utility for file I/O operations
 * Demonstrates:
 * - File I/O
 * - Try-with-resources
 * - Exception handling
 */
public class CSVUtil {

    /**
     * Read all lines from CSV file
     * Uses try-with-resources for automatic resource management
     */
    public static List<String> readCSV(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();

        File file = new File(filePath);
        if (!file.exists()) {
            return lines; // Return empty list if file doesn't exist
        }

        // Try-with-resources - automatically closes BufferedReader
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + filePath);
            throw e; // Re-throw after logging
        }

        return lines;
    }

    /**
     * Write lines to CSV file
     * Uses try-with-resources
     */
    public static void writeCSV(String filePath, List<String> lines) throws IOException {
        // Create directory if it doesn't exist
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        // Try-with-resources - automatically closes BufferedWriter
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing CSV file: " + filePath);
            throw e;
        }
    }

    /**
     * Append line to CSV file
     */
    public static void appendCSV(String filePath, String line) throws IOException {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error appending to CSV file: " + filePath);
            throw e;
        }
    }

    /**
     * Check if file exists
     */
    public static boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }

    /**
     * Delete file
     */
    public static boolean deleteFile(String filePath) {
        return new File(filePath).delete();
    }

    /**
     * Parse CSV line (simple split by comma)
     */
    public static String[] parseLine(String line) {
        return line.split(",");
    }
}
