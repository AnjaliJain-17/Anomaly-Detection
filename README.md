Navigate to the start-up script folder:

cd Anomaly-Detection/start-up-script


Create executable of the script with the follwoing command:

chmod +x start_elk.sh


Command to execute the startup script that starts the ElasticSearch, Logstash and Kibana services on the ec2 instance:

./start_elk.sh


Check if the services are up and running:

sudo systemctl status elasticsearch

sudo systemctl status logstash

sudo systemctl status kibana

