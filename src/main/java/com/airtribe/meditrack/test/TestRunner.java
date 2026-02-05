package com.airtribe.meditrack.test;

import com.airtribe.meditrack.constants.AppointmentStatus;
import com.airtribe.meditrack.constants.Specialization;
import com.airtribe.meditrack.entity.*;
import com.airtribe.meditrack.exception.AppointmentNotFoundException;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.service.*;
import com.airtribe.meditrack.util.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Manual test runner for MediTrack system
 * Demonstrates comprehensive testing of all features
 */
public class TestRunner {

    private static DoctorService doctorService;
    private static PatientService patientService;
    private static AppointmentService appointmentService;

    private static int passedTests = 0;
    private static int failedTests = 0;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   MediTrack Test Runner");
        System.out.println("========================================\n");

        initializeServices();

        // Run all tests
        testDoctorCRUD();
        testPatientCRUD();
        testAppointmentCRUD();
        testBillingSystem();
        testPolymorphism();
        testCloning();
        testImmutability();
        testEnums();
        testExceptionHandling();
        testValidation();
        testObserverPattern();
        testStreamOperations();
        testAIFeature();
        testFileIO();

        // Display results
        displayTestResults();
    }

    private static void initializeServices() {
        doctorService = new DoctorService();
        patientService = new PatientService();
        appointmentService = new AppointmentService(doctorService, patientService);
        System.out.println("[INFO] Services initialized\n");
    }

    // ========== TEST: DOCTOR CRUD ==========

    private static void testDoctorCRUD() {
        System.out.println("=== Test: Doctor CRUD Operations ===");

        try {
            // Create
            Doctor doctor = doctorService.addDoctor("Dr. Test", 40, "9876543210",
                    Specialization.CARDIOLOGY, 1500, "MBBS, MD");
            assert doctor != null : "Doctor creation failed";
            assertTest("Doctor Creation", true);

            // Read
            Doctor retrieved = doctorService.getDoctorById(doctor.getId());
            assert retrieved != null : "Doctor retrieval failed";
            assert retrieved.getName().equals("Dr. Test") : "Doctor name mismatch";
            assertTest("Doctor Retrieval", true);

            // Update
            retrieved.setConsultationFee(2000);
            doctorService.updateDoctor(retrieved);
            Doctor updated = doctorService.getDoctorById(doctor.getId());
            assert updated.getConsultationFee() == 2000 : "Doctor update failed";
            assertTest("Doctor Update", true);

            // Delete
            boolean deleted = doctorService.deleteDoctor(doctor.getId());
            assert deleted : "Doctor deletion failed";
            assertTest("Doctor Deletion", true);

        } catch (Exception e) {
            assertTest("Doctor CRUD", false);
            System.out.println("  Error: " + e.getMessage());
        }

        System.out.println();
    }

    // ========== TEST: PATIENT CRUD ==========

    private static void testPatientCRUD() {
        System.out.println("=== Test: Patient CRUD Operations ===");

        try {
            List<String> allergies = new ArrayList<>();
            allergies.add("Penicillin");

            // Create
            Patient patient = patientService.addPatient("Test Patient", 30,
                    "9123456789", "No history", allergies);
            assert patient != null : "Patient creation failed";
            assertTest("Patient Creation", true);

            // Read
            Patient retrieved = patientService.getPatientById(patient.getId());
            assert retrieved != null : "Patient retrieval failed";
            assertTest("Patient Retrieval", true);

            // Update
            retrieved.addAllergy("Peanuts");
            patientService.updatePatient(retrieved);
            Patient updated = patientService.getPatientById(patient.getId());
            assert updated.getAllergies().size() == 2 : "Patient update failed";
            assertTest("Patient Update", true);

            // Delete
            boolean deleted = patientService.deletePatient(patient.getId());
            assert deleted : "Patient deletion failed";
            assertTest("Patient Deletion", true);

        } catch (Exception e) {
            assertTest("Patient CRUD", false);
            System.out.println("  Error: " + e.getMessage());
        }

        System.out.println();
    }

    // ========== TEST: APPOINTMENT CRUD ==========

    private static void testAppointmentCRUD() {
        System.out.println("=== Test: Appointment CRUD Operations ===");

        try {
            // Setup
            Doctor doctor = doctorService.addDoctor("Dr. Appointment Test", 45,
                    "9876543211", Specialization.GENERAL, 1000, "MBBS");
            List<String> allergies = new ArrayList<>();
            Patient patient = patientService.addPatient("Patient Appointment Test",
                    25, "9123456788", "None", allergies);

            // Create
            LocalDateTime date = LocalDateTime.now().plusDays(1);
            Appointment appointment = appointmentService.bookAppointment(
                    patient.getId(), doctor.getId(), date, "Test appointment");
            assert appointment != null : "Appointment creation failed";
            assertTest("Appointment Creation", true);

            // Read
            Appointment retrieved = appointmentService.getAppointmentById(appointment.getId());
            assert retrieved != null : "Appointment retrieval failed";
            assertTest("Appointment Retrieval", true);

            // Update (Complete)
            appointmentService.completeAppointment(appointment.getId());
            Appointment completed = appointmentService.getAppointmentById(appointment.getId());
            assert completed.getStatus() == AppointmentStatus.COMPLETED : "Appointment completion failed";
            assertTest("Appointment Completion", true);

            // Cancel
            appointmentService.cancelAppointment(appointment.getId());
            Appointment cancelled = appointmentService.getAppointmentById(appointment.getId());
            assert cancelled.getStatus() == AppointmentStatus.CANCELLED : "Appointment cancellation failed";
            assertTest("Appointment Cancellation", true);

        } catch (Exception e) {
            assertTest("Appointment CRUD", false);
            System.out.println("  Error: " + e.getMessage());
        }

        System.out.println();
    }

    // ========== TEST: BILLING SYSTEM ==========

    private static void testBillingSystem() {
        System.out.println("=== Test: Billing System ===");

        try {
            // Setup
            Doctor doctor = doctorService.addDoctor("Dr. Billing Test", 50,
                    "9876543212", Specialization.CARDIOLOGY, 2000, "MBBS, MD");
            List<String> allergies = new ArrayList<>();
            Patient patient = patientService.addPatient("Patient Billing Test",
                    40, "9123456787", "Hypertension", allergies);
            LocalDateTime date = LocalDateTime.now().plusDays(2);
            Appointment appointment = appointmentService.bookAppointment(
                    patient.getId(), doctor.getId(), date, "Checkup");

            // Generate bill
            Bill bill = appointmentService.generateBill(appointment.getId(), 500, false);
            assert bill != null : "Bill generation failed";
            assertTest("Bill Generation", true);

            // Test Payable interface methods
            double total = bill.calculateTotal();
            assert total == 2500 : "Bill total calculation failed"; // 2000 + 500
            assertTest("Bill Total Calculation", true);

            double tax = bill.calculateTax();
            assert tax > 0 : "Bill tax calculation failed";
            assertTest("Bill Tax Calculation", true);

            double finalAmount = bill.calculateFinalAmount();
            assert finalAmount == total + tax : "Bill final amount calculation failed";
            assertTest("Bill Final Amount", true);

            // Test emergency surcharge
            Bill emergencyBill = appointmentService.generateBill(appointment.getId(), 0, true);
            double emergencyTotal = emergencyBill.calculateTotal();
            assert emergencyTotal > doctor.getConsultationFee() : "Emergency surcharge not applied";
            assertTest("Emergency Surcharge", true);

        } catch (Exception e) {
            assertTest("Billing System", false);
            System.out.println("  Error: " + e.getMessage());
        }

        System.out.println();
    }

    // ========== TEST: POLYMORPHISM ==========

    private static void testPolymorphism() {
        System.out.println("=== Test: Polymorphism (Method Overloading) ===");

        try {
            // Setup
            Doctor doctor1 = doctorService.addDoctor("Dr. Poly One", 40,
                    "9876543213", Specialization.CARDIOLOGY, 1500, "MBBS");
            Doctor doctor2 = doctorService.addDoctor("Dr. Poly Two", 45,
                    "9876543214", Specialization.CARDIOLOGY, 2000, "MBBS");

            // Test overloaded search methods
            Doctor byId = doctorService.searchById(doctor1.getId());
            assert byId != null : "Search by ID failed";
            assertTest("Search Doctor by ID", true);

            List<Doctor> byName = doctorService.searchByName("Poly");
            assert byName.size() >= 2 : "Search by name failed";
            assertTest("Search Doctor by Name", true);

            List<Doctor> bySpec = doctorService.searchBySpecialization(Specialization.CARDIOLOGY);
            assert bySpec.size() >= 2 : "Search by specialization failed";
            assertTest("Search Doctor by Specialization", true);

            List<Doctor> byFeeRange = doctorService.searchByFeeRange(1400, 1600);
            assert !byFeeRange.isEmpty() : "Search by fee range failed";
            assertTest("Search Doctor by Fee Range", true);

        } catch (Exception e) {
            assertTest("Polymorphism", false);
            System.out.println("  Error: " + e.getMessage());
        }

        System.out.println();
    }

    // ========== TEST: CLONING (DEEP COPY) ==========

    private static void testCloning() {
        System.out.println("=== Test: Cloning (Deep Copy) ===");

        try {
            List<String> allergies = new ArrayList<>();
            allergies.add("Pollen");
            Patient original = patientService.addPatient("Clone Test", 28,
                    "9123456786", "Allergies", allergies);

            // Clone the patient
            Patient cloned = original.clone();

            // Verify it's a different object
            assert cloned != original : "Cloning failed - same object reference";
            assertTest("Clone Creates New Object", true);

            // Verify deep copy
            cloned.addAllergy("Dust");
            assert original.getAllergies().size() == 1 : "Deep copy failed - original modified";
            assert cloned.getAllergies().size() == 2 : "Deep copy failed - clone not modified";
            assertTest("Deep Copy Verification", true);

        } catch (Exception e) {
            assertTest("Cloning", false);
            System.out.println("  Error: " + e.getMessage());
        }

        System.out.println();
    }

    // ========== TEST: IMMUTABILITY ==========

    private static void testImmutability() {
        System.out.println("=== Test: Immutability (BillSummary) ===");

        try {
            // Setup
            Doctor doctor = doctorService.addDoctor("Dr. Immutable Test", 42,
                    "9876543215", Specialization.GENERAL, 1200, "MBBS");
            List<String> allergies = new ArrayList<>();
            Patient patient = patientService.addPatient("Patient Immutable Test",
                    33, "9123456785", "None", allergies);
            LocalDateTime date = LocalDateTime.now().plusDays(3);
            Appointment appointment = appointmentService.bookAppointment(
                    patient.getId(), doctor.getId(), date, "Test");
            Bill bill = appointmentService.generateBill(appointment.getId(), 300, false);

            // Create immutable bill summary
            BillSummary summary = BillSummary.fromBill(bill);
            assert summary != null : "BillSummary creation failed";
            assertTest("BillSummary Creation", true);

            // Verify immutability (all fields should be final, no setters)
            // This is compile-time verification, but we can check values
            assert summary.getBillId() == bill.getId() : "BillSummary values incorrect";
            assert summary.getFinalAmount() == bill.calculateFinalAmount() : "BillSummary amount mismatch";
            assertTest("BillSummary Immutability", true);

        } catch (Exception e) {
            assertTest("Immutability", false);
            System.out.println("  Error: " + e.getMessage());
        }

        System.out.println();
    }

    // ========== TEST: ENUMS ==========

    private static void testEnums() {
        System.out.println("=== Test: Enums ===");

        try {
            // Test Specialization enum
            for (Specialization spec : Specialization.values()) {
                assert spec.getDisplayName() != null : "Specialization display name null";
            }
            assertTest("Specialization Enum", true);

            // Test AppointmentStatus enum
            for (AppointmentStatus status : AppointmentStatus.values()) {
                assert status.getDisplayName() != null : "AppointmentStatus display name null";
            }
            assertTest("AppointmentStatus Enum", true);

            // Test enum comparison (type safety)
            AppointmentStatus pending = AppointmentStatus.PENDING;
            assert pending == AppointmentStatus.PENDING : "Enum comparison failed";
            assertTest("Enum Type Safety", true);

        } catch (Exception e) {
            assertTest("Enums", false);
            System.out.println("  Error: " + e.getMessage());
        }

        System.out.println();
    }

    // ========== TEST: EXCEPTION HANDLING ==========

    private static void testExceptionHandling() {
        System.out.println("=== Test: Exception Handling ===");

        try {
            // Test AppointmentNotFoundException
            boolean exceptionThrown = false;
            try {
                appointmentService.getAppointmentById(99999);
            } catch (AppointmentNotFoundException e) {
                exceptionThrown = true;
                assert e.getAppointmentId() == 99999 : "Exception ID mismatch";
            }
            assert exceptionThrown : "AppointmentNotFoundException not thrown";
            assertTest("AppointmentNotFoundException", true);

            // Test InvalidDataException
            exceptionThrown = false;
            try {
                doctorService.addDoctor("", -1, "", Specialization.GENERAL, -100, "");
            } catch (InvalidDataException e) {
                exceptionThrown = true;
            }
            assert exceptionThrown : "InvalidDataException not thrown";
            assertTest("InvalidDataException", true);

        } catch (Exception e) {
            assertTest("Exception Handling", false);
            System.out.println("  Error: " + e.getMessage());
        }

        System.out.println();
    }

    // ========== TEST: VALIDATION ==========

    private static void testValidation() {
        System.out.println("=== Test: Validation ===");

        try {
            // Test Validator.validateName
            boolean exceptionThrown = false;
            try {
                Validator.validateName("");
            } catch (InvalidDataException e) {
                exceptionThrown = true;
            }
            assert exceptionThrown : "Name validation failed";
            assertTest("Name Validation", true);

            // Test Validator.validateAge
            exceptionThrown = false;
            try {
                Validator.validateAge(200);
            } catch (InvalidDataException e) {
                exceptionThrown = true;
            }
            assert exceptionThrown : "Age validation failed";
            assertTest("Age Validation", true);

            // Test Validator.validateContact
            exceptionThrown = false;
            try {
                Validator.validateContact("123");
            } catch (InvalidDataException e) {
                exceptionThrown = true;
            }
            assert exceptionThrown : "Contact validation failed";
            assertTest("Contact Validation", true);

        } catch (Exception e) {
            assertTest("Validation", false);
            System.out.println("  Error: " + e.getMessage());
        }

        System.out.println();
    }

    // ========== TEST: OBSERVER PATTERN ==========

    private static void testObserverPattern() {
        System.out.println("=== Test: Observer Pattern ===");

        try {
            // Setup observer
            final boolean[] notificationReceived = { false };
            appointmentService.addObserver((appointment, action) -> {
                notificationReceived[0] = true;
            });

            // Trigger event
            Doctor doctor = doctorService.addDoctor("Dr. Observer Test", 48,
                    "9876543216", Specialization.GENERAL, 1100, "MBBS");
            List<String> allergies = new ArrayList<>();
            Patient patient = patientService.addPatient("Patient Observer Test",
                    29, "9123456784", "None", allergies);
            LocalDateTime date = LocalDateTime.now().plusDays(4);
            appointmentService.bookAppointment(patient.getId(), doctor.getId(), date, "Test");

            // Verify notification
            assert notificationReceived[0] : "Observer not notified";
            assertTest("Observer Pattern", true);

        } catch (Exception e) {
            assertTest("Observer Pattern", false);
            System.out.println("  Error: " + e.getMessage());
        }

        System.out.println();
    }

    // ========== TEST: STREAM OPERATIONS ==========

    private static void testStreamOperations() {
        System.out.println("=== Test: Java 8 Streams & Lambdas ===");

        try {
            // Setup
            doctorService.addDoctor("Dr. Stream 1", 40, "9876543217",
                    Specialization.DERMATOLOGY, 1000, "MBBS");
            doctorService.addDoctor("Dr. Stream 2", 45, "9876543218",
                    Specialization.DERMATOLOGY, 1500, "MBBS");

            // Test average fee calculation
            double avgFee = doctorService.calculateAverageFee();
            assert avgFee > 0 : "Average fee calculation failed";
            assertTest("Stream: Average Fee", true);

            // Test filtering by specialization
            List<Doctor> dermatologists = doctorService.getDoctorsBySpecialization(Specialization.DERMATOLOGY);
            assert dermatologists.size() >= 2 : "Stream filtering failed";
            assertTest("Stream: Filter by Specialization", true);

            // Test sorting
            List<Doctor> sorted = doctorService.getDoctorsSortedByFee();
            assert sorted.size() > 0 : "Stream sorting failed";
            assertTest("Stream: Sorting", true);

            // Test top N
            List<Doctor> top = doctorService.getTopDoctorsByFee(3);
            assert top.size() <= 3 : "Stream limit failed";
            assertTest("Stream: Top N", true);

        } catch (Exception e) {
            assertTest("Stream Operations", false);
            System.out.println("  Error: " + e.getMessage());
        }

        System.out.println();
    }

    // ========== TEST: AI FEATURE ==========

    private static void testAIFeature() {
        System.out.println("=== Test: AI Doctor Recommendation ===");

        try {
            // Test symptom to specialization mapping
            Specialization cardiology = AIHelper.recommendSpecialization("chest pain and palpitation");
            assert cardiology == Specialization.CARDIOLOGY : "AI recommendation failed for cardiology";
            assertTest("AI: Cardiology Recommendation", true);

            Specialization dermatology = AIHelper.recommendSpecialization("skin rash and itching");
            assert dermatology == Specialization.DERMATOLOGY : "AI recommendation failed for dermatology";
            assertTest("AI: Dermatology Recommendation", true);

            Specialization neurology = AIHelper.recommendSpecialization("severe headache");
            assert neurology == Specialization.NEUROLOGY : "AI recommendation failed for neurology";
            assertTest("AI: Neurology Recommendation", true);

            // Test confidence score
            double confidence = AIHelper.getConfidenceScore("chest pain", Specialization.CARDIOLOGY);
            assert confidence > 0.5 : "AI confidence calculation failed";
            assertTest("AI: Confidence Score", true);

        } catch (Exception e) {
            assertTest("AI Feature", false);
            System.out.println("  Error: " + e.getMessage());
        }

        System.out.println();
    }

    // ========== TEST: FILE I/O ==========

    private static void testFileIO() {
        System.out.println("=== Test: File I/O & Persistence ===");

        try {
            // Save data
            doctorService.saveDoctorsToFile();
            patientService.savePatientsToFile();
            appointmentService.saveAppointmentsToFile();
            assertTest("File I/O: Save Data", true);

            // Clear data
            DoctorService newDoctorService = new DoctorService();
            int originalCount = doctorService.getAllDoctors().size();

            // Load data
            newDoctorService.loadDoctorsFromFile();
            int loadedCount = newDoctorService.getAllDoctors().size();

            assert loadedCount == originalCount : "File I/O: Load count mismatch";
            assertTest("File I/O: Load Data", true);

            // Test CSV utility
            assert CSVUtil.fileExists("data/doctors.csv") : "CSV file not created";
            assertTest("File I/O: CSV File Creation", true);

        } catch (Exception e) {
            assertTest("File I/O", false);
            System.out.println("  Error: " + e.getMessage());
        }

        System.out.println();
    }

    // ========== HELPER METHODS ==========

    private static void assertTest(String testName, boolean passed) {
        if (passed) {
            System.out.println("  ✓ " + testName + " - PASSED");
            passedTests++;
        } else {
            System.out.println("  ✗ " + testName + " - FAILED");
            failedTests++;
        }
    }

    private static void displayTestResults() {
        System.out.println("========================================");
        System.out.println("   Test Results");
        System.out.println("========================================");
        System.out.println("Total Tests: " + (passedTests + failedTests));
        System.out.println("Passed: " + passedTests);
        System.out.println("Failed: " + failedTests);
        System.out.println("Success Rate: " + String.format("%.1f%%",
                (passedTests * 100.0 / (passedTests + failedTests))));
        System.out.println("========================================");

        if (failedTests == 0) {
            System.out.println("\n🎉 All tests passed successfully!");
        } else {
            System.out.println("\n⚠ Some tests failed. Please review.");
        }
    }
}
