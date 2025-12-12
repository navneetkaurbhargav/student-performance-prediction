package com.studentperformance.model.dto;

import java.util.Date;

public class ModelStatusDTO {

    private Long id;
    private String modelName;
    private String algorithm;
    private Double accuracy;
    private Double modelPrecision;
    private Double recall;
    private Date trainedDate;
    private String trainingStatus;
    private boolean isActive;

    public ModelStatusDTO(
            long id,
            String modelName,
            String algorithm,
            Double accuracy,
            Double modelPrecision,
            Double recall,
            Date trainedDate,
            String trainingStatus,
            boolean isActive
    ) {
        this.id = id;
        this.modelName = modelName;
        this.algorithm = algorithm;
        this.accuracy = accuracy;
        this.modelPrecision = modelPrecision;
        this.recall = recall;
        this.trainedDate = trainedDate;
        this.trainingStatus = trainingStatus;
        this.isActive = isActive;
    }


}
