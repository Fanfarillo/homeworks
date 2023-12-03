#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include "mpi.h"

#define N 1000  //rows of the matrix and num of components of result vector
#define M 1200  //columns of the matrix and num of components of multiplicative vector

int main(int argc, char **argv) {
    //variabili MPI
    int my_rank;
    int p;
    int source;
    int dest;
    int tag;
    int rows;               //pari a N, è la prima variabile che il mittente (rank 0) passa per messaggio agli altri processi.
    int columns;            //pari a M, è la seconda variabile che il mittente (rank 0) passa per messaggio agli altri processi.
    MPI_Status status;      //status della ricezione del messaggio
    MPI_Datatype twoInt;    //tipo di dato MPI che comprende due interi
    MPI_Datatype vectInt;   //tipo di dato MPI che comprende un numero di interi pari alla dimensione dell'array risultato (+2)
    //buffer di memoria (invio / ricezione)
    int *send_buf;
    int *recv_buf;
    //variabili utili per suddividere il lavoro nel modo più uniforme possibile
    int initial_n;      //numero INIZIALE di righe per la sottomatrice da assegnare a ciascun processo
    int initial_m;      //numero INIZIALE di colonne per la sottomatrice da assegnare a ciascun processo
    int n;              //numero di righe per la sottomatrice da assegnare a ciascun processo
    int m;              //numero di colonne per la sottomatrice da assegnare a ciascun processo
    int p_vert;         //numero di righe della mesh di processi; sarebbe N/n
    int p_orizz;        //numero di colonne della mesh di processi; sarebbe M/m
    int p_vert_hat;     //numero di righe che, nella mesh di processi, sono composte da una riga di matrice in più; sarebbe N%n
    int p_orizz_hat;    //numero di colonne che, nella mesh di processi, sono composte da una colonna di matrice in più; sarebbe M%m
    int mesh_row_index; //indice riga del processo my_rank all'interno della mesh dei processi
    int mesh_col_index; //indice colonna del processo my_rank all'interno della mesh dei processi
    //variabili che indicano come verrà effettivamente suddiviso il lavoro
    int base_row;   //indice riga della matrice da cui parte la porzione per il processo my_rank
    int num_rows;   //numero di righe della matrice su cui il processo my_rank deve lavorare
    int base_col;   //indice riga della matrice da cui parte la porzione per il processo my_rank
    int num_cols;   //numero di righe della matrice su cui il processo my_rank deve lavorare
    //puntatori alle aree di memoria su cui ciascun processo dovrà lavorare
    int **my_matrix_portion;
    int *my_mult_vector_portion;
    int *my_result_vector_portion;
    //indici ciclo for
    int i;
    int j;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &p);
    MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);
    printf("Hello from process %d of %d!\n", my_rank, p);

    tag = 0;
    MPI_Type_contiguous(2, MPI_INT, &twoInt);
    MPI_Type_commit(&twoInt);
    MPI_Type_contiguous(N+2, MPI_INT, &vectInt);
    MPI_Type_commit(&vectInt);

    if(my_rank == 0) {  //processo mittente: deve comunicare agli altri processi i valori di N, M.
        send_buf = (int *)malloc(2*sizeof(int));
        if(!send_buf) {
            printf("Unable to allocate send_buf.\n");
            MPI_Abort(MPI_COMM_WORLD, EXIT_FAILURE);

        }
        rows = N;
        columns = M;
        send_buf[0] = N;
        send_buf[1] = M;

        for(dest=1; dest<p; dest++) {
            MPI_Send(send_buf, 1, twoInt, dest, tag, MPI_COMM_WORLD);
        
        }

    } else {  //processi destinatari
        recv_buf = (int *)malloc(2*sizeof(int));
        if(!recv_buf) {
            printf("Unable to allocate recv_buf.\n");
            MPI_Abort(MPI_COMM_WORLD, EXIT_FAILURE);

        }
        //azzeramento di recv_buf
        for(i=0; i<2; i++) {
            recv_buf[i] = 0;

        }

        source = 0;
        MPI_Recv(recv_buf, 1, twoInt, source, tag, MPI_COMM_WORLD, &status);
        rows = recv_buf[0];
        columns = recv_buf[1];

    }
    
    /* M*N = all components of the matrix
     * M*N/p = #components of the sub-matrix for each process
     * sqrt(M*N/p) = numero iniziale di righe e di colonne per la sottomatrice da assegnare a ciascun processo
     *
     * l'idea è quella di verificare se p sottomatrici siffatte c'entrano nella matrice di partenza; se no, si cerca di allungare la forma delle sottomatrici
     * (facendola tendere alle proporzioni della matrice iniziale) fin tanto che non si riesce a ottenere una mesh valida.
     */
    initial_n = sqrt(columns*rows/p);
    initial_m = sqrt(columns*rows/p);
    n = initial_n;
    m = initial_m;
    p_vert = rows/n;
    p_orizz = columns/m;

    //caso in cui è stata trovata una mesh di matrici in cui tutti i p processi riescono ad aver riservata la propria sottomatrice n*m
    if(p_vert*p_orizz == p)   goto submatrix_found;  //in una versione originale, al posto di '==' c'era '>='

    while(1) {
        //se p matrici n*m non c'entrano in una matrice N*M, si prova a cambiare la forma delle sottomatrici.
        if(rows > columns) {    //caso in cui la matrice iniziale ha più righe che colonne --> incremento del numero di righe && decremento del numero di colonne delle sottomatrici
            n++;
            p_vert = rows/n;
            if(p_vert*p_orizz == p)   break;

            m--;
            if(m>0) {
                p_orizz = columns/m;
                if(p_vert*p_orizz == p)   break;
            }

            //check se il numero di righe delle sottomatrici ha sforato il valore di rows && se il numero di colonne delle sottomatrici ha raggiunto il valore minimo (1)
            if(n >= rows || m <= 1) {   //in tal caso, si ripete tutto ripartendo da delle sottomatrici quadrate più piccole di quelle di partenza
                initial_n--;
                initial_m--;
                n = initial_n;
                m = initial_m;
                p_vert = rows/n;
                p_orizz = columns/m;
                if(p_vert*p_orizz == p)   break;
                
            }

        } else {  //caso in cui la matrice iniziale ha più colonne che righe --> incremento del numero di colonne && decremento del numero di righe delle sottomatrici
            m++;
            p_orizz = columns/m;
            if(p_vert*p_orizz == p)   break;

            n--;
            if(n>0) {
                p_vert = rows/n;
                if(p_vert*p_orizz == p)   break;
            }

            //check se il numero di colonne delle sottomatrici ha sforato il valore di columns && se il numero di righe delle sottomatrici ha raggiunto il valore minimo (1)
            if(m >= columns || n <= 1) {   //in tal caso, si ripete tutto ripartendo da delle sottomatrici quadrate più piccole di quelle di partenza
                initial_n--;
                initial_m--;
                n = initial_n;
                m = initial_m;
                p_vert = rows/n;
                p_orizz = columns/m;
                if(p_vert*p_orizz == p)   break;
                
            }

        }
    
    }

submatrix_found:
    //a questo punto, abbiamo trovato il numero di righe n e il numero di colonne m di ciascuna sottomatrice (oltre a p_vert, p_orizz).
    p_vert_hat = rows % n;
    p_orizz_hat = columns % m;

    //calcolo degli indici riga e colonna del processo my_rank all'interno della mesh dei processi (i.e. indici risp p_orizz, p_vert)
    //indice riga
    for(i=0; i<p_vert; i++) {
        if(my_rank < i*p_orizz) {
            mesh_row_index = i;
            break;

        }

    }
    //indice colonna
    mesh_col_index = my_rank % p_orizz;

    //calcolo delle variabili che indicano come verrà effettivamente suddiviso il lavoro (potrebbero esservi dei processi che prendono una riga e/o una colonna in più).
    //riga base
    if(mesh_row_index <= p_vert_hat)    //caso in cui il processo my_rank è preceduto esclusivamente da processi che prendono una riga in più
        base_row = mesh_row_index*(n+1);
    else
        base_row = p_vert_hat*(n+1) + (mesh_row_index-p_vert_hat)*n;

    //num righe
    if(mesh_row_index < p_vert_hat)     //caso in cui il processo my_rank prende una riga in più
        num_rows = n+1;
    else
        num_rows = n;

    //colonna base
    if(mesh_col_index <= p_orizz_hat)   //caso in cui il processo my_rank è preceduto esclusivamente da processi che prendono una colonna in più
        base_col = mesh_col_index*(m+1);
    else
        base_col = p_orizz_hat*(m+1) + (mesh_col_index-p_orizz_hat)*m;

    //num colonne
    if(mesh_col_index < p_orizz_hat)    //caso in cui il processo my_rank prende una colonna in più
        num_cols = m+1;
    else
        num_cols = m;


    //allocazione delle aree di memoria (porzione di matrice e porzioni di vettori) su cui ciascun processo lavorerà
    //porzione di matrice
    my_matrix_portion = (int **)malloc(num_rows*sizeof(int *));
    if(!my_matrix_portion) {
        printf("Unable to allocate my_matrix_portion.\n");
        MPI_Abort(MPI_COMM_WORLD, EXIT_FAILURE);

    }
    for(i=0; i<num_rows; i++) {
        my_matrix_portion[i] = (int *)malloc(num_cols*sizeof(int));
        if(!my_matrix_portion[i]) {
            printf("Unable to allocate my_matrix_portion[%d].\n", i);
            MPI_Abort(MPI_COMM_WORLD, EXIT_FAILURE);

        }

    }

    //porzione di vettore moltiplicativo
    my_mult_vector_portion = (int *)malloc(num_cols*sizeof(int));
    if(!my_mult_vector_portion) {
        printf("Unable to allocate my_mult_vector_portion.\n");
        MPI_Abort(MPI_COMM_WORLD, EXIT_FAILURE);

    }

    //porzione di vettore risultato
    my_result_vector_portion = (int *)malloc((num_rows+2)*sizeof(int)); //il primo componente del vettore ospiterà base_row. Il secondo ospiterà num_rows.
    if(!my_result_vector_portion) {
        printf("Unable to allocate my_result_vector_portion.\n");
        MPI_Abort(MPI_COMM_WORLD, EXIT_FAILURE);

    }

    //inizializzazione pseudo-randomica di porzione di matrice e di porzione di vettore moltiplicativo
    for(i=0; i<num_rows; i++) {
        for(j=0; j<num_cols; j++) {
            my_matrix_portion[i][j] = rand() % 10;  //stiamo imponendo che tutte le componenti assumano valori compresi tra 0 e 9.

        }

    }
    for(j=0; j<num_cols; j++) {
        my_mult_vector_portion[j] = rand() % 10;

    }
    //azzeramento (iniziale) della porzione di vettore risultato (con tanto di inizializzazione del primo componente al valore di base_row e del secondo al valore di num_rows)
    my_result_vector_portion[0] = base_row;
    my_result_vector_portion[1] = num_rows;
    for(i=2; i<num_rows+2; i++) {
        my_result_vector_portion[i] = 0;

    }

    //prodotto matrice*vettore vero e proprio
    for(i=0; i<num_rows; i++) {
        for(j=0; j<num_cols; j++) {
            my_result_vector_portion[i+2] += my_matrix_portion[i][j]*my_mult_vector_portion[j];

        }

    }

    //i processi con my_rank > 0 inviano i propri risultati parziali al processo con my_rank = 0
    if(my_rank > 0) {        
        dest = 0;
        MPI_Send(my_result_vector_portion, 1, vectInt, dest, tag, MPI_COMM_WORLD);

    } else {
        int *entire_result_vector;
        int index;

        //allocation of entire result vector and of recv_buf
        entire_result_vector = (int *)malloc(rows*sizeof(int));
        if(!entire_result_vector) {
            printf("Unable to allocate entire_result_vector.\n");
            MPI_Abort(MPI_COMM_WORLD, EXIT_FAILURE);

        }
        recv_buf = (int *)malloc((rows+2)*sizeof(int));
        if(!recv_buf) {
            printf("Unable to allocate recv_buf.\n");
            MPI_Abort(MPI_COMM_WORLD, EXIT_FAILURE);

        }
        //azzeramento iniziale di entire_result_vector e di recv_buf
        for(i=0; i<rows; i++) {
            entire_result_vector[i] = 0;

        }
        for(i=0; i<rows+2; i++) {
            recv_buf[i] = 0;

        }

        //inizializzazione dell'entire_result_vector coi PROPRI risultati parziali
        for(i=0; i<num_rows; i++) {
            index = i + base_row;
            entire_result_vector[index] += my_result_vector_portion[i+2];

        }

        //inizializzazione dell'entire_result_vector coi risultati parziali inviati degli altri processi
        for(source=1; source<p; source++) {
            MPI_Recv(recv_buf, 1, vectInt, MPI_ANY_SOURCE, tag, MPI_COMM_WORLD, &status);
            
            for(i=0; i<recv_buf[1]; i++) {  //recv_buf[1] = num_rows per il processo source
                index = i + recv_buf[0];    //recv_buf[0] = base_row per il processo source
                entire_result_vector[index] += recv_buf[i+2];   //le restanti componenti di recv_buf formano il my_result_vector_portion per il processo source.

            }

        }

        printf("[PROCESS %d] Here I would have to show the result of matrix*vector product.\n", my_rank);
        free(send_buf);
        free(entire_result_vector);

    }

    //cleanup
    free(recv_buf);
    free(my_matrix_portion);
    free(my_mult_vector_portion);
    free(my_result_vector_portion);

    printf("[PROCESS %d] Bye!\n", my_rank);

    MPI_Type_free(&twoInt);
    MPI_Type_free(&vectInt);
    MPI_Finalize();
    
    return 0;

}
