#!/bin/bash

cd /etc/freeradius/3.0
echo "client cumulus1 {\n\t ipaddr = 10.0.30.1\n\t secret = radiussecret\n\t shortname = lab\n}" >> clients.conf
echo "pippo\t Cleartext-Password := 'pippo'\n\t Service-Type = Framed-User,\n\t Tunnel-Type = 13,\n\t Tunnel-Medium-Type = 6,\n\t Tunnel-Private-Group-ID = 10\n\n" >> users
echo "pluto\t Cleartext-Password := 'pluto'\n\t Service-Type = Framed-User,\n\t Tunnel-Type = 13,\n\t Tunnel-Medium-Type = 6,\n\t Tunnel-Private-Group-ID = 20" >> users