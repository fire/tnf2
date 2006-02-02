CFLAGS=-g 
PROGRAMS=combat count
SAMPLE= bigtarget6 bigtarget8 bigtarget84 breve cylon minim quaver quaver2 shogun target tommy tracker wilma wobble wobble2 zigger ziggy

PB= pbv1 pbv2 pbv3 pbv3a pbv4 pbv4a pbv5 pbv5a pbv5b pbv6 pbv6a pbv6b pbv7 pbv8
ROBOTS= $(PB) $(SAMPLE)
CC=g++

all: $(PROGRAMS) $(ROBOTS)
bots: $(ROBOTS)

clean:
	rm -f $(PROGRAMS) $(ROBOTS) *.o core

combat:	combat.o 
	$(CC) $(CFLAGS) -o $@.exe combat.o -lm

combat.o:	combat.c 
	$(CC) $(CFLAGS) -c combat.c

robots.o: robots.C robots.h
	$(CC) -c robots.C

count: count.c
	$(CC) -o $@.exe count.c

.c: $@.c robots.o
	$(CC) $(FLAGS) -o $@.exe $@.c robots.o 

