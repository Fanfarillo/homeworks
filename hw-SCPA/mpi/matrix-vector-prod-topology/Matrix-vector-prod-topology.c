#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "mpi.h"

#define N 5 //rows of the matrix and num of components of result vector
#define M 7 //columns of the matrix and num of components of multiplicative vector
#define DIMS 2  //number of dimensions of a matrix
#define NO 0
#define YES 1
//#define DEBUG

int main(int argc, char **argv) {
    //informazioni che il processo 0 deve inviare agli altri processi
    int num_rows;
    int num_columns;
    //matrice e array completi
    int total_matrix[N][M];
    int total_mul_vector[M];
    int total_res_vector[N];
    //porzioni locali della matrice e degli array
    int *local_matrix;
    int *local_mul_vector;
    int *local_res_vector;
    //porzione dell'array risultato della stessa dimensione di local_res_vector; ospiterà il valore definitivo di quella porzione dell'array risultato.
    int *intermediate_res_vector;
    //variabili MPI
    int my_rank;
    int my_cart_rank;           //ho osservato empiricamente che my_rank e my_cart_rank corrispondono.
    int p;
    MPI_Comm comm_cart;         //nuovo comunicatore relativo alla griglia di processi da associare alla matrice
    //variabili utili per suddividere il lavoro dei processi
    int n;                      //numero di righe per la sottomatrice da assegnare a ciascun processo
    int m;                      //numero di colonne per la sottomatrice da assegnare a ciascun processo
    int proc_dims[DIMS];        //array che indica il numero di processi che va a finire in ciascuna dimensione della matrice
    int proc_dims_hat[DIMS];    //array che indica il numero di righe e colonne che, nella mesh di processi, sono composte da una riga/colonmna di matrice in più (le entry sono risp. N%n, M%m)
    //variabili di appoggio
    int periods[DIMS];          //array che indica se ognuna dimensione della matrice deve essere periodica (i.e. circolare) o meno
    int mesh_row_index;         //variabile che tiene traccia dell'indice riga all'interno della mesh di processi
    int mesh_col_index;         //variabile che tiene traccia dell'indice colonna all'interno della mesh di processi
    int my_mesh_row;            //il PROPRIO indice riga all'interno della mesh di processo
    int my_mesh_col;            //il PROPRIO indice colonna all'interno della mesh di processo
    //indici ciclo for
    int i;
    int j;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &p);
    MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);
    printf("Hello from process %d of %d!\n", my_rank, p);

    //inizializzazione di periods a soli false + inizializzazione di proc_dims a soli 0
    for(i=0; i<DIMS; i++) {
        periods[i] = NO;
        proc_dims[i] = 0;   //proc_dims[0] = #righe della mesh di processi; proc_dims[1] = #colonne della mesh di processi

    }

    //definizione (e successiva creazione) della topologia più quadrata possibile per i p processi
    MPI_Dims_create(p, DIMS, proc_dims);
    MPI_Cart_create(MPI_COMM_WORLD, DIMS, proc_dims, periods, NO, &comm_cart);
    MPI_Comm_rank(comm_cart, &my_cart_rank);    //ottenimento del rank basato sul comunicatore comm_cart

    //definizione dei comunicatori relativi alle singole righe della mesh + del comunicatore dei rappresentanti delle righe (conterrà i processi della prima colonna della mesh)
    MPI_Comm row_comms[proc_dims[0]];
    MPI_Comm col_comm;
    //definizione dei gruppi di processi (quello relativo al comunicatore MPI_COMM_WORLD, quelli relativi alle singole righe della mesh e quello relativo alla prima colonna della mesh)
    MPI_Group total_group;              //gruppo associato al comunicatore MPI_COMM_WORLD
    MPI_Group row_groups[proc_dims[0]]; //gruppi associati ai singoli comunicatori relativi alle righe della mesh
    MPI_Group col_group;

    //definizione di tutti gli array di processi per ciascuna riga della mesh (i.e. per ciascun sotto-comunicatore) + dell'array di processi per la prima colonna della mesh
    int ranks_list[proc_dims[0]][proc_dims[1]];
    int col_ranks[proc_dims[0]];
    //array di appoggio che tiene traccia del prossimo indice da popolare per ciascun array contenuto in ranks_list
    int indexes_list[proc_dims[0]];
    //variabile d'appoggio: ospita le coordinate di tutti i processi nell'ambito della griglia cartesiana
    int all_cart_coords[p][DIMS];

    //inizializzazione a 0 di tutti i campi di indexes_list
    for(i=0; i<proc_dims[0]; i++) {
        indexes_list[i] = 0;
    }

    //ottenimento del gruppo dei processi che partecipano al comunicatore MPI_COMM_WORLD (che sarebbero tutti i processi)
    MPI_Comm_group(MPI_COMM_WORLD, &total_group);

    //ottenimento di tutti i gruppi associati alle singole righe della mesh + quello associato alla prima colonna
    for(i=0; i<p; i++) {
        MPI_Cart_coords(comm_cart, i, DIMS, all_cart_coords[i]);  //calcolo di delle cordinate per il processo i
        
        //popolamento dell'opportuno array contenuto in ranks_list; all_cart_coords[i][0] è l'indice riga del processo all'interno della mesh
        ranks_list[all_cart_coords[i][0]][indexes_list[all_cart_coords[i][0]]] = i;     //i = rank corrente
        indexes_list[all_cart_coords[i][0]]++;

        if(indexes_list[all_cart_coords[i][0]] == 1) {  //caso in cui si tratta del primo processo appartenente alla riga della mesh di riferimento --> apparterrà al comunicatore dei rappresentanti.
            col_ranks[all_cart_coords[i][0]] = i;

        }

    }

    //ottenimento di tutti i sottogruppi legati alle singole righe della mesh e ai relativi sotto-comunicatori
    for(i=0; i<proc_dims[0]; i++) {
        //ricordiamo che abbiamo un numero di sottogruppi pari al numero di righe della mesh e un numero di processi in ogni sottogruppo pari al numero di colonne della mesh
        MPI_Group_incl(total_group, proc_dims[1], ranks_list[i], &row_groups[i]);
        MPI_Comm_create(MPI_COMM_WORLD, row_groups[i], &row_comms[i]);   //i comunicatori qui definiti (in row_comms) verranno utilizzati nella funzione MPI_Scatterv().

    }
    //ottenimento del sottogruppo legato alla prima colonna della mesh e al relativo sotto-comunicatore
    MPI_Group_incl(total_group, proc_dims[0], col_ranks, &col_group);
    MPI_Comm_create(MPI_COMM_WORLD, col_group, &col_comm);


    //si assume che solo il processo 0 inizialmente conosca il valore di N, M, la matrice e l'array moltiplicativo.
    if(my_rank == 0) {
        num_rows = N;
        num_columns = M;

        //inizializzazione randomica delle porzioni del vettore e della matrice
        for(j=0; j<num_columns; j++) {
            total_mul_vector[j] = rand() % 10;

            #ifdef DEBUG
            printf("Mul_vect[%d] = %d\n", j, total_mul_vector[j]);
            #endif

            for(i=0; i<num_rows; i++) {
                total_matrix[i][j] = rand() % 10;

                #ifdef DEBUG
                printf("Matr[%d][%d] = %d\n", i, j, total_matrix[i][j]);
                #endif

            }

        }

    }

    //invio dei messaggi a tutti gli altri processi da parte del processo 0
    MPI_Bcast(&num_rows, 1, MPI_INT, 0, MPI_COMM_WORLD);
    MPI_Bcast(&num_columns, 1, MPI_INT, 0, MPI_COMM_WORLD);

    //calcolo delle dimensioni di base delle sottomatrici da assegnare a ciascun processo
    n = num_rows/proc_dims[0];
    m = num_columns/proc_dims[1];

    //calcolo del numero di righe e di colonne che, nella mesh di processi, sono composte da una riga/colonna di matrice in più
    proc_dims_hat[0] = num_rows - n*proc_dims[0];
    proc_dims_hat[1] = num_columns - m*proc_dims[1];

    
    //array con numero righe, numero colonne, spiazzamento righe e spiazzamento colonne della sottomatrice di ciascun processo
    int row_sizes[proc_dims[0]];
    int row_displs[proc_dims[0]];
    int col_sizes[proc_dims[1]];
    int col_displs[proc_dims[1]];

    //calcolo delle entry dei 4 array suddetti che indicano come verrà effettivamente suddiviso il lavoro (potrebbero esservi dei processi che prendono una riga e/o una colonna in più).
    mesh_row_index = 0;
    mesh_col_index = 0;

    for(i=0; i<p; i++) {
        //riga base + num righe
        if(all_cart_coords[i][0] < proc_dims_hat[0] && i%proc_dims[0] == 0) {    //caso in cui il processo i prende una riga in più
            row_displs[mesh_row_index] = all_cart_coords[i][0]*(n+1);
            row_sizes[mesh_row_index] = n+1;
            mesh_row_index++;

        } else if(all_cart_coords[i][0] >= proc_dims_hat[0] && i%proc_dims[0] == 0) {
            row_displs[mesh_row_index] = proc_dims_hat[0]*(n+1) + (all_cart_coords[i][0]-proc_dims_hat[0])*n;
            row_sizes[mesh_row_index] = n;
            mesh_row_index++;
        
        }
            
        //colonna base + num colonne
        if(all_cart_coords[i][1] < proc_dims_hat[1] && i < proc_dims[1]) {   //caso in cui il processo i prende una colonna in più
            col_displs[mesh_col_index] = all_cart_coords[i][1]*(m+1);
            col_sizes[mesh_col_index] = m+1;
            mesh_col_index++;
        
        } else if(all_cart_coords[i][1] >= proc_dims_hat[1] && i < proc_dims[1]) {
            col_displs[mesh_col_index] = proc_dims_hat[1]*(m+1) + (all_cart_coords[i][1]-proc_dims_hat[1])*m;
            col_sizes[mesh_col_index] = m;
            mesh_col_index++;

        }    

    }

    //inizializzazione del proprio indice riga e del proprio indice colonna all'interno della mesh di processi
    my_mesh_row = all_cart_coords[my_cart_rank][0];
    my_mesh_col = all_cart_coords[my_cart_rank][1];


    //allocazione della porzione locale della matrice, della porzione locale dei due vettori e di intermediate_res_vector
    local_matrix = (int *)malloc(row_sizes[my_mesh_row]*col_sizes[my_mesh_col]*sizeof(int));
    if(!local_matrix) {
        printf("Unable to allocate local_matrix.\n");
        MPI_Abort(MPI_COMM_WORLD, EXIT_FAILURE);

    }

    local_mul_vector = (int *)malloc(col_sizes[my_mesh_col]*sizeof(int));
    if(!local_mul_vector) {
        printf("Unable to allocate local_mul_vector.\n");
        MPI_Abort(MPI_COMM_WORLD, EXIT_FAILURE);

    }

    local_res_vector = (int *)malloc(row_sizes[my_mesh_row]*sizeof(int));
    if(!local_res_vector) {
        printf("Unable to allocate local_res_vector.\n");
        MPI_Abort(MPI_COMM_WORLD, EXIT_FAILURE);

    }

    intermediate_res_vector = (int *)malloc(row_sizes[my_mesh_row]*sizeof(int));
    if(!intermediate_res_vector) {
        printf("Unable to allocate intermediate_res_vector.\n");
        MPI_Abort(MPI_COMM_WORLD, EXIT_FAILURE);

    }

    //azzeramento esplicito di local_matrix, local_mul_vector, local_res_vector e intermediate_res_vector
    memset(local_matrix, 0, row_sizes[my_mesh_row]*col_sizes[my_mesh_col]*sizeof(int));
    memset(local_mul_vector, 0, col_sizes[my_mesh_col]*sizeof(int));
    memset(local_res_vector, 0, row_sizes[my_mesh_row]*sizeof(int));
    memset(intermediate_res_vector, 0, row_sizes[my_mesh_row]*sizeof(int));


    //il processo 0 invia al rappresentante di ciascuna riga di mesh l'intera matrice e l'intero vettore moltiplicativo
    if(col_comm != MPI_COMM_NULL) {   //check su se il processo appartiene effettivamente al comunicatore col_comm
        MPI_Bcast(total_matrix, num_rows*num_columns, MPI_INT, 0, col_comm);
        MPI_Bcast(total_mul_vector, num_columns, MPI_INT, 0, col_comm);

    }

    //split della matrice tra tutti i processi
    for(i=0; i<num_rows; i++) { //nel caso della mesh deve essere distribuita una riga per volta.
        //calcolo del comunicatore (i.e. dell'insieme di processi) a cui deve essere distribuita la riga i-esima
        for(j=0; j<proc_dims[0]; j++) {
            if(i >= row_displs[j] && (j == proc_dims[0]-1 || i < row_displs[j+1])) { //la riga esatta della matrice deve appartenere al range di indici della corrispettiva riga di mesh.
                mesh_row_index = j;
                break;
            
            }

        }    

        if(row_comms[mesh_row_index] != MPI_COMM_NULL)  //check su se il processo appartiene effettivamente al comunicatore row_comms[mesh_row_index]
            MPI_Scatterv(total_matrix[i], col_sizes, col_displs, MPI_INT, &local_matrix[(i-row_displs[my_mesh_row])*col_sizes[my_mesh_col]], col_sizes[my_mesh_col], MPI_INT, 0, row_comms[mesh_row_index]);
                                                                
    }
    
    //split del vettore moltiplicativo tra i processi
    for(i=0; i<proc_dims[0]; i++) { //una Scatterv() per ogni sotto-comunicatore (i.e. per ogni riga di cache)
        if(row_comms[i] != MPI_COMM_NULL)   //check su se il processo appartiene effettivamente al comunicatore row_comms[i]
            MPI_Scatterv(total_mul_vector, col_sizes, col_displs, MPI_INT, local_mul_vector, col_sizes[my_mesh_col], MPI_INT, 0, row_comms[i]);
    
    }

    //calcolo dei risultati parziali del prodotto tra la matrice e il vettore moltiplicativo
    for(i=0; i<row_sizes[my_mesh_row]; i++) {
        for(j=0; j<col_sizes[my_mesh_col]; j++) {
            local_res_vector[i] += local_matrix[i*col_sizes[my_mesh_col]+j]*local_mul_vector[j];

        }
        
    }

    //ricollezionamento dei risultati parziali contestualmente alle singole righe della mesh di processi
    for(i=0; i<proc_dims[0]; i++) {
        if(row_comms[i] != MPI_COMM_NULL)   //check su se il processo appartiene effettivamente al comunicatore row_comms[i]
            MPI_Reduce(local_res_vector, intermediate_res_vector, row_sizes[i], MPI_INT, MPI_SUM, 0, row_comms[i]);

    }
    //a questo punto si mettono insieme i risultati parziali per ottenere il risultato finale del prodotto matrice*vettore
    if(col_comm != MPI_COMM_NULL)   //check su se il processo appartiene effettivamente al comunicatore col_comm
        MPI_Gatherv(intermediate_res_vector, row_sizes[my_mesh_row], MPI_INT, total_res_vector, row_sizes, row_displs, MPI_INT, 0, col_comm);

    //stampa del risultato finale
    if(my_rank == 0) {
        for(i=0; i<num_rows; i++) {
            printf("Res_vect[%d] = %d\n", i, total_res_vector[i]);
        
        }

    }

    MPI_Finalize();
    return 0;

}
