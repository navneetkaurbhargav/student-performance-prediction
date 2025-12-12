package com.studentperformance.service;

import com.studentperformance.model.domain.MLModel;
import com.studentperformance.repository.ModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class ModelService {

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private WekaModelService wekaModelService;


    @PostConstruct
    public void initializeDefaultModel() {
        // Check if any model exists
        long modelCount = modelRepository.count();

        if (modelCount == 0) {
            try {
                System.out.println("Creating default ML model...");

                MLModel defaultModel = new MLModel();
                defaultModel.setModelName("Default_RandomForest");
                defaultModel.setAlgorithm("RANDOM_FOREST");
                defaultModel.setAccuracy(0.85);
                defaultModel.setModelPrecision(0.83);
                defaultModel.setRecall(0.86);
                defaultModel.setTrainingSamples(100);
                defaultModel.setIsActive(true);
                defaultModel.setVersion(1);

                // Create a simple dummy model (in production, you would train with real data)
                // For now, just create empty byte array
                byte[] dummyModel = new byte[0];
                defaultModel.setSerializedModel(dummyModel);

                modelRepository.save(defaultModel);
                System.out.println("✅ Default model created successfully!");

            } catch (Exception e) {
                System.err.println("❌ Failed to create default model: " + e.getMessage());
                // Don't throw exception - let application continue without default model
                e.printStackTrace();
            }
        } else {
            System.out.println("✅ Models already exist in database: " + modelCount + " found");

            // Check if any model is active
            List<MLModel> activeModels = modelRepository.findByIsActive(true);
            if (activeModels.isEmpty()) {
                System.out.println("⚠️  No active model found. Activating first model...");
                List<MLModel> allModels = modelRepository.findAll();
                if (!allModels.isEmpty()) {
                    MLModel firstModel = allModels.get(0);
                    firstModel.setIsActive(true);
                    modelRepository.save(firstModel);
                    System.out.println("✅ Activated model: " + firstModel.getModelName());
                }
            }
        }
    }

    private Double safeDouble(double value) {
        return Double.isNaN(value) || Double.isInfinite(value)
                ? null
                : value;
    }

    @Transactional
    public MLModel trainAndSaveModel(Long modelId, String algorithm, Instances trainingData) throws Exception {
        MLModel model = modelRepository.findById(modelId)
                .orElseThrow(() -> new RuntimeException("Model not found: " + modelId));
        // 1. Train model
        Classifier classifier = wekaModelService.trainModel(algorithm, trainingData);

        // 2. Evaluate using cross-validation
        Evaluation eval;

        if (trainingData.numInstances() < 3) {
            eval = new Evaluation(trainingData);
            eval.evaluateModel(classifier, trainingData); // fallback
        } else {
            int folds = Math.min(10, trainingData.numInstances());
            eval = new Evaluation(trainingData);
            eval.crossValidateModel(classifier, trainingData, folds, new Random(1));
        }

        // 3. Versioning
        int version = getNextVersion(algorithm);
        String finalModelName = model.getModelName().replace("_pending", "") + "_v" + version;
        Thread.sleep(7000);

        // 4. Serialize model
        byte[] modelBytes = serializeClassifier(classifier);

        // 5. Create entity
        model.setModelName(finalModelName);
        model.setAlgorithm(algorithm);
        model.setAccuracy(safeDouble(eval.pctCorrect() / 100.0));
        model.setModelPrecision(safeDouble(eval.weightedPrecision()));
        model.setRecall(safeDouble(eval.weightedRecall()));
        model.setTrainingSamples(trainingData.numInstances());
        model.setTrainedDate(new Date());
        model.setIsActive(false);
        model.setVersion(version);
        model.setSerializedModel(modelBytes);

        return modelRepository.save(model);
    }

    public MLModel getActiveModel() {
        List<MLModel> activeModels = modelRepository.findByIsActive(true);
        return activeModels.isEmpty() ? null : activeModels.get(0);
    }

    public void activateModel(Long modelId) {
        // Deactivate all models first
        List<MLModel> allModels = modelRepository.findAll();
        for (MLModel model : allModels) {
            if (model.getIsActive()) {
                model.setIsActive(false);
                modelRepository.save(model);
            }
        }

        // Activate selected model
        MLModel modelToActivate = modelRepository.findById(modelId).orElse(null);
        if (modelToActivate != null) {
            modelToActivate.setIsActive(true);
            modelRepository.save(modelToActivate);
        }
    }

    public List<MLModel> getAllModels() {
        return modelRepository.findAll();
    }

    public MLModel getModelById(Long id) {
        return modelRepository.findById(id).orElse(null);
    }

    public void deleteModel(Long id) {
        MLModel model = modelRepository.findById(id).orElse(null);
        if (model != null && !model.getIsActive()) {
            modelRepository.deleteById(id);
        }
    }

    private Integer getNextVersion(String algorithm) {
        List<MLModel> existingModels = modelRepository.findByAlgorithm(algorithm);
        return existingModels.size() + 1;
    }

    private byte[] serializeClassifier(Classifier classifier) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(classifier);
        oos.flush();
        return baos.toByteArray();
    }

    @Transactional
    public MLModel createPlaceholder(String algorithm, String modelName) {

        MLModel model = new MLModel();
        model.setModelName(modelName + "_pending");
        model.setAlgorithm(algorithm);
        model.setTrainingStatus("QUEUED");
        model.setIsActive(false);
        model.setCreatedAt(new Date());

        return modelRepository.save(model);
    }

    @Transactional
    public void updateTrainingStatus(Long modelId, String status) {
        MLModel model = modelRepository.findById(modelId)
                .orElseThrow(() -> new RuntimeException("Model not found: " + modelId));
        model.setTrainingStatus(status);
        modelRepository.save(model);
    }
}