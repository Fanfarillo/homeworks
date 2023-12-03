# BGP MPLS VPN

### Step 1
Fare il setup dei router (vedere il laboratory 'BGP example').

### Step 2
Impostare il provide edge PE-1:

    configure terminal

    ip vrf vpnA	#crea una VPN routing and forwarding table
    rd 100:0	#rd = route distinguisher (è un identificatore)
    route-target import 100:1
    route-target export 100:2
    exit
    ip vrf vpnB
    rd 200:0
    route-target import 200:1
    route-target export 200:1
    exit

    int g1/0
    ip vrf forwarding vpnA	#specifica il customer che si trova l'interfaccia g1/0 (i.e. vpnA)
    ip addr 10.1.1.1 255.255.255.252
    exit
    int g2/0
    ip vrf forwarding vpnB
    ip addr 10.0.11.1 255.255.255.252
    exit

    router bgp 100
    neighbor 2.2.2.2 remote-as 100
    neighbor 2.2.2.2 update-source lo0
    neighbor 2.2.2.2 next-hop-self
    neighbor 3.3.3.3 remote-as 100
    neighbor 3.3.3.3 update-source lo0
    neighbor 3.3.3.3 next-hop-self

    address-family vpnv4	#è la sottoconfigurazione di BGP per gli indirizzi VPNv4
    neighbor 2.2.2.2 activate
    neighbor 2.2.2.2 send-community extended	#estende l'attributo 'community' in modo da avere il route target dentro BGP MPLS
    neighbor 2.2.2.2 next-hop-self
    neighbor 3.3.3.3 activate
    neighbor 3.3.3.3 send-community extended
    neighbor 3.3.3.3 next-hop-self
    exit

    address-family ipv4 vrf vpnA
    network 192.168.0.0
    exit
    address-family ipv4 vrf vpnB
    network 192.168.0.0
    exit
    exit

    ip route vrf vpnA 192.168.0.0 255.255.255.0 10.1.1.2	#per raggiungere CE-A1 si usa 10.1.1.2 come next-hop
    ip route vrf vpnB 192.168.0.0 255.255.255.0 10.0.11.2
    end

### Step 3
Impostare il provide edge PE-2:

    configure terminal

    ip vrf vpnA
    rd 100:0
    route-target import 100:1
    route-target export 100:2
    exit
    ip vrf vpnB
    rd 200:0
    route-target import 200:1
    route-target export 200:1
    exit

    int g1/0
    ip vrf forwarding vpnA
    ip addr 10.2.2.1 255.255.255.252
    exit
    int g2/0
    ip vrf forwarding vpnB
    ip addr 10.0.22.1 255.255.255.252
    exit

    router bgp 100
    neighbor 1.1.1.1 remote-as 100
    neighbor 1.1.1.1 update-source lo0
    neighbor 1.1.1.1 next-hop-self
    neighbor 3.3.3.3 remote-as 100
    neighbor 3.3.3.3 update-source lo0
    neighbor 3.3.3.3 next-hop-self

    address-family vpnv4
    neighbor 1.1.1.1 activate
    neighbor 1.1.1.1 send-community extended
    neighbor 1.1.1.1 next-hop-self
    neighbor 3.3.3.3 activate
    neighbor 3.3.3.3 send-community extended
    neighbor 3.3.3.3 next-hop-self
    exit

    address-family ipv4 vrf vpnA
    network 192.168.1.0
    exit
    address-family ipv4 vrf vpnB
    network 192.168.1.0
    exit
    exit

    ip route vrf vpnA 192.168.1.0 255.255.255.0 10.2.2.2
    ip route vrf vpnB 192.168.1.0 255.255.255.0 10.0.22.2
    end

### Step 4
Impostare il provide edge PE-3:

    configure terminal

    ip vrf vpnA
    rd 100:0
    route-target import 100:2
    route-target export 100:1
    exit

    int g1/0
    ip vrf forwarding vpnA
    ip addr 10.3.3.1 255.255.255.252
    exit

    router bgp 100
    neighbor 1.1.1.1 remote-as 100
    neighbor 1.1.1.1 update-source lo0
    neighbor 1.1.1.1 next-hop-self
    neighbor 2.2.2.2 remote-as 100
    neighbor 2.2.2.2 update-source lo0
    neighbor 2.2.2.2 next-hop-self

    address-family vpnv4
    neighbor 1.1.1.1 activate
    neighbor 1.1.1.1 send-community extended
    neighbor 1.1.1.1 next-hop-self
    neighbor 2.2.2.2 activate
    neighbor 2.2.2.2 send-community extended
    neighbor 2.2.2.2 next-hop-self
    exit

    address-family ipv4 vrf vpnA
    network 192.168.2.0
    exit
    address-family ipv4 vrf vpnA
    network 0.0.0.0 mask 0.0.0.0	#esporta la route di default per la porzione di rete comprendente CE-A3
    exit
    exit

    ip route vrf vpnA 192.168.2.0 255.255.255.0 10.3.3.2
    ip route vrf vpnA 0.0.0.0 0.0.0.0 10.3.3.2
    end
