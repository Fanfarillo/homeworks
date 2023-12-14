// Copyright 2018, Cranfield University
// All rights reserved
// Author: Salvatore Filippone

#include <iostream>

#include <cuda_runtime.h>
#include <helper_cuda.h>
#include <helper_timer.h>

const int VERSION=0;

// Computes the reduction using the CPU.
double Reduction(int n, const double* x) {
  double result = 0.0f;
  for (int i = 0; i < n; ++i) {
    result += x[i] ;
  }
  return result;
}

__global__ void reduce(int n, double *g_idata, double *g_odata) {
  extern __shared__ double sdata[];
  // each thread loads one element from global to shared mem
  unsigned int tid = threadIdx.x;
  unsigned int i = blockIdx.x*blockDim.x + threadIdx.x;
  sdata[tid] = 0.0;
  if (i<n) 
    sdata[tid] = g_idata[i];
  __syncthreads();
  // do reduction in shared mem
  for(unsigned int s=1; s < blockDim.x; s *= 2) {
    if (tid % (2*s) == 0) {
      sdata[tid] += sdata[tid + s];
    }
    __syncthreads();
  }
  // write result for this block to global mem
  if (tid == 0) g_odata[blockIdx.x] += sdata[0];
}

const int MAX_BLOCKS   = 15*1024;
const int THREAD_BLOCK = 256;

void do_gpu_reduce(int n, double *g_idata, double *g_odata)
{
  const int shmem_size   = THREAD_BLOCK*sizeof(double);
  int   nblocks = ((n + THREAD_BLOCK - 1) / THREAD_BLOCK);
  reduce<<<nblocks,THREAD_BLOCK,shmem_size,0>>>(n,g_idata,g_odata);
  
}

double gpu_reduce(int n, double *d_v)
{
  const int MAXN_CALL    = MAX_BLOCKS*THREAD_BLOCK;
  const int RED_SZ       = MAX_BLOCKS/THREAD_BLOCK;
  double *o_data, *d_res_data, *h_res_data;
  int i, sz;
  
  cudaMalloc(&o_data,MAX_BLOCKS*sizeof(double));
  cudaMemset((void *) o_data, 0, MAX_BLOCKS*sizeof(double));
  cudaMalloc(&d_res_data,(RED_SZ)*sizeof(double));
  cudaMemset((void *)d_res_data,0,(RED_SZ)*sizeof(double));
  h_res_data = (double *)malloc((RED_SZ)*sizeof(double));

  for (i=0; i<n; ) {
    sz = ((n-i) > MAXN_CALL ? MAXN_CALL : (n-i));
    do_gpu_reduce(sz, d_v, o_data);
    d_v += sz;
    i += sz;
  }
  do_gpu_reduce(MAX_BLOCKS,o_data,d_res_data);
  
  cudaError_t err = cudaMemcpy(h_res_data, d_res_data, RED_SZ*sizeof(double), cudaMemcpyDeviceToHost);
  
  return(Reduction(RED_SZ,h_res_data));
  
}
  


// Returns a random number from range [0, 1).
double rand_double() {
  return static_cast<double>(rand()) / RAND_MAX;
}

int main(int argc, char** argv) {

  if (argc < 2) { 
    std::cerr << "Usage: " <<argv[0] << " N" << std::endl;
    exit(1);
  }

  int N = atoi(argv[1]);

  double bdwdth;
  double *h_x=(double *) malloc(N*sizeof(double));
  double *d_x;

  srand(time(0));

  for (int i=0; i<N; i++)
    h_x[i]=rand_double();
  
  cudaError_t err=cudaMalloc((void **)&d_x,(N*sizeof(double)));
  err = cudaMemcpy(d_x, h_x, N*sizeof(double), cudaMemcpyHostToDevice);

  StopWatchInterface* timer = 0;
  sdkCreateTimer(&timer);

  std::cout << "Testing reduction algorithm " << VERSION << "  on a DOUBLE vector of size:  " << N << std::endl;
  // Calculate the reduction on the host.
  timer->start();
  double cpu_sum = Reduction(N, h_x);

  timer->stop();
  std::cout << "CPU reduction:            " << cpu_sum
      << " " << timer->getTime() << " ms. " << std::endl;


// ------------ GPU  reduction ------------ //

  timer->reset();
  timer->start();
  
  double gpu_sum = gpu_reduce(N,d_x);

  timer->stop();
  // FANFA: compute the effective bandwidth in GB/s.
  // Note: the timer is in milliseconds! 
  bdwdth = (N*sizeof(double))/(timer->getTime()*1000000);

  std::cout << "GPU reduction:            " << gpu_sum
      << " " << timer->getTime() << " ms. " << std::endl;
  
  std::cout << "Relative difference:            " << abs(gpu_sum-cpu_sum)/gpu_sum << std::endl;
  std::cout << "Measured bandwidth:             " << bdwdth << " GB/s" << std::endl;
  

// ------------------------------- Cleaning up ------------------------------ //

  delete timer;

  checkCudaErrors(cudaDeviceReset());
  return 0;
}
