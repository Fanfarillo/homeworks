# 802.1x lab

### Step 1
Disabilitare tutte le connessioni di rete in Lubuntu 1 e in Lubuntu 2.

### Step 2
Installare Freeradius in Lubuntu 3, che fungerà proprio da server Radius (scripts/lubuntu3/InstallFreeradius.sh):

    sudo apt install -y freeradius

### Step 3
Impostare il client (Cumulus 4.1-1) nel file di configurazione /etc/freeradius/3.0/clients.conf in Lubuntu 3 (scripts/lubuntu3/ConfClientAndUsers.sh):

    client cumulus1 {
	  ipaddr = 10.0.30.1
	  secret = radiussecret
	  shortname = lab
    }

### Step 4
Impostare gli user (pippo in Lubuntu 1 e pluto in Lubuntu 2) nel file di configurazione /etc/freeradius/3.0/users in Lubuntu 3 (scripts/lubuntu3/ConfClientAndUsers.sh):

    pippo	Cleartext-Password := "pippo"
		Service-Type = Framed-User,
		Tunnel-Type = 13,
		Tunnel-Medium-Type = 6,
		Tunnel-Private-Group-ID = 10

    pluto	Cleartext-Password := "pluto"
		Service-Type = Framed-User,
		Tunnel-Type = 13,
		Tunnel-Medium-Type = 6,
		Tunnel-Private-Group-ID = 20

### Step 5
Fare il setup di Lubuntu 3 (scripts/lubuntu3/Setup.sh):

    sudo ip addr add 10.0.30.2/24 dev enp0s3
    sudo ip route add default via 10.0.30.1

### Step 6
Fare il setup del bridge e dell'interfaccia VLAN statica in Cumulus 4.1-1 (scripts/cumulus4.1-1/BridgeAndStaticInterface.sh):

    net del all
    net commit
    net add bridge bridge ports swp1,swp2,swp3
    net add bridge bridge vids 30			#definisce l'identificatore VLAN configurato staticamente sullo switch
    net add interface swp3 bridge access 30		#configura la porta swp3 per essere un accesso per VLAN30
    net commit

### Step 7
Aggiungere le varie VLAN (statica e dinamiche) in Cumulus 4.1-1 (scripts/cumulus4.1-1/DefineVlans.sh):

    net add vlan 10 ip address 10.0.10.1/24
    net add vlan 10 ip address 10.0.20.1/24
    net add vlan 10 ip address 10.0.30.1/24
    net commit

### Step 8
Configurare dot1x in Cumulus 4.1-1 (scripts/cumulus4.1-1/ConfigureDot1x.sh):

    net add dot1x radius shared-secret radiussecret
    net add dot1x radius server-ip 10.0.30.2	#indirizzo IP di Lubuntu 3 (che è il server Radius)
    net add dot1x dynamic-vlan
    net add interface swp1,swp2 dot1x
    net add dot1x dynamic-vlan require		#non autorizziamo gli utenti non autenticati sul server Radius
    net commit

### Step 9
Avviare Freeradius in Lubuntu 3 (scripts/lubuntu3/StartFreeradius.sh):

    sudo service freeradius stop
    sudo freeradius -X

### Step 10
Definire una nuova connessione di rete per Lubuntu 1 e Lubuntu 2 esattamente come riportato nelle immagini presenti in network-conf/lubuntu1 e network-conf/lubuntu2.