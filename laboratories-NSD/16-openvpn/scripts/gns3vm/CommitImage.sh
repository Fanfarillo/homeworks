#!/bin/bash

docker commit $CONTAINER_ID openvpn:latest
docker rm $CONTAINER_ID