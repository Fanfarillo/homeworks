# hw-SCPA

## MPI

**Per compilare un programma X:**
mpicc X.c -o X [-lm]

**Per lanciare il programma X con N processi:**
mpirun -np N --oversubscribe ./X

## OpenMP

**Per compilare un programma X:**
gcc X.c -o X -fopenmp [-lm]

**Per lanciare il programma X:**
./X

**Per impostare il numero di thread N da lanciare:**
export OMP_NUM_THREADS=N
