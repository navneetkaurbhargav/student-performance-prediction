package com.studentperformance.model.enums;

public enum ModelType {
    RANDOM_FOREST("Random Forest"),
    NAIVE_BAYES("Naive Bayes"),
    J48("J48 Decision Tree"),
    LOGISTIC_REGRESSION("Logistic Regression"),
    SUPPORT_VECTOR_MACHINE("SVM"),
    NEURAL_NETWORK("Neural Network");
    
    private final String displayName;
    
    ModelType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static ModelType fromString(String text) {
        for (ModelType type : ModelType.values()) {
            if (type.name().equalsIgnoreCase(text)) {
                return type;
            }
        }
        return RANDOM_FOREST; // default
    }
}