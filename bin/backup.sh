#!/bin/bash
export tnf2root=~judda/prog/tnf2
cd $tnf2root
touch backup1.tar.gz backup2.tar.gz
cp backup2.tar.gz backup3.tar.gz
cp backup1.tar.gz backup2.tar.gz
tar -czf backup1.tar.gz data
