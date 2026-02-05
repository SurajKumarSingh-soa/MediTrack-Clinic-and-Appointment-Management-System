package com.airtribe.meditrack;

import com.airtribe.meditrack.constants.AppointmentStatus;
import com.airtribe.meditrack.constants.Specialization;
import com.airtribe.meditrack.entity.*;
import com.airtribe.meditrack.exception.AppointmentNotFoundException;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.service.*;
import com.airtribe.meditrack.util.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main application class with menu-driven CLI
 * Demonstrates: Application flow, user interaction, integration of all
 * components
 */
public class Main {

    private static DoctorService doctorService;
    private static PatientService patientService;
    private static AppointmentService appointmentService;
    private static Scanner scanner;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Welcome to MediTrack System");
        System.out.println("========================================\n");

        // Initialize services
        initializeServices();

        // Check for command-line arguments
        boolean loadData = false;
        if (args.length > 0 && "--loadData".equals(args[0])) {
            loadData = true;
        }

        // Load data if requested
        if (loadData) {
            loadDataFromFiles();
        } else {
            System.out.println("[INFO] Starting with fresh data. Use --loadData to load from files.");
            loadSampleData(); // Load some sample data for demo
        }

        // Setup Observer for appointment notifications
        setupObserver();

        // Main menu loop
        scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            displayMainMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        manageDoctor();
                        break;
                    case "2":
                        managePatients();
                        break;
                    case "3":
                        manageAppointments();
                        break;
                    case "4":
                        manageBilling();
                        break;
                    case "5":
                        viewAnalytics();
                        break;
                    case "6":
                        aiFeatures();
                        break;
                    case "7":
                        saveDataToFiles();
                        break;
                    case "8":
                        loadDataFromFiles();
                        break;
                    case "9":
                        demonstrateAdvancedFeatures();
                        break;
                    case "0":
                        running = false;
                        System.out.println("\nSaving data and exiting...");
                        saveDataToFiles();
                        System.out.println("Thank you for using MediTrack!");
                        break;
                    default:
                        System.out.println("[ERROR] Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        }

        scanner.close();
    }

    private static void displayMainMenu() {
        System.out.println("\n========== MAIN MENU ==========");
        System.out.println("1. Manage Doctors");
        System.out.println("2. Manage Patients");
        System.out.println("3. Manage Appointments");
        System.out.println("4. Billing");
        System.out.println("5. View Analytics");
        System.out.println("6. AI Features");
        System.out.println("7. Save Data");
        System.out.println("8. Load Data");
        System.out.println("9. Demo Advanced Features");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void initializeServices() {
        doctorService = new DoctorService();
        patientService = new PatientService();
        appointmentService = new AppointmentService(doctorService, patientService);
    }

    private static void setupObserver() {
        appointmentService.addObserver((appointment, action) -> {
            System.out.println("\n[NOTIFICATION] Appointment " + action + " - Patient: "
                    + appointment.getPatient().getName() + ", Doctor: "
                    + appointment.getDoctor().getName());
        });
    }

    // ========== DOCTOR MANAGEMENT ==========

    private static void manageDoctor() throws InvalidDataException {
        System.out.println("\n--- Doctor Management ---");
        System.out.println("1. Add Doctor");
        System.out.println("2. View All Doctors");
        System.out.println("3. Search Doctor");
        System.out.println("4. Update Doctor");
        System.out.println("5. Delete Doctor");
        System.out.println("6. View Doctor Statistics");
        System.out.print("Enter choice: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                addDoctor();
                break;
            case "2":
                viewAllDoctors();
                break;
            case "3":
                searchDoctor();
                break;
            case "4":
                updateDoctor();
                break;
            case "5":
                deleteDoctor();
                break;
            case "6":
                doctorService.displayStatistics();
                break;
        }
    }

    private static void addDoctor() throws InvalidDataException {
        System.out.println("\n--- Add New Doctor ---");
        System.out.println("[HINT] Contact must be exactly 10 digits (e.g., 9876543210)\n");

        try {
            System.out.print("Name: ");
            String name = scanner.nextLine();

            System.out.print("Age: ");
            int age = Integer.parseInt(scanner.nextLine());

            System.out.print("Contact (10 digits): ");
            String contact = scanner.nextLine();

            System.out.println("Specialization:");
            displaySpecializations();
            System.out.print("Enter number: ");
            int specIndex = Integer.parseInt(scanner.nextLine()) - 1;
            Specialization specialization = Specialization.values()[specIndex];

            System.out.print("Consultation Fee: ");
            double fee = Double.parseDouble(scanner.nextLine());

            System.out.print("Qualification: ");
            String qualification = scanner.nextLine();

            Doctor doctor = doctorService.addDoctor(name, age, contact, specialization, fee, qualification);
            System.out.println("\n✓ [SUCCESS] Doctor added successfully!");
            System.out.println(doctor);
        } catch (InvalidDataException e) {
            System.out.println("\n✗ [FAILED] Could not add doctor: " + e.getMessage());
            System.out.println("[HINT] Please check the requirements and try again.");
            throw e;
        }
    }

    private static void displaySpecializations() {
        int i = 1;
        for (Specialization spec : Specialization.values()) {
            System.out.println(i++ + ". " + spec.getDisplayName());
        }
    }

    private static void viewAllDoctors() {
        List<Doctor> doctors = doctorService.getAllDoctors();
        System.out.println("\n--- All Doctors (" + doctors.size() + ") ---");
        for (Doctor doctor : doctors) {
            System.out.println(doctor);
        }
    }

    private static void searchDoctor() {
        System.out.println("\n--- Search Doctor ---");
        System.out.println("1. By ID");
        System.out.println("2. By Name");
        System.out.println("3. By Specialization");
        System.out.print("Enter choice: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                System.out.print("Enter Doctor ID: ");
                int id = Integer.parseInt(scanner.nextLine());
                Doctor doctor = doctorService.searchById(id);
                if (doctor != null) {
                    System.out.println("\n" + doctor);
                } else {
                    System.out.println("[INFO] Doctor not found");
                }
                break;
            case "2":
                System.out.print("Enter Doctor Name: ");
                String name = scanner.nextLine();
                List<Doctor> doctors = doctorService.searchByName(name);
                doctorService.displaySearchSummary(doctors);
                for (Doctor d : doctors) {
                    System.out.println(d);
                }
                break;
            case "3":
                displaySpecializations();
                System.out.print("Enter number: ");
                int specIndex = Integer.parseInt(scanner.nextLine()) - 1;
                Specialization spec = Specialization.values()[specIndex];
                List<Doctor> specDoctors = doctorService.searchBySpecialization(spec);
                System.out.println("\nFound " + specDoctors.size() + " doctor(s)");
                for (Doctor d : specDoctors) {
                    System.out.println(d);
                }
                break;
        }
    }

    private static void updateDoctor() throws InvalidDataException {
        System.out.print("Enter Doctor ID to update: ");
        int id = Integer.parseInt(scanner.nextLine());
        Doctor doctor = doctorService.getDoctorById(id);

        if (doctor == null) {
            System.out.println("[ERROR] Doctor not found");
            return;
        }

        System.out.println("Current: " + doctor);
        System.out.print("New Consultation Fee (press Enter to skip): ");
        String feeStr = scanner.nextLine();
        if (!feeStr.trim().isEmpty()) {
            doctor.setConsultationFee(Double.parseDouble(feeStr));
        }

        doctorService.updateDoctor(doctor);
    }

    private static void deleteDoctor() {
        System.out.print("Enter Doctor ID to delete: ");
        int id = Integer.parseInt(scanner.nextLine());
        doctorService.deleteDoctor(id);
    }

    // ========== PATIENT MANAGEMENT ==========

    private static void managePatients() throws InvalidDataException {
        System.out.println("\n--- Patient Management ---");
        System.out.println("1. Add Patient");
        System.out.println("2. View All Patients");
        System.out.println("3. Search Patient");
        System.out.println("4. Delete Patient");
        System.out.println("5. View Patient Statistics");
        System.out.println("6. Demo Cloning (Deep Copy)");
        System.out.print("Enter choice: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                addPatient();
                break;
            case "2":
                viewAllPatients();
                break;
            case "3":
                searchPatient();
                break;
            case "4":
                deletePatient();
                break;
            case "5":
                patientService.displayStatistics();
                break;
            case "6":
                demonstrateCloning();
                break;
        }
    }

    private static void addPatient() throws InvalidDataException {
        System.out.println("\n--- Add New Patient ---");
        System.out.println("[HINT] Contact must be exactly 10 digits (e.g., 9123456789)\n");

        try {
            System.out.print("Name: ");
            String name = scanner.nextLine();

            System.out.print("Age: ");
            int age = Integer.parseInt(scanner.nextLine());

            System.out.print("Contact (10 digits): ");
            String contact = scanner.nextLine();

            System.out.print("Medical History: ");
            String history = scanner.nextLine();

            System.out.print("Allergies (comma-separated): ");
            String allergiesStr = scanner.nextLine();
            List<String> allergies = new ArrayList<>();
            if (!allergiesStr.trim().isEmpty()) {
                for (String allergy : allergiesStr.split(",")) {
                    allergies.add(allergy.trim());
                }
            }

            Patient patient = patientService.addPatient(name, age, contact, history, allergies);
            System.out.println("\n✓ [SUCCESS] Patient added successfully!");
            System.out.println(patient);
        } catch (InvalidDataException e) {
            System.out.println("\n✗ [FAILED] Could not add patient: " + e.getMessage());
            System.out.println("[HINT] Please check the requirements and try again.");
            throw e;
        }
    }

    private static void viewAllPatients() {
        List<Patient> patients = patientService.getAllPatients();
        System.out.println("\n--- All Patients (" + patients.size() + ") ---");
        for (Patient patient : patients) {
            System.out.println(patient);
        }
    }

    private static void searchPatient() {
        System.out.println("\n--- Search Patient ---");
        System.out.println("1. By ID");
        System.out.println("2. By Name");
        System.out.println("3. By Age");
        System.out.print("Enter choice: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                System.out.print("Enter Patient ID: ");
                int id = Integer.parseInt(scanner.nextLine());
                Patient patient = patientService.searchById(id);
                if (patient != null) {
                    System.out.println("\n" + patient);
                } else {
                    System.out.println("[INFO] Patient not found");
                }
                break;
            case "2":
                System.out.print("Enter Patient Name: ");
                String name = scanner.nextLine();
                List<Patient> patients = patientService.searchByName(name);
                patientService.displaySearchSummary(patients);
                for (Patient p : patients) {
                    System.out.println(p);
                }
                break;
            case "3":
                System.out.print("Enter Age: ");
                int age = Integer.parseInt(scanner.nextLine());
                List<Patient> agePatients = patientService.searchByAge(age);
                System.out.println("\nFound " + agePatients.size() + " patient(s)");
                for (Patient p : agePatients) {
                    System.out.println(p);
                }
                break;
        }
    }

    private static void deletePatient() {
        System.out.print("Enter Patient ID to delete: ");
        int id = Integer.parseInt(scanner.nextLine());
        patientService.deletePatient(id);
    }

    // ========== APPOINTMENT MANAGEMENT ==========

    private static void manageAppointments() throws AppointmentNotFoundException, InvalidDataException {
        System.out.println("\n--- Appointment Management ---");
        System.out.println("1. Book Appointment");
        System.out.println("2. View All Appointments");
        System.out.println("3. View Upcoming Appointments");
        System.out.println("4. Cancel Appointment");
        System.out.println("5. Complete Appointment");
        System.out.println("6. Search Appointments");
        System.out.print("Enter choice: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                bookAppointment();
                break;
            case "2":
                viewAllAppointments();
                break;
            case "3":
                viewUpcomingAppointments();
                break;
            case "4":
                cancelAppointment();
                break;
            case "5":
                completeAppointment();
                break;
            case "6":
                searchAppointments();
                break;
        }
    }

    private static void bookAppointment() throws AppointmentNotFoundException, InvalidDataException {
        System.out.println("\n--- Book Appointment ---");
        System.out.println("[HINT] Date format: yyyy-MM-dd HH:mm (e.g., 2026-02-15 14:30)\n");

        try {
            System.out.print("Patient ID: ");
            int patientId = Integer.parseInt(scanner.nextLine());

            System.out.print("Doctor ID: ");
            int doctorId = Integer.parseInt(scanner.nextLine());

            System.out.print("Appointment Date & Time (yyyy-MM-dd HH:mm): ");
            String dateStr = scanner.nextLine();
            LocalDateTime appointmentDate = DateUtil.parseDate(dateStr);

            System.out.print("Notes: ");
            String notes = scanner.nextLine();

            Appointment appointment = appointmentService.bookAppointment(patientId, doctorId, appointmentDate, notes);
            System.out.println("\n✓ [SUCCESS] Appointment booked successfully!");
            System.out.println(appointment);
        } catch (DateTimeParseException e) {
            System.out.println("\n✗ [FAILED] Invalid date format. Use yyyy-MM-dd HH:mm (e.g., 2026-02-15 14:30)");
            throw new InvalidDataException("date", "Invalid date format");
        } catch (AppointmentNotFoundException | InvalidDataException e) {
            System.out.println("\n✗ [FAILED] Could not book appointment: " + e.getMessage());
            throw e;
        }
    }

    private static void viewAllAppointments() {
        List<Appointment> appointments = appointmentService.getAllAppointments();
        System.out.println("\n--- All Appointments (" + appointments.size() + ") ---");
        for (Appointment appointment : appointments) {
            System.out.println(appointment);
        }
    }

    private static void viewUpcomingAppointments() {
        List<Appointment> appointments = appointmentService.getUpcomingAppointments();
        System.out.println("\n--- Upcoming Appointments (" + appointments.size() + ") ---");
        for (Appointment appointment : appointments) {
            System.out.println(appointment);
        }
    }

    private static void cancelAppointment() throws AppointmentNotFoundException {
        System.out.print("Enter Appointment ID to cancel: ");
        int id = Integer.parseInt(scanner.nextLine());
        appointmentService.cancelAppointment(id);
    }

    private static void completeAppointment() throws AppointmentNotFoundException {
        System.out.print("Enter Appointment ID to complete: ");
        int id = Integer.parseInt(scanner.nextLine());
        appointmentService.completeAppointment(id);
    }

    private static void searchAppointments() {
        System.out.println("\n--- Search Appointments ---");
        System.out.println("1. By Patient ID");
        System.out.println("2. By Doctor ID");
        System.out.println("3. By Status");
        System.out.print("Enter choice: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                System.out.print("Enter Patient ID: ");
                int patientId = Integer.parseInt(scanner.nextLine());
                List<Appointment> patientAppts = appointmentService.getAppointmentsByPatient(patientId);
                System.out.println("\nFound " + patientAppts.size() + " appointment(s)");
                for (Appointment a : patientAppts) {
                    System.out.println(a);
                }
                break;
            case "2":
                System.out.print("Enter Doctor ID: ");
                int doctorId = Integer.parseInt(scanner.nextLine());
                List<Appointment> doctorAppts = appointmentService.getAppointmentsByDoctor(doctorId);
                System.out.println("\nFound " + doctorAppts.size() + " appointment(s)");
                for (Appointment a : doctorAppts) {
                    System.out.println(a);
                }
                break;
            case "3":
                System.out.println("Status:");
                int i = 1;
                for (AppointmentStatus status : AppointmentStatus.values()) {
                    System.out.println(i++ + ". " + status);
                }
                System.out.print("Enter number: ");
                int statusIndex = Integer.parseInt(scanner.nextLine()) - 1;
                AppointmentStatus status = AppointmentStatus.values()[statusIndex];
                List<Appointment> statusAppts = appointmentService.getAppointmentsByStatus(status);
                System.out.println("\nFound " + statusAppts.size() + " appointment(s)");
                for (Appointment a : statusAppts) {
                    System.out.println(a);
                }
                break;
        }
    }

    // ========== BILLING ==========

    private static void manageBilling() throws AppointmentNotFoundException {
        System.out.println("\n--- Billing Management ---");
        System.out.println("1. Generate Bill");
        System.out.println("2. View All Bills");
        System.out.println("3. View Bill Details");
        System.out.println("4. Create Bill Summary (Immutable)");
        System.out.print("Enter choice: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                generateBill();
                break;
            case "2":
                viewAllBills();
                break;
            case "3":
                viewBillDetails();
                break;
            case "4":
                createBillSummary();
                break;
        }
    }

    private static void generateBill() throws AppointmentNotFoundException {
        System.out.println("\n--- Generate Bill ---");
        System.out.print("Appointment ID: ");
        int appointmentId = Integer.parseInt(scanner.nextLine());

        System.out.print("Additional Charges: ");
        double additionalCharges = Double.parseDouble(scanner.nextLine());

        System.out.print("Is Emergency? (yes/no): ");
        boolean isEmergency = scanner.nextLine().toLowerCase().startsWith("y");

        Bill bill = appointmentService.generateBill(appointmentId, additionalCharges, isEmergency);
        System.out.println(bill.generateDetailedBill());
    }

    private static void viewAllBills() {
        List<Bill> bills = appointmentService.getAllBills();
        System.out.println("\n--- All Bills (" + bills.size() + ") ---");
        for (Bill bill : bills) {
            System.out.println(bill);
        }
    }

    private static void viewBillDetails() {
        System.out.print("Enter Bill ID: ");
        int billId = Integer.parseInt(scanner.nextLine());
        Bill bill = appointmentService.getBillById(billId);

        if (bill != null) {
            System.out.println(bill.generateDetailedBill());
        } else {
            System.out.println("[ERROR] Bill not found");
        }
    }

    private static void createBillSummary() {
        System.out.print("Enter Bill ID: ");
        int billId = Integer.parseInt(scanner.nextLine());
        BillSummary summary = appointmentService.createBillSummary(billId);

        if (summary != null) {
            System.out.println("\n[Immutable Bill Summary Created]");
            System.out.println(summary);
        } else {
            System.out.println("[ERROR] Bill not found");
        }
    }

    // ========== ANALYTICS ==========

    private static void viewAnalytics() {
        System.out.println("\n========== ANALYTICS ==========");

        // Doctor analytics
        doctorService.displayStatistics();

        // Patient analytics
        patientService.displayStatistics();

        // Appointment analytics
        appointmentService.displayStatistics();

        // Top doctors
        System.out.println("\n--- Top 5 Doctors by Fee ---");
        List<Doctor> topDoctors = doctorService.getTopDoctorsByFee(5);
        for (Doctor doctor : topDoctors) {
            System.out.println(doctor.getName() + " - ₹" + doctor.getConsultationFee());
        }

        // Appointments per doctor
        System.out.println("\n--- Appointments per Doctor ---");
        appointmentService.getAppointmentsPerDoctor()
                .forEach((name, count) -> System.out.println(name + ": " + count + " appointment(s)"));
    }

    // ========== AI FEATURES ==========

    private static void aiFeatures() {
        System.out.println("\n--- AI Doctor Recommendation ---");
        System.out.print("Enter symptoms: ");
        String symptoms = scanner.nextLine();

        String recommendation = AIHelper.generateRecommendationReport(symptoms);
        System.out.println("\n" + recommendation);

        Specialization recommended = AIHelper.recommendSpecialization(symptoms);
        List<Doctor> doctors = doctorService.searchBySpecialization(recommended);

        if (!doctors.isEmpty()) {
            System.out.println("\nAvailable Doctors:");
            for (Doctor doctor : doctors) {
                System.out.println("  - " + doctor.getName() + " (Fee: ₹" + doctor.getConsultationFee() + ")");
            }
        } else {
            System.out.println("\nNo doctors available for this specialization");
        }
    }

    // ========== FILE I/O ==========

    private static void saveDataToFiles() {
        try {
            doctorService.saveDoctorsToFile();
            patientService.savePatientsToFile();
            appointmentService.saveAppointmentsToFile();
            System.out.println("[SUCCESS] All data saved successfully!");
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to save data: " + e.getMessage());
        }
    }

    private static void loadDataFromFiles() {
        try {
            System.out.println("\n[INFO] Loading data from files...");
            doctorService.loadDoctorsFromFile();
            patientService.loadPatientsFromFile();
            appointmentService.loadAppointmentsFromFile();
            System.out.println("[SUCCESS] Data loaded successfully!");
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to load data: " + e.getMessage());
        }
    }

    // ========== ADVANCED FEATURES DEMONSTRATION ==========

    private static void demonstrateAdvancedFeatures() {
        System.out.println("\n========== ADVANCED FEATURES DEMO ==========");

        // 1. Demonstrate cloning
        System.out.println("\n1. Deep Copy (Cloning) Demo:");
        if (!patientService.getAllPatients().isEmpty()) {
            Patient original = patientService.getAllPatients().get(0);
            Patient cloned = original.clone();

            System.out.println("Original: " + original);
            System.out.println("Cloned: " + cloned);

            cloned.addAllergy("Demonstration Allergy");
            System.out.println("\nAfter modifying clone:");
            System.out.println("Original allergies: " + original.getAllergies());
            System.out.println("Cloned allergies: " + cloned.getAllergies());
            System.out.println("[INFO] Deep copy successful - original unaffected!");
        }

        // 2. Demonstrate immutability
        System.out.println("\n2. Immutability Demo (BillSummary):");
        if (!appointmentService.getAllBills().isEmpty()) {
            Bill bill = appointmentService.getAllBills().get(0);
            BillSummary summary = BillSummary.fromBill(bill);
            System.out.println("BillSummary created: " + summary);
            System.out.println("[INFO] BillSummary is immutable - no setters available!");
        }

        // 3. Demonstrate polymorphism
        System.out.println("\n3. Polymorphism Demo (Method Overloading):");
        System.out.println("Searching patients by different criteria:");
        if (!patientService.getAllPatients().isEmpty()) {
            Patient p = patientService.getAllPatients().get(0);
            System.out.println("  - By ID: " + (patientService.searchById(p.getId()) != null ? "Found" : "Not found"));
            System.out.println("  - By Name: " + patientService.searchByName(p.getName()).size() + " result(s)");
            System.out.println("  - By Age: " + patientService.searchByAge(p.getAge()).size() + " result(s)");
        }

        // 4. Demonstrate streams
        System.out.println("\n4. Java 8 Streams Demo:");
        System.out.println("Average doctor fee: ₹" + String.format("%.2f", doctorService.calculateAverageFee()));
        System.out.println("Average patient age: " + String.format("%.1f", patientService.calculateAverageAge()));

        System.out.println("\n[INFO] All advanced features demonstrated!");
    }

    private static void demonstrateCloning() {
        if (patientService.getAllPatients().isEmpty()) {
            System.out.println("[INFO] No patients available. Add a patient first.");
            return;
        }

        Patient original = patientService.getAllPatients().get(0);
        System.out.println("\nOriginal Patient:");
        System.out.println(original);

        // Clone the patient
        Patient cloned = original.clone();
        System.out.println("\nCloned Patient:");
        System.out.println(cloned);

        // Modify the clone
        cloned.addAllergy("New Test Allergy");
        cloned.setName(cloned.getName() + " (Clone)");

        System.out.println("\nAfter modifying clone:");
        System.out.println("Original: " + original);
        System.out.println("Cloned: " + cloned);
        System.out.println("\n[SUCCESS] Deep copy works - Original patient's allergies unchanged!");
    }

    // ========== SAMPLE DATA ==========

    private static void loadSampleData() {
        try {
            System.out.println("[INFO] Loading sample data...");

            // Add sample doctors
            doctorService.addDoctor("Dr. Rajesh Kumar", 45, "9876543210",
                    Specialization.CARDIOLOGY, 1500, "MBBS, MD (Cardiology)");
            doctorService.addDoctor("Dr. Priya Sharma", 38, "9876543211",
                    Specialization.PEDIATRICS, 1200, "MBBS, MD (Pediatrics)");
            doctorService.addDoctor("Dr. Amit Patel", 50, "9876543212",
                    Specialization.ORTHOPEDICS, 1800, "MBBS, MS (Orthopedics)");

            // Add sample patients
            List<String> allergies1 = new ArrayList<>();
            allergies1.add("Penicillin");
            patientService.addPatient("Rahul Verma", 30, "9123456789",
                    "Hypertension", allergies1);

            List<String> allergies2 = new ArrayList<>();
            allergies2.add("Peanuts");
            patientService.addPatient("Sneha Gupta", 5, "9123456790",
                    "Asthma", allergies2);

            System.out.println("[SUCCESS] Sample data loaded!");

        } catch (InvalidDataException e) {
            System.out.println("[ERROR] Failed to load sample data: " + e.getMessage());
        }
    }
}
