#!/bin/bash

net del all
net commit
net add bridge bridge ports swp1,swp2,swp3
net commit