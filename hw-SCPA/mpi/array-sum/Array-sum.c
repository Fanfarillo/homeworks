#include <stdio.h>
#include <stdlib.h>
#include "mpi.h"

//#define VERY_LARGE_SIZE 300000
#define VERY_LARGE_SIZE 15
#define NUM_STRUCT_FIELDS 3
//#define DEBUG

struct PartialResult {
    int local_base_offset;
    int local_size;
    int *local_z;
};

int main(int argc, char **argv) {
    //informazioni che il processo 0 deve inviare agli altri processi
    int array_size;
    //variabili che aiutano nel definire le informazioni che i vari processi devono inviare al processo 0
    int blockcounts[NUM_STRUCT_FIELDS];     //array che indica la molteplicità di ciascun campo della struct (1 se è una variabile semplice, N se è un array)
    MPI_Datatype types[NUM_STRUCT_FIELDS];  //array che indica il tipo di dato di ciascun campo della struct
    MPI_Aint offsets[NUM_STRUCT_FIELDS];    //array che indica lo spiazzamento di ciascun campo della struct
    MPI_Datatype struct_type;               //tipo di dato MPI (definito da me) che ospiterà la struct PartialResult
    struct PartialResult *struct_buf;
    //porzioni locali degli array x, y
    int *local_x;
    int *local_y;
    //offset iniziale e size con cui deve lavorare ciascun processo
    int my_base_offset;
    int my_size;
    //variabili utili per calcolare my_base_offset e my_size
    int min_size;   //quoziente di array_size/p
    int rest;       //resto di array_size/p
    //variabili MPI
    int my_rank;
    int p;
    int source;
    int dest;
    int tag;
    MPI_Status status;  //status della ricezione del messaggio
    //indice ciclo for
    int i;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &p);
    MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);
    
    printf("Hello from process %d of %d!\n", my_rank, p);
    tag = 0;

    //allocazione di struct_buf
    struct_buf = (struct PartialResult *)malloc(sizeof(struct PartialResult));
    if(!struct_buf) {
        printf("Unable to allocate struct_buf.\n");
        MPI_Abort(MPI_COMM_WORLD, EXIT_FAILURE);

    } 

    //definizione del tipo di dato MPI complesso (struct)
    blockcounts[0] = 1;
    blockcounts[1] = 1;
    blockcounts[2] = my_size;
    
    for(i=0; i<NUM_STRUCT_FIELDS; i++) {
        types[i] = MPI_INT;
    }

    offsets[0] = offsetof(struct PartialResult, local_base_offset);
    offsets[1] = offsetof(struct PartialResult, local_size);
    offsets[2] = offsetof(struct PartialResult, local_z);

    MPI_Type_create_struct(NUM_STRUCT_FIELDS, blockcounts, offsets, types, &struct_type);
    MPI_Type_commit(&struct_type);

    //invio del messaggio a tutti gli altri processi da parte del processo 0
    if(my_rank == 0) {
        array_size = VERY_LARGE_SIZE;

        for(dest=1; dest<p; dest++) {
            MPI_Send(&array_size, 1, MPI_INT, dest, tag, MPI_COMM_WORLD);

        }

    } else {    //ricezione del messaggio proveniente dal processo 0
        source = 0;
        MPI_Recv(&array_size, 1, MPI_INT, source, tag, MPI_COMM_WORLD, &status);

    }

    //calcolo di min_size e rest
    min_size = array_size / p;
    rest = array_size % p;

    //calcolo di my_base_offset e my_size
    if(my_rank < rest) {
        my_base_offset = my_rank*(min_size+1);
        my_size = min_size + 1;

    } else {
        my_base_offset = rest*(min_size+1) + (my_rank-rest)*min_size;
        my_size = min_size;

    }

    //allocazione delle porzioni locali dei vettori da parte di tutti i processi
    local_x = (int *)malloc(my_size*sizeof(int));
    if(!local_x) {
        printf("Unable to allocate local_x.\n");
        MPI_Abort(MPI_COMM_WORLD, EXIT_FAILURE);

    }

    local_y = (int *)malloc(my_size*sizeof(int));
    if(!local_y) {
        printf("Unable to allocate local_y.\n");
        MPI_Abort(MPI_COMM_WORLD, EXIT_FAILURE);

    }

    struct_buf->local_z = (int *)malloc(my_size*sizeof(int));
    if(!struct_buf->local_z) {
        printf("Unable to allocate local_z.\n");
        MPI_Abort(MPI_COMM_WORLD, EXIT_FAILURE);

    }

    //inizializzazione randomica delle porzioni dei due vettori di input (x, y)
    for(i=0; i<my_size; i++) {
        local_x[i] = rand() % 10;
        local_y[i] = rand() % 10;

        #ifdef DEBUG
        printf("[PROCESS %d] local_x[%d] = %d\n", my_rank, i, local_x[i]);
        printf("[PROCESS %d] local_y[%d] = %d\n", my_rank, i, local_y[i]);
        #endif

    }

    //a questo punto è possibile procedere con il calcolo
    for(i=0; i<my_size; i++) {
        struct_buf->local_z[i] = local_x[i] + local_y[i];

    }

    //i processi con my_rank > 0 inviano i propri risultati parziali al processo con my_rank = 0
    if(my_rank > 0) {
        struct_buf->local_base_offset = my_base_offset;
        struct_buf->local_size = my_size;

        dest = 0;
        MPI_Send(struct_buf, 1, struct_type, dest, tag, MPI_COMM_WORLD);

    } else {
        //stampa di tutti i risultati parziali
        for(i=0; i<my_size; i++) {
            printf("z[%d] = %d\n", i+my_base_offset, struct_buf->local_z[i]);
        }

        //ricezione di tutti i messaggi e relative stampe
        for(source=1; source<p; source++) {
            MPI_Recv(struct_buf, 1, struct_type, source, tag, MPI_COMM_WORLD, &status);

            for(i=0; i<struct_buf->local_size; i++) {
                printf("z[%d] = %d\n", i+struct_buf->local_base_offset, struct_buf->local_z[i]);
            }
            
        }

    }

    MPI_Finalize();
    return 0;

}
