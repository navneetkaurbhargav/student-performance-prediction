package com.studentperformance.controller;

import com.studentperformance.model.dto.PredictionRequest;
import com.studentperformance.model.dto.PredictionResponse;
import com.studentperformance.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {
    
    @Autowired
    private PredictionService predictionService;
    
    @PostMapping("/predict")
    public ResponseEntity<PredictionResponse> predict(@RequestBody PredictionRequest request) {
        try {
            PredictionResponse response = predictionService.predict(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/predict/batch")
    public ResponseEntity<List<PredictionResponse>> batchPredict(@RequestBody List<PredictionRequest> requests) {
        try {
            List<PredictionResponse> responses = predictionService.batchPredict(requests);
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}