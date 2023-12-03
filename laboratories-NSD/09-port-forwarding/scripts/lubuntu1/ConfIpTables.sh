#!/bin/bash

iptables -F									#effettua il flush della vecchia configurazione dell'IP table, se presente
iptables -P FORWARD DROP						#permette tutto il traffico in uscita
iptables -A PREROUTING -t nat -d 1.0.0.2 -p tcp --dport 8080 -j DNAT --to-destination 10.0.0.100:80	#redireziona il traffico diretto alla porta 8080 verso la porta 80 di Tiny 1
iptables -A POSTROUTING -t nat -o enp0s8 -j MASQUERADE
iptables -A FORWARD -i enp0s8 -d 10.0.0.100 -p tcp --dport 80 -j ACCEPT		#permette il traffico TCP con sorgente=Internet, destinazione=LAN e porta=80
iptables -A FORWARD -i enp0s3 -j ACCEPT				#permette il traffico (in forwarding) con sorgente=LAN