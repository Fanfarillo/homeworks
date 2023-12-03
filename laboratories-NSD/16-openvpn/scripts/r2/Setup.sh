#!/bin/bash

configure terminal

interface GigabitEthernet 1/0
ip address 1.1.2.2 255.255.255.252
no shutdown
exit
interface GigabitEthernet 2/0
ip address 2.0.0.2 255.255.255.0
no shutdown
exit
interface GigabitEthernet 3/0
ip address 192.168.0.1 255.255.255.0
no shutdown
exit
interface GigabitEthernet 4/0
ip address 1.2.2.2 255.255.255.252
no shutdown
end

copy running-config startup-config		#salva la configurazione del router