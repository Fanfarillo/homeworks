#!/bin/bash

ip addr add 10.0.30.2/24 dev enp0s3
ip route add default via 10.0.30.1