#!/bin/bash

# Start Elasticsearch
sudo systemctl start elasticsearch

# Start Logstash
sudo systemctl start logstash

# Start Kibana
sudo systemctl start kibana

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
