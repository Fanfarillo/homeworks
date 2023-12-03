// Copyright 2014, Cranfield University
// All rights reserved
// Author: Michał Czapiński (mczapinski@gmail.com)
//
// Demonstrates the most basic CUDA concepts on the example
// of single precision AXPY operation.
// AXPY stands for y = y + alpha * x, where x, and y are vectors.

#include <iostream>

#include <cuda_runtime.h>  // For CUDA runtime API
#include <helper_cuda.h>   // For checkCudaError macro
#include <helper_timer.h>  // For CUDA SDK timers

// FANFA: compilation macro
#define MULTI

// With this implementation and 256 threads per block, works only for up to 16M. Why?
const int N = 15 * 1024 * 1024;
const dim3 BLOCK_DIM = 256;

// Simple CPU implementation of a single precision AXPY operation.
void CpuSaxpy(int n, float alpha, const float* x, float* y) {
  for (int i = 0; i < n; ++i) {
    y[i] += alpha * x[i];
  }
}

// GPU implementation of AXPY operation - one CUDA thread per vector element.
__global__ void GpuSaxpy(int n, float alpha, const float* x, float* y) {
  // FANFA: Calculate the index of the vector element updated by this thread.
  // Assume 1D grid of blocks.
  int idx = threadIdx.x + blockIdx.x * blockDim.x;

  // FANFA: Make sure that no threads access memory outside the allocated area.
  if(idx < n) {
    y[idx] += alpha * x[idx];
  }

}

// GPU implementation of AXPY operation - CUDA thread updates multiple vector elements.
__global__ void GpuSaxpyMulti(int n, float alpha, const float* x, float* y) {
  // FANFA: Implement CUDA kernel where threads update more than one vector element.
  // Assume 1D grid of blocks.
  int idx = threadIdx.x;
  int thread_count = blockDim.x;

  for(; idx<n; idx+=thread_count) {
    y[idx] += alpha * x[idx];
  }

  // TODO(later) Check if it's faster than the original implementation.
}

int main(int argc, char** argv) {

// ----------------------- Host memory initialisation ----------------------- //

  float* h_x = new float[N];
  float* h_y = new float[N];

  // Initialise vectors on the CPU.
  std::fill_n(h_x, N, 1.0f);  // Vector of ones
  for (int i = 0; i < N; ++i) {
    h_y[i] = 0.33f * (i + 1);
  }

// ---------------------- Device memory initialisation ---------------------- //

  // FANFA: Allocate global memory on the GPU. Each vector should have N float elements.
  float* d_x = 0;
  float* d_y = 0;

  cudaMalloc((void **)&d_x, N*sizeof(float));
  cudaMalloc((void **)&d_y, N*sizeof(float));

  // FANFA: Copy vectors from the host (CPU) to the device (GPU).
  cudaMemcpy(d_x, h_x, N*sizeof(float), cudaMemcpyHostToDevice);
  cudaMemcpy(d_y, h_y, N*sizeof(float), cudaMemcpyHostToDevice);

// --------------------- Calculations for CPU implementation ---------------- //

  // Create the CUDA SDK timer.
  StopWatchInterface* timer = 0;
  sdkCreateTimer(&timer);

  timer->start();
  CpuSaxpy(N, 0.25f, h_x, h_y);   // y = y + 0.25 * x;
  CpuSaxpy(N, -10.5f, h_x, h_y);  // y = y - 10.5 * x;

  timer->stop();
  std::cout << "CPU time: " << timer->getTime() << " ms." << std::endl;

// --------------------- Calculations for GPU implementation ---------------- //

  // FANFA: Calculate the number of required thread blocks (one thread per vector element).
  #ifndef MULTI
  const dim3 GRID_DIM = 1024*1024*15/256;
  #endif

  timer->reset();
  timer->start();

  // FANFA: Insert the correct kernel invocation parameters.
  #ifndef MULTI
  GpuSaxpy<<<GRID_DIM, BLOCK_DIM>>>(N, 0.25f, d_x, d_y);  // y = y + 0.25 * x;
  GpuSaxpy<<<GRID_DIM, BLOCK_DIM>>>(N, -10.5f, d_x, d_y); // y = y - 10.5 * x;
  // FANFA: This should work as well.
  #else
  GpuSaxpyMulti<<<1, BLOCK_DIM>>>(N, 0.25f, d_x, d_y);    // y = y + 0.25 * x;
  GpuSaxpyMulti<<<1, BLOCK_DIM>>>(N, -10.5f, d_x, d_y);   // y = y - 10.5 * x;
  #endif

  // Kernel calls are asynchronous with respect to the host, i.e. control is returned to
  // the CPU immediately. It is possible that the second operation is submitted _before_
  // the first one is completed. However, CUDA driver will ensure that they will be
  // completed in FIFO order, one at a time.

  // CPU has to explicitly wait for the device to complete
  // in order to get meaningful time measurement.
  checkCudaErrors(cudaDeviceSynchronize());
  timer->stop();
  std::cout << "GPU time: " << timer->getTime() << " ms." << std::endl;

  // FANFA: Download the resulting vector d_y from the device and store it in h_x.
  cudaMemcpy(h_x, d_y, N*sizeof(float), cudaMemcpyDeviceToHost);

  // cudaMemcpy is synchronous, i.e. it will wait for any computation on the GPU to
  // complete before any data is copied (as if cudaDeviceSynchronize() was called before).

  // Now let's check if the results are the same.
  float diff = 0.0f;
  for (int i = 0; i < N; ++i) {
    diff = std::max(diff, std::abs(h_x[i] - h_y[i]));
  }
  std::cout << "Max diff = " << diff << std::endl;  // Should be (very close to) zero.

// ------------------------------- Cleaning up ------------------------------ //

  delete timer;
  delete[] h_x;
  delete[] h_y;

  // FANFA: Don't forget to free host and device memory!
  checkCudaErrors(cudaFree(d_x));
  checkCudaErrors(cudaFree(d_y));

  return 0;
}
