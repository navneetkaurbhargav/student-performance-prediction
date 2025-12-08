package com.example.sp.service;

import org.springframework.stereotype.Component;
import org.springframework.jms.annotation.JmsListener;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

import java.io.File;

@Component
public class TrainingJmsListener {

    private final WekaService wekaService;

    public TrainingJmsListener(WekaService wekaService) {
        this.wekaService = wekaService;
    }

    @JmsListener(destination = "training.queue")
    public void receiveTrainingRequest(String datasetPath) {
        try {
            System.out.println("Received training request: " + datasetPath);
            CSVLoader loader = new CSVLoader();
            loader.setSource(new File(datasetPath));
            Instances data = loader.getDataSet();
            if (data.classIndex() == -1) data.setClassIndex(data.numAttributes() - 1);
            Classifier cls = new J48();
            cls.buildClassifier(data);
            wekaService.saveModelToDb(cls, "J48-" + System.currentTimeMillis());
            System.out.println("Training completed and model saved.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
