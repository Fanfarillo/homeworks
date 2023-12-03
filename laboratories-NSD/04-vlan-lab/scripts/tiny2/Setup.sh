#!/bin/bash

ip addr add 10.0.20.101/24 dev eth0
ip route add default via 10.0.20.1