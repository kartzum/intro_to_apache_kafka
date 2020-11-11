#!/usr/bin/env python3

from http.client import HTTPConnection
import json

host = "localhost"
port = 8000
topic = "q-data"
key = "42"
value = "73"


def produce(host, port, topic, key, value):
    connection = HTTPConnection(host, port)
    connection.request("GET", "/mq/produce?topic={topic}&key={key}&value={value}".format(
        topic=topic, key=key, value=value
    ))
    response = connection.getresponse()
    return json.loads(response.read().decode("utf-8"))


def main():
    print(produce(host=host, port=port, topic=topic, key=key, value=value))


if __name__ == "__main__":
    main()
