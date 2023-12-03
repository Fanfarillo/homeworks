#!/bin/bash

docker run -it -d weibeld/ubuntu-networking
docker exec -it $CONTAINER_ID bash