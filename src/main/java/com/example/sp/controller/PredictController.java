package com.example.sp.controller;

import com.example.sp.service.WekaService;
import org.springframework.web.bind.annotation.*;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

@RestController
@RequestMapping("/predict")
public class PredictController {

    private final WekaService wekaService;

    public PredictController(WekaService wekaService) {
        this.wekaService = wekaService;
    }

    @PostMapping
    public Object predict(@RequestParam String templatePath, @RequestBody double[] attributeValues) throws Exception {
        DataSource ds = new DataSource(templatePath);
        Instances header = ds.getDataSet();
        if (header.classIndex() == -1) header.setClassIndex(header.numAttributes() - 1);
        DenseInstance inst = new DenseInstance(header.numAttributes());
        inst.setDataset(header);
        for (int i = 0; i < attributeValues.length; i++) {
            inst.setValue(i, attributeValues[i]);
        }
        double[] probs = wekaService.predict(inst);
        double passProb = probs.length > 1 ? probs[1] : probs[0];
        String pred = passProb >= 0.5 ? "Pass" : "Fail";
        return java.util.Map.of("passProbability", passProb, "prediction", pred);
    }
}
