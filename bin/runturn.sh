#!/bin/bash
export tnf2root=~judda/prog/tnf2
export tnf2data=$tnf2root/data
export tnf2jar=$tnf2root/bin/nf.jar
export tnf2logs=$tnf2root/bin/logs
./clean_test.sh
./backup.sh
java -cp $tnf2jar aj.nf.Main ExecuteTurn -nomail -data$tnf2data $1 $2 > $tnf2logs/run.log 2> $tnf2logs/run_err.log
