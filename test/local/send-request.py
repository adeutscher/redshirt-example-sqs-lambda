#!/usr/bin/env python

import json
import requests

record = 'Hello World'

payload = {'Records': [{'body': json.dumps(record)}]}

response = requests.post(
    "http://localhost:9000/2015-03-31/functions/function/invocations",
    json.dumps(payload),
)
print(response.status_code, response.text)
