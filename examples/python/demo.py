import requests
from hashlib import md5
import base64
import random
import string
import time

with open("../test.jpg", "rb") as image_file:
    encoded_string = base64.b64encode(image_file.read())

app_key = '<your appKey>'
app_secret = '<your appSecret>'
nonstr = ''.join(random.choice(string.ascii_uppercase + string.digits) for _ in range(8))
timestamp = str(time.time()*1000)

m = md5()
m.update('%s:%s:%s:%s' % (app_key, app_secret, nonstr, timestamp))
app_signature = m.hexdigest()

data = {
    'type': 'express',
    'data': encoded_string
}

headers = {
    'app-timestamp': timestamp,
    'app-key': app_key,
    'app-nonstr': nonstr,
    'app-signature': app_signature
}

result = requests.post('http://rbs.hexindeu.com/api/open/task/detect', data=data, headers=headers).json()

print result
