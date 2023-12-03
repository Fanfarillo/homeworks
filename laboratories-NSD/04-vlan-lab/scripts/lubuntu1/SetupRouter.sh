#!/bin/bash

sysctl -w net.ipv4.ip_forward=1					#abilita il forwarding dei pacchetti ipv4
ip link add link enp0s3 name enp0s3.10 type vlan id 10	#crea l'interfaccia virtuale che gestisce VLAN 10
ip link add link enp0s3 name enp0s3.20 type vlan id 20	#crea l'interfaccia virtuale che gestisce VLAN 20
ip link set enp0s3.10 up
ip link set enp0s3.20 up
ip addr add 10.0.10.1/24 dev enp0s3.10				#aggiunge l'indirizzo IP per l'interfaccia VLAN 10
ip addr add 10.0.20.1/24 dev enp0s3.20				#aggiunge l'indirizzo IP per l'interfaccia VLAN 20