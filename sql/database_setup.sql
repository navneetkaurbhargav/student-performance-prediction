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

-- 1. Users table (for authentication)
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. Students table
CREATE TABLE students (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id VARCHAR(50) UNIQUE,
    name VARCHAR(100),
    email VARCHAR(100),
    attendance_rate DOUBLE,
    previous_gpa DOUBLE,
    study_hours_weekly INT,
    assignment_scores_avg DOUBLE,
    extracurricular_hours INT,
    family_support INT CHECK (family_support BETWEEN 1 AND 5),
    financial_stability INT CHECK (financial_stability BETWEEN 1 AND 5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 3. Courses table
CREATE TABLE courses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    course_code VARCHAR(20) UNIQUE NOT NULL,
    course_name VARCHAR(100) NOT NULL,
    credits INT DEFAULT 3,
    department VARCHAR(50),
    academic_year VARCHAR(10),
    semester VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. ML Models table
CREATE TABLE ml_models (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    model_name VARCHAR(100) UNIQUE NOT NULL,
    algorithm VARCHAR(50) NOT NULL,
    serialized_model LONGBLOB,
    accuracy DOUBLE,
    precision DOUBLE,
    recall DOUBLE,
    training_samples INT,
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

-- Insert default users
INSERT INTO users (username, password, email, role) VALUES
('student', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV2UiK', 'student@university.edu', 'STUDENT'),
('faculty', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV2UiK', 'faculty@university.edu', 'FACULTY'),
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV2UiK', 'admin@university.edu', 'ADMIN');

-- Insert sample students
INSERT INTO students (student_id, name, email, attendance_rate, previous_gpa, study_hours_weekly, 
                      assignment_scores_avg, extracurricular_hours, family_support, financial_stability) VALUES
('S001', 'John Doe', 'john.doe@university.edu', 85.5, 3.2, 15, 78.5, 5, 4, 4),
('S002', 'Jane Smith', 'jane.smith@university.edu', 92.0, 3.8, 20, 88.0, 2, 5, 5),
('S003', 'Bob Wilson', 'bob.wilson@university.edu', 65.0, 2.1, 8, 62.0, 10, 2, 3),
('S004', 'Alice Johnson', 'alice.johnson@university.edu', 78.0, 2.8, 12, 72.0, 8, 3, 4),
('S005', 'Charlie Brown', 'charlie.brown@university.edu', 55.0, 1.8, 6, 58.0, 12, 1, 2);

-- Insert sample courses
INSERT INTO courses (course_code, course_name, credits, department, academic_year, semester) VALUES
('CS101', 'Introduction to Programming', 3, 'Computer Science', '2024', 'Spring'),
('MATH201', 'Calculus I', 4, 'Mathematics', '2024', 'Spring'),
('ENG102', 'English Composition', 3, 'English', '2024', 'Spring'),
('SCI301', 'General Science', 3, 'Science', '2024', 'Spring');

-- Insert default ML model (placeholder)
INSERT INTO ml_models (model_name, algorithm, accuracy, precision, recall, training_samples, is_active) VALUES
('Default_RandomForest', 'RANDOM_FOREST', 0.85, 0.83, 0.86, 100, TRUE);

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