#!/bin/bash

ip addr add 1.2.2.1/30 dev eth0
ip addr add 10.0.0.1/24 dev eth1
ip route add 2.0.0.0/24 via 1.2.2.2
ip route add 1.1.2.0/30 via 1.2.2.2
iptables -t nat -A POSTROUTING -o enp0s3 -j MASQUERADE
echo 1 > /proc/sys/net/ipv4/ip_forward