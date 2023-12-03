# Port forwarding

### Step 1
Fare il setup di Tiny 1 (scripts/tiny1/Setup.sh):

    sudo ip addr add 10.0.0.100/24 dev eth0
    sudo ip route add default via 10.0.0.1

### Step 2
Fare il setup di Tiny 2 (scripts/tiny2/Setup.sh):

    sudo ip addr add 1.0.0.1/30 dev eth0

### Step 3
Configurare gli indirizzi IP di Lubutu1, che farà da router (scripts/lubuntu1/Setup.sh):

    sudo ip addr add 10.0.0.1/24 dev enp0s3		#configura l'indirizzo IP dell'interfaccia esposta alla rete LAN
    sudo ip addr add 1.0.0.2/30 dev enp0s8		#configura l'indirizzo IP dell'interfaccia esposta all'esterno
    sudo echo 1 > /proc/sys/net/ipv4/ip_forward	#abilita il forwarding dei pacchetti

### Step 4
Configurare le IP table (col forwarding delle porte) in Lubuntu 1 (scripts/lubuntu1/ConfIpTables.sh):

    sudo iptables -F	                                                        #effettua il flush della vecchia configurazione dell'IP table, se presente
    sudo iptables -P FORWARD DROP	                                                #permette tutto il traffico in uscita
    sudo iptables -A PREROUTING -t nat -d 1.0.0.2 -p tcp --dport 8080 -j DNAT --to-destination 10.0.0.100:80    #redireziona il traffico diretto alla porta 8080 verso la porta 80 di Tiny 1
    sudo iptables -A POSTROUTING -t nat -o enp0s8 -j MASQUERADE
    sudo iptables -A FORWARD -i enp0s8 -d 10.0.0.100 -p tcp --dport 80 -j ACCEPT    #permette il traffico TCP con sorgente=Internet, destinazione=LAN e porta=80
    sudo iptables -A FORWARD -i enp0s3 -j ACCEPT				        #permette il traffico (in forwarding) con sorgente=LAN
