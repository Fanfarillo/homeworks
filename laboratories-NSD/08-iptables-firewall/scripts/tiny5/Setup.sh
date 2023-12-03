#!/bin/bash

ip addr add 1.0.0.1/30 dev eth0				#configura l'indirizzo IP su eth0
echo 1 > /proc/sys/net/ipv4/ip_forward			#abilita il forwarding dei pacchetti
ip route add 16.0.0.0/24 via 1.0.0.2			#aggiunge la rotta per la rete DMZ 16.0.0.0
ip route add 10.0.0.0/24 via 1.0.0.2			#aggiunge la rotta per la rete LAN 10.0.0.0
iptables -t nat -A POSTROUTING -o eth1 -j MASQUERADE	#MASQUERADE traduce gli indirizzi sorgente