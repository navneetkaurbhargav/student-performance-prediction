package com.studentperformance.service;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class WekaModelService {
    
    public Classifier trainModel(String algorithm, Instances data) throws Exception {
        Classifier classifier;
        
        switch (algorithm.toUpperCase()) {
            case "NAIVE_BAYES":
                classifier = new NaiveBayes();
                break;
            case "J48":
                classifier = new J48();
                break;
            case "RANDOM_FOREST":
                classifier = new RandomForest();
                break;
            case "LOGISTIC_REGRESSION":
                classifier = new Logistic();
                break;
            default:
                classifier = new RandomForest();
        }
        
        classifier.buildClassifier(data);
        return classifier;
    }
    
    public Evaluation evaluateModel(Classifier classifier, Instances data) throws Exception {
        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(classifier, data, 10, new Random(1));
        return eval;
    }
    
    public double predict(Classifier classifier, Instance instance) throws Exception {
        return classifier.classifyInstance(instance);
    }
    
    public Instances createDataset(ArrayList<Attribute> attributes, int capacity) {
        Instances dataset = new Instances("StudentPerformance", attributes, capacity);
        dataset.setClassIndex(dataset.numAttributes() - 1);
        return dataset;
    }
    
    public ArrayList<Attribute> createAttributes() {
        ArrayList<Attribute> attributes = new ArrayList<>();

        // Numeric attributes
        attributes.add(new Attribute("attendance_rate"));
        attributes.add(new Attribute("previous_gpa"));
        attributes.add(new Attribute("study_hours_weekly"));
        attributes.add(new Attribute("assignment_scores_avg"));
        attributes.add(new Attribute("extracurricular_hours"));
        attributes.add(new Attribute("family_support"));
        attributes.add(new Attribute("financial_stability"));
        
        // Class attribute (pass/fail)
        ArrayList<String> classValues = new ArrayList<>();
        classValues.add("fail");
        classValues.add("pass");
        attributes.add(new Attribute("class", classValues));
        
        return attributes;
    }
    
    public Instance createInstance(ArrayList<Attribute> attributes,
                                   double attendanceRate,
                                   double previousGPA,
                                   int studyHours,
                                   double assignmentAvg,
                                   int extracurricularHours,
                                   int familySupport,
                                   int financialStability) {

        Instances dataset = new Instances("PredictionInstance", attributes, 1);
        dataset.setClassIndex(attributes.size() - 1);

        DenseInstance instance = new DenseInstance(attributes.size());
        instance.setDataset(dataset);
        instance.setValue(attributes.get(0), attendanceRate);
        instance.setValue(attributes.get(1), previousGPA);
        instance.setValue(attributes.get(2), studyHours);
        instance.setValue(attributes.get(3), assignmentAvg);
        instance.setValue(attributes.get(4), extracurricularHours);
        instance.setValue(attributes.get(5), familySupport);
        instance.setValue(attributes.get(6), financialStability);
        
        return instance;
    }
    
    public byte[] serializeModel(Classifier classifier) throws Exception {
        File tempFile = File.createTempFile("model", ".ser");
        SerializationHelper.write(tempFile.getAbsolutePath(), classifier);
        
        try (FileInputStream fis = new FileInputStream(tempFile)) {
            byte[] bytes = new byte[(int) tempFile.length()];
            fis.read(bytes);
            return bytes;
        } finally {
            tempFile.delete();
        }
    }
    
    public Classifier deserializeModel(byte[] modelBytes) throws Exception {
        File tempFile = File.createTempFile("model", ".ser");
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(tempFile)) {
            fos.write(modelBytes);
        }
        
        Classifier classifier = (Classifier) SerializationHelper.read(tempFile.getAbsolutePath());
        tempFile.delete();
        return classifier;
    }
}