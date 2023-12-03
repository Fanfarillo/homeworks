#!/bin/bash

ip addr add 16.0.0.100/24 dev eth0
ip route add default via 16.0.0.1