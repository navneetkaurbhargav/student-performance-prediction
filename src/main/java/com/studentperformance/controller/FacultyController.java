package com.studentperformance.controller;

import com.studentperformance.model.dto.PredictionRequest;
import com.studentperformance.model.dto.PredictionResponse;
import com.studentperformance.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/faculty")
public class FacultyController {

    @Autowired
    private PredictionService predictionService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        model.addAttribute("role", "FACULTY");

        // Add faculty dashboard data
        model.addAttribute("totalStudents", 45);
        model.addAttribute("averagePassRate", 72.5);
        model.addAttribute("atRiskStudents", 8);
        model.addAttribute("totalPredictions", 120);

        return "faculty/dashboard";
    }

    @GetMapping("/batch-predict")
    public String showBatchPredictionForm(Model model) {
        List<PredictionRequest> requests = new ArrayList<>();
        // Add 3 empty request forms by default
        for (int i = 0; i < 3; i++) {
            requests.add(new PredictionRequest());
        }
        model.addAttribute("requests", requests);
        return "faculty/batch-predict";
    }

    @PostMapping("/batch-predict")
    public String makeBatchPredictions(
            @RequestParam(value = "attendanceRate", required = false) List<Double> attendanceRates,
            @RequestParam(value = "previousGPA", required = false) List<Double> previousGPAs,
            @RequestParam(value = "studyHoursWeekly", required = false) List<Integer> studyHours,
            @RequestParam(value = "assignmentScoresAvg", required = false) List<Double> assignmentScores,
            @RequestParam(value = "extracurricularHours", required = false) List<Integer> extracurricularHours,
            @RequestParam(value = "familySupport", required = false) List<Integer> familySupports,
            @RequestParam(value = "financialStability", required = false) List<Integer> financialStabilities,
            Model model) {

        List<PredictionRequest> requests = new ArrayList<>();
        List<PredictionResponse> responses = new ArrayList<>();

        try {
            // Validate input
            if (attendanceRates == null || attendanceRates.isEmpty()) {
                throw new IllegalArgumentException("No student data provided");
            }

            // Create request objects
            for (int i = 0; i < attendanceRates.size(); i++) {
                PredictionRequest request = new PredictionRequest();
                request.setAttendanceRate(attendanceRates.get(i));
                request.setPreviousGPA(previousGPAs.get(i));
                request.setStudyHoursWeekly(studyHours.get(i));
                request.setAssignmentScoresAvg(assignmentScores.get(i));
                request.setExtracurricularHours(extracurricularHours.get(i));
                request.setFamilySupport(familySupports.get(i));
                request.setFinancialStability(financialStabilities.get(i));
                requests.add(request);

                // Get prediction for each student
                PredictionResponse response = predictionService.predict(request);
                responses.add(response);
            }

            model.addAttribute("requests", requests);
            model.addAttribute("responses", responses);
            model.addAttribute("success", true);

        } catch (Exception e) {
            model.addAttribute("error", "Batch prediction failed: " + e.getMessage());
            model.addAttribute("success", false);

            // Re-add empty forms
            List<PredictionRequest> emptyRequests = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                emptyRequests.add(new PredictionRequest());
            }
            model.addAttribute("requests", emptyRequests);
        }

        return "faculty/batch-predict";
    }

    @GetMapping("/analytics")
    public String showAnalytics(Model model) {
        // Add analytics data
        model.addAttribute("totalPredictions", 150);
        model.addAttribute("averageAccuracy", 87.5);
        model.addAttribute("passRate", 72.3);
        model.addAttribute("failRate", 27.7);
        model.addAttribute("averageAttendance", 78.5);
        model.addAttribute("averageGPA", 2.8);
        model.addAttribute("atRiskStudents", 15);

        // Sample student performance data
        model.addAttribute("performanceData", Arrays.asList(
                new String[]{"S001", "John Doe", "85%", "PASS", "Low Risk"},
                new String[]{"S002", "Jane Smith", "92%", "PASS", "Low Risk"},
                new String[]{"S003", "Bob Wilson", "35%", "FAIL", "High Risk"},
                new String[]{"S004", "Alice Johnson", "68%", "PASS", "Medium Risk"}
        ));

        return "faculty/analytics";
    }

    @GetMapping("/students")
    public String viewStudents(Model model) {
        // Sample student data
        model.addAttribute("students", Arrays.asList(
                new String[]{"S001", "John Doe", "85.5%", "3.2", "15 hrs", "Computer Science"},
                new String[]{"S002", "Jane Smith", "92.0%", "3.8", "20 hrs", "Mathematics"},
                new String[]{"S003", "Bob Wilson", "65.0%", "2.1", "8 hrs", "Engineering"},
                new String[]{"S004", "Alice Johnson", "78.0%", "2.8", "12 hrs", "Science"}
        ));

        return "faculty/students";
    }
}