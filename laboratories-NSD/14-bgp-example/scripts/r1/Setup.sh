#!/bin/bash

configure terminal

interface Loopback 0
ip address 1.0.0.1 255.255.0.0
exit
interface Loopback 1
ip address 1.1.0.1 255.255.0.0
exit

interface GigabitEthernet 1/0
ip address 10.0.12.1 255.255.255.252
no shutdown
exit

ip route 1.0.0.0 255.0.0.0 Null0	#fake route

router bgp 100
network 1.0.0.0
neighbor 10.0.12.2 remote-as 200
end