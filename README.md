# Student Performance Prediction System

A lightweight enterprise-style system for predicting student academic performance using machine learning.

## Features
- 🤖 Machine Learning predictions using Weka
- 🔄 Asynchronous model retraining with JMS
- 📊 RESTful API for integration
- 👥 Multi-role authentication (Student, Faculty, Admin)
- 💾 MySQL database storage
- 🎨 Thymeleaf UI templates

## Tech Stack
- Java 1.8
- Spring Boot 2.7.18
- Weka 3.8.5
- ActiveMQ (JMS)
- MySQL 8.0+
- Thymeleaf
- Maven 3.8+

## Setup Instructions

### 1. Prerequisites
```bash
# Install Java 1.8
sudo apt install openjdk-8-jdk

# Install Maven
sudo apt install maven

# Install MySQL
sudo apt install mysql-server

# Download ActiveMQ
wget https://archive.apache.org/dist/activemq/5.16.6/apache-activemq-5.16.6-bin.tar.gz
tar -xzf apache-activemq-5.16.6-bin.tar.gz