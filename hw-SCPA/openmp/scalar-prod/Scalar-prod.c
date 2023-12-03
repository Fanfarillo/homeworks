#include <omp.h>
#include <stdio.h>
#include <stdlib.h>

#define VERY_LARGE_SIZE 15
#define CHUNK 4

int main(int argc, char **argv) {
    //array x, y
    double x[VERY_LARGE_SIZE];
    double y[VERY_LARGE_SIZE];
    //final result
    double res;
    //openMP variabiles
    int tid;
    //loop index
    int i;

    //inizializzazione randomica degli array di input x, y
    for(i=0; i<VERY_LARGE_SIZE; i++) {
        x[i] = rand() % 10;
        y[i] = rand() % 10;

    }

    //inizio dell'esecuzione parallela del ciclo for che effettua la somma
    #pragma omp parallel shared(x,y) private(i,tid)
    {
        tid = omp_get_thread_num();

        #pragma omp for schedule(static, CHUNK) reduction(+: res)
            for(i=0; i<VERY_LARGE_SIZE; i++) {
                res += x[i]*y[i];

                printf("THREAD %d: x[%d] = %lf\n", tid, i, x[i]);
                printf("THREAD %d: y[%d] = %lf\n", tid, i, y[i]);

            }
    
    }
    //fine dell'esecuzione parallela
    printf("Res = %lf\n", res);

    return 0;

}
