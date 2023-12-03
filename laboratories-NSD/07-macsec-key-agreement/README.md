# MacSEC Key Agreement

### Step 1
Disabilitare tutte le connessioni di rete in Lubuntu 1 e in Lubuntu 2.

### Step 2
Creare una nuova connessione MacSEC in Lubuntu 1 (scripts/lubuntu1/CreateConnection.sh):

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
    ipv4.addresses 10.0.10.1/24

### Step 3
Creare una nuova connessione MacSEC in Lubuntu 2 (scripts/lubuntu2/CreateConnection.sh):

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

### Step 4
Avviare la connessione MacSEC in Lubuntu 1 (scripts/lubuntu1/StartConnection.sh):

    nmcli connection up test-macsec

### Step 5
Avviare la connessione MacSEC in Lubuntu 2 (scripts/lubuntu2/StartConnection.sh):

    nmcli connection up test-macsec
