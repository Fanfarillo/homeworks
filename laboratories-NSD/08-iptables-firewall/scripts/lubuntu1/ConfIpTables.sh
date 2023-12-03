#!/bin/bash

iptables -F									#effettua il flush della vecchia configurazione dell'IP table, se presente
iptables -P FORWARD DROP
iptables -P INPUT DROP
iptables -P OUTPUT ACCEPT						#permette tutto il traffico in uscita
iptables -A FORWARD -m state --state ESTABLISHED -j ACCEPT	#permette il traffico (in forwarding) relativo alle connessioni già stabilite
iptables -A FORWARD -i $LAN -p tcp --dport 22 -j ACCEPT	#permette il traffico SSH con sorgente=LAN e destinazione=Internet
iptables -A FORWARD -i $LAN -p tcp --dport 80 -j ACCEPT	#permette il traffico HTTP con sorgente=LAN e destinazione=Internet
iptables -A FORWARD -i $LAN -p tcp --dport 443 -j ACCEPT	#permette il traffico HTTPS con sorgente=LAN e destinazione=Internet
iptables -A FORWARD -i $LAN -p tcp --dport 53 -j ACCEPT	#permette il traffico DNS con sorgente=LAN e destinazione=Internet
iptables -A FORWARD -i $LAN -o $DMZ -j ACCEPT			#permette tutto il traffico con sorgente=LAN e destinazione=DMZ
iptables -A FORWARD -i $EXT -o $DMZ -j ACCEPT			#permette tutto il traffico con sorgente=Internet e destinazione=DMZ
iptables -A FORWARD -i $DMZ -o $EXT -j ACCEPT			#permette tutto il traffico con sorgente=DMZ e destinazione=Internet
iptables -A INPUT -m state --state ESTABLISHED -j ACCEPT	#permette il traffico in ingresso relativo alle connessioni già stabilite
iptables -A INPUT -i $LAN -p tcp --dport 22 -j ACCEPT		#permette il traffico SSH in ingresso con sorgente=LAN
iptables -A INPUT -i $EXT -p tcp --dport 22 -j ACCEPT		#permette il traffico SSH in ingresso con sorgente=Internet
iptables -A INPUT -p icmp -j ACCEPT					#permette il traffico ICMP in ingresso
iptables -A FORWARD -p icmp -j ACCEPT				#permette il traffico ICMP (in forwarding)