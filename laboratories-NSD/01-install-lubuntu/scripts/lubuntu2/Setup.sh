#!/bin/bash

ip addr add 10.0.0.102/24 dev enp0s3
ip link set enp0s3 up