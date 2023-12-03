#!/bin/bash

cd nsd01-test
make
cd ..
apt-get install git libpcap-dev
make