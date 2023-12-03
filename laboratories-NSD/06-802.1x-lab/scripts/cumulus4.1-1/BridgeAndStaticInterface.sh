#!/bin/bash

net del all
net commit
net add bridge bridge ports swp1,swp2,swp3
net add bridge vids 30
net add interface swp3 bridge access 30
net commit