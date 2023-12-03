#!/bin/bash

iptables -F									#effettua il flush della vecchia configurazione dell'IP table, se presente
iptables -P INPUT DROP
iptables -P OUTPUT ACCEPT						#permette tutto il traffico in uscita
iptables -A INPUT -m state --state ESTABLISHED -j ACCEPT	#permette il traffico in ingresso relativo alle connessioni già stabilite
iptables -A INPUT -p tcp --dport 22 -j ACCEPT			#permette il traffico in ingresso SSH
iptables -A INPUT -p tcp --dport 80 -j ACCEPT			#permette il traffico in ingresso HTTP
iptables -A INPUT -p tcp --dport 443 -j ACCEPT			#permette il traffico in ingresso HTTPS