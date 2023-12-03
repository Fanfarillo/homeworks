#!/bin/bash

cd nsd02-bit-vector-linear-search-fw
make
cd ..
apt-get install git libpcap-dev
make