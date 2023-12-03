#!/bin/bash

apt install linux-tools-$(uname -r)
chmod ugo+x configure_test.sh
./configure_test.sh