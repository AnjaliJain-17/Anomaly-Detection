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

### To create index on elastic search

```
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
```

Replace index_name with the index name that you want to create

### To view all indexes 

```sudo curl -XGET 'localhost:9200/_cat/indices?v&pretty'```

### Kibana UI

Change DNS as required

```http://ec2-13-57-247-5.us-west-1.compute.amazonaws.com:5601/app/discover#/```

### Get all documents in an index

#### Using Kibana UI

Hamburger Menu -> Dev tools (Under Management section)
Use Query

```
GET ml-results/_search
{
    "query": {
        "match_all": {}
    }
}
```

#### Using curl

```
curl -X GET "localhost:9200/ml-results/_search?pretty" -H 'Content-Type: application/json' -d'
{
    "query": {
        "match_all": {}
    }
}'
```

## Steps to setup ML cron job
1. Log in to your ec2 instance & install crond service
2. Start the cron service 
      ```
      sudo service cron start
    ```
3. Configure the cron job in crontab file
      ``` 
      crontab -e 
      ```
4. Add the expression in the file
      ```
      */5 * * * * /Anomaly-Detection/anomaly-detection-ml/log_analysis/ml-jobrunner.sh
      ```

  
