#include <stdio.h>

typedef long long int lli;

void* image=NULL;

void setSource(lli address) {
	image=(void*)address;
}

int convert(int px[], int *out[]) {
	printf("convert...\n");
	printf("px0: %d\n", px[0]);
	*out[0]=19;
	return 0;
}

void __attribute__ ((constructor)) initLibrary(void) {
 //
 // Function that is called when the library is loaded
 //
    printf("Library is initialized\n"); 
}
void __attribute__ ((destructor)) cleanUpLibrary(void) {
 //
 // Function that is called when the library is »closed«.
 //
    printf("Library is exited\n"); 
}
