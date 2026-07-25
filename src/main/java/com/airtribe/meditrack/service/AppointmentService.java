package com.airtribe.meditrack.service;

import com.airtribe.meditrack.constants.AppointmentStatus;
import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.Bill;
import com.airtribe.meditrack.entity.BillSummary;
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.exception.AppointmentNotFoundException;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.util.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for Appointment operations
 * Demonstrates: CRUD, Observer pattern (notifications), Strategy pattern
 * (billing)
 */
public class AppointmentService {

    private final DataStore<Appointment> appointmentStore;
    private final DataStore<Bill> billStore;
    private final IdGenerator idGenerator;
    private final DoctorService doctorService;
    private final PatientService patientService;

    // Observer pattern - listeners for appointment changes
    private final List<AppointmentObserver> observers;

    public AppointmentService(DoctorService doctorService, PatientService patientService) {
        this.appointmentStore = new DataStore<>();
        this.billStore = new DataStore<>();
        this.idGenerator = IdGenerator.getInstance();
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.observers = new ArrayList<>();
    }

    /**
     * Book a new appointment
     */
    public Appointment bookAppointment(int patientId, int doctorId,
            LocalDateTime appointmentDate, String notes)
            throws InvalidDataException, AppointmentNotFoundException {
        // Get patient and doctor
        Patient patient = patientService.getPatientById(patientId);
        Doctor doctor = doctorService.getDoctorById(doctorId);

        if (patient == null) {
            throw new AppointmentNotFoundException("Patient not found with ID: " + patientId);
        }
        if (doctor == null) {
            throw new AppointmentNotFoundException("Doctor not found with ID: " + doctorId);
        }

        // Create appointment
        int id = idGenerator.generateAppointmentId();
        Appointment appointment = new Appointment(id, patient, doctor, appointmentDate,
                AppointmentStatus.CONFIRMED, notes);
        appointment.validate();

        appointmentStore.add(id, appointment);
        System.out.println("[SUCCESS] Appointment booked (ID: " + id + ")");

        // Notify observers
        notifyObservers(appointment, "BOOKED");

        return appointment;
    }

    /**
     * Get appointment by ID
     */
    public Appointment getAppointmentById(int id) throws AppointmentNotFoundException {
        Appointment appointment = appointmentStore.getById(id);
        if (appointment == null) {
            throw new AppointmentNotFoundException(id);
        }
        return appointment;
    }

    /**
     * Get all appointments
     */
    public List<Appointment> getAllAppointments() {
        return appointmentStore.getAll();
    }

    /**
     * Cancel appointment
     */
    public void cancelAppointment(int id) throws AppointmentNotFoundException {
        Appointment appointment = getAppointmentById(id);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentStore.update(id, appointment);

        System.out.println("[SUCCESS] Appointment cancelled (ID: " + id + ")");
        notifyObservers(appointment, "CANCELLED");
    }

    /**
     * Complete appointment
     */
    public void completeAppointment(int id) throws AppointmentNotFoundException {
        Appointment appointment = getAppointmentById(id);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentStore.update(id, appointment);

        System.out.println("[SUCCESS] Appointment completed (ID: " + id + ")");
        notifyObservers(appointment, "COMPLETED");
    }

    /**
     * Get appointments by patient
     */
    public List<Appointment> getAppointmentsByPatient(int patientId) {
        return appointmentStore.search(a -> a.getPatient().getId() == patientId);
    }

    /**
     * Get appointments by doctor
     */
    public List<Appointment> getAppointmentsByDoctor(int doctorId) {
        return appointmentStore.search(a -> a.getDoctor().getId() == doctorId);
    }

    /**
     * Get appointments by status
     */
    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentStore.search(a -> a.getStatus() == status);
    }

    /**
     * Get upcoming appointments (sorted by date)
     */
    public List<Appointment> getUpcomingAppointments() {
        return appointmentStore.getAll().stream()
                .filter(a -> a.getAppointmentDate().isAfter(LocalDateTime.now()))
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .sorted(Comparator.comparing(Appointment::getAppointmentDate))
                .collect(Collectors.toList());
    }

    // ========== BILLING (Strategy Pattern & Payable Interface) ==========

    
    public Bill generateBill(int appointmentId, double additionalCharges, boolean isEmergency)
            throws AppointmentNotFoundException {
        Appointment appointment = getAppointmentById(appointmentId);

        int billId = idGenerator.generateBillId();
        double consultationFee = appointment.getDoctor().getConsultationFee();

        Bill bill = new Bill(billId, appointment, consultationFee,
                additionalCharges, isEmergency);

        billStore.add(billId, bill);
        System.out.println("[SUCCESS] Bill generated (ID: " + billId + ")");

        return bill;
    }

    /**
     * Get bill by ID
     */
    public Bill getBillById(int billId) {
        return billStore.getById(billId);
    }

    /**
     * Get all bills
     */
    public List<Bill> getAllBills() {
        return billStore.getAll();
    }

    /**
     * Create immutable bill summary
     */
    public BillSummary createBillSummary(int billId) {
        Bill bill = billStore.getById(billId);
        if (bill == null)
            return null;
        return BillSummary.fromBill(bill);
    }

    // ========== JAVA 8 STREAMS & ANALYTICS ==========

    /**
     * Get appointments per doctor (analytics)
     */
    public Map<String, Long> getAppointmentsPerDoctor() {
        return appointmentStore.getAll().stream()
                .collect(Collectors.groupingBy(
                        a -> a.getDoctor().getName(),
                        Collectors.counting()));
    }

    /**
     * Calculate total revenue
     */
    public double calculateTotalRevenue() {
        return billStore.getAll().stream()
                .mapToDouble(Bill::calculateFinalAmount)
                .sum();
    }

    /**
     * Get average bill amount
     */
    public double getAverageBillAmount() {
        return billStore.getAll().stream()
                .mapToDouble(Bill::calculateFinalAmount)
                .average()
                .orElse(0.0);
    }

    // ========== OBSERVER PATTERN ==========

    /**
     * Observer interface for appointment notifications
     */
    public interface AppointmentObserver {
        void onAppointmentChange(Appointment appointment, String action);
    }

    /**
     * Add observer
     */
    public void addObserver(AppointmentObserver observer) {
        observers.add(observer);
    }

    /**
     * Notify all observers
     */
    private void notifyObservers(Appointment appointment, String action) {
        for (AppointmentObserver observer : observers) {
            observer.onAppointmentChange(appointment, action);
        }
    }

    // ========== FILE I/O PERSISTENCE ==========

    /**
     * Save appointments to CSV file
     */
    public void saveAppointmentsToFile() throws IOException {
        List<String> lines = appointmentStore.getAll().stream()
                .map(Appointment::toCSV)
                .collect(Collectors.toList());

        CSVUtil.writeCSV(Constants.APPOINTMENTS_FILE, lines);
        System.out.println("[INFO] Saved " + lines.size() + " appointments to file");
    }

    /**
     * Load appointments from CSV file
     */
    public void loadAppointmentsFromFile() throws IOException {
        if (!CSVUtil.fileExists(Constants.APPOINTMENTS_FILE)) {
            System.out.println("[INFO] No appointments file found, starting fresh");
            return;
        }

        List<String> lines = CSVUtil.readCSV(Constants.APPOINTMENTS_FILE);
        int count = 0;

        for (String line : lines) {
            try {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                int patientId = Integer.parseInt(parts[1]);
                int doctorId = Integer.parseInt(parts[2]);
                LocalDateTime date = DateUtil.parseDate(parts[3]);
                AppointmentStatus status = AppointmentStatus.valueOf(parts[4]);
                String notes = parts.length > 5 ? parts[5].replace("~", ",") : "";

                Patient patient = patientService.getPatientById(patientId);
                Doctor doctor = doctorService.getDoctorById(doctorId);

                if (patient != null && doctor != null) {
                    Appointment appointment = new Appointment(id, patient, doctor, date, status, notes);
                    appointmentStore.add(id, appointment);

                    // Update ID generator
                    idGenerator.setCounters(0, 0, id, 0);
                    count++;
                }
            } catch (Exception e) {
                System.err.println("[ERROR] Failed to load appointment: " + line);
            }
        }

        System.out.println("[INFO] Loaded " + count + " appointments from file");
    }

    /**
     * Display statistics
     */
    public void displayStatistics() {
        System.out.println("\n===== Appointment Statistics =====");
        System.out.println("Total Appointments: " + appointmentStore.size());
        System.out.println("Total Bills: " + billStore.size());
        System.out.println("Total Revenue: ₹" + String.format("%.2f", calculateTotalRevenue()));
        System.out.println("Average Bill: ₹" + String.format("%.2f", getAverageBillAmount()));

        System.out.println("\nAppointments by Status:");
        for (AppointmentStatus status : AppointmentStatus.values()) {
            long count = appointmentStore.getAll().stream()
                    .filter(a -> a.getStatus() == status)
                    .count();
            if (count > 0) {
                System.out.println("  " + status + ": " + count);
            }
        }
    }
}
