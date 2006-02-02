CFLAGS=-g 
CC=g++

clean:
	rm -f *.o core

.c: $@.c robots.o
	$(CC) $(FLAGS) -o ../$@ $@.c robots.o 

