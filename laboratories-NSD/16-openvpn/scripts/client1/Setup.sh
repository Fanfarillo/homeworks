#!/bin/bash

ip addr add 192.168.1.100/24 dev eth0
ip route add default via 192.168.1.1