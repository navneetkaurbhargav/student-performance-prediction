package com.example.sp.service;

import com.example.sp.model.MLModelEntity;
import com.example.sp.repository.MLModelRepository;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;

import java.io.*;

@Service
public class WekaService {

    private final MLModelRepository repo;

    public WekaService(MLModelRepository repo) {
        this.repo = repo;
    }

    public Classifier loadModelFromDb() throws Exception {
        MLModelEntity ent = repo.findTopByOrderByCreatedAtDesc()
                .orElseThrow(() -> new IllegalStateException("No model in DB"));
        byte[] blob = ent.getModelBlob();
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(blob))) {
            Object obj = ois.readObject();
            return (Classifier) obj;
        }
    }

    public double[] predict(weka.core.Instance inst) throws Exception {
        Classifier cls = loadModelFromDb();
        return cls.distributionForInstance(inst);
    }

    public void saveModelToDb(Classifier cls, String name) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(cls);
            oos.flush();
        }
        MLModelEntity ent = new MLModelEntity();
        ent.setName(name);
        ent.setModelBlob(bos.toByteArray());
        ent.setCreatedAt(java.time.Instant.now());
        repo.save(ent);
    }
}
