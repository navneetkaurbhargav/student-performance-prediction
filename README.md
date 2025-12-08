# Student Performance Prediction - Spring Boot + Weka

Java: 1.8
Maven: 3.8+

This project is a runnable skeleton for the Student Performance Prediction system.
It uses:
- Spring Boot 2.7.x
- Weka 3.8
- ActiveMQ for JMS (broker expected at tcp://localhost:61616)
- MySQL (configure credentials in src/main/resources/application.properties)

## Quick start (MySQL)

1. Create database and tables:
   - Run sql/schema.sql on your MySQL server:
     mysql -u root -p < sql/schema.sql

2. Update `src/main/resources/application.properties` with your MySQL credentials.

3. Start ActiveMQ (example with Docker):
   docker run -d --name activemq -p 61616:61616 -p 8161:8161 rmohr/activemq:latest

4. Build and run:
   mvn clean package
   java -jar target/student-performance-prediction-0.1.0.jar

5. Open Admin UI:
   http://localhost:8080/admin.html
   Upload `data/sample_student_data.csv` to test training.

6. After training, open Student UI:
   http://localhost:8080/student.html

## Notes
- The predict endpoint expects a template ARFF to describe attribute order. A template is included at src/main/resources/template/student_template.arff.
- For quick testing you can switch to H2 by changing application.properties settings.
