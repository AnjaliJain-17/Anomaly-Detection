# ELK start up script

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

# Commands 

1. To create index on elastic search

curl -XPUT -H 'Content-Type: application/json' http://localhost:9200/<index_name> -d '
{
  "mappings": {
    "properties": {
      "message": {
        "type": "text"
      },
      "@timestamp": {
        "type": "date"
      }
    }
  }
}'

Replace index_name with the index name that you want to create

2. To view all indexes 

sudo curl -XGET 'localhost:9200/_cat/indices?v&pretty'
