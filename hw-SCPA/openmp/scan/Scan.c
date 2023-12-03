#include <math.h>
#include <omp.h>
#include <stdio.h>
#include <stdlib.h>

#define VERY_LARGE_SIZE 15
#define CHUNK 4

int main(int argc, char **argv) {
    //array x, y
    double x[VERY_LARGE_SIZE];  //input array
    double y[VERY_LARGE_SIZE];  //output array
    //variabili di appoggio
    double *tmp;    //array di appoggio; tiene traccia della somma totale degli elementi dell'array x trattati da uno stesso thread (i.e. elementi dell'array appartenenti a uno stesso chunk).
    int num_chunks;
    //openMP variabiles
    int tid;
    //loop indexes
    int i;
    int j;

    //inizializzazione randomica dell'array di input x
    for(i=0; i<VERY_LARGE_SIZE; i++) {
        x[i] = rand() % 10;
    }

    //inizio dell'esecuzione parallela del ciclo for che effettua la somma
    #pragma omp parallel shared(x,y,tmp,num_chunks) private(i,j,tid)
    {
        tid = omp_get_thread_num();

        #pragma omp single
        {
            num_chunks = ceil(1.0*VERY_LARGE_SIZE/CHUNK);

            //allocazione dell'array di appoggio
            tmp = (double *)malloc(num_chunks*sizeof(double));
            if(!tmp) {
                printf("[ERROR] Unable to allocate new memory.\n");
                exit(-1);

            }

        }

        #pragma omp for schedule(static, CHUNK)
            for(i=0; i<VERY_LARGE_SIZE; i++) {

                if(i%CHUNK == 0) {
                    y[i] = x[i];

                } else if(i%CHUNK == CHUNK-1) {
                    y[i] = x[i]+y[i-1];
                    tmp[i/CHUNK] = y[i];

                } else {
                    y[i] = x[i]+y[i-1];

                }

            }

        #pragma omp for schedule(static, CHUNK)
            for(i=0; i<VERY_LARGE_SIZE; i++) {
                
                for(j=0; j<i/CHUNK; j++)
                    y[i] += tmp[j];

                printf("THREAD %d: x[%d] = %lf\n", tid, i, x[i]);
                printf("THREAD %d: y[%d] = %lf\n", tid, i, y[i]);
            
            }
    
    }
    //fine dell'esecuzione parallela

    return 0;

}
