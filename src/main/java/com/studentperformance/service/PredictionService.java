package com.studentperformance.service;

import com.googlecode.jfilechooserbookmarks.AbstractBookmarksManager;
import com.studentperformance.model.domain.MLModel;
import com.studentperformance.model.domain.Prediction;
import com.studentperformance.model.domain.StudentCourse;
import com.studentperformance.model.dto.PredictionRequest;
import com.studentperformance.model.dto.PredictionResponse;
import com.studentperformance.repository.ModelRepository;
import com.studentperformance.repository.PredictionHistoryRepository;
import com.studentperformance.repository.StudentCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PredictionService {
    
    @Autowired
    private ModelRepository modelRepository;
    
    @Autowired
    private WekaModelService wekaModelService;

    @Autowired
    private StudentCourseRepository studentCourseRepository;

    @Autowired
    private PredictionHistoryRepository predictionRepository;

    public List<PredictionResponse> predictAllCoursesForStudent(Long studentId) throws Exception {

        // Fetch the student courses
        List<StudentCourse> courses = studentCourseRepository.findByStudentId(studentId);
        if (courses.isEmpty()) {
            throw new RuntimeException("No courses found for this student.");
        }

        List<PredictionResponse> allResponses = new ArrayList<>();

        for (StudentCourse course : courses) {

            // Build PredictionRequest for each course
            PredictionRequest req = new PredictionRequest(
                    course.getAttendanceRate(),
                    course.getPreviousGPA(),
                    course.getStudyHoursWeekly(),
                    course.getAssignmentScoresAvg(),
                    course.getExtracurricularHours(),
                    course.getStudent().getFamilySupport(),
                    course.getStudent().getFinancialStability()
            );

            // Reuse your existing prediction logic
            PredictionResponse response = predict(req);

            // Save into predictions_history table
            Prediction p = new Prediction();
            p.setStudent(course.getStudent());
            p.setPredictionValue(response.getPrediction().equals("PASS") ? 1.0 : 0.0);
            p.setProbability(response.getPassingProbability());
            p.setPredictionLabel(response.getPrediction());
            p.setModelUsed(response.getModelUsed());
            p.setConfidenceScore(response.getConfidenceScore());
            p.setPredictionDate(new Date());
            p.setCreatedAt(new Date());
            p.setCourse(course.getCourse());
            p.setFeaturesJson("null");

            predictionRepository.save(p);

            allResponses.add(response);
        }

        return allResponses;
    }


    public PredictionResponse predict(PredictionRequest request) throws Exception {
        // Get active model
        List<MLModel> activeModels = modelRepository.findByIsActive(true);
        if (activeModels.isEmpty()) {
            throw new RuntimeException("No active model found");
        }
        
        MLModel activeModel = activeModels.get(0);
        Classifier classifier = wekaModelService.deserializeModel(activeModel.getSerializedModel());
        
        // Create instance for prediction
        ArrayList<weka.core.Attribute> attributes = wekaModelService.createAttributes();
        Instance instance = wekaModelService.createInstance(
            attributes,
            request.getAttendanceRate(),
            request.getPreviousGPA(),
            request.getStudyHoursWeekly(),
            request.getAssignmentScoresAvg(),
            request.getExtracurricularHours(),
            request.getFamilySupport(),
            request.getFinancialStability()
        );
        
        // Make prediction
        double prediction = wekaModelService.predict(classifier, instance);
        
        // Calculate probability (simple approach)
        double probability = prediction == 1.0 ? 0.85 : 0.15;
        
        // Prepare response
        PredictionResponse response = new PredictionResponse();
        response.setPassingProbability(probability * 100);
        response.setPrediction(prediction == 1.0 ? "PASS" : "FAIL");
        response.setConfidenceScore(activeModel.getAccuracy());
        response.setModelUsed(activeModel.getModelName());
        response.setAlgorithm(activeModel.getAlgorithm());
        response.setTimestamp(new Date());
        
        // Add recommendations based on prediction
        List<String> recommendations = new ArrayList<>();
        if (prediction == 0.0) { // Fail prediction
            if (request.getAttendanceRate() < 70) {
                recommendations.add("Improve attendance (currently " + request.getAttendanceRate() + "%)");
            }
            if (request.getStudyHoursWeekly() < 10) {
                recommendations.add("Increase study hours (currently " + request.getStudyHoursWeekly() + " hours/week)");
            }
            if (request.getAssignmentScoresAvg() < 60) {
                recommendations.add("Focus on assignment submissions (currently " + request.getAssignmentScoresAvg() + "%)");
            }
        }
        response.setRecommendations(recommendations);
        
        return response;
    }
    
    public List<PredictionResponse> batchPredict(List<PredictionRequest> requests) throws Exception {
        List<PredictionResponse> responses = new ArrayList<>();
        for (PredictionRequest request : requests) {
            responses.add(predict(request));
        }
        return responses;
    }

    public long totalPredictions() {
        return predictionRepository.count();
    }
}