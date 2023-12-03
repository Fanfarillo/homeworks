# IP tables & firewall

### Step 1
Disabilitare tutte le connessioni di rete in Lubuntu 1.

### Step 2
Fare il setup di Tiny 5 (scripts/tiny5/Setup.sh):

    sudo ip addr add 1.0.0.1/30 dev eth0				#configura l'indirizzo IP su eth0
    sudo echo 1 > /proc/sys/net/ipv4/ip_forward			#abilita il forwarding dei pacchetti
    sudo ip route add 16.0.0.0/24 via 1.0.0.2			#aggiunge la rotta per la rete DMZ 16.0.0.0
    sudo ip route add 10.0.0.0/24 via 1.0.0.2			#aggiunge la rotta per la rete LAN 10.0.0.0
    iptables -t nat -A POSTROUTING -o eth1 -j MASQUERADE	        #MASQUERADE traduce gli indirizzi sorgente

### Step 3
Configurare gli indirizzi IP di Lubutu1, che farà da router (scripts/lubuntu1/Setup.sh):

    export DMZ=enp0s3
    export LAN=enp0s8
    export EXT=enp0s9
    sudo ip addr add 16.0.0.1/24 dev $DMZ		#configura l'indirizzo IP dell'interfaccia esposta alla rete DMZ
    sudo ip addr add 10.0.0.1/24 dev $LAN		#configura l'indirizzo IP dell'interfaccia esposta alla rete LAN
    sudo ip addr add 1.0.0.2/30 dev $EXT		#configura l'indirizzo IP dell'interfaccia esposta all'esterno
    sudo echo 1 > /proc/sys/net/ipv4/ip_forward	#abilita il forwarding dei pacchetti
    sudo ip route add default via 1.0.0.1		#abilita il routing dei pacchetti

### Step 4
Fare il setup di Tiny 1 (scripts/tiny1/Setup.sh):

    sudo ip addr add 16.0.0.100/24 dev eth0
    sudo ip route add default via 16.0.0.1

### Step 5
Fare il setup di Tiny 2 (scripts/tiny2/Setup.sh):

    sudo ip addr add 16.0.0.101/24 dev eth0
    sudo ip route add default via 16.0.0.1

### Step 6
Fare il setup di Tiny 3 (scripts/tiny3/Setup.sh):

    sudo ip addr add 10.0.0.100/24 dev eth0
    sudo ip route add default via 10.0.0.1

### Step 7
Fare il setup di Tiny 4 (scripts/tiny4/Setup.sh):

    sudo ip addr add 10.0.0.101/24 dev eth0
    sudo ip route add default via 10.0.0.1

### Step 8
Configurare le IP table in Lubuntu 1 (scripts/lubuntu1/ConfIpTables.sh):

    sudo iptables -F						#effettua il flush della vecchia configurazione dell'IP table, se presente
    sudo iptables -P FORWARD DROP
    sudo iptables -P INPUT DROP
    sudo iptables -P OUTPUT ACCEPT					#permette tutto il traffico in uscita
    sudo iptables -A FORWARD -m state --state ESTABLISHED -j ACCEPT	#permette il traffico (in forwarding) relativo alle connessioni già stabilite
    sudo iptables -A FORWARD -i $LAN -p tcp --dport 22 -j ACCEPT	#permette il traffico SSH con sorgente=LAN e destinazione=Internet
    sudo iptables -A FORWARD -i $LAN -p tcp --dport 80 -j ACCEPT	#permette il traffico HTTP con sorgente=LAN e destinazione=Internet
    sudo iptables -A FORWARD -i $LAN -p tcp --dport 443 -j ACCEPT	#permette il traffico HTTPS con sorgente=LAN e destinazione=Internet
    sudo iptables -A FORWARD -i $LAN -p tcp --dport 53 -j ACCEPT	#permette il traffico DNS con sorgente=LAN e destinazione=Internet
    sudo iptables -A FORWARD -i $LAN -o $DMZ -j ACCEPT		#permette tutto il traffico con sorgente=LAN e destinazione=DMZ
    sudo iptables -A FORWARD -i $EXT -o $DMZ -j ACCEPT		#permette tutto il traffico con sorgente=Internet e destinazione=DMZ
    sudo iptables -A FORWARD -i $DMZ -o $EXT -j ACCEPT		#permette tutto il traffico con sorgente=DMZ e destinazione=Internet
    sudo iptables -A INPUT -m state --state ESTABLISHED -j ACCEPT	#permette il traffico in ingresso relativo alle connessioni già stabilite
    sudo iptables -A INPUT -i $LAN -p tcp --dport 22 -j ACCEPT	#permette il traffico SSH in ingresso con sorgente=LAN
    sudo iptables -A INPUT -i $EXT -p tcp --dport 22 -j ACCEPT	#permette il traffico SSH in ingresso con sorgente=Internet
    sudo iptables -A INPUT -p icmp -j ACCEPT			#permette il traffico ICMP in ingresso
    sudo iptables -A FORWARD -p icmp -j ACCEPT			#permette il traffico ICMP (in forwarding)

### Step 9
Configurare le IP table in Tiny 1 (scripts/tiny1/ConfIpTables.sh):

    sudo iptables -F					        #effettua il flush della vecchia configurazione dell'IP table, se presente
    sudo iptables -P INPUT DROP
    sudo iptables -P OUTPUT ACCEPT				        #permette tutto il traffico in uscita
    sudo iptables -A INPUT -m state --state ESTABLISHED -j ACCEPT	#permette il traffico in ingresso relativo alle connessioni già stabilite
    sudo iptables -A INPUT -p tcp --dport 22 -j ACCEPT	        #permette il traffico in ingresso SSH
    sudo iptables -A INPUT -p tcp --dport 80 -j ACCEPT	        #permette il traffico in ingresso HTTP
    sudo iptables -A INPUT -p tcp --dport 443 -j ACCEPT	        #permette il traffico in ingresso HTTPS
