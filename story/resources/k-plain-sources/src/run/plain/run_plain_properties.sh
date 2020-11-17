#!/usr/bin/env bash

SCRIPT_DIR=$(dirname "$0")

java -jar $SCRIPT_DIR/simple-1.0.0-SNAPSHOT-standalone.jar -c $SCRIPT_DIR/plain.properties
