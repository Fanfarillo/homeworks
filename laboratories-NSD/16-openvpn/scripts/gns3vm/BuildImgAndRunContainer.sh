#!/bin/bash

docker build -t weibeld/ubuntu-networking .
docker run -it weibeld/ubuntu-networking:latest