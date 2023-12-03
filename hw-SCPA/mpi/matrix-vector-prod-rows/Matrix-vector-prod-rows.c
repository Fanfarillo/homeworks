#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "mpi.h"

#define N 5 //rows of the matrix and num of components of result vector
#define M 7 //columns of the matrix and num of components of multiplicative vector
//#define DEBUG

int main(int argc, char **argv) {
    //informazioni che il processo 0 deve inviare agli altri processi
    int num_rows;
    int num_columns;
    //matrice e array completi
    int total_matrix[N*M];
    int total_mul_vector[M];
    int total_res_vector[N];
    //porzioni locali della matrice e dell'array risultato
    int *local_matrix;
    int *local_res_vector;
    //variabili MPI
    int my_rank;
    int p;
    //variabili di appoggio
    int min_rows;   //quoziente di num_rows/p
    int rest;       //resto di num_rows/p
    //indici ciclo for
    int i;
    int j;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &p);
    MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);
    printf("Hello from process %d of %d!\n", my_rank, p);

    //array che indicheranno rispettivamente dimensioni e displacement (i.e. offset base) delle porzioni di matrice e array risultato su cui dovranno lavorare i vari processi
    int sizes_matr[p];
    int displs_matr[p];
    int sizes_vect[p];
    int displs_vect[p];

    //si assume che solo il processo 0 inizialmente conosca i valori di N, M, la matrice e l'array moltiplicativo.
    if(my_rank == 0) {
        num_rows = N;
        num_columns = M;

        //inizializzazione randomica del vettore e della matrice
        for(j=0; j<num_columns; j++) {
            total_mul_vector[j] = rand() % 10;

            #ifdef DEBUG
            printf("[PROCESS %d] mul_vect[%d] = %d\n", my_rank, j, total_mul_vector[j]);
            #endif

            for(i=0; i<num_rows; i++) {
                total_matrix[i*num_columns+j] = rand() % 10;

                #ifdef DEBUG
                printf("[PROCESS %d] matr[%d][%d] = %d\n", my_rank, i, j, total_matrix[i*num_columns+j]);
                #endif

            }

        }

    }

    //invio dei messaggi a tutti gli altri processi da parte del processo 0
    MPI_Bcast(&num_rows, 1, MPI_INT, 0, MPI_COMM_WORLD);
    MPI_Bcast(&num_columns, 1, MPI_INT, 0, MPI_COMM_WORLD);

    //calcolo di min_rows e rest
    min_rows = num_rows / p;
    rest = num_rows % p;

    //inizializzazione di sizes e displs sia per il vettore risultato che per la matrice
    for(i=0; i<p; i++) {
        if(i < rest) {
            sizes_vect[i] = min_rows + 1;
            displs_vect[i] = i*(min_rows+1);

        } else {
            sizes_vect[i] = min_rows;
            displs_vect[i] = rest*(min_rows+1) + (i-rest)*min_rows;
                
        }

        sizes_matr[i] = sizes_vect[i]*num_columns;
        displs_matr[i] = displs_vect[i]*num_columns;

    }

    //allocazione della porzione locale della matrice e della porzione locale del vettore risultato
    local_matrix = (int *)malloc(sizes_matr[my_rank]*sizeof(int));
    if(!local_matrix) {
        printf("Unable to allocate local_matrix.\n");
        MPI_Abort(MPI_COMM_WORLD, EXIT_FAILURE);

    }

    local_res_vector = (int *)malloc(sizes_vect[my_rank]*sizeof(int));
    if(!local_res_vector) {
        printf("Unable to allocate local_res_vector.\n");
        MPI_Abort(MPI_COMM_WORLD, EXIT_FAILURE);

    }

    //azzeramento esplicito di local_matrix e local_res_vector
    memset(local_matrix, 0, sizes_matr[my_rank]*sizeof(int));
    memset(local_res_vector, 0, sizes_vect[my_rank]*sizeof(int));

    //split della matrice tra tutti i processi
    MPI_Scatterv(total_matrix, sizes_matr, displs_matr, MPI_INT, local_matrix, sizes_matr[my_rank], MPI_INT, 0, MPI_COMM_WORLD);
    //invio del vettore moltiplicativo per intero
    MPI_Bcast(total_mul_vector, num_columns, MPI_INT, 0, MPI_COMM_WORLD);

    //calcolo dei risultati parziali del prodotto tra la matrice e il vettore moltiplicativo
    for(i=0; i<sizes_vect[my_rank]; i++) {
        for(j=0; j<num_columns; j++) {
            local_res_vector[i] += local_matrix[i*num_columns+j]*total_mul_vector[j];

        }
        
    }
    //si mettono insieme i risultati parziali per ottenere il risultato finale del prodotto matrice*vettore
    MPI_Gatherv(local_res_vector, sizes_vect[my_rank], MPI_INT, total_res_vector, sizes_vect, displs_vect, MPI_INT, 0, MPI_COMM_WORLD);

    //stampa del risultato finale
    if(my_rank == 0) {
        for(i=0; i<num_rows; i++) {
            printf("Res_vect[%d] = %d\n", i, total_res_vector[i]);
        
        }

    }

    MPI_Finalize();
    return 0;

}
