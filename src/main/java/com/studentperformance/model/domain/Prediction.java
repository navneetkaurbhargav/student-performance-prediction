package com.studentperformance.model.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "predictions_history")
public class Prediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private Course course;

    @Column(name = "prediction_value")
    private Double predictionValue;

    @Column(name = "probability")
    private Double probability;

    @Column(name = "prediction_label")
    private String predictionLabel;

    @Column(name = "model_used")
    private String modelUsed;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "features_json", columnDefinition = "TEXT")
    private String featuresJson;

    @Column(name = "prediction_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date predictionDate;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    // Constructors
    public Prediction() {
        this.predictionDate = new Date();
        this.createdAt = new Date();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getPredictionValue() { return predictionValue; }
    public void setPredictionValue(Double predictionValue) { this.predictionValue = predictionValue; }

    public Double getProbability() { return probability; }
    public void setProbability(Double probability) { this.probability = probability; }

    public String getPredictionLabel() { return predictionLabel; }
    public void setPredictionLabel(String predictionLabel) { this.predictionLabel = predictionLabel; }

    public String getModelUsed() { return modelUsed; }
    public void setModelUsed(String modelUsed) { this.modelUsed = modelUsed; }

    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getFeaturesJson() { return featuresJson; }
    public void setFeaturesJson(String featuresJson) { this.featuresJson = featuresJson; }

    public Date getPredictionDate() { return predictionDate; }
    public void setPredictionDate(Date predictionDate) { this.predictionDate = predictionDate; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
}