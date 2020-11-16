#!/usr/bin/env bash

SCRIPT_DIR=$(dirname "$0")

sudo docker-compose stop zookeeper
sudo docker-compose stop broker