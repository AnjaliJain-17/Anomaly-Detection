import requests
import time
import random
from faker import Faker
import time


# Base URL for the Card API
url = "http://ec2-18-144-169-193.us-west-1.compute.amazonaws.com:8080"

# Endpoint paths
endpoints = [ 
                ["/cards/", "GET"], 
                ["/cards/{id}", "GET"], 
                ["/cards/", "POST"],
                ["/cards/{id}", "PUT"], 
                ["/cards/{id}", "DELETE"]
  ]

endpoints_anomaly = [ 
                ["/cards/error?count={count}&type=random", "GET"], 
  ]
  

WINDOW_TIME = 600 # n seconds 

fake = Faker()

def get_dummy_card():
    # generate random expiration date
    exp_month = random.randint(1, 12)
    exp_year = random.randint(2023, 2030)
    exp_date = f"{exp_month}/{exp_year}"

    id = random.randint(1, 100)
    data = {
            "id": id,
            "cardNumber" : fake.credit_card_number(),
            "expirationDate" : exp_date,
            "cardHolderName" : fake.name(),
            "userId": id
        }
    return data

# Generate normal window logs for 10 mins
def generate_normal_window_logs():
    print("=======================Generating normal logs ==================================")
    start_time = time.time()
    end_time = start_time + WINDOW_TIME  # 600 seconds = 10 minutes

    while time.time() < end_time:
        # Generate a random endpoint path and HTTP method
        endpoint = random.choice(endpoints)
        
        # Generate random data for POST and PUT requests
        data = get_dummy_card()
        
        # Make the HTTP request and print the response
        method = endpoint[1]
        path = endpoint[0]

        response = None
        if "{id}" in path:
            full_url = url + path.replace("{id}", str(random.randint(1, 100)))
        else:
            full_url = url + path

        # print(method, full_url)

        if method == "GET":
            response = requests.get(full_url)
        elif method == "PUT":
            response = requests.put(full_url, json=data)
        elif method == "POST":
            response = requests.post(full_url, json=data)
        elif method == "DELETE":
            response = requests.delete(full_url)
        
        print(f"Normal request: {method} {url}{path} - Response status code: {response.status_code}")

        # wait for 1 second before executing the block of code again
        time.sleep(1)

    print("Finished executing code for 10 minutes.")

# Generate anomalous window logs for 10 mins
def generate_anomalous_window_logs():
    print("=======================Generating anomalous logs ==================================")

    start_time = time.time()
    end_time = start_time + WINDOW_TIME  # 600 seconds = 10 minutes

    while time.time() < end_time:
        # Generate a random endpoint path and HTTP method
        endpoint = random.choice(endpoints_anomaly)
        
        # Make the HTTP request and print the response
        method = endpoint[1]
        path = endpoint[0]

        response = None
        if "{count}" in path:
            full_url = url + path.replace("{count}", str(random.randint(1, 5)))
        else:
            full_url = url + path

        # print(method, full_url)

        if method == "GET":
            response = requests.get(full_url)
        
        print(f"Normal request: {method} {url}{path} - Response status code: {response.status_code}")

        # wait for 1 second before executing the block of code again
        time.sleep(3)

    print("Finished executing code for 10 minutes.") 


# Define the start time
start_time = time.time()

# Run the program for 2 hours
while (time.time() - start_time) < (2 * 60 * 60):
    if random.random() < 0.9:
        generate_anomalous_window_logs()
    else:
        generate_anomalous_window_logs()
    time.sleep(3)

print("Program has finished running for 2 hours", time.time())
