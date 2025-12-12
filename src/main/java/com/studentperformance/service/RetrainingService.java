package com.studentperformance.service;

import com.studentperformance.model.domain.MLModel;
import com.studentperformance.model.dto.RetrainingRequest;
import com.studentperformance.repository.ModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.File;

@Service
public class RetrainingService {
    
    @Autowired
    private ModelService modelService;
    
    @Autowired
    private ModelRepository modelRepository;

    @JmsListener(destination = "model.training.queue")
    public void processRetrainingRequest(RetrainingRequest request) {
        MLModel placeholder = modelService.createPlaceholder(
                request.getAlgorithm(), request.getModelName()
        );
        try {
            modelService.updateTrainingStatus(placeholder.getId(), "RUNNING");

            File file = new File(request.getDatasetPath());

            ArffLoader loader = new ArffLoader();
            loader.setSource(file);
            Instances data = loader.getDataSet();
            data.setClassIndex(data.numAttributes() - 1);

            MLModel newModel = modelService.trainAndSaveModel(
                    placeholder.getId(),
                    request.getAlgorithm(),
                    data
            );

            System.out.println("Model retraining completed: " + request.getModelName());
            System.out.println("Accuracy: " + (newModel.getAccuracy() * 100) + "%");
            modelService.updateTrainingStatus(placeholder.getId(), "COMPLETED");

        } catch (Exception e) {
            modelService.updateTrainingStatus(placeholder.getId(), "FAILED");
            // In production, you might want to send to DLQ or notify admin
        }
    }

}