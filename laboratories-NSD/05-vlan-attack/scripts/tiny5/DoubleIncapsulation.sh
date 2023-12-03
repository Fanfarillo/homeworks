#!/bin/bash

ip link add link eth0 name eth0.1 type vlan id 1
ip link add link eth0.1 name eth0.1.20 type vlan id 20
ip link set eth0.1 up
ip link set eth0.1.20 up
ip addr add 10.0.20.250/24 dev eth0.1.20
arp -s 10.0.20.102 08:00:27:a2:8d:5b	#inserisce nella tabella ARP gli indirizzi MAC e IP di Tiny 4