package com.studentperformance.model.dto;

import java.io.Serializable;

public class RetrainingRequest  implements Serializable {

    private static final long serialVersionUID = 1L;

    private String datasetPath;
    private String algorithm;
    private String modelName;
    
    // Constructors
    public RetrainingRequest() {}
    
    public RetrainingRequest(String datasetPath, String algorithm, String modelName) {
        this.datasetPath = datasetPath;
        this.algorithm = algorithm;
        this.modelName = modelName;
    }
    
    // Getters and Setters
    public String getDatasetPath() { return datasetPath; }
    public void setDatasetPath(String datasetPath) { this.datasetPath = datasetPath; }
    
    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
    
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    
    @Override
    public String toString() {
        return "RetrainingRequest{" +
               "datasetPath='" + datasetPath + '\'' +
               ", algorithm='" + algorithm + '\'' +
               ", modelName='" + modelName + '\'' +
               '}';
    }
}