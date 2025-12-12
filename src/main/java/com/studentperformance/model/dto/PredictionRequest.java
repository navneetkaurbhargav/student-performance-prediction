package com.studentperformance.model.dto;

public class PredictionRequest {
    private Double attendanceRate;
    private Double previousGPA;
    private Integer studyHoursWeekly;
    private Double assignmentScoresAvg;
    private Integer extracurricularHours;
    private Integer familySupport;
    private Integer financialStability;
    
    // Default constructor
    public PredictionRequest() {}
    
    // Constructor with parameters
    public PredictionRequest(Double attendanceRate, Double previousGPA, Integer studyHoursWeekly,
                           Double assignmentScoresAvg, Integer extracurricularHours,
                           Integer familySupport, Integer financialStability) {
        this.attendanceRate = attendanceRate;
        this.previousGPA = previousGPA;
        this.studyHoursWeekly = studyHoursWeekly;
        this.assignmentScoresAvg = assignmentScoresAvg;
        this.extracurricularHours = extracurricularHours;
        this.familySupport = familySupport;
        this.financialStability = financialStability;
    }
    
    // Getters and Setters
    public Double getAttendanceRate() { return attendanceRate; }
    public void setAttendanceRate(Double attendanceRate) { this.attendanceRate = attendanceRate; }

    public Double getPreviousGPA() { return previousGPA; }
    public void setPreviousGPA(Double previousGPA) { this.previousGPA = previousGPA; }
    
    public Integer getStudyHoursWeekly() { return studyHoursWeekly; }
    public void setStudyHoursWeekly(Integer studyHoursWeekly) { this.studyHoursWeekly = studyHoursWeekly; }
    
    public Double getAssignmentScoresAvg() { return assignmentScoresAvg; }
    public void setAssignmentScoresAvg(Double assignmentScoresAvg) { this.assignmentScoresAvg = assignmentScoresAvg; }
    
    public Integer getExtracurricularHours() { return extracurricularHours; }
    public void setExtracurricularHours(Integer extracurricularHours) { this.extracurricularHours = extracurricularHours; }
    
    public Integer getFamilySupport() { return familySupport; }
    public void setFamilySupport(Integer familySupport) { this.familySupport = familySupport; }
    
    public Integer getFinancialStability() { return financialStability; }
    public void setFinancialStability(Integer financialStability) { this.financialStability = financialStability; }
}