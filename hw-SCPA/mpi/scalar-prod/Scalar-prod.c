#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "mpi.h"

//#define VERY_LARGE_SIZE 300000
#define VERY_LARGE_SIZE 14
//#define DEBUG

int main(int argc, char **argv) {
    //informazioni che il processo 0 deve inviare agli altri processi
    int array_size;
    //array x, y completi
    int x[VERY_LARGE_SIZE];
    int y[VERY_LARGE_SIZE];
    //porzioni locali degli array x, y
    int *local_x;
    int *local_y;
    //risultato parziale e finale del prodotto scalare
    int partial_result;
    int final_result;
    //variabili MPI
    int my_rank;
    int p;
    //variabili di appoggio
    int min_size;   //quoziente di array_size/p
    int rest;       //resto di array_size/p
    //indice ciclo for
    int i;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &p);
    MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);
    printf("Hello from process %d of %d!\n", my_rank, p);

    //array che indicheranno rispettivamente dimensioni e displacement (i.e. offset base) delle porzioni di array su cui dovranno lavorare i vari processi
    int sizes[p];
    int displs[p];

    //si assume che solo il processo 0 inizialmente conosca il valore di VERY_LARGE_SIZE e gli array x, y
    if(my_rank == 0) {
        array_size = VERY_LARGE_SIZE;

        //inizializzazione randomica dei due vettori di input (x, y)
        for(i=0; i<array_size; i++) {
            x[i] = rand() % 10;
            y[i] = rand() % 10;

            #ifdef DEBUG
            printf("[PROCESS %d] x[%d] = %d\n", my_rank, i, x[i]);
            printf("[PROCESS %d] y[%d] = %d\n", my_rank, i, y[i]);
            #endif

        }

    }

    //invio del messaggio a tutti gli altri processi da parte del processo 0
    MPI_Bcast(&array_size, 1, MPI_INT, 0, MPI_COMM_WORLD);

    //calcolo di min_size e rest
    min_size = array_size / p;
    rest = array_size % p;

    //allocazione delle porzioni locali degli array x, y
    local_x = (int *)malloc((min_size+1)*sizeof(int));
    if(!local_x) {
        printf("Unable to allocate local_x.\n");
        MPI_Abort(MPI_COMM_WORLD, EXIT_FAILURE);

    }

    local_y = (int *)malloc((min_size+1)*sizeof(int));
    if(!local_y) {
        printf("Unable to allocate local_y.\n");
        MPI_Abort(MPI_COMM_WORLD, EXIT_FAILURE);

    }

    //azzeramento esplicito di local_x, local_y
    memset(local_x, 0, (min_size+1)*sizeof(int));
    memset(local_y, 0, (min_size+1)*sizeof(int));

    //inizializzazione di sizes e displs
    for(i=0; i<p; i++) {
        if(i < rest) {
            sizes[i] = min_size + 1;
            displs[i] = i*(min_size+1);

        } else {
            sizes[i] = min_size;
            displs[i] = rest*(min_size+1) + (i-rest)*min_size;
                
        }

    }

    //split degli array x, y tra tutti i processi
    MPI_Scatterv(x, sizes, displs, MPI_INT, local_x, min_size+1, MPI_INT, 0, MPI_COMM_WORLD);
    MPI_Scatterv(y, sizes, displs, MPI_INT, local_y, min_size+1, MPI_INT, 0, MPI_COMM_WORLD);

    //calcolo dei risultati parziali del prodotto scalare tra x e y
    partial_result = 0;
    for(i=0; i<min_size+1; i++) {
        partial_result += local_x[i]*local_y[i];
        
    }
    //si mettono insieme i risultati parziali per ottenere il risultato finale del prodotto scalare
    MPI_Reduce(&partial_result, &final_result, 1, MPI_INT, MPI_SUM, 0, MPI_COMM_WORLD);

    //stampa del risultato finale
    if(my_rank == 0) {
        printf("Scalar product result = %d\n", final_result);
    }

    MPI_Finalize();
    return 0;

}
