#!/bin/bash

cd /usr/share/easy-rsa
cp openssl-1.0.0.cnf openssl.cnf	#copia la configurazione di openssl-1.0.0.cnf nel file openssl.cnf
. ./vars					#esporta le variabili di easy-rsa come variabili d'ambiente
./clean-all
./build-ca					#qui dovranno essere inseriti dei dati (country name, ecc.)
./build-key-server server		#qui dovranno essere inseriti dei dati (country name, ecc.)
./build-key client1			#qui dovranno essere inseriti dei dati (country name, ecc.)
./build-key client2			#qui dovranno essere inseriti dei dati (country name, ecc.)
./build-dh					#dh = Diffie-Hellman