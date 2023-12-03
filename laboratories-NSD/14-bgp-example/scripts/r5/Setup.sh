#!/bin/bash

configure terminal

interface Loopback 0
ip address 3.0.0.1 255.255.0.0
exit
interface Loopback 1
ip address 3.1.0.1 255.255.0.0
exit

interface GigabitEthernet 1/0
ip address 10.0.45.2 255.255.255.252
no shutdown
exit

ip route 3.0.0.0 255.0.0.0 Null0	#fake route

router bgp 300
network 3.0.0.0
neighbor 10.0.45.1 remote-as 200
end