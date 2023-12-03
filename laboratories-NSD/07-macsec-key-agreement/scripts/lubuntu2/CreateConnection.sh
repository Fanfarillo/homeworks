#!/bin/bash

export MKA_CAK=00112233445566778899aabbccddeeff
export MKA_CKN=00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff

nmcli connection add type macsec \
con-name test-macsec ifname macsec0 \
connection.autoconnect no \
macsec.parent enp0s3 \
macsec.mode psk \
macsec.mka-cak $MKA_CAK \
macsec.mka-cak-flags 0 \
macsec.mka-ckn $MKA_CKN \
ipv4.addresses 10.0.10.2/24
