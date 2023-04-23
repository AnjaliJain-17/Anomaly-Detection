import requests
import time
import random
from faker import Faker
import time
from time import strftime, localtime



# Base URL for the Card API
url = "http://ec2-13-57-247-5.us-west-1.compute.amazonaws.com:8080"

# Endpoint paths
endpoints_normal = [ 
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

def call_card_service(endpoint):

    # Make the HTTP request and print the response
    method = endpoint[1]
    path = endpoint[0]

    response = None

    if endpoint in endpoints_anomaly:
        if "{count}" in path:
            full_url = url + path.replace("{count}", str(random.randint(1, 5)))
        else:
            full_url = url + path
    else:
        data = get_dummy_card()

        if "{id}" in path:
            full_url = url + path.replace("{id}", str(random.randint(1, 100)))
        else:
            full_url = url + path


    if method == "GET":
        response = requests.get(full_url)
    elif method == "PUT":
        response = requests.put(full_url, json=data)
    elif method == "POST":
        response = requests.post(full_url, json=data)
    elif method == "DELETE":
        response = requests.delete(full_url)

    print(f"Request: {method} {full_url} - Response status code: {response.status_code}")

# Generate normal window logs for 10 mins
def generate_normal_window_logs():
    # print("=======================Generating normal logs ==================================")

    start_time = time.time()
    end_time = start_time + WINDOW_TIME  # 600 seconds = 10 minutes

    print(f"Normal window: {strftime('%l:%M%p %Z on %b %d, %Y', localtime(start_time))} {strftime('%l:%M%p %Z on %b %d, %Y', localtime(end_time))}")

    while time.time() < end_time:
        # Call normal endpoint 90% of the time and anomlous 10% of the time 
        if random.random() < 0.9:
            endpoint = random.choice(endpoints_normal)
        else:
            endpoint = random.choice(endpoints_anomaly)
            time.sleep(2) # extra 2 secs of sleep for anomalous as they generate a lot of data
    
        call_card_service(endpoint)    
        time.sleep(1)

    print("Finished executing normal window for 10 minutes.")

# Generate anomalous window logs for 10 mins
def generate_anomalous_window_logs():
    print("=======================Generating anomalous logs ==================================") 
    if random.random() < 0.5:
        generate_more_errors()
    else:
        generate_lots_of_messages()


def generate_more_errors():
    print("=======================Generating logs with 90% error and 10% info ==================================")

    start_time = time.time()
    end_time = start_time + WINDOW_TIME  # 600 seconds = 10 minutes

    while time.time() < end_time:

        # Call anomalous endpoint 90% of the time and normal 10% of the time
        if random.random() < 0.9:
            endpoint = random.choice(endpoints_anomaly)
            time.sleep(2) # extra 2 secs of sleep for anomalous as they generate a lot of data
        else:
            endpoint = random.choice(endpoints_normal)
    
        call_card_service(endpoint)    
        time.sleep(1)

    print("Finished executing anomalous window for 10 minutes - 90% ERROR") 

def generate_lots_of_messages():
    # print("=======================Generating lots of INFO logs ==================================")

    start_time = time.time()
    end_time = start_time + WINDOW_TIME  # 600 seconds = 10 minutes
    
    print(f"Normal window: {strftime('%l:%M%p %Z on %b %d, %Y', localtime(start_time))} {strftime('%l:%M%p %Z on %b %d, %Y', localtime(end_time))}")

    while time.time() < end_time:
        # Call normal endpoint all the time 
        endpoint = random.choice(endpoints_normal)
        call_card_service(endpoint)    

    print("Finished executing anomalous window for 10 minutes - 100% INFO")

# Define the start time
start_time = time.time()

# Run the program for HOURS_TO_RUN hours
HOURS_TO_RUN = 2
while (time.time() - start_time) < (HOURS_TO_RUN * 60 * 60):
    if random.random() < 0.9:
        generate_normal_window_logs()
    else:
        generate_anomalous_window_logs()
    time.sleep(3)

print("Program has finished running for 2 hours", time.time())
