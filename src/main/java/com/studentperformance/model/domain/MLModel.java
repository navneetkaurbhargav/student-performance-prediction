package com.studentperformance.model.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ml_models")
public class MLModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "model_name", nullable = false, unique = true)
    private String modelName;

    @Column(name = "algorithm", nullable = false)
    private String algorithm;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "serialized_model")
    private byte[] serializedModel;

    @Column(name = "accuracy")
    private Double accuracy;

    @Column(name = "model_precision")
    private Double modelPrecision;

    @Column(name = "parent_model_id")
    private Long parentModelId;

    @Column(name = "recall")
    private Double recall;

    @Column(name = "training_samples")
    private Integer trainingSamples;

    @Column(name = "trained_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date trainedDate;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "version")
    private Integer version;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "training_status")
    private String trainingStatus;

    // Constructors
    public MLModel() {
        this.trainedDate = new Date();
        this.createdAt = new Date();
        this.isActive = false;
        this.version = 1;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }

    public byte[] getSerializedModel() { return serializedModel; }
    public void setSerializedModel(byte[] serializedModel) { this.serializedModel = serializedModel; }

    public Double getAccuracy() { return accuracy; }
    public void setAccuracy(Double accuracy) { this.accuracy = accuracy; }

    public Double getModelPrecision() { return modelPrecision; }
    public void setModelPrecision(Double modelPrecision) { this.modelPrecision = modelPrecision; }

    public Double getRecall() { return recall; }
    public void setRecall(Double recall) { this.recall = recall; }

    public Integer getTrainingSamples() { return trainingSamples; }
    public void setTrainingSamples(Integer trainingSamples) { this.trainingSamples = trainingSamples; }

    public Date getTrainedDate() { return trainedDate; }
    public void setTrainedDate(Date trainedDate) { this.trainedDate = trainedDate; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getTrainingStatus() { return trainingStatus; }
    public void setTrainingStatus(String trainingStatus) { this.trainingStatus = trainingStatus; }

    public Long getParentModelId() { return parentModelId; }
    public void setParentModelId(Long parentModelId) { this.parentModelId = MLModel.this.parentModelId; }
}