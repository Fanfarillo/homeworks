# XDP test

### Step 1
Installare il repository xdp-project e la relativa libreria LIBBPF in Lubuntu 1 (scripts/lubuntu1/InstallRepoAndLibs.sh):

    git clone https://github.com/xdp-project/xdp-tutorial
    cd xdp-tutorial
    git submodule update --init	#scarica LIBBPF nel repo, una libreria che permette di interagire col sottosistema BPF di Linux
    sudo apt update
    sudo apt install clang llvm libelf-dev libcap-dev gcc-multilib build-essential

### Step 2
Inserire all'interno della directory xdp-tutorial le cartelle nsd01-test e nsd02-bit-vector-linear-search-fw (gentilmente offerte dal prof).

### Step 3
Compilare il contenuto di nsd01-test e xdp-project tramite gli appositi Makefile (scripts/lubuntu1/CompileAll.sh):

    cd nsd01-test
    make
    cd ..
    sudo apt-get install git libpcap-dev
    make

### Step 4
Caricare il programma xdp-tutorial/nsd01-test/xdp_prog_kern.o nel kernel di Lubuntu 1 (scripts/lubuntu1/LoadProgOnKernel.sh):

    cd nsd01-test
    sudo ../basic-solutions/xdp_loader -d enp0s3 --filename xdp_prog_kern.o -S

### Step 5
Lanciare il file bash xdp-tutorial/nsd01-test/configure_test.sh (scripts/lubuntu1/ConfigureTest.sh):

    sudo apt install linux-tools-$(uname -r)
    sudo chmod ugo+x configure_test.sh
    sudo ./configure_test.sh

### Step 6
Mostrare l'output delle printk all'interno di xdp (scripts/lubuntu1/ShowPrintkOutput.sh):

    sudo cat /sys/kernel/debug/tracing/trace_pipe

### Step 7
Installare e lanciare Ipython3 in Lubuntu 2 (scripts/lubuntu2/RunIpython3.sh):

    sudo apt update
    sudo apt install ipython3 python3-scapy
    sudo ipython3

### Step 8
Digitare i seguenti comandi in Ipython3 per inviare un pacchetto (scripts/lubuntu2/SendPacket.py):

    from scapy.all import *
    p = Ether(dst="ff:ff:ff:ff:ff:ff")/IP(src="10.0.0.1")/TCP(sport=10000, dport=80)
    sendp(p, iface="enp0s3")
