#!/bin/bash

export DMZ=enp0s3
export LAN=enp0s8
export EXT=enp0s9
ip addr add 16.0.0.1/24 dev $DMZ		#configura l'indirizzo IP dell'interfaccia esposta alla rete DMZ
ip addr add 10.0.0.1/24 dev $LAN		#configura l'indirizzo IP dell'interfaccia esposta alla rete LAN
ip addr add 1.0.0.2/30 dev $EXT		#configura l'indirizzo IP dell'interfaccia esposta all'esterno
echo 1 > /proc/sys/net/ipv4/ip_forward	#abilita il forwarding dei pacchetti
ip route add default via 1.0.0.1		#abilita il routing dei pacchetti