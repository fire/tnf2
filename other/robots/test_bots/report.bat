@echo off
@echo His Drive
@cat %1 |grep drive |grep B |wc
@echo His Scan
@cat %1 |grep scan |grep B |wc
@echo His Scan Miss
@cat %1 |grep scan |grep B |grep "returns 0" |wc
@echo His Cannon
@cat %1 |grep cann |grep B |wc
@echo His Cannon Miss
@cat %1 |grep cann |grep B |grep "returns 0" |wc
@echo My Drive
@cat %1 |grep drive |grep A |wc
@echo My Scan
@cat %1 |grep scan |grep A |wc
@echo My Scan Miss
@cat %1 |grep scan |grep A |grep "returns 0" |wc
@echo My Cannon
@cat %1 |grep cann |grep A |wc
@echo My Cannon Miss
@cat %1 |grep cann |grep A |grep "returns 0" |wc
