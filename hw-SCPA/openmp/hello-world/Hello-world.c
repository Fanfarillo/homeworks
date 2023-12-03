#include <omp.h>
#include <stdio.h>

int main(int argc, char **argv) {
    //useful variables
    int tid;    //thread id
    int nt;     //num threads

    #pragma omp parallel private(tid)
    {
        #pragma omp single
        {
            //only one thread does this
            tid = -1;
            nt = omp_get_num_threads();
        }

        //obtain and print thread id (tid)
        tid = omp_get_thread_num();
        printf("Hello from thread %d of %d!\n", tid, nt);

    }   //all threads join master thread and terminate

    return 0;

}
