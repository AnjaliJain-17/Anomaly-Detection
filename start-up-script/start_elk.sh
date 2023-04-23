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
if pgrep -f ElkApplication &>/dev/null; then
    pkill -f ElkApplication
else
    nohup java -jar ElkApplication-0.0.1-SNAPSHOT.jar &
fi

# wait for the spring app to start
sleep 30s

# Run the log generator script
cd Anomaly-Detection/log-collector-application/log-generator/

if pgrep -f log_generation_smart &>/dev/null; then
    pkill -f log_generation_smart
else
    nohup python3 log_generation_smart.py &
fi

