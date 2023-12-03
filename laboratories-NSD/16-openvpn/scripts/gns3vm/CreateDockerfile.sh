#!/bin/bash

mkdir openvpn-image
cd openvpn-image
touch Dockerfile
echo "FROM openvpn:latest\nRUN mkdir /rw\nVOLUME /rw" > Dockerfile