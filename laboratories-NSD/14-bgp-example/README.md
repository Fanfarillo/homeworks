# BGP example

### Step 1
Fare il setup del router R1 (scripts/r1/Setup.sh):

    configure terminal

    interface Loopback 0
    ip address 1.0.0.1 255.255.0.0
    exit
    interface Loopback 1
    ip address 1.1.0.1 255.255.0.0
    exit

    interface GigabitEthernet 1/0
    ip address 10.0.12.1 255.255.255.252
    no shutdown
    exit

    ip route 1.0.0.0 255.0.0.0 Null0	#fake route

    router bgp 100
    network 1.0.0.0
    neighbor 10.0.12.2 remote-as 200
    end

### Step 2
Fare il setup del router R2 (scripts/r2/Setup.sh):

    configure terminal

    interface Loopback 0
    ip address 2.2.0.1 255.255.0.0
    exit
    interface Loopback 1
    ip address 2.255.0.2 255.255.255.255
    exit

    interface GigabitEthernet 1/0
    ip address 10.0.12.2 255.255.255.252
    no shutdown
    exit
    interface GigabitEthernet 2/0
    ip address 10.0.23.1 255.255.255.252
    no shutdown
    exit
    interface GigabitEthernet 3/0
    ip address 10.0.24.1 255.255.255.252
    no shutdown
    exit

    router ospf 1
    router-id 2.255.0.2
    network 2.255.0.2 0.0.0.0 area 0
    network 2.2.0.0 0.0.255.255 area 0
    network 10.0.23.0 0.0.0.3 area 0
    network 10.0.34.0 0.0.0.3 area 0
    exit

    ip route 2.0.0.0 255.0.0.0 Null0	#fake route

    router bgp 200
    network 2.0.0.0
    neighbor 10.0.12.1 remote-as 100
    neighbor 2.255.0.3 remote-as 200
    neighbor 2.255.0.3 update-source lo1
    neighbor 2.255.0.4 remote-as 200
    neighbor 2.255.0.4 update-source lo1
    end

### Step 3
Fare il setup del router R3 (scripts/r3/Setup.sh):

    configure terminal

    interface Loopback 0
    ip address 2.3.0.1 255.255.0.0
    exit
    interface Loopback 1
    ip address 2.255.0.3 255.255.255.255
    exit

    interface GigabitEthernet 1/0
    ip address 10.0.23.2 255.255.255.252
    no shutdown
    exit
    interface GigabitEthernet 2/0
    ip address 10.0.34.1 255.255.255.252
    no shutdown
    exit

    router ospf 1
    router-id 2.255.0.3
    network 0.0.0.0 0.0.0.0 area 0	#esporta tutti i route in OSPF
    exit

    ip route 2.0.0.0 255.0.0.0 Null0	#fake route

    router bgp 200
    network 2.0.0.0
    neighbor 2.255.0.2 remote-as 200
    neighbor 2.255.0.2 update-source lo1
    neighbor 2.255.0.4 remote-as 200
    neighbor 2.255.0.4 update-source lo1
    end

### Step 4
Fare il setup del router R4 (scripts/r4/Setup.sh):

    configure terminal

    interface Loopback 0
    ip address 2.4.0.1 255.255.0.0
    exit
    interface Loopback 1
    ip address 2.255.0.4 255.255.255.255
    exit

    interface GigabitEthernet 1/0
    ip address 10.0.34.2 255.255.255.252
    no shutdown
    exit
    interface GigabitEthernet 2/0
    ip address 10.0.45.1 255.255.255.252
    no shutdown
    exit
    interface GigabitEthernet 3/0
    ip address 10.0.24.2 255.255.255.252
    no shutdown
    exit

    router ospf 1
    router-id 2.255.0.4
    network 0.0.0.0 0.0.0.0 area 0	#esporta tutti i route in OSPF
    exit

    ip route 2.0.0.0 255.0.0.0 Null0	#fake route

    router bgp 200
    network 2.0.0.0
    neighbor 2.255.0.2 remote-as 200
    neighbor 2.255.0.2 update-source lo1
    neighbor 2.255.0.3 remote-as 200
    neighbor 2.255.0.3 update-source lo1
    neighbor 10.0.45.2 remote-as 300
    end

### Step 5
Fare il setup del router R5 (scripts/r5/Setup.sh):

    configure terminal

    interface Loopback 0
    ip address 3.0.0.1 255.255.0.0
    exit
    interface Loopback 1
    ip address 3.1.0.1 255.255.0.0
    exit

    interface GigabitEthernet 1/0
    ip address 10.0.45.2 255.255.255.252
    no shutdown
    exit

    ip route 3.0.0.0 255.0.0.0 Null0	#fake route

    router bgp 300
    network 3.0.0.0
    neighbor 10.0.45.1 remote-as 200
    end
