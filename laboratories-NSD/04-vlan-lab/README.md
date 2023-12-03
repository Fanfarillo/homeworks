# VLAN lab

### Step 1
Fare il setup di Tiny 1 (scripts/tiny1/Setup.sh):

    sudo ip addr add 10.0.10.101/24 dev eth0
    sudo ip route add default via 10.0.10.1

### Step 2
Fare il setup di Tiny 2 (scripts/tiny2/Setup.sh):

    sudo ip addr add 10.0.20.101/24 dev eth0
    sudo ip route add default via 10.0.20.1

### Step 3
Fare il setup di Tiny 3 (scripts/tiny3/Setup.sh):

    sudo ip addr add 10.0.10.102/24 dev eth0
    sudo ip route add default via 10.0.10.1

### Step 4
Fare il setup di Tiny 4 (scripts/tiny4/Setup.sh):

    sudo ip addr add 10.0.20.102/24 dev eth0
    sudo ip route add default via 10.0.20.1

### Step 5
Fare il setup del bridge e delle interfacce VLAN in Cumulus 1 (scripts/cumulus1/BridgeAndInterfaces.sh):

    net del all
    net commit
    net add bridge bridge ports swp1,swp2,swp3
    net add bridge bridge pvid 1			#definisce l'id del pacchetto 'untagged'
    net add bridge bridge vids 10,20		#definisce gli identifivatori VLAN configurati sullo switch
    net add interface swp1 bridge access 10		#configura la porta swp1 per essere un accesso per VLAN10
    net add interface swp2 bridge access 20		#configura la porta swp2 per essere un accesso per VLAN20
    net commit

### Step 6
Fare il setup del bridge e delle interfacce VLAN in Cumulus 2 (scripts/cumulus2/BridgeAndInterfaces.sh):

    net del all
    net commit
    net add bridge bridge ports swp1,swp2,swp3,swp4
    net add bridge bridge pvid 1			#definisce l'id del pacchetto 'untagged'
    net add bridge bridge vids 10,20		#definisce gli identifivatori VLAN configurati sullo switch
    net add interface swp1 bridge access 10		#configura la porta swp1 per essere un accesso per VLAN10
    net add interface swp2 bridge access 20		#configura la porta swp2 per essere un accesso per VLAN20
    net commit

### Step 7
Rendere Lubuntu 1 un router per VLAN 10 e VLAN 20 (scripts/lubuntu1/SetupRouter.sh):

    sudo sysctl -w net.ipv4.ip_forward=1				#abilita il forwarding dei pacchetti ipv4
    sudo ip link add link enp0s3 name enp0s3.10 type vlan id 10	#crea l'interfaccia virtuale che gestisce VLAN 10
    sudo ip link add link enp0s3 name enp0s3.20 type vlan id 20	#crea l'interfaccia virtuale che gestisce VLAN 20
    sudo ip link set enp0s3.10 up
    sudo ip link set enp0s3.20 up
    sudo ip addr add 10.0.10.1/24 dev enp0s3.10			#aggiunge l'indirizzo IP per l'interfaccia VLAN 10
    sudo ip addr add 10.0.20.1/24 dev enp0s3.20			#aggiunge l'indirizzo IP per l'interfaccia VLAN 20
