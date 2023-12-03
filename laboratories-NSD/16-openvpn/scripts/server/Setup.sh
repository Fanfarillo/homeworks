#!/bin/bash

ip addr add 2.0.0.1/24 dev eth0
ip route add default via 2.0.0.2
iptables -t nat -A POSTROUTING -o enp0s3 -j MASQUERADE
echo 1 > /proc/sys/net/ipv4/ip_forward