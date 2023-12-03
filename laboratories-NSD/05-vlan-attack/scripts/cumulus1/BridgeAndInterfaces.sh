#!/bin/bash

net del all
net commit
net add bridge bridge ports swp1,swp2,swp3,swp4
net add bridge bridge pvid 1				#definisce l'id del pacchetto 'untagged'
net add bridge bridge vids 10,20			#definisce gli identifivatori VLAN configurati sullo switch
net add interface swp1 bridge access 10		#configura la porta swp1 per essere un accesso per VLAN10
net add interface swp2 bridge access 20		#configura la porta swp2 per essere un accesso per VLAN20
net commit