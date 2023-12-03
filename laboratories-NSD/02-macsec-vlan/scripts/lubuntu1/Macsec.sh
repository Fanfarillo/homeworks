#!/bin/bash

ip link add link enp0s3 macsec0 type macsec
ip macsec add macsec0 tx sa 0 pn 1 on key 01 09876543210987654321098765432109
ip macsec add macsec0 rx address 08:00:27:c2:be:91 port 1
ip macsec add macsec0 rx address 08:00:27:c2:be:91 port 1 sa 0 pn 1 on key 02 12345678901234567890123456789012
ip link set dev macsec0 up
ip addr add 10.100.0.1/24 dev macsec0