package com.airtribe.meditrack.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Generic in-memory data store
 * Demonstrates:
 * - Generics for type safety
 * - Collections (ArrayList, HashMap)
 */
public class DataStore<T> {

    private final Map<Integer, T> dataMap;
    private final List<T> dataList;

    public DataStore() {
        this.dataMap = new HashMap<>();
        this.dataList = new ArrayList<>();
    }

    /**
     * Add item to store
     */
    public void add(int id, T item) {
        dataMap.put(id, item);
        if (!dataList.contains(item)) {
            dataList.add(item);
        }
    }

    /**
     * Get item by ID
     */
    public T getById(int id) {
        return dataMap.get(id);
    }

    /**
     * Get all items
     */
    public List<T> getAll() {
        return new ArrayList<>(dataList);
    }

    /**
     * Remove item by ID
     */
    public boolean remove(int id) {
        T item = dataMap.remove(id);
        if (item != null) {
            dataList.remove(item);
            return true;
        }
        return false;
    }

    /**
     * Update item
     */
    public void update(int id, T item) {
        T oldItem = dataMap.get(id);
        if (oldItem != null) {
            dataList.remove(oldItem);
        }
        dataMap.put(id, item);
        dataList.add(item);
    }

    /**
     * Check if ID exists
     */
    public boolean exists(int id) {
        return dataMap.containsKey(id);
    }

    /**
     * Get count of items
     */
    public int size() {
        return dataList.size();
    }

    /**
     * Clear all data
     */
    public void clear() {
        dataMap.clear();
        dataList.clear();
    }

    /**
     * Search using predicate (Java 8 streams)
     */
    public List<T> search(java.util.function.Predicate<T> predicate) {
        return dataList.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }
}
