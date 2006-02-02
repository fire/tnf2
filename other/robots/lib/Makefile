CFLAGS=-g 
PROGRAMS=combat count
CC=g++

all: $(PROGRAMS)

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

