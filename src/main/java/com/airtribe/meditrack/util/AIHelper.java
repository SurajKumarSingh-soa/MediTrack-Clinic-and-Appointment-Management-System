package com.airtribe.meditrack.util;

import com.airtribe.meditrack.constants.Specialization;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.List;

/**
 * AI Helper for rule-based doctor recommendations
 * Demonstrates: AI feature (bonus requirement)
 */
public class AIHelper {

    // Symptom to specialization mapping
    private static final Map<String, Specialization> symptomMap = new HashMap<>();

    // Static initialization block
    static {
        // Cardiology symptoms
        symptomMap.put("chest pain", Specialization.CARDIOLOGY);
        symptomMap.put("heart", Specialization.CARDIOLOGY);
        symptomMap.put("palpitation", Specialization.CARDIOLOGY);
        symptomMap.put("blood pressure", Specialization.CARDIOLOGY);

        // Dermatology symptoms
        symptomMap.put("skin", Specialization.DERMATOLOGY);
        symptomMap.put("rash", Specialization.DERMATOLOGY);
        symptomMap.put("acne", Specialization.DERMATOLOGY);
        symptomMap.put("itch", Specialization.DERMATOLOGY);

        // Orthopedics symptoms
        symptomMap.put("bone", Specialization.ORTHOPEDICS);
        symptomMap.put("fracture", Specialization.ORTHOPEDICS);
        symptomMap.put("joint", Specialization.ORTHOPEDICS);
        symptomMap.put("back pain", Specialization.ORTHOPEDICS);

        // Pediatrics symptoms
        symptomMap.put("child", Specialization.PEDIATRICS);
        symptomMap.put("infant", Specialization.PEDIATRICS);
        symptomMap.put("baby", Specialization.PEDIATRICS);

        // Neurology symptoms
        symptomMap.put("headache", Specialization.NEUROLOGY);
        symptomMap.put("migraine", Specialization.NEUROLOGY);
        symptomMap.put("seizure", Specialization.NEUROLOGY);
        symptomMap.put("brain", Specialization.NEUROLOGY);

        // Psychiatry symptoms
        symptomMap.put("depression", Specialization.PSYCHIATRY);
        symptomMap.put("anxiety", Specialization.PSYCHIATRY);
        symptomMap.put("stress", Specialization.PSYCHIATRY);
        symptomMap.put("mental", Specialization.PSYCHIATRY);

        // Ophthalmology symptoms
        symptomMap.put("eye", Specialization.OPHTHALMOLOGY);
        symptomMap.put("vision", Specialization.OPHTHALMOLOGY);
        symptomMap.put("sight", Specialization.OPHTHALMOLOGY);

        // ENT symptoms
        symptomMap.put("ear", Specialization.ENT);
        symptomMap.put("nose", Specialization.ENT);
        symptomMap.put("throat", Specialization.ENT);
        symptomMap.put("hearing", Specialization.ENT);

        // Gynecology symptoms
        symptomMap.put("pregnancy", Specialization.GYNECOLOGY);
        symptomMap.put("menstrual", Specialization.GYNECOLOGY);
        symptomMap.put("gynec", Specialization.GYNECOLOGY);
    }

    /**
     * Recommend specialization based on symptoms
     */
    public static Specialization recommendSpecialization(String symptoms) {
        if (symptoms == null || symptoms.trim().isEmpty()) {
            return Specialization.GENERAL;
        }

        String lowerSymptoms = symptoms.toLowerCase();

        // Check for keyword matches
        for (Map.Entry<String, Specialization> entry : symptomMap.entrySet()) {
            if (lowerSymptoms.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        // Default to general medicine
        return Specialization.GENERAL;
    }

    /**
     * Get confidence score for recommendation
     */
    public static double getConfidenceScore(String symptoms, Specialization recommendation) {
        if (symptoms == null || symptoms.trim().isEmpty()) {
            return 0.5;
        }

        String lowerSymptoms = symptoms.toLowerCase();
        int matchCount = 0;
        int totalKeywords = 0;

        for (Map.Entry<String, Specialization> entry : symptomMap.entrySet()) {
            if (entry.getValue() == recommendation) {
                totalKeywords++;
                if (lowerSymptoms.contains(entry.getKey())) {
                    matchCount++;
                }
            }
        }

        if (totalKeywords == 0)
            return 0.5;
        return Math.min(0.5 + (matchCount * 0.2), 1.0);
    }

    /**
     * Generate recommendation report
     */
    public static String generateRecommendationReport(String symptoms) {
        Specialization recommended = recommendSpecialization(symptoms);
        double confidence = getConfidenceScore(symptoms, recommended);

        return String.format("Recommended Specialization: %s (Confidence: %.0f%%)",
                recommended.getDisplayName(), confidence * 100);
    }

    // Private constructor
    private AIHelper() {
        throw new AssertionError("Cannot instantiate AIHelper");
    }
}
