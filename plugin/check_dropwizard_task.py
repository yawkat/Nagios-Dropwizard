#!/usr/bin/env python

#  Executes an HTTP request to a Dropwizard Task endpoint.
#  This script expects the task endpoint to return a properly formatted Nagios status check message.

import sys, requests
from requests.auth import HTTPBasicAuth

def dump_results(exit_value, message):
    """
    Dump the message to the console and exit with the supplied exit code.
    :param exit_value: exit code
    :param message: message to print before exiting.
    :return: nothing.
    """
    print message
    sys.exit(exit_value)

username = sys.argv[1]
password = sys.argv[2]
hostname = sys.argv[3]
port     = sys.argv[4]
task     = sys.argv[5]

if len(sys.argv) == 7:
    params = sys.argv[6]
else:
    params = None

auth = HTTPBasicAuth(username, password)

url = "http://{0}:{1}/tasks/{2}".format(hostname, port, task)

if params != None:
    url = url + "?{0}".format(params)

response = requests.post(url, auth=auth)

if response.status_code > 300:
    # 'UNKNOWN' is expected by the testing framework.  While it's not required by Nagios, it
    # makes my life easier.
    dump_results(3, "UNKNOWN - Error making request; status code: {0}".format(response.status_code))

status_check = response.text.strip()

if status_check.startswith("OK"):
    dump_results(0, status_check)

elif status_check.startswith("WARNING"):
    dump_results(1, status_check)

elif status_check.startswith("CRITICAL"):
    dump_results(2, status_check)

else:
    dump_results(3, status_check)