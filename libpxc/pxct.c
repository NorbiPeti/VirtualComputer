#include <stdio.h>

/* https://www.cprogramming.com/tutorial/shared-libraries-linux-gcc.html
gcc -Wall -c -fpic pxc.c
gcc -Wall -shared pxc.o -o pxc.so
gcc -Wall pxct.c -L. -lpxc -Wl,-rpath=.
*/

int convert(int px[], int *out[]);

int main() {
	convert(NULL, NULL);
	return 0;
}

