# OpenVPN

### Step 1
Scrivere un Dockerfile in GNS3 VM per definire un'immagine relativa a container dotati di volumes (scripts/gns3vm/CreateDockerfile.sh):

    mkdir openvpn-image
    cd openvpn-image
    touch Dockerfile
    echo "FROM openvpn:latest\nRUN mkdir /rw\nVOLUME /rw" > Dockerfile

### Step 2
Fare la build dell'immagine e il run del container (scripts/gns3vm/BuildImgAndRunContainer.sh):

    docker build -t weibeld/ubuntu-networking .
    docker run -it weibeld/ubuntu-networking:latest

### Step 3
Installare tmux e openvpn easy-rsa all'interno del container (scripts/gns3vm/InstallTmuxAndEasyRSA.sh):

    apt update
    apt install -y tmux
    apt install -y openvpn easy-rsa
    exit

### Step 4
Creare in GNS3 VM un'immagine associata al nuovo container (scripts/gns3vm/CommitImage.sh):

    docker commit $CONTAINER_ID openvpn:latest
    docker rm $CONTAINER_ID

### Step 5
Importare il nuovo container in GNS3 creando manualmente un nuovo template e selezionando un'immagine esistente (openvpn:latest).

### Step 6
Creare il certificato e le chiavi (server, client1, client2 e DH) all'interno del server (scripts/server/CreateCrtAndKeys.sh):

    cd /usr/share/easy-rsa
    cp openssl-1.0.0.cnf openssl.cnf	#copia la configurazione di openssl-1.0.0.cnf nel file openssl.cnf
    . ./vars				#esporta le variabili di easy-rsa come variabili d'ambiente
    ./clean-all
    ./build-ca				#qui dovranno essere inseriti dei dati (country name, ecc.)
    ./build-key-server server		#qui dovranno essere inseriti dei dati (country name, ecc.)
    ./build-key client1			#qui dovranno essere inseriti dei dati (country name, ecc.)
    ./build-key client2			#qui dovranno essere inseriti dei dati (country name, ecc.)
    ./build-dh				#dh = Diffie-Hellman

### Step 7
Fare il setup del router R1 (scripts/r1/Setup.sh):

    configure terminal

    interface GigabitEthernet 1/0
    ip address 1.1.2.1 255.255.255.252
    ip nat outside
    no shutdown
    exit
    interface GigabitEthernet 2/0
    ip address 192.168.1.1 255.255.255.0
    ip nat inside
    no shutdown
    exit

    ip route 2.0.0.0 255.255.255.0 1.1.2.2
    ip route 1.2.2.0 255.255.255.252 1.1.2.2
    access-list 101 permit ip 192.168.1.0 0.0.0.255 any
    ip nat inside source list 101 interface g1/0 overload	#g1/0 = GigabitEthernet 1/0
    end

    copy running-config startup-config			#salva la configurazione del router

### Step 8
Fare il setup del router R2 (scripts/r2/Setup.sh):

    configure terminal

    interface GigabitEthernet 1/0
    ip address 1.1.2.2 255.255.255.252
    no shutdown
    exit
    interface GigabitEthernet 2/0
    ip address 2.0.0.2 255.255.255.0
    no shutdown
    exit
    interface GigabitEthernet 3/0
    ip address 192.168.0.1 255.255.255.0
    no shutdown
    exit
    interface GigabitEthernet 4/0
    ip address 1.2.2.2 255.255.255.252
    no shutdown
    end

    copy running-config startup-config		#salva la configurazione del router

### Step 9
Rendere il certificato e le chiavi persistenti all'interno del server (scripts/server/MakeCrtAndKeysPersistent.sh):

    cp -r keys/ /gns3volumes/rw/	#/gns3volumes/rw è la directory persistente dei nostri container
    cd /gns3volumes/rw/keys
    mkdir ccd			#qui verranno creati dei file di configurazione per OpenVPN
    cd ccd

### Step 10
Fare il setup del server (scripts/server/Setup.sh):

    ip addr add 2.0.0.1/24 dev eth0
    ip route add default via 2.0.0.2
    iptables -t nat -A POSTROUTING -o enp0s3 -j MASQUERADE
    echo 1 > /proc/sys/net/ipv4/ip_forward

### Step 11
Nel server, creare all'interno della directory /gns3volumes/rw un file bash che abbia esattamente il contenuto di scripts/server/Setup.sh.

### Step 12
Fare il setup del client 1 (scripts/client1/Setup.sh):

    ip addr add 192.168.1.100/24 dev eth0
    ip route add default via 192.168.1.1

### Step 13
Nel client 1, creare all'interno della directory /gns3volumes/rw un file bash che abbia esattamente il contenuto di scripts/client1/Setup.sh. Creare inoltre una cartella di nome openvpn-cfg.

### Step 14
Fare il setup del client 2 (scripts/client2/Setup.sh):

    ip addr add 1.2.2.1/30 dev eth0
    ip addr add 10.0.0.1/24 dev eth1
    ip route add 2.0.0.0/24 via 1.2.2.2
    ip route add 1.1.2.0/30 via 1.2.2.2
    iptables -t nat -A POSTROUTING -o enp0s3 -j MASQUERADE
    echo 1 > /proc/sys/net/ipv4/ip_forward

### Step 15
Nel client 2, creare all'interno della directory /gns3volumes/rw un file bash che abbia esattamente il contenuto di scripts/client2/Setup.sh. Creare inoltre una cartella di nome openvpn-cfg.

### Step 16
Fare il setup dell'host A (scripts/hostA/Setup.sh):

    ip addr add 10.0.0.100/24 dev eth0
    ip route add default via 10.0.0.1

### Step 17
Nell'host A, creare all'interno della directory /gns3volumes/rw un file bash che abbia esattamente il contenuto di scripts/hostA/Setup.sh.

### Step 18
Fare il setup dell'host B (scripts/hostB/Setup.sh):

    ip addr add 192.168.0.100/24 dev eth0
    ip route add default via 192.168.0.1

### Step 19
Nell'host B, creare all'interno della directory /gns3volumes/rw un file bash che abbia esattamente il contenuto di scripts/hostB/Setup.sh.

### Step 20
Nel server, creare all'interno della directory /gns3volumes/rw/keys/ccd il file client1 che avrà il seguente contenuto:

    if-config-push 192.168.100.101 192.168.100.102

### Step 21
Nel server, creare all'interno della directory /gns3volumes/rw/keys/ccd il file client2 che avrà il seguente contenuto:

    if-config-push 192.168.100.105 192.168.100.106
    iroute 10.0.0.0 255.255.255.0

### Step 22
Nel server, creare all'interno della directory /gns3volumes/rw/keys il file server.ovpn che avrà il seguente contenuto:

    port 1194
    proto udp
    dev tun
    ca ca.crt
    cert server.crt
    key server.key
    dh dh2048.pem
    server 192.168.100.0 255.255.255.0
    push "route 192.168.0.0 255.255.255.0"
    push "route 10.0.0.0 255.255.255.0"
    route 10.0.0.0 255.255.255.0
    client-config-dir ccd
    client-to-client
    keepalive 10 120
    cipher AES-256-CBC

### Step 23
Nel client 1, creare all'interno della directory /gns3volumes/rw/openvpn-cfg il file client1.crt, che avrà esattamente il contenuto del file omonimo all'interno del server.

### Step 24
Nel client 1, creare all'interno della directory /gns3volumes/rw/openvpn-cfg il file client1.key, che avrà esattamente il contenuto del file omonimo all'interno del server.

### Step 25
Nel client 1, creare all'interno della directory /gns3volumes/rw/openvpn-cfg il file ca.crt, che avrà esattamente il contenuto del file omonimo all'interno del server.

### Step 26
Nel client 2, creare all'interno della directory /gns3volumes/rw/openvpn-cfg il file client2.crt, che avrà esattamente il contenuto del file omonimo all'interno del server.

### Step 27
Nel client 2, creare all'interno della directory /gns3volumes/rw/openvpn-cfg il file client2.key, che avrà esattamente il contenuto del file omonimo all'interno del server.

### Step 28
Nel client 2, creare all'interno della directory /gns3volumes/rw/openvpn-cfg il file ca.crt, che avrà esattamente il contenuto del file omonimo all'interno del server.

### Step 29
Nel client 1, creare all'interno della directory /gns3volumes/rw/openvpn-cfg il file client1.ovpn che avrà il seguente contenuto:

    client
    dev tun
    proto udp
    remote 2.0.0.1 1194
    resolv-retry infinite
    ca ca.crt
    cert client1.crt
    key client1.key
    remote-cert-tls server
    cipher AES-256-CBC

### Step 30
Nel client 2, creare all'interno della directory /gns3volumes/rw/openvpn-cfg il file client2.ovpn che avrà il seguente contenuto:

    client
    dev tun
    proto udp
    remote 2.0.0.1 1194
    resolv-retry infinite
    ca ca.crt
    cert client2.crt
    key client2.key
    remote-cert-tls server
    cipher AES-256-CBC

### Step 31
Nel server, aprire tmux e lanciare il seguente comando:

    openvpn server.ovpn

### Step 32
Nel client 1, aprire tmux e lanciare il seguente comando per connettersi al server:

    openvpn client1.ovpn

### Step 33
Nel client 2, aprire tmux e lanciare il seguente comando per connettersi al server:

    openvpn client2.ovpn
