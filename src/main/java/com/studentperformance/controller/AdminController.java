package com.studentperformance.controller;

import com.studentperformance.model.domain.MLModel;
import com.studentperformance.model.dto.RetrainingRequest;
import com.studentperformance.repository.ModelRepository;
import com.studentperformance.service.JmsService;
import com.studentperformance.service.ModelService;
import com.studentperformance.service.PredictionService;
import com.studentperformance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ModelService modelService;

    @Autowired
    private JmsService jmsService;
    @Autowired
    private ModelRepository modelRepository;
    @Autowired
    private PredictionService predictionService;
    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        model.addAttribute("role", "ADMIN");

        List<MLModel> models = modelService.getAllModels();
        MLModel activeModel = modelService.getActiveModel();

        model.addAttribute("totalUsers", userService.getAllUsersCount());
        model.addAttribute("models", models);
        model.addAttribute("activeModel", activeModel);
        model.addAttribute("totalUsers", 3);
        model.addAttribute("totalPredictions",  predictionService.totalPredictions());
        model.addAttribute("systemUptime", "5 hours");
        model.addAttribute("databaseSize", "15.2 MB");

        return "admin/dashboard";
    }

    @PostMapping("/models/upload")
    public String uploadModel(
            @RequestParam("dataSetPath") String dataSetPath,
            @RequestParam("modelName") String modelName,
            @RequestParam("algorithm") String algorithm,
            Model model
    ) {
        try {
            File file = new File(dataSetPath);

            ArffLoader loader = new ArffLoader();
            loader.setSource(file);
            Instances data = loader.getDataSet();
            data.setClassIndex(data.numAttributes() - 1);

            data.setClassIndex(data.numAttributes() - 1);

            modelService.trainAndSaveModel(modelRepository.findByModelName(modelName).getId(), algorithm, data);
            model.addAttribute("success", true);
            model.addAttribute("message", "Model uploaded successfully");
        } catch (Exception e) {
            model.addAttribute("success", false);
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/admin/models";
    }

    @GetMapping("/models")
    public String manageModels(Model model) {
        List<MLModel> models = modelService.getAllModels();
        MLModel activeModel = modelService.getActiveModel();

        model.addAttribute("models", models);
        model.addAttribute("activeModel", activeModel);
        return "admin/models";
    }

    @PostMapping("/models/activate/{id}")
    public String activateModel(@PathVariable Long id) {
        modelService.activateModel(id);
        return "redirect:/admin/models";
    }

    @GetMapping("/retrain")
    public String showRetrainForm(Model model) {
        model.addAttribute("retrainingRequest", new RetrainingRequest());
        return "admin/retrain";
    }

    @PostMapping("/retrain")
    public String triggerRetraining(@ModelAttribute RetrainingRequest request, Model model) {
        try {
            RetrainingRequest msg = new RetrainingRequest();
            msg.setAlgorithm(request.getAlgorithm());
            msg.setModelName(request.getModelName());
            msg.setDatasetPath(request.getDatasetPath());

            model.addAttribute("message", "Training started in background");
            jmsService.sendRetrainingRequest(request);
            model.addAttribute("message", "Retraining request submitted successfully!");
            model.addAttribute("success", true);
        } catch (Exception e) {
            model.addAttribute("error", "Failed to submit retraining request: " + e.getMessage());
            model.addAttribute("success", false);
        }

        model.addAttribute("retrainingRequest", new RetrainingRequest());
        return "admin/retrain";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        // Sample user data
        model.addAttribute("users", Arrays.asList(
                new String[]{"1", "student", "student@university.edu", "STUDENT", "Active"},
                new String[]{"2", "faculty", "faculty@university.edu", "FACULTY", "Active"},
                new String[]{"3", "admin", "admin@university.edu", "ADMIN", "Active"}
        ));

        return "admin/users";
    }

    @GetMapping("/system")
    public String systemMonitoring(Model model) {
        // System monitoring data
        model.addAttribute("activeConnections", 5);
        model.addAttribute("memoryUsage", "65%");
        model.addAttribute("diskUsage", "42%");
        model.addAttribute("jmsQueueSize", 0);
        model.addAttribute("databaseConnections", 3);

        return "admin/system";
    }

    @PostMapping("/models/delete/{id}")
    public String deleteModel(@PathVariable Long id) {
        modelService.deleteModel(id);
        return "redirect:/admin/models";
    }

    @GetMapping("/models/status")
    @ResponseBody
    public List<MLModel> getModelStatuses() {
        return modelService.getAllModels();
    }
}