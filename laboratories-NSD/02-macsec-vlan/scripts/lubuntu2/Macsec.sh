#!/bin/bash

ip link add link enp0s3 macsec0 type macsec
ip macsec add macsec0 tx sa 0 pn 1 on key 01 12345678901234567890123456789012
ip macsec add macsec0 rx address 08:00:27:6a:dc:01 port 1
ip macsec add macsec0 rx address 08:00:27:6a:dc:01 port 1 sa 0 pn 1 on key 02 09876543210987654321098765432109
ip link set dev macsec0 up
ip addr add 10.100.0.2/24 dev macsec0