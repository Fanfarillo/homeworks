# MacSEC vlan

### Step 1
Fare il setup dell'indirizzo IP di Lubuntu 1 (scripts/lubuntu1/Setup.sh):

    sudo ip addr add 10.0.0.101/24 dev enp0s3
    sudo ip link set enp0s3 up

### Step 2
Fare il setup dell'indirizzo IP di Lubuntu 2 (scripts/lubuntu2/Setup.sh):

    sudo ip addr add 10.0.0.102/24 dev enp0s3
    sudo ip link set enp0s3 up

### Step 3
Fare il setup dell'indirizzo IP di Lubuntu 3 (scripts/lubuntu3/Setup.sh):

    sudo ip addr add 10.0.0.103/24 dev enp0s3
    sudo ip link set enp0s3 up

### Step 4
Fare il setup del bridge in Cumulus 1 (scripts/cumulus1/SetupBridge.sh):

    net del all
    net commit
    net add bridge bridge ports swp1,swp2,swp3
    net commit

### Step 5
Fare il setup del canale MacSEC lato Lubuntu 1 (scripts/lubuntu1/Macsec.sh):

    sudo ip link add link enp0s3 macsec0 type macsec
    sudo ip macsec add macsec0 tx sa 0 pn 1 on key 01 09876543210987654321098765432109
    sudo ip macsec add macsec0 rx address 08:00:27:c2:be:91 port 1
    sudo ip macsec add macsec0 rx address 08:00:27:c2:be:91 port 1 sa 0 pn 1 on key 02 12345678901234567890123456789012
    sudo ip link set dev macsec0 up
    sudo ip addr add 10.100.0.1/24 dev macsec0

### Step 6
Fare il setup del canale MacSEC lato Lubuntu 2 (scripts/lubuntu2/Macsec.sh):

    sudo ip link add link enp0s3 macsec0 type macsec
    sudo ip macsec add macsec0 tx sa 0 pn 1 on key 01 12345678901234567890123456789012
    sudo ip macsec add macsec0 rx address 08:00:27:6a:dc:01 port 1
    sudo ip macsec add macsec0 rx address 08:00:27:6a:dc:01 port 1 sa 0 pn 1 on key 02 09876543210987654321098765432109
    sudo ip link set dev macsec0 up
    sudo ip addr add 10.100.0.2/24 dev macsec0

### Step 7
Attivare la cifratura nel canale MacSEC lato Lubuntu 1 (scripts/lubuntu1/Encryption.sh):

    sudo ip link set macsec0 type macsec encrypt on

### Step 8
Attivare la cifratura nel canale MacSEC lato Lubuntu 2 (scripts/lubuntu2/Encryption.sh):

    sudo ip link set macsec0 type macsec encrypt on
