CREATE DATABASE IF NOT EXISTS spdb;
USE spdb;

CREATE TABLE IF NOT EXISTS ml_models (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  model_blob LONGBLOB NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Example student data table (audit columns included)
CREATE TABLE IF NOT EXISTS students_data (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  attendance DOUBLE,
  assignment DOUBLE,
  midterm DOUBLE,
  study_hours DOUBLE,
  status VARCHAR(16),
  uploaded_by VARCHAR(100),
  uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  notes VARCHAR(512)
);
