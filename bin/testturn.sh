#!/bin/bash
export tnf2root=~judda/prog/tnf2
export tnf2data=$tnf2root/data/test
cd $tnf2root/data
rm -rf test
mkdir test
cp *.gml *.txt test
mkdir test/orders
cp orders/* test/orders/
mkdir test/planets
cp planets/* test/planets/
mkdir test/reports
cd ../bin
java -cp $tnf2root/bin/nf.jar aj.nf.Main ExecuteTurn -nomail -data$tnf2data $1 $2 >$tnf2root/bin/logs/testrun.log
