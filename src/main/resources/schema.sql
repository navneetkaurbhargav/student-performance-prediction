-- Student Performance System Database Setup
-- Run this script before starting the application

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS student_performance_db;
USE student_performance_db;

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS predictions_history;
DROP TABLE IF EXISTS ml_models;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS student_courses;

-- 1.) Students table
CREATE TABLE students (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          student_id VARCHAR(50) NOT NULL UNIQUE,
                          name VARCHAR(255) NOT NULL,
                          email VARCHAR(255) UNIQUE,
                          family_support INT CHECK (family_support >= 1 AND family_support <= 5),
                          financial_stability INT CHECK (financial_stability >= 1 AND financial_stability <= 5),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2.) Users table (with foreign key to students)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    role VARCHAR(20) NOT NULL,
    student_id BIGINT,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE SET NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Courses table
CREATE TABLE courses (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         course_code VARCHAR(20) NOT NULL UNIQUE,
                         course_name VARCHAR(255) NOT NULL,
                         credits INT,
                         department VARCHAR(100),
                         academic_year VARCHAR(20),
                         semester VARCHAR(20),
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE student_courses (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 student_id BIGINT NOT NULL,
                                 course_code BIGINT NOT NULL,
                                 attendance_rate DOUBLE,
                                 previous_gpa DOUBLE CHECK (previous_gpa >= 0 AND previous_gpa <= 4.0),
                                 study_hours_weekly INT CHECK (study_hours_weekly >= 0),
                                 assignment_scores_avg DOUBLE CHECK (assignment_scores_avg >= 0 AND assignment_scores_avg <= 100),
                                 extracurricular_hours INT CHECK (extracurricular_hours >= 0),
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                 UNIQUE KEY unique_student_course_semester (student_id, course_code)
);

-- 4. ML Models table
CREATE TABLE ml_models (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           model_name VARCHAR(100) UNIQUE NOT NULL,
                           algorithm VARCHAR(50) NOT NULL,
                           serialized_model LONGBLOB,
                           accuracy DOUBLE NULL,
                           model_precision DOUBLE NULL,
                           recall DOUBLE NULL,
                           training_samples INT,
                           parent_model_id BIGINT,
                           training_status VARCHAR(100),
                           trained_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           is_active BOOLEAN DEFAULT FALSE,
                           version INT DEFAULT 1,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. Predictions History table
CREATE TABLE predictions_history (
                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                     student_id BIGINT,
                                     course_id BIGINT,
                                     prediction_value DOUBLE,
                                     probability DOUBLE,
                                     prediction_label VARCHAR(20),
                                     model_used VARCHAR(100),
                                     confidence_score DOUBLE,
                                     features_json TEXT,
                                     prediction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE SET NULL,
                                     FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE SET NULL
);

-- 6. System Logs table (optional)
CREATE TABLE system_logs (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             log_level VARCHAR(20),
                             component VARCHAR(50),
                             message TEXT,
                             details TEXT,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample students
INSERT INTO students (student_id, name, email, family_support, financial_stability) VALUES
                                                                                        ('S001', 'John Doe', 'john.doe@university.edu', 4, 4),
                                                                                        ('S002', 'Jane Smith', 'jane.smith@university.edu', 5, 5),
                                                                                        ('S003', 'Bob Wilson', 'bob.wilson@university.edu', 2, 3),
                                                                                        ('S004', 'Alice Johnson', 'alice.johnson@university.edu',  3, 4),
                                                                                        ('S005', 'Charlie Brown', 'charlie.brown@university.edu',  1, 2);
-- Get the ID of student S001 to use in users table
SET @student1_id = (SELECT id FROM students WHERE student_id = 'S001');

-- Now insert users, using the actual student ID
INSERT INTO users (username, password, email, role, student_id) VALUES
                                                                    ('student', '$2a$10$HCVBzHjG7XpyzEaWDOT8/.bTG0uPPMkQzuLW0tZ4HegYKBHg9SYVO', 'john.doe@university.edu', 'STUDENT', @student1_id),
                                                                    ('faculty', '$2a$10$HCVBzHjG7XpyzEaWDOT8/.bTG0uPPMkQzuLW0tZ4HegYKBHg9SYVO', 'faculty@university.edu', 'FACULTY', NULL),
                                                                    ('admin', '$2a$10$HCVBzHjG7XpyzEaWDOT8/.bTG0uPPMkQzuLW0tZ4HegYKBHg9SYVO', 'admin@university.edu', 'ADMIN', NULL);


-- Insert sample courses
INSERT INTO courses (course_code, course_name, credits, department, academic_year, semester) VALUES
                                                                                                 ('CS101', 'Introduction to Programming', 3, 'Computer Science', '2024', 'Spring'),
                                                                                                 ('MATH201', 'Calculus I', 4, 'Mathematics', '2024', 'Spring'),
                                                                                                 ('ENG102', 'English Composition', 3, 'English', '2024', 'Spring'),
                                                                                                 ('SCI301', 'General Science', 3, 'Science', '2024', 'Spring');

-- Get the ID of student S001 to use in users table
SET @course1_id = (SELECT id FROM courses WHERE course_code = 'CS101');
SET @course2_id = (SELECT id FROM courses WHERE course_code = 'MATH201');
SET @course3_id = (SELECT id FROM courses WHERE course_code = 'ENG102');
-- Get the ID of student S001 to use in users table
SET @studentC_id = (SELECT id FROM students WHERE student_id = 'S001');


INSERT INTO student_courses (student_id, course_code, attendance_rate, previous_gpa, study_hours_weekly,
                             assignment_scores_avg, extracurricular_hours) VALUES
-- John Doe's courses
(@studentC_id, @course1_id, 85.5, 3.2, 15, 78.5, 5),
(@studentC_id, @course2_id, 90.0, 3.2, 12, 85.0, 5),
(@studentC_id, @course3_id, 60.0, 3.2, 8, 65.0, 5);

-- Insert default ML model (placeholder)
INSERT INTO ml_models (model_name, algorithm, accuracy, precision, recall, training_samples, is_active, training_status) VALUES
    ('Default_RandomForest', 'RANDOM_FOREST', 0.85, 0.83, 0.86, 100, TRUE, 'COMPLETED');

-- Create indexes for better performance
CREATE INDEX idx_students_email ON students(email);
CREATE INDEX idx_predictions_student ON predictions_history(student_id);
CREATE INDEX idx_predictions_date ON predictions_history(prediction_date);
CREATE INDEX idx_models_active ON ml_models(is_active);
CREATE INDEX idx_users_username ON users(username);

-- Create views for reporting
CREATE VIEW student_performance_view AS
SELECT
    s.student_id,
    s.name,
    s.previous_gpa,
    s.attendance_rate,
    COUNT(p.id) as total_predictions,
    AVG(p.probability) as avg_pass_probability,
    MAX(p.prediction_date) as last_prediction_date
FROM students s
         LEFT JOIN predictions_history p ON s.id = p.student_id
GROUP BY s.id;

CREATE VIEW model_performance_view AS
SELECT
    model_name,
    algorithm,
    accuracy,
        precision,
        recall,
        training_samples,
        trained_date,
        is_active
        FROM ml_models
        ORDER BY trained_date DESC;

-- Show success message
SELECT 'Database setup completed successfully!' as message;
SELECT COUNT(*) as total_users FROM users;
SELECT COUNT(*) as total_students FROM students;
SELECT COUNT(*) as total_courses FROM courses;