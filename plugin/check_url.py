#!/usr/bin/env python

import requests

from requests.auth import HTTPBasicAuth

from pynag.Plugins import PluginHelper, ok, warning, critical, unknown

def useBasicAuth(options):
    return options.user != None and options.password != None

def useSsl(options):
    return options.keycert != None or options.usessl != None

def useSslAuth(options):
    return options.keycert != None

def normalizeRelPath(relpath):
    path = relpath
    if relpath.startswith("/"):
        path = relpath[1:]
    return path

helper = PluginHelper()

helper.parser.add_option("-H", help="Host to connect to.", dest="host", default='localhost')
helper.parser.add_option("-P", help="Port on the host.", dest="port", default='80')

helper.parser.add_option("-U", help="Relative path and query params.", dest="relpath", default='/')
helper.parser.add_option("-m", help="HTTP Method (POST, GET, PUT, DELETE).", dest="method", default="POST")

# If using Basic Authentication
helper.parser.add_option("-u", help="Username (if using basic auth).", dest="user")
helper.parser.add_option("-p", help="Password (if using basic auth).", dest="password")

# If using SSL Authentication
helper.parser.add_option("-k", help="Key-cert (PEM with both private key and cert, if using mutual auth).", dest="keycert")
helper.parser.add_option("-K", help="Use SSL - any value means true (Unnecessary if key-cert is specified)", dest="usessl", default=None)

helper.parse_arguments()

# Basic Authentication
auth = None

if useBasicAuth(helper.options):
    auth = HTTPBasicAuth(helper.options.user, helper.options.password)

# Use SSL?
prefix = "http"

if useSsl(helper.options):
    prefix = "https"

# SSL Authentication
cert = None

if useSslAuth(helper.options):
    cert = helper.options.keycert

relpath = normalizeRelPath(helper.options.relpath)

url = "{0}://{1}:{2}/{3}".format(prefix, helper.options.host, helper.options.port, relpath)

method = getattr(requests, helper.options.method.lower())
response = method(url, auth=auth, cert=cert)
status_code = response.status_code
status_check = response.text.strip()
# status_code = 200
# status_check = "WARNING - sprocket count ok | count=12 | latency=1s"

if status_code > 300:
    helper.status(unknown)
    helper.add_summary("Error making request; status code: {0}".format(status_code))

else:

    status  = status_check[:status_check.find("-")].strip()
    message = status_check[status_check.find("-") + 1:].strip()

    helper.add_summary(message)

    if status.startswith("OK"):
        helper.status(ok)
    elif status.startswith("WARNING"):
        helper.status(warning)
    elif status.startswith("CRITICAL"):
        helper.status(critical)
    else:
        helper.status(unknown)

helper.check_all_metrics()

helper.exit()