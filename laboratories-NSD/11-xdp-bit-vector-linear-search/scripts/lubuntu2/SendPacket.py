from scapy.all import *

p = Ether(dst="ff:ff:ff:ff:ff:ff")/IP(src="10.0.0.1", dst="8.8.8.9")/TCP(sport=10000, dport=22)
sendp(p, iface="enp0s3")