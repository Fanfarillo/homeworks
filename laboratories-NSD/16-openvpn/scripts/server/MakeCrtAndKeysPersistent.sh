#!/bin/bash

cp -r keys/ /gns3volumes/rw/	#/gns3volumes/rw è la directory persistente dei nostri container
cd /gns3volumes/rw/keys
mkdir ccd				#qui verranno creati dei file di configurazione per OpenVPN
cd ccd