CFLAGS=-g
PROGRAMS=combat 
ROBOTS=cylon tracker target

CC=g++ -g

all: $(PROGRAMS) $(ROBOTS)

clean:
	rm -f $(PROGRAMS) $(ROBOTS) *.o core

combat:	combat.o 
	$(CC) $(CFLAGS) -o $@ combat.o -lm

combat.o:	combat.c 
	$(CC) $(CFLAGS) -c combat.c

robots.o: robots.C robots.h
	g++ -c robots.C

cylon: cylon.o robots.o
	g++ -static -o $@ cylon.o robots.o 

cylon.o: cylon.c robots.h
	g++ -c cylon.c

tracker: tracker.o robots.o
	g++ -static -o $@ tracker.o robots.o 

tracker.o: tracker.c robots.h
	g++ -c tracker.c

target: target.o robots.o
	g++ -static -o $@ target.o robots.o 

target.o: target.c robots.h
	g++ -c target.c

