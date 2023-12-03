#!/bin/bash

net add dot1x radius shared-secret radiussecret
net add dot1x radius server-ip 10.0.30.2	#indirizzo IP di Lubuntu 3 (che è il server Radius)
net add dot1x dynamic-vlan
net add interface swp1,swp2 dot1x
net add dot1x dynamic-vlan require		#non autorizziamo gli utenti non autenticati sul server Radius
net commit