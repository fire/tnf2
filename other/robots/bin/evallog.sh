echo EVAL COMBAT TRACE $1
cp $1 log2
grep "A " log2 >A.log
grep "B " log2 >B.log

tail -n2 log2|head -n1>resultA.log
echo damage >>resultA.log
grep "A " A.log| grep -v "A  "| grep -v "Explo"|tail -n1|awk {'print $10'}>>resultA.log
grep Time log2|tail -n1>>resultA.log
grep scan log2 |sort -nk6 |tail -n1>>resultA.log
grep cannon A.log >canA.log
grep drive A.log >drivA.log
grep scan A.log >scanA.log
grep scan A.log |grep "returns 0">scanmA.log
grep cannon A.log |grep "returns 0" >canmA.log
wc -l canA.log drivA.log scanA.log >>resultA.log
wc -l canmA.log scanmA.log >>resultA.log
echo " ">>resultA.log
echo " ">>resultA.log

tail -n1 log2|head -n1>resultB.log
echo damage >>resultB.log
grep "B " B.log| grep -v "B  "|grep -v "Explo"|tail -n1|awk {'print $10'}>>resultB.log
grep Time log2 |tail -n1>>resultB.log
grep scan log2 |sort -nk6 |tail -n1>>resultB.log
grep cannon B.log >canB.log
grep cannon B.log |grep "returns 0" >canmB.log
grep drive B.log >drivB.log
grep scan B.log >scanB.log
grep scan B.log |grep "returns 0">scanmB.log
wc -l canB.log drivB.log scanB.log >>resultB.log
wc -l canmB.log scanmB.log >>resultB.log
echo " ">>resultB.log
echo " ">>resultB.log

grep -v "B " log2 >A.log
grep -v "A " log2 >B.log

cat resultA.log resultB.log
