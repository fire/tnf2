CFLAGS=-g 

PB= pbv1 pbv2 pbv3 pbv3a pbv4 pbv4a pbv5 pbv5a pbv5b pbv6 pbv6a pbv6b pbv7 pbv8
ROBOTS= $(PB)
CC=g++

all: $(ROBOTS)

clean:
	rm -f $(PROGRAMS) $(ROBOTS) *.o core

.c: $@.c robots.o
	$(CC) $(FLAGS) -o $@.exe $@.c robots.o 

