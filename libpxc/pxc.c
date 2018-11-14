#include <stdio.h>
#include <stdlib.h>

#define MAPSIZE 128

typedef long long int addr;

void* image=NULL;
const void* maps=NULL; //Per map data (1st map, second map etc.)

int width, height, mapc;

void setSource(addr address, int w, int h, int mc) {
	image=(void*)address;
	maps=malloc(MAPSIZE*MAPSIZE*mc); //1 byte per pixel
	width=w, height=h, mapc=mc;
}

//May return 0
addr updateAndGetMap(short mapnum) {
	if(mapnum<0||mapnum>=mapc) return 0; //Out of bounds
	//TODO: Converter
	//return (long)map+mapx*MAPSIZE + mapy*MAPSIZE*width; - not good, we need to order the data per map
	return (long)maps + MAPSIZE*MAPSIZE*mapnum;
}

//Testing only
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
