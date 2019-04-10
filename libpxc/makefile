CC=gcc
# CFLAGS=-I.

pxct: pxct.c pxc
	$(CC) -Wall pxct.c -Lout -lpxc -Wl,-rpath=out -o out/pxct

pxc: pxc.c
	$(CC) -Wall -O4 -c -fpic pxc.c -o obj/pxc.o
	$(CC) -Wall -shared obj/pxc.o -o out/libpxc.so
	cp out/libpxc.so ../

