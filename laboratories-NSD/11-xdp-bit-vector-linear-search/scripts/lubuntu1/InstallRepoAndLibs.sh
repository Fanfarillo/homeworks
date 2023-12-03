#!/bin/bash

git clone https://github.com/xdp-project/xdp-tutorial
cd xdp-tutorial
git submodule update --init		#scarica LIBBPF nel repo, una libreria che permette di interagire col sottosistema BPF di Linux
apt update
apt install clang llvm libelf-dev libcap-dev gcc-multilib build-essential
