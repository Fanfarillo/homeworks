#!/bin/bash

configure terminal

interface Loopback 0
ip address 2.3.0.1 255.255.0.0
exit
interface Loopback 1
ip address 2.255.0.3 255.255.255.255
exit

interface GigabitEthernet 1/0
ip address 10.0.23.2 255.255.255.252
no shutdown
exit
interface GigabitEthernet 2/0
ip address 10.0.34.1 255.255.255.252
no shutdown
exit

router ospf 1
router-id 2.255.0.3
network 0.0.0.0 0.0.0.0 area 0	#esporta tutti i route in OSPF
exit

ip route 2.0.0.0 255.0.0.0 Null0	#fake route

router bgp 200
network 2.0.0.0
neighbor 2.255.0.2 remote-as 200
neighbor 2.255.0.2 update-source lo1
neighbor 2.255.0.4 remote-as 200
neighbor 2.255.0.4 update-source lo1
end