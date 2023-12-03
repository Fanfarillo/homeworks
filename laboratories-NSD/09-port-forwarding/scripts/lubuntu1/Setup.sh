#!/bin/bash

ip addr add 10.0.0.1/24 dev enp0s3		#configura l'indirizzo IP dell'interfaccia esposta alla rete LAN
ip addr add 1.0.0.2/30 dev enp0s8		#configura l'indirizzo IP dell'interfaccia esposta all'esterno
echo 1 > /proc/sys/net/ipv4/ip_forward	#abilita il forwarding dei pacchetti