package com.studentperformance.util;

import weka.classifiers.Classifier;
import weka.core.SerializationHelper;

import java.io.*;

public class ModelSerializer {
    
    public static byte[] serializeModel(Object model) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(model);
        oos.flush();
        return baos.toByteArray();
    }
    
    public static Object deserializeModel(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return ois.readObject();
    }
    
    public static void saveModelToFile(Classifier classifier, String filePath) throws Exception {
        SerializationHelper.write(filePath, classifier);
    }
    
    public static Classifier loadModelFromFile(String filePath) throws Exception {
        return (Classifier) SerializationHelper.read(filePath);
    }
}