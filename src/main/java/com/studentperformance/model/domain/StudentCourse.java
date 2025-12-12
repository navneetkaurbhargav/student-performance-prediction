package com.studentperformance.model.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "student_courses",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_code"}))
public class StudentCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Course-specific performance metrics
    @Column(name = "attendance_rate")
    private Double attendanceRate;

    @Column(name = "previous_gpa")
    private Double previousGPA;

    @Column(name = "study_hours_weekly")
    private Integer studyHoursWeekly;

    @Column(name = "assignment_scores_avg")
    private Double assignmentScoresAvg;

    @Column(name = "extracurricular_hours")
    private Integer extracurricularHours;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;


    // Relationships
    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_code", referencedColumnName = "id")
    private Course course;

    // Constructors
    public StudentCourse() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
}