package com.airtribe.meditrack.api;

import com.airtribe.meditrack.constants.AppointmentStatus;
import com.airtribe.meditrack.constants.Specialization;
import com.airtribe.meditrack.entity.*;
import com.airtribe.meditrack.service.*;
import com.airtribe.meditrack.util.AIHelper;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Built-in HTTP REST API Server for MediTrack.
 * Exposes JSON REST endpoints for the React frontend application.
 */
public class MediTrackServer {

    private static final int PORT = 8080;
    private static DoctorService doctorService;
    private static PatientService patientService;
    private static AppointmentService appointmentService;

    public static void main(String[] args) throws IOException {
        doctorService = new DoctorService();
        patientService = new PatientService();
        appointmentService = new AppointmentService(doctorService, patientService);

        // Load existing data if available
        try {
            doctorService.loadDoctorsFromFile();
            patientService.loadPatientsFromFile();
            appointmentService.loadAppointmentsFromFile();
            System.out.println("[INFO] Loaded existing persistent data into memory.");
        } catch (Exception e) {
            System.out.println("[INFO] No previous CSV data loaded, starting fresh or with in-memory state.");
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api", new ApiHandler());
        server.setExecutor(null);

        System.out.println("=========================================");
        System.out.println("🚀 MediTrack REST API Server is running!");
        System.out.println("📡 Server listening on: http://localhost:" + PORT + "/api");
        System.out.println("=========================================");

        server.start();
    }

    static class ApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Enable CORS
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();
            String method = exchange.getRequestMethod();

            try {
                if (path.startsWith("/api/doctors")) {
                    handleDoctors(exchange, method, query);
                } else if (path.startsWith("/api/patients")) {
                    handlePatients(exchange, method, query);
                } else if (path.startsWith("/api/appointments")) {
                    handleAppointments(exchange, method, query, path);
                } else if (path.startsWith("/api/billing")) {
                    handleBilling(exchange, method);
                } else if (path.startsWith("/api/ai")) {
                    handleAI(exchange, method);
                } else if (path.startsWith("/api/analytics")) {
                    handleAnalytics(exchange);
                } else if (path.startsWith("/api/data/save")) {
                    handleSaveData(exchange);
                } else if (path.startsWith("/api/data/load")) {
                    handleLoadData(exchange);
                } else {
                    sendJsonResponse(exchange, 404, "{\"error\": \"Endpoint not found\"}");
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendJsonResponse(exchange, 500, "{\"error\": \"" + escapeJson(e.getMessage()) + "\"}");
            }
        }

        // --- DOCTORS HANDLER ---
        private void handleDoctors(HttpExchange exchange, String method, String query) throws Exception {
            if ("GET".equals(method)) {
                Map<String, String> params = parseQueryParams(query);
                List<Doctor> doctors = doctorService.getAllDoctors();

                if (params.containsKey("q")) {
                    String q = params.get("q").toLowerCase();
                    doctors = doctors.stream()
                        .filter(d -> d.getName().toLowerCase().contains(q) ||
                                     d.getSpecialization().name().toLowerCase().contains(q) ||
                                     d.getQualification().toLowerCase().contains(q))
                        .toList();
                } else if (params.containsKey("spec")) {
                    String specStr = params.get("spec");
                    doctors = doctors.stream()
                        .filter(d -> d.getSpecialization().name().equalsIgnoreCase(specStr))
                        .toList();
                }

                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < doctors.size(); i++) {
                    sb.append(doctorToJson(doctors.get(i)));
                    if (i < doctors.size() - 1) sb.append(",");
                }
                sb.append("]");
                sendJsonResponse(exchange, 200, sb.toString());

            } else if ("POST".equals(method)) {
                String body = readRequestBody(exchange);
                Map<String, String> json = parseSimpleJson(body);

                String name = json.getOrDefault("name", "");
                int age = Integer.parseInt(json.getOrDefault("age", "30"));
                String contact = json.getOrDefault("contact", "");
                Specialization spec = Specialization.valueOf(json.getOrDefault("specialization", "GENERAL"));
                double fee = Double.parseDouble(json.getOrDefault("consultationFee", "500"));
                String qual = json.getOrDefault("qualification", "MBBS");

                Doctor doc = doctorService.addDoctor(name, age, contact, spec, fee, qual);
                sendJsonResponse(exchange, 201, doctorToJson(doc));

            } else if ("PUT".equals(method)) {
                String body = readRequestBody(exchange);
                Map<String, String> json = parseSimpleJson(body);
                int id = Integer.parseInt(json.get("id"));

                Doctor doc = doctorService.getDoctorById(id);
                if (doc != null) {
                    if (json.containsKey("name")) doc.setName(json.get("name"));
                    if (json.containsKey("age")) doc.setAge(Integer.parseInt(json.get("age")));
                    if (json.containsKey("contact")) doc.setContact(json.get("contact"));
                    if (json.containsKey("specialization")) doc.setSpecialization(Specialization.valueOf(json.get("specialization")));
                    if (json.containsKey("consultationFee")) doc.setConsultationFee(Double.parseDouble(json.get("consultationFee")));
                    if (json.containsKey("qualification")) doc.setQualification(json.get("qualification"));

                    doctorService.updateDoctor(doc);
                    sendJsonResponse(exchange, 200, doctorToJson(doc));
                } else {
                    sendJsonResponse(exchange, 404, "{\"error\": \"Doctor not found\"}");
                }

            } else if ("DELETE".equals(method)) {
                Map<String, String> params = parseQueryParams(query);
                if (params.containsKey("id")) {
                    int id = Integer.parseInt(params.get("id"));
                    boolean deleted = doctorService.deleteDoctor(id);
                    sendJsonResponse(exchange, 200, "{\"success\": " + deleted + "}");
                } else {
                    sendJsonResponse(exchange, 400, "{\"error\": \"Missing doctor id\"}");
                }
            }
        }

        // --- PATIENTS HANDLER ---
        private void handlePatients(HttpExchange exchange, String method, String query) throws Exception {
            if ("GET".equals(method)) {
                Map<String, String> params = parseQueryParams(query);
                List<Patient> patients = patientService.getAllPatients();

                if (params.containsKey("q")) {
                    String q = params.get("q").toLowerCase();
                    patients = patients.stream()
                        .filter(p -> p.getName().toLowerCase().contains(q) ||
                                     p.getMedicalHistory().toLowerCase().contains(q) ||
                                     p.getContact().contains(q))
                        .toList();
                }

                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < patients.size(); i++) {
                    sb.append(patientToJson(patients.get(i)));
                    if (i < patients.size() - 1) sb.append(",");
                }
                sb.append("]");
                sendJsonResponse(exchange, 200, sb.toString());

            } else if ("POST".equals(method)) {
                String body = readRequestBody(exchange);
                Map<String, String> json = parseSimpleJson(body);

                String name = json.getOrDefault("name", "");
                int age = Integer.parseInt(json.getOrDefault("age", "25"));
                String contact = json.getOrDefault("contact", "");
                String history = json.getOrDefault("medicalHistory", "None");

                List<String> allergies = new ArrayList<>();
                if (json.containsKey("allergies")) {
                    String[] raw = json.get("allergies").split(",");
                    for (String a : raw) if (!a.trim().isEmpty()) allergies.add(a.trim());
                }

                Patient p = patientService.addPatient(name, age, contact, history, allergies);
                sendJsonResponse(exchange, 201, patientToJson(p));

            } else if ("PUT".equals(method)) {
                String body = readRequestBody(exchange);
                Map<String, String> json = parseSimpleJson(body);
                int id = Integer.parseInt(json.get("id"));

                Patient p = patientService.getPatientById(id);
                if (p != null) {
                    if (json.containsKey("name")) p.setName(json.get("name"));
                    if (json.containsKey("age")) p.setAge(Integer.parseInt(json.get("age")));
                    if (json.containsKey("contact")) p.setContact(json.get("contact"));
                    if (json.containsKey("medicalHistory")) p.setMedicalHistory(json.get("medicalHistory"));
                    if (json.containsKey("allergies")) {
                        List<String> allergies = new ArrayList<>();
                        for (String a : json.get("allergies").split(",")) if (!a.trim().isEmpty()) allergies.add(a.trim());
                        p.setAllergies(allergies);
                    }

                    patientService.updatePatient(p);
                    sendJsonResponse(exchange, 200, patientToJson(p));
                } else {
                    sendJsonResponse(exchange, 404, "{\"error\": \"Patient not found\"}");
                }

            } else if ("DELETE".equals(method)) {
                Map<String, String> params = parseQueryParams(query);
                if (params.containsKey("id")) {
                    int id = Integer.parseInt(params.get("id"));
                    boolean deleted = patientService.deletePatient(id);
                    sendJsonResponse(exchange, 200, "{\"success\": " + deleted + "}");
                } else {
                    sendJsonResponse(exchange, 400, "{\"error\": \"Missing patient id\"}");
                }
            }
        }

        // --- APPOINTMENTS HANDLER ---
        private void handleAppointments(HttpExchange exchange, String method, String query, String path) throws Exception {
            if (path.endsWith("/complete") && "PUT".equals(method)) {
                Map<String, String> params = parseQueryParams(query);
                int id = Integer.parseInt(params.get("id"));
                appointmentService.completeAppointment(id);
                sendJsonResponse(exchange, 200, "{\"success\": true, \"message\": \"Appointment completed\"}");
                return;
            }

            if (path.endsWith("/cancel") && "PUT".equals(method)) {
                Map<String, String> params = parseQueryParams(query);
                int id = Integer.parseInt(params.get("id"));
                appointmentService.cancelAppointment(id);
                sendJsonResponse(exchange, 200, "{\"success\": true, \"message\": \"Appointment cancelled\"}");
                return;
            }

            if ("GET".equals(method)) {
                List<Appointment> appts = appointmentService.getAllAppointments();
                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < appts.size(); i++) {
                    sb.append(appointmentToJson(appts.get(i)));
                    if (i < appts.size() - 1) sb.append(",");
                }
                sb.append("]");
                sendJsonResponse(exchange, 200, sb.toString());

            } else if ("POST".equals(method)) {
                String body = readRequestBody(exchange);
                Map<String, String> json = parseSimpleJson(body);

                int patientId = Integer.parseInt(json.get("patientId"));
                int doctorId = Integer.parseInt(json.get("doctorId"));
                String dateStr = json.getOrDefault("date", LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                String notes = json.getOrDefault("notes", "Routine checkup");

                LocalDateTime date;
                try {
                    date = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                } catch (Exception e) {
                    date = LocalDateTime.now().plusDays(1);
                }

                Appointment appt = appointmentService.bookAppointment(patientId, doctorId, date, notes);
                sendJsonResponse(exchange, 201, appointmentToJson(appt));
            }
        }

        // --- BILLING HANDLER ---
        private void handleBilling(HttpExchange exchange, String method) throws Exception {
            if ("POST".equals(method)) {
                String body = readRequestBody(exchange);
                Map<String, String> json = parseSimpleJson(body);

                int appointmentId = Integer.parseInt(json.get("appointmentId"));
                double additionalCharges = Double.parseDouble(json.getOrDefault("additionalCharges", "0"));
                boolean isEmergency = Boolean.parseBoolean(json.getOrDefault("isEmergency", "false"));

                Bill bill = appointmentService.generateBill(appointmentId, additionalCharges, isEmergency);
                BillSummary summary = BillSummary.fromBill(bill);

                String res = String.format(Locale.US,
                    "{\"billId\": %d, \"appointmentId\": %d, \"patientName\": \"%s\", \"doctorName\": \"%s\", \"consultationFee\": %.2f, \"additionalCharges\": %.2f, \"isEmergency\": %b, \"totalAmount\": %.2f, \"tax\": %.2f, \"finalAmount\": %.2f, \"generatedDate\": \"%s\"}",
                    bill.getId(), bill.getAppointment().getId(),
                    escapeJson(bill.getAppointment().getPatient().getName()),
                    escapeJson(bill.getAppointment().getDoctor().getName()),
                    bill.getAppointment().getDoctor().getConsultationFee(),
                    bill.getAdditionalCharges(),
                    bill.isEmergency(),
                    bill.calculateTotal(),
                    bill.calculateTax(),
                    summary.getFinalAmount(),
                    summary.getBillDate().toString()
                );
                sendJsonResponse(exchange, 200, res);
            }
        }

        // --- AI RECOMMENDATION HANDLER ---
        private void handleAI(HttpExchange exchange, String method) throws IOException {
            if ("POST".equals(method)) {
                String body = readRequestBody(exchange);
                Map<String, String> json = parseSimpleJson(body);
                String symptoms = json.getOrDefault("symptoms", "");

                Specialization recommended = AIHelper.recommendSpecialization(symptoms);
                double confidence = AIHelper.getConfidenceScore(symptoms, recommended);

                List<Doctor> matchingDoctors = doctorService.getDoctorsBySpecialization(recommended);

                StringBuilder docsJson = new StringBuilder("[");
                for (int i = 0; i < matchingDoctors.size(); i++) {
                    docsJson.append(doctorToJson(matchingDoctors.get(i)));
                    if (i < matchingDoctors.size() - 1) docsJson.append(",");
                }
                docsJson.append("]");

                String res = String.format(Locale.US,
                    "{\"symptoms\": \"%s\", \"recommendedSpecialization\": \"%s\", \"confidenceScore\": %.2f, \"recommendedDoctors\": %s}",
                    escapeJson(symptoms), recommended.name(), confidence, docsJson.toString());

                sendJsonResponse(exchange, 200, res);
            }
        }

        // --- ANALYTICS HANDLER ---
        private void handleAnalytics(HttpExchange exchange) throws IOException {
            int totalDoctors = doctorService.getAllDoctors().size();
            int totalPatients = patientService.getAllPatients().size();
            int totalAppointments = appointmentService.getAllAppointments().size();
            double avgDoctorFee = doctorService.calculateAverageFee();

            long completedAppts = appointmentService.getAllAppointments().stream()
                .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED).count();
            long cancelledAppts = appointmentService.getAllAppointments().stream()
                .filter(a -> a.getStatus() == AppointmentStatus.CANCELLED).count();
            long pendingAppts = appointmentService.getAllAppointments().stream()
                .filter(a -> a.getStatus() == AppointmentStatus.PENDING).count();

            Map<Specialization, Long> specCounts = doctorService.getAllDoctors().stream()
                .collect(Collectors.groupingBy(Doctor::getSpecialization, Collectors.counting()));
            StringBuilder specSb = new StringBuilder("{");
            int idx = 0;
            for (Map.Entry<Specialization, Long> entry : specCounts.entrySet()) {
                specSb.append(String.format("\"%s\": %d", entry.getKey().name(), entry.getValue()));
                if (idx < specCounts.size() - 1) specSb.append(",");
                idx++;
            }
            specSb.append("}");

            String res = String.format(Locale.US,
                "{\"totalDoctors\": %d, \"totalPatients\": %d, \"totalAppointments\": %d, \"completedAppointments\": %d, \"pendingAppointments\": %d, \"cancelledAppointments\": %d, \"averageDoctorFee\": %.2f, \"specializationBreakdown\": %s}",
                totalDoctors, totalPatients, totalAppointments, completedAppts, pendingAppts, cancelledAppts, avgDoctorFee, specSb.toString());

            sendJsonResponse(exchange, 200, res);
        }

        // --- DATA SAVE / LOAD ---
        private void handleSaveData(HttpExchange exchange) throws IOException {
            try {
                doctorService.saveDoctorsToFile();
                patientService.savePatientsToFile();
                appointmentService.saveAppointmentsToFile();
                sendJsonResponse(exchange, 200, "{\"success\": true, \"message\": \"Data saved to CSV files successfully!\"}");
            } catch (Exception e) {
                sendJsonResponse(exchange, 500, "{\"success\": false, \"error\": \"" + escapeJson(e.getMessage()) + "\"}");
            }
        }

        private void handleLoadData(HttpExchange exchange) throws IOException {
            try {
                doctorService.loadDoctorsFromFile();
                patientService.loadPatientsFromFile();
                appointmentService.loadAppointmentsFromFile();
                sendJsonResponse(exchange, 200, "{\"success\": true, \"message\": \"Data loaded from CSV files successfully!\"}");
            } catch (Exception e) {
                sendJsonResponse(exchange, 500, "{\"success\": false, \"error\": \"" + escapeJson(e.getMessage()) + "\"}");
            }
        }

        // --- HELPER SERIALIZERS ---
        private String doctorToJson(Doctor d) {
            return String.format(Locale.US,
                "{\"id\": %d, \"name\": \"%s\", \"age\": %d, \"contact\": \"%s\", \"role\": \"%s\", \"specialization\": \"%s\", \"consultationFee\": %.2f, \"qualification\": \"%s\"}",
                d.getId(), escapeJson(d.getName()), d.getAge(), escapeJson(d.getContact()),
                d.getRole(), d.getSpecialization().name(), d.getConsultationFee(), escapeJson(d.getQualification()));
        }

        private String patientToJson(Patient p) {
            StringBuilder allergiesSb = new StringBuilder("[");
            for (int i = 0; i < p.getAllergies().size(); i++) {
                allergiesSb.append("\"").append(escapeJson(p.getAllergies().get(i))).append("\"");
                if (i < p.getAllergies().size() - 1) allergiesSb.append(",");
            }
            allergiesSb.append("]");

            return String.format(Locale.US,
                "{\"id\": %d, \"name\": \"%s\", \"age\": %d, \"contact\": \"%s\", \"role\": \"%s\", \"medicalHistory\": \"%s\", \"allergies\": %s}",
                p.getId(), escapeJson(p.getName()), p.getAge(), escapeJson(p.getContact()),
                p.getRole(), escapeJson(p.getMedicalHistory()), allergiesSb.toString());
        }

        private String appointmentToJson(Appointment a) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return String.format(Locale.US,
                "{\"id\": %d, \"patient\": %s, \"doctor\": %s, \"appointmentDate\": \"%s\", \"status\": \"%s\", \"notes\": \"%s\"}",
                a.getId(), patientToJson(a.getPatient()), doctorToJson(a.getDoctor()),
                a.getAppointmentDate().format(formatter), a.getStatus().name(), escapeJson(a.getNotes()));
        }

        private String readRequestBody(HttpExchange exchange) throws IOException {
            InputStream is = exchange.getRequestBody();
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }

        private void sendJsonResponse(HttpExchange exchange, int statusCode, String responseJson) throws IOException {
            byte[] bytes = responseJson.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(statusCode, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }

        private Map<String, String> parseQueryParams(String query) {
            Map<String, String> map = new HashMap<>();
            if (query == null || query.isEmpty()) return map;
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length > 1) map.put(pair[0], pair[1]);
                else if (pair.length == 1) map.put(pair[0], "");
            }
            return map;
        }

        private Map<String, String> parseSimpleJson(String json) {
            Map<String, String> map = new HashMap<>();
            if (json == null || json.trim().isEmpty()) return map;
            Matcher m = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(\"[^\"]*\"|true|false|[0-9\\.]+)").matcher(json);
            while (m.find()) {
                String key = m.group(1);
                String val = m.group(2).replaceAll("^\"|\"$", "");
                map.put(key, val);
            }
            return map;
        }

        private String escapeJson(String s) {
            if (s == null) return "";
            return s.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\b", "\\b")
                    .replace("\f", "\\f")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
        }
    }
}
