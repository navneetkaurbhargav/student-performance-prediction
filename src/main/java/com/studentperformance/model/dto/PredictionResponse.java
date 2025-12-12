package com.studentperformance.model.dto;

import java.util.Date;
import java.util.List;

public class PredictionResponse {
    private Double passingProbability;
    private String prediction;
    private Double confidenceScore;
    private String modelUsed;
    private String algorithm;
    private Date timestamp;
    private List<String> recommendations;
    
    // Constructors
    public PredictionResponse() {
        this.timestamp = new Date();
    }
    
    // Getters and Setters
    public Double getPassingProbability() { return passingProbability; }
    public void setPassingProbability(Double passingProbability) { this.passingProbability = passingProbability; }
    
    public String getPrediction() { return prediction; }
    public void setPrediction(String prediction) { this.prediction = prediction; }
    
    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }
    
    public String getModelUsed() { return modelUsed; }
    public void setModelUsed(String modelUsed) { this.modelUsed = modelUsed; }
    
    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
    
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    
    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
}