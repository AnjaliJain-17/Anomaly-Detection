import requests
import time
import random
from faker import Faker
import time


# Base URL for the Card API
url = "http://ec2-54-151-57-38.us-west-1.compute.amazonaws.com:8080"

# Endpoint paths
endpoints = [ 
                ["/cards/", "GET"], 
                ["/cards/{id}", "GET"], 
                ["/cards/", "POST"],
                ["/cards/{id}", "PUT"], 
                ["/cards/{id}", "DELETE"]
            ]

# Normal window interval in seconds
normal_window_interval = 5 * 60

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

    start_time = time.time()
    end_time = start_time + 600  # 600 seconds = 10 minutes

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

        print(method, full_url)

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

generate_normal_window_logs()

"""
# Anomalous window interval in seconds
anomalous_window_interval = 5 * 60


# Generate anomalous window logs
def generate_anomalous_window_logs():
    # Random number of requests between 10 and 20
    num_requests = random.randint(10, 20)
    # Random delay between 1 and 5 seconds
    delay = random.uniform(1, 5)
    
    for i in range(num_requests):
        # Generate a random endpoint path and HTTP method
        endpoint = random.choice(endpoints)
        method = random.choice(methods)
        
        # Generate random data for POST and PUT requests
        data = {
            "name": "Card Name",
            "description": "Card Description",
            "value": random.randint(1, 100)
        }
        
        # Make the HTTP request and print the response
        response = None
        if method == "GET":
            response = requests.get(url + endpoint)
        elif method == "PUT":
            response = requests.put(url + endpoint + "/" + str(random.randint(1, 10)), json=data)
        elif method == "POST":
            response = requests.post(url + endpoint, json=data)
        elif method == "DELETE":
            response = requests.delete(url + endpoint + "/" + str(random.randint(1, 10)))
        
        # Generate anomalous logs in 50% of the requests
        if random.random() < 0.5
"""