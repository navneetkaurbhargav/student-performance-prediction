package com.example.sp.controller;

import com.example.sp.service.WekaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import weka.core.converters.CSVLoader;
import weka.core.Instances;
import weka.classifiers.trees.J48;
import weka.classifiers.Classifier;

import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private WekaService wekaService;

    @Autowired
    private JmsTemplate jmsTemplate;

    @PostMapping("/upload-dataset")
    public ResponseEntity<?> uploadDataset(@RequestParam("file") MultipartFile file) throws Exception {
        Path tmp = Files.createTempFile("dataset-", ".csv");
        file.transferTo(tmp.toFile());
        jmsTemplate.convertAndSend("training.queue", tmp.toAbsolutePath().toString());
        return ResponseEntity.ok("Dataset uploaded and training triggered.");
    }

    @PostMapping("/train-now")
    public ResponseEntity<?> trainNow(@RequestParam("file") MultipartFile file) throws Exception {
        Path tmp = Files.createTempFile("dataset-", ".csv");
        file.transferTo(tmp.toFile());
        CSVLoader loader = new CSVLoader();
        loader.setSource(tmp.toFile());
        Instances data = loader.getDataSet();
        if (data.classIndex() == -1) data.setClassIndex(data.numAttributes() - 1);
        Classifier cls = new J48();
        cls.buildClassifier(data);
        wekaService.saveModelToDb(cls, "J48-sync-" + System.currentTimeMillis());
        return ResponseEntity.ok("Training completed and model saved.");
    }
}
