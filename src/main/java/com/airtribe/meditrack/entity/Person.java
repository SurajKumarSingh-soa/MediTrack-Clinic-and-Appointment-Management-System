package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.exception.InvalidDataException;

/**
 * Abstract base class for all persons (Inheritance & Abstraction)
 * Demonstrates:
 * - Abstract class
 * - Encapsulation (private fields)
 * - Template Method pattern (validate method)
 */
public abstract class Person {

    // Private fields for encapsulation
    private int id;
    private String name;
    private int age;
    private String contact;

    // Constructor chaining
    public Person() {
        this(0, "Unknown", 0, "");
    }

    public Person(int id, String name, int age, String contact) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.contact = contact;
    }

    // Getters and Setters (Encapsulation)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    // Template Method Pattern - validate() can be overridden
    public void validate() throws InvalidDataException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("name", "Name cannot be empty");
        }
        if (age < 0 || age > 150) {
            throw new InvalidDataException("age", "Age must be between 0 and 150");
        }
        if (contact == null || contact.trim().isEmpty()) {
            throw new InvalidDataException("contact", "Contact cannot be empty");
        }
    }

    // Abstract method - forces subclasses to implement
    public abstract String getRole();

    // Method to be overridden
    @Override
    public String toString() {
        return String.format("ID: %d, Name: %s, Age: %d, Contact: %s",
                id, name, age, contact);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Person person = (Person) obj;
        return id == person.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
