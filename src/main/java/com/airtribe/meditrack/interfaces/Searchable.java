package com.airtribe.meditrack.interfaces;

import java.util.List;

/**
 * Interface for searchable entities
 * Demonstrates default methods in interfaces
 */
public interface Searchable<T> {

    /**
     * Search by ID
     */
    T searchById(int id);

    /**
     * Search by name
     */
    List<T> searchByName(String name);

    /**
     * Default method to check if search results are empty
     */
    default boolean hasResults(List<T> results) {
        return results != null && !results.isEmpty();
    }

    /**
     * Default method to display search summary
     */
    default void displaySearchSummary(List<T> results) {
        if (hasResults(results)) {
            System.out.println("Found " + results.size() + " result(s)");
        } else {
            System.out.println("No results found");
        }
    }
}
