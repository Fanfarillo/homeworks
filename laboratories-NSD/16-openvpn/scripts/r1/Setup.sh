#!/bin/bash

configure terminal

interface GigabitEthernet 1/0
ip address 1.1.2.1 255.255.255.252
ip nat outside
no shutdown
exit
interface GigabitEthernet 2/0
ip address 192.168.1.1 255.255.255.0
ip nat inside
no shutdown
exit

ip route 2.0.0.0 255.255.255.0 1.1.2.2
ip route 1.2.2.0 255.255.255.252 1.1.2.2
access-list 101 permit ip 192.168.1.0 0.0.0.255 any
ip nat inside source list 101 interface g1/0 overload	#g1/0 = GigabitEthernet 1/0
end

copy running-config startup-config				#salva la configurazione del router