#include <stdio.h>
#include "mpi.h"

#define GHERTZ 2.8
#define ITERATIONS 1000
#define MUL_BYTES 1000
#define SINGLE_OUTLIER 200
#define MUL_OUTLIER 5000

#define RDTSC(value) asm ("xor %%rax, %%rax; mfence; rdtsc; mfence" : "=a" (value))   //macro che serve a recuperare il timestamp corrente

int main(int argc, char **argv) {
    //variabili utili ai fini del calcolo delle prestazioni
    unsigned long timestamp1;                   
    unsigned long timestamp2;                   
    double timestamp1_ns;               //nanoseconds
    double timestamp2_ns;               //nanoseconds
    double single_byte_cumulative_time; //nanoseconds
    double single_byte_avg_time;        //nanoseconds
    double mul_bytes_cumulative_time;   //nanoseconds
    double mul_bytes_avg_time;          //nanoseconds
    double transmission_time;           //nanoseconds
    double bandwidth;                   //Mbytes per second
    double latency;                     //nanoseconds
    //variabili MPI
    int my_rank;
    int p;
    int tag;
    //indice ciclo for
    int i;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &p);
    MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);
    printf("Hello from process %d of %d!\n", my_rank, p);

    //affinché il programma possa funzionare, devono essere lanciati almeno due processi
    if(p < 2) {
        printf("At least 2 processes must be launched.\n");
        MPI_Finalize();
        return 0;

    }

    MPI_Status status;          //status della ricezione del messaggio
    tag = 0;                    //voglio che, a parità di destinatario, i messaggi vengano consegnati in ordine (i.e. abbiano lo stesso envelope).


    //il processo 0 invia ITERATIONS volte un byte e ITERATIONS volte due byte al processo 1, ed effettua delle misurazioni sul tempo; eventuali altri processi non devono far nulla.
    if(my_rank == 0) {
        //variabili utili per il processo 0
        int dest;
        char single_byte_send_buff;
        char mul_bytes_send_buff[MUL_BYTES];
        int not_counted;    //variabile che indica il numero di tempi misurati non considerati nel calcolo della media perché outlier

        dest = 1;
        single_byte_cumulative_time = 0;
        mul_bytes_cumulative_time = 0;
        not_counted = 0;

        single_byte_send_buff = 'a';
        for(i=0; i<MUL_BYTES; i++) {
            mul_bytes_send_buff[i] = 'b';
        }

        //invii dei singoli byte
        for(i=0; i<ITERATIONS; i++) {
            RDTSC(timestamp1);
            timestamp1_ns = 1.0*timestamp1/GHERTZ;    //conversione da num cicli clock a nanosecondi
            MPI_Send(&single_byte_send_buff, sizeof(char), MPI_CHAR, dest, tag, MPI_COMM_WORLD);
            RDTSC(timestamp2);
            timestamp2_ns = 1.0*timestamp2/GHERTZ;    //conversione da num cicli clock a nanosecondi
            //printf("[PROCESS %d] Single byte time = %f ns.\n", my_rank, timestamp2_ns-timestamp1_ns);

            if(timestamp2_ns - timestamp1_ns < SINGLE_OUTLIER)
                single_byte_cumulative_time += (timestamp2_ns - timestamp1_ns); //somma cumulativa di tanti valori dati da (tempo di trasmissione di 1 byte + latenza)
            else
                not_counted++;

        }
        single_byte_avg_time = single_byte_cumulative_time / (ITERATIONS-not_counted);

        not_counted = 0;

        //invii dei byte doppi
        for(i=0; i<ITERATIONS; i++) {
            RDTSC(timestamp1);
            timestamp1_ns = 1.0*timestamp1/GHERTZ;    //conversione da num cicli clock a nanosecondi
            MPI_Send(&mul_bytes_send_buff, MUL_BYTES*sizeof(char), MPI_CHAR, dest, tag, MPI_COMM_WORLD);
            RDTSC(timestamp2);
            timestamp2_ns = 1.0*timestamp2/GHERTZ;    //conversione da num cicli clock a nanosecondi
            //printf("[PROCESS %d] Multiple bytes time = %f ns.\n", my_rank, timestamp2_ns-timestamp1_ns);
            
            if(timestamp2_ns - timestamp1_ns < MUL_OUTLIER)
                mul_bytes_cumulative_time += (timestamp2_ns - timestamp1_ns);
            else
                not_counted++;

        }
        mul_bytes_avg_time = mul_bytes_cumulative_time / (ITERATIONS-not_counted);

        //calcolo metriche prestazionali
        transmission_time = (mul_bytes_avg_time-single_byte_avg_time)/(MUL_BYTES-1);  //tempo di trasmissione di 1 byte
        bandwidth = 1000.0/transmission_time;   //conversione da ns a Mbyte/s
        latency = (double)single_byte_avg_time - transmission_time;

        printf("[PROCESS %d] Single byte avg time = %f ns.\n", my_rank, single_byte_avg_time);
        printf("[PROCESS %d] Multiple bytes avg time = %f ns.\n", my_rank, mul_bytes_avg_time);
        printf("[PROCESS %d] Transmission time = %f ns.\n", my_rank, transmission_time);
        printf("[PROCESS %d] Bandwidth = %f MB/s.\n", my_rank, bandwidth);
        printf("[PROCESS %d] Latency = %f ns.\n", my_rank, latency);

    }

    else if(my_rank == 1) {
        //variabili utili per il processo 1
        int source;
        char single_byte_recv_buff;
        char mul_bytes_recv_buff[MUL_BYTES];

        source = 0;

        //ricezioni dei singoli byte
        for(i=0; i<ITERATIONS; i++) {
            MPI_Recv(&single_byte_recv_buff, sizeof(char), MPI_CHAR, source, tag, MPI_COMM_WORLD, &status);

        }

        //ricezioni dei byte doppi
        for(i=0; i<ITERATIONS; i++) {
            MPI_Recv(&mul_bytes_recv_buff, MUL_BYTES*sizeof(char), MPI_CHAR, source, tag, MPI_COMM_WORLD, &status);

        }

        printf("[PROCESS %d] Reception completed.\n", my_rank);

    }

    else {
        printf("[PROCESS %d] Nothing to do.\n", my_rank);

    }

    MPI_Finalize();
    return 0;

}
