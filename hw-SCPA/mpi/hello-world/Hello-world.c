#include <stdio.h>
#include "mpi.h"

int main(int argc, char **argv) {
    //variabili MPI
    int my_rank;
    int p;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &p);
    MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);
    printf("Hello from process %d of %d!\n", my_rank, p);

    MPI_Finalize();
    return 0;

}
