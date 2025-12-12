package com.studentperformance.util;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.SerializationHelper;

import java.io.*;

public class WekaUtil {
    
    public static Classifier loadModel(String filePath) throws Exception {
        return (Classifier) SerializationHelper.read(filePath);
    }
    
    public static void saveModel(Classifier classifier, String filePath) throws Exception {
        SerializationHelper.write(filePath, classifier);
    }
    
    public static Instances loadDataset(String filePath) throws Exception {
        weka.core.converters.ConverterUtils.DataSource source = 
            new weka.core.converters.ConverterUtils.DataSource(filePath);
        return source.getDataSet();
    }
    
    public static byte[] classifierToBytes(Classifier classifier) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(classifier);
        oos.flush();
        return baos.toByteArray();
    }
    
    public static Classifier bytesToClassifier(byte[] bytes) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return (Classifier) ois.readObject();
    }
}