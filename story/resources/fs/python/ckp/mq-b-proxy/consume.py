#!/usr/bin/env python3

from http.client import HTTPConnection
import json

host = "localhost"
port = 8000
group_id = "simple"
topics = "q-data"


def consume(host, port, group_id, topics):
    connection = HTTPConnection(host, port)
    connection.request("GET", "/mq/consume?group_id={group_id}&topics={topics}&timeout={timeout}".format(
        group_id=group_id, topics=topics, timeout=15
    ))
    response = connection.getresponse()
    return json.loads(response.read().decode("utf-8"))


def main():
    print(consume(host=host, port=port, group_id=group_id, topics=topics))


if __name__ == "__main__":
    main()
