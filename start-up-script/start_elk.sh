#!/bin/bash

# Start Elasticsearch
sudo systemctl restart elasticsearch

# Start Logstash
sudo systemctl restart logstash

# Start Kibana
sudo systemctl restart kibana

#Go to home directory
cd

# Change to the directory where your Spring Boot application is located
cd Anomaly-Detection/log-collector-application/Bank-Management-Spring-ELK/

# Build the Spring Boot application with Maven
mvn clean package

# Change to the java jar directory
cd target

#Run the jar file for the Java application
java -jar ElkApplication-0.0.1-SNAPSHOT.jar
