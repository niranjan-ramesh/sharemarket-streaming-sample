from kafka import KafkaConsumer
from threading import Thread
from os import path
import json
import matplotlib.pyplot as plt
import matplotlib.animation as anim

kafka_consumer = KafkaConsumer('sensex-events', group_id='sensex-chart-maker')
def consumer():
    print('Starting consumer')
    for message in kafka_consumer:
        decoded_message = message.value.decode('utf-8')
        if path.isfile('./sensex.txt'): 
            with open('sensex.txt', 'a', encoding='utf-8') as text_file:
                print(decoded_message, file = text_file)
        else: 
            with open('sensex.txt', 'w', encoding='utf-8') as text_file:
                print(decoded_message, file = text_file)

def animate(fig):
    data = open('./sensex.txt', 'r').read()
    data = data.split('\n')
    x = []
    y = []
    for points in data:
        points = points.strip()
        if (points != None) and (points != ''):
            point = json.loads(points)
            y.append(point.get('currentValue'))
            x.append(point.get('lastRefreshed'))
    plt.plot(x, y)

consumer_thread = Thread(target=consumer)
consumer_thread.start()

fig = plt.figure()
line_chart = anim.FuncAnimation(fig, animate, interval=5000)
plt.show()