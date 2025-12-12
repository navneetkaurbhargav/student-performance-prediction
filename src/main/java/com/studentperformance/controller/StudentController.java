package com.studentperformance.controller;

import com.studentperformance.model.domain.*;
import com.studentperformance.model.dto.PredictionResponse;
import com.studentperformance.repository.*;
import com.studentperformance.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentCourseRepository studentCourseRepository;


    @Autowired
    private PredictionService predictionService;

    @Autowired
    private PredictionHistoryRepository predictionRepository;

    // Main Dashboard API
    @GetMapping("/dashboard")
    public String showDashboard(Model model, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username);

            if (user == null || user.getStudent() == null) {
                model.addAttribute("error", "Student information not found");
                return "student/dashboard";
            }

            Student student = user.getStudent();

            // Get current courses (where final grade is null/passed is null)
            List<StudentCourse> currentCourses = studentCourseRepository
                    .findByStudentId(student.getId());

            // Calculate statistics
            int currentCoursesCount = currentCourses.size();


            // Identify improvement areas
            List<String> improvementAreas = new ArrayList<>();
            for (StudentCourse course : currentCourses) {
                if (course.getAttendanceRate() != null && course.getAttendanceRate() < 75) {
                    improvementAreas.add("Increase attendance in " + course.getCourse().getCourseCode());
                }
                if (course.getAssignmentScoresAvg() != null && course.getAssignmentScoresAvg() < 70) {
                    improvementAreas.add("Improve assignment scores in " + course.getCourse().getCourseCode());
                }
            }


            // Add all attributes to the model
            model.addAttribute("student", student);
            model.addAttribute("currentCourses", currentCourses);
            model.addAttribute("currentCoursesCount", currentCoursesCount);
            model.addAttribute("academicYear", "2025"); // You can make this dynamic
            model.addAttribute("semester", "Fall");     // You can make this dynamic
            model.addAttribute("improvementAreas", improvementAreas);

            List<Prediction> prediction = predictionRepository.findByStudentId(student.getId());
            if(prediction != null && !prediction.isEmpty()) {
                updateAvgPassProb(prediction, model, student.getId());
            }

            return "student/dashboard";

        } catch (Exception e) {
            model.addAttribute("error", "Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
            return "student/dashboard";
        }
    }

    private void updateAvgPassProb(List<Prediction> prediction, Model model, Long id) {
        double avgPassProb = prediction.stream()
                .map(Prediction::getProbability)   // probability already in [0,1]
                .filter(Objects::nonNull)
                .mapToDouble(p -> p / 100.0)
                .average()
                .orElse(0.0);

        model.addAttribute("averagePassProbability", avgPassProb);
        Map<String, Long> atRiskCoursesCount =
                prediction.stream()
                        .filter(p -> "FAIL".equals(p.getPredictionLabel()))
                        .collect(Collectors.groupingBy(
                                p -> p.getCourse().getCourseCode(),
                                Collectors.counting()
                        ));
        model.addAttribute("atRiskCoursesCount", atRiskCoursesCount.isEmpty() ? 0 : atRiskCoursesCount.size());
    }

    // View course details
    @GetMapping("/course/{courseId}/details")
    public String viewCourseDetails(@PathVariable Long courseId, Model model, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username);

            if (user == null || user.getStudent() == null) {
                model.addAttribute("error", "Student information not found");
                return "student/course-details";
            }

            StudentCourse studentCourse = studentCourseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));

            // Check authorization
            if (!studentCourse.getStudent().getStudentId().equals(user.getStudent().getStudentId())) {
                model.addAttribute("error", "Unauthorized access");
                return "student/course-details";
            }

            model.addAttribute("course", studentCourse);
            return "student/course-details";

        } catch (Exception e) {
            model.addAttribute("error", "Error loading course details: " + e.getMessage());
            return "student/course-details";
        }
    }


    // API to update course metrics
    @PostMapping("/api/courses/{courseId}/update")
    @ResponseBody
    public Map<String, Object> updateCourseMetrics(@PathVariable Long courseId,
                                                   @RequestBody Map<String, Object> metrics,
                                                   Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username);

            if (user == null || user.getStudent() == null) {
                response.put("success", false);
                response.put("message", "Student not found");
                return response;
            }

            StudentCourse studentCourse = studentCourseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));

            // Check authorization
            if (!studentCourse.getStudent().getStudentId().equals(user.getStudent().getStudentId())) {
                response.put("success", false);
                response.put("message", "Unauthorized access");
                return response;
            }

            // Update metrics
            if (metrics.containsKey("attendanceRate")) {
                studentCourse.setAttendanceRate(Double.parseDouble(metrics.get("attendanceRate").toString()));
            }
            if (metrics.containsKey("assignmentScoresAvg")) {
                studentCourse.setAssignmentScoresAvg(Double.parseDouble(metrics.get("assignmentScoresAvg").toString()));
            }
            if (metrics.containsKey("studyHoursWeekly")) {
                studentCourse.setStudyHoursWeekly(Integer.parseInt(metrics.get("studyHoursWeekly").toString()));
            }

            studentCourse.setUpdatedAt(new Date());
            studentCourseRepository.save(studentCourse);

            response.put("success", true);
            response.put("message", "Course metrics updated successfully");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating course metrics: " + e.getMessage());
        }

        return response;
    }

    @PostMapping("/{studentId}/predict-all-courses")
    @ResponseBody
    public ResponseEntity<?> predictAllCourses(@PathVariable Long studentId) {
        try {
            List<PredictionResponse> results = predictionService.predictAllCoursesForStudent(studentId);

            return ResponseEntity.ok( new HashMap<String, Object>() {{
                put("success", true);
                put("predictions", results);
            }});
        } catch (Exception e) {
            return ResponseEntity.badRequest().body( new HashMap<String, Object>() {{
                put("success", false);
                put("message", e.getMessage());
            }});
        }
    }

    @GetMapping("/{studentId}/prediction-results")
    public String showPredictionResults(
            @PathVariable Long studentId,
            Model model) {

        List<Prediction> history = predictionRepository.findByStudentId(studentId);

        model.addAttribute("predictions", history);
        return "student/predict-all-results";
    }



}