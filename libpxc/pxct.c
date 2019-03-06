#include <stdio.h>
#include <stdlib.h>

/* https://www.cprogramming.com/tutorial/shared-libraries-linux-gcc.html
gcc -Wall -c -fpic pxc.c
gcc -Wall -shared pxc.o -o pxc.so
gcc -Wall pxct.c -L. -lpxc -Wl,-rpath=.
*/

int convert(int px[], int *out[]);

typedef long long int addr;
void setSource(addr address, short w, short h, short mcx, short mcy);
void* updateAndGetMap(int x, int y, int w, int h, int** out_changed);

int main() {
	printf("Setting source...");
	void* p = malloc(640*480*4);
	setSource((addr)p, 640, 480, 5, 4);
	printf("Updating map...");
	void* x=updateAndGetMap(0, 0, 640, 480, NULL);
	free(p);
	return 0;
}

