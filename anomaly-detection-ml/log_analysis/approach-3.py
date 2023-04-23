#!/usr/bin/env python
# coding: utf-8

# In[1]:


"""

Java/HDFS logs

t1 INFO added user abc
t2 ERROR deleted user xyz

Elastic search

t1 | INFO | added user abc
t2 | ERROR | deleted user xyz

ML 

Events

event_id | event
e1 | added user <>
e2 | deleted user <>


Logs

0-5 mins - tw1
t1 | INFO | added user 1
t2 | ERROR | deleted user 2
t3 | INFO | added user 3
t4 | ERROR | deleted user 4
t5 | INFO | added user 5


6-10 mins
t6 | ERROR | deleted user 6
t7 | INFO | added user 7
t8 | ERROR | deleted user 8

11-15 mins
t9 | INFO | added user 9
t10 | ERROR | deleted user 0


Log_summary


time_window_id | count_info | count_error | count_e1 | count_e2
tw1 | 3 | 2 | 3 | 2
tw2 | 1 | 2 | 1 | 2
tw3 | 1 | 1 | 1 | 1

"""


# In[2]:


import datetime
    
end_time = datetime.datetime.utcnow()
start_time = end_time - datetime.timedelta(days = 1)

print(start_time, end_time)


# In[3]:


# Elasticsearch connection

from elasticsearch import Elasticsearch

AWS_HOSTNAME = 'http://localhost'
ELASTIC_PORT = 9200

es = Elasticsearch(["{}:{}".format(AWS_HOSTNAME, ELASTIC_PORT)],timeout=30)


# In[4]:


import os

def fetch_logs(start_time, end_time):

    search_body = {
        "query": {
            "range": {
                "@timestamp": {
                    "gte": start_time,
                    "lte": end_time
                }
            }
        }
    }
    search_result = es.search(index='spring-elk-logs', body=search_body)
    hits = search_result['hits']['hits']    
    log_dir = "data/unstructured/Java/"
    log_file_path = os.path.join(log_dir, f"application.log")
    with open(log_file_path, 'a') as log_file:
        for hit in hits:
            log_file.write(f"{hit['_source']['message']}\n")
    return log_file_path

log_file_path = fetch_logs(start_time, end_time)
print(f"Logs fetched from elasticsearch and file is generated  at {log_file_path}")


# In[5]:


import pandas as pd
import os
import numpy as np
import re
from sklearn.utils import shuffle
from collections import OrderedDict
import sys
sys.path.append('../')
from log_parser import Drain

log_file_path = 'data/unstructured/Java/'
#label_file_name = 'data/unstructured/HDFS/anomaly_label.csv'
unstructured_log_filename = 'application.log'
structured_log_file_path = 'data/structured/Java/'
structured_log_filename = 'application.log_structured.csv'


def parseLog(log_file_path, log_file_name, structured_log_file_path, log_type):
    if log_type == 'HDFS':
        log_format = '<Date> <Time> <Pid> <Level> <Component>: <Content>'
        
    if log_type == 'Java':
        log_format = '<Date> <Time> <Pid> <Level> <Component> - <Content>'

    # Regular expression list for optional preprocessing (default: [])
    regex      = [
        r'blk_(|-)[0-9]+' , # block id
        r'(/|)([0-9]+\.){3}[0-9]+(:[0-9]+|)(:|)', # IP
        r'(?<=[^A-Za-z0-9])(\-?\+?\d+)(?=[^A-Za-z0-9])|[0-9]+$', # Numbers
    ]
    st         = 0.5  # Similarity threshold
    depth      = 4  # Depth of all leaf nodes

    parser = Drain.LogParser(log_format, indir=log_file_path, outdir=structured_log_file_path,  depth=depth, st=st, rex=regex)
    parser.parse(log_file_name)

## parse the logs - convert unstructured to structured log
parseLog(log_file_path, unstructured_log_filename, structured_log_file_path, 'Java')
    

## read structured log 
print("Loading", structured_log_file_path+structured_log_filename)
structured_log = pd.read_csv(structured_log_file_path+structured_log_filename, engine='c', na_filter=False, memory_map=True)

structured_log


# In[6]:


# Filter null dates

structured_log = structured_log[structured_log['Date'] != '']
structured_log.head()


# In[7]:


def fill_zeros(x):
    if len(str(x)) < 6:
        return str(x).zfill(6)
    else:
        return str(x)
    
structured_log['Date'] = structured_log['Date'].apply(fill_zeros)
structured_log['Time'] = structured_log['Time'].apply(fill_zeros)

structured_log.loc[:,'Date'] = pd.to_datetime(structured_log.Date.astype(str)+' '+structured_log.Time.astype(str), format="%d-%m-%Y %H:%M:%S.%f")
# structured_log.set_index("Date", inplace=True)

structured_log = structured_log.drop(columns=['Time'])

structured_log.head()

# structured_log['EventTemplate'].nunique()


# In[8]:


# TODO: Cleaning: Remove rows and colums with count 0 (no data present)


# In[9]:


# Adding relevant columns to the dataframe
LOG_LEVELS = ['WARN', 'INFO', 'DEBUG', 'TRACE', 'ERROR', 'FATAL']
df_grouped = structured_log.groupby(pd.Grouper(key='Date', freq='5Min',closed='right',label='right')).agg(
    total_msgs=pd.NamedAgg(column="Content", aggfunc="count"),    
).reset_index()

for level in LOG_LEVELS:
    df_grouped[level + '_count'] = 0

for event_id in structured_log['EventId'].unique():
    df_grouped[event_id + '_count'] = 0

df_grouped.head()


# In[10]:


# Populating all the log level counts
df_grouped_logLevel = structured_log.groupby([pd.Grouper(key='Date', freq='5Min',closed='right',label='right'), 'Level']).agg(
    count=pd.NamedAgg(column="Level", aggfunc="count"),    
).reset_index()

for row in df_grouped_logLevel.itertuples():
    df_grouped.loc[df_grouped['Date'] == row.Date, row.Level + '_count'] = row.count

df_grouped.head()


# In[11]:


# Populating all the event id counts
df_grouped_eventId = structured_log.groupby([pd.Grouper(key='Date', freq='5Min',closed='right',label='right'), 'EventId']).agg(
    count=pd.NamedAgg(column="EventId", aggfunc="count"),    
).reset_index()

for row in df_grouped_eventId.itertuples():
    df_grouped.loc[df_grouped['Date'] == row.Date, row.EventId + '_count'] = row.count

df_grouped


# In[12]:


#Manually labelling anomalous window to compare later with model output

df_grouped['anomaly_manual'] = (df_grouped['ERROR_count'] > 0).astype(int)


# In[13]:


# Filtering only the feature columns

feature_cols = list(df_grouped.columns);
feature_cols.remove('Date')

data_with_feature_columns = df_grouped[feature_cols]


# In[14]:


#Scaling the values of the input data

from sklearn import preprocessing

min_max_scaler = preprocessing.MinMaxScaler()

data_scaled = min_max_scaler.fit_transform(data_with_feature_columns)
data_scaled = pd.DataFrame(data_scaled, columns=feature_cols)

data_scaled


# In[15]:


# Visualizing the relationship between log_levels and the event_ids

import seaborn as sns

all_columns = list(df_grouped.columns)
log_level_columns = all_columns[2:8]
event_id_columns = all_columns[8:-1]

sns.pairplot(df_grouped, y_vars= event_id_columns,
                  x_vars= log_level_columns)


# In[16]:


# Applying Kmeans clustering to different values of k (1-15)

import matplotlib.pyplot as plt
from sklearn.cluster import KMeans
RANDOM_STATE = 123

cluster_check_range = range(1, 15)

kmeans = [None] * (len(cluster_check_range) + 1)
scores = [0] * (len(cluster_check_range) + 1)
for i in cluster_check_range:
    if i == 0:
        continue
    kmeans[i] = KMeans(n_clusters=i, random_state=RANDOM_STATE).fit(data_scaled) 
    scores[i] = kmeans[i].score(data_scaled)


# In[17]:


# Finding the elbow point

fig, ax = plt.subplots()
ax.plot(cluster_check_range, scores[1:])
plt.show()


# In[18]:


# Selecting appropriate k. Here we chose k = 8

k = 2
cluster_model = kmeans[k]

df_grouped['cluster'] = cluster_model.predict(data_scaled)
df_grouped['cluster'].value_counts()
df = df_grouped['cluster'].value_counts().rename_axis('Cluster number').reset_index(name='counts')
df


# In[19]:


# final_data - df_grouped
# data_new - data_scaled


# In[20]:


# Applying tSNE to visualise data in 2D

from sklearn.manifold import TSNE

tsne = TSNE(n_components=2, verbose=1, perplexity=40, n_iter=300, random_state=RANDOM_STATE)
tsne_results = tsne.fit_transform(data_scaled)

df_grouped['tsne-x-axis'] = tsne_results[:,0]
df_grouped['tsne-y-axis'] = tsne_results[:,1]
df_grouped

tsne_cluster = df_grouped.groupby('cluster').agg({'tsne-x-axis':'mean', 'tsne-y-axis':'mean'}).reset_index()

plt.figure(figsize=(16,10))

sns.scatterplot(
    x="tsne-x-axis", y="tsne-y-axis",
    hue="cluster",
    palette=sns.color_palette("hls", k),
    data=df_grouped,
    legend="full",
    alpha=1
)

plt.scatter(x="tsne-x-axis", y="tsne-y-axis", data=tsne_cluster, s=100, c='b')
plt.show()


# In[21]:


# Plotting histogram of sum_squared_distances of all points from the center of clusters

def get_ssd(data, cluster_model, feature_cols):
    centers = cluster_model.cluster_centers_
    points = np.asarray(data[feature_cols])
    total_distance = pd.Series()
    for i in range(len(points)):
        total_distance.at[i] = get_distance(centers, points, i)
    return total_distance  

def get_distance(centers, points, i):
    distance = 0
    for j in range(len(centers)):
        d = np.linalg.norm(points[i] - centers[j])
        distance += d**2
    return distance


centers = cluster_model.cluster_centers_
points = np.asarray(data_scaled)

df_grouped['ssd'] = get_ssd(data_scaled, cluster_model, feature_cols)

plt.hist(df_grouped['ssd'], bins=100)


# In[22]:


# Setting cutoff to ssd for anomaly

cutoff = 8.5
df_grouped['anomaly_kmeans'] = (df_grouped['ssd'] >= cutoff).astype(int)
# score is calculated between 0 and 1, where score nearer to 1 will indicate an anomaly. This data will be rendenered on kibana.
df_grouped['anomaly_kmeans_score'] = (df_grouped['ssd'] - cutoff) / cutoff

sns.scatterplot(
    x="tsne-x-axis", y="tsne-y-axis",
    hue="anomaly_kmeans",
    data=df_grouped,
    legend="full",
    alpha=1
)


# In[23]:


# Listing anomalous rows according to k-means

df_grouped.loc[df_grouped['anomaly_kmeans']==1]


# In[24]:


# Clustering using Isolation forest algorithm

from sklearn.ensemble import IsolationForest

outlier_fraction = 0.03

model =  IsolationForest(n_jobs=-1, n_estimators=200, max_features=3, random_state=RANDOM_STATE, contamination=outlier_fraction)
model.fit(data_scaled)

df_grouped['anomaly_isolated'] = pd.Series(model.predict(data_scaled))
df_grouped['anomaly_isolated'] = df_grouped['anomaly_isolated'].map( {1: 0, -1: 1} )

sns.scatterplot(
    x="tsne-x-axis", y="tsne-y-axis",
    hue="anomaly_isolated",
    data=df_grouped,
    legend="full",
    alpha=1
)

# decision_function will calculate score for each data point. 
#In our context, based on contamination factor set negative values will represent likelihood of anamoly & positive value indicates normal values.
df_grouped['anomaly_score_isof'] = model.decision_function(data_scaled)

df_grouped.loc[df_grouped['anomaly_isolated']==1]


# In[25]:


import matplotlib.pyplot as plt
import numpy as np
from sklearn.metrics import f1_score, accuracy_score, confusion_matrix,ConfusionMatrixDisplay, recall_score

def calculate_metrics(y_true, y_pred):
    cf = confusion_matrix(y_true, y_pred)
    sensitivity = cf[0,0]/(cf[:,0].sum())
    specificity = cf[1,1]/(cf[:,1].sum())
    f1 = f1_score(y_true, y_pred)
    acc = accuracy_score(y_true, y_pred)
    recall = recall_score(y_true, y_pred, average='binary')
    return {'f1_score': f1, 'accuracy': acc, 'sensitivity': sensitivity, 'specificity': specificity, 'recall': recall}

anomaly_manual = df_grouped['anomaly_manual']
anomaly_isolated = df_grouped['anomaly_isolated']
anomaly_kmeans = df_grouped['anomaly_kmeans']

kmeans_metrics = calculate_metrics(anomaly_manual, anomaly_isolated)
iso_metrics = calculate_metrics(anomaly_manual, anomaly_kmeans)


# Create dataframes from the metrics dictionaries
kmeans_df = pd.DataFrame.from_dict(kmeans_metrics, orient='index', columns=['K-means'])
iso_df = pd.DataFrame.from_dict(iso_metrics, orient='index', columns=['Isolation Forest'])

# Combine the dataframes
metrics_df = pd.concat([kmeans_df, iso_df], axis=1)

# Print the dataframe
print(metrics_df)


# In[26]:


def plot_metric_comparison(kmeans_metrics, iso_metrics):
    fig, ax = plt.subplots()
    ax.bar(kmeans_metrics.keys(), kmeans_metrics.values(), width=-0.4, align='edge', label='K-means')
    ax.bar(iso_metrics.keys(), iso_metrics.values(), width=0.4, align='edge', label='Isolation Forest')
    ax.set_xlabel('Metrics')
    ax.set_ylabel('Scores')
    ax.set_title('Model Comparison')
    ax.legend()
    plt.show()


plot_metric_comparison(kmeans_metrics, iso_metrics)


# In[27]:


def plot_confusion_matrices(models, true_labels):
    n_models = len(models)
    fig, axes = plt.subplots(1, n_models, figsize=(5 * n_models, 5), sharey='row')
    
    for i, (model_name, y_pred) in enumerate(models.items()):
        cm = confusion_matrix(true_labels, y_pred)
        disp = ConfusionMatrixDisplay(confusion_matrix=cm, display_labels=['Negative', 'Positive'])
        disp.plot(ax=axes[i])
        disp.im_.colorbar.remove()
        disp.ax_.set_title(f"Confusion Matrix for {model_name}")
        
    plt.subplots_adjust(wspace=0.6, hspace=0.01)
   
    plt.show()
    
models = {
    'K Means': df_grouped['anomaly_kmeans'],
    'Isolated Forest': df_grouped['anomaly_isolated']
}

true_labels = df_grouped['anomaly_manual']
plot_confusion_matrices(models, true_labels)


# In[28]:


# Saving window results of ML run

interval_row = df_grouped.loc[df_grouped['Date'].idxmax()]

index_name = 'ml-results'

kmeans_metrics['anomaly_score'] = interval_row['anomaly_kmeans_score']
isolation_forest_metrics['anomaly_score'] = interval_row['anomaly_score_isof']

window_summary = {
    'start_time': df_grouped['Date'].max() - datetime.timedelta(minutes = 5),
    'end_time': df_grouped['Date'].max(),
    'k_means_metrics': kmeans_metrics,
    'isolation_forest_metrics': iso_metrics,
    'log_level_counts': interval_row[log_level_columns].to_dict(),
    'template_counts': interval_row[event_id_columns].to_dict(),    
}

print(window_summary)

es.index(index=index_name, document=window_summary)

