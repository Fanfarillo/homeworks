// Copyright 2014, Cranfield University
// All rights reserved
// Author: Michał Czapiński (mczapinski@gmail.com)
//
// Adds two matrices on the GPU. Matrices are stored in linear memory in row-major order,
// i.e. A[i, j] is stored in i * COLS + j element of the vector.

#include <iostream>

#include <cuda_runtime.h>  // For CUDA runtime API
#include <helper_cuda.h>   // For checkCudaError macro
#include <helper_timer.h>  // For CUDA SDK timers

#include <stdlib.h>

// Matrix dimensions. Can you make these input arguments? [DEFAULT: 4096]
int ROWS;
int COLS;

// TODO(later) Play a bit with the block size. Is 16x16 setup the fastest possible?
// Note: For meaningful time measurements you need sufficiently large matrix.
const dim3 BLOCK_DIM(16, 16);

// Simple CPU implementation of matrix addition.
void CpuMatrixAdd(int rows, int cols, const float* A, const float* B, float* C) {
  for (int row = 0; row < rows; ++row) {
    for (int col = 0; col < cols; ++col) {
      int idx = row * cols + col;
      C[idx] = A[idx] + B[idx];
    }
  }
}

// GPU implementation of matrix add using one CUDA thread per vector element.
__global__ void GpuMatrixAdd(int rows, int cols, const float* A, const float* B, float* C) {
  // FANFA: Calculate indices of matrix elements added by this thread. Assume 2D grid of blocks.
  int col = threadIdx.x + blockIdx.x * blockDim.x;
  int row = threadIdx.y + blockIdx.y * blockDim.y;
  // TODO(later) Does it matter if you index rows with x or y dimension of threadIdx and blockIdx?

  // FANFA: Calculate the element index in the global memory and add the values.
  int idx = col + row * blockDim.x * gridDim.x;

  // FANFA: Make sure that no threads access memory outside the allocated area.
  if(idx < rows*cols) {
    C[idx] = A[idx] + B[idx];
  }

}

int main(int argc, char** argv) {

// ------------------- FANFA: Input arguments acquisition ------------------- //

  if(argc < 3) {
    std::cout << "Usage: ./matrix_add ROWS COLS" << std::endl;
    return -1;
  }

  ROWS = atoi(argv[1]);
  COLS = atoi(argv[2]);

  if(ROWS <= 0 || COLS <= 0) {
    std::cout << "Invalid input. Please try again." << std::endl;
    return -1;
  }

  // Variables useful for GRID_DIM definition.
  int grid_dim_1 = ROWS/16;
  int grid_dim_2 = COLS/16;

// ----------------------- Host memory initialisation ----------------------- //

  float* h_A = new float[ROWS * COLS];
  float* h_B = new float[ROWS * COLS];
  float* h_C = new float[ROWS * COLS];

  srand(time(0));
  for (int row = 0; row < ROWS; ++row) {
    for (int col = 0; col < COLS; ++col) {
      int idx = row * COLS + col;
      h_A[idx] = 100.0f * static_cast<float>(rand()) / RAND_MAX;
      h_B[idx] = 100.0f * static_cast<float>(rand()) / RAND_MAX;
    }
  }

// ---------------------- Device memory initialisation ---------------------- //

  // FANFA: Allocate global memory on the GPU.
  float *d_A, *d_B, *d_C;

  checkCudaErrors(cudaMalloc((void **)&d_A, ROWS*COLS*sizeof(float)));
  checkCudaErrors(cudaMalloc((void **)&d_B, ROWS*COLS*sizeof(float)));
  checkCudaErrors(cudaMalloc((void **)&d_C, ROWS*COLS*sizeof(float)));

  // FANFA: Copy matrices from the host (CPU) to the device (GPU).
  checkCudaErrors(cudaMemcpy(d_A, h_A, ROWS*COLS*sizeof(float), cudaMemcpyHostToDevice));
  checkCudaErrors(cudaMemcpy(d_B, h_B, ROWS*COLS*sizeof(float), cudaMemcpyHostToDevice));
  checkCudaErrors(cudaMemcpy(d_C, h_C, ROWS*COLS*sizeof(float), cudaMemcpyHostToDevice));

// ------------------------ Calculations on the CPU ------------------------- //

  // Create the CUDA SDK timer.
  StopWatchInterface* timer = 0;
  sdkCreateTimer(&timer);

  timer->start();
  CpuMatrixAdd(ROWS, COLS, h_A, h_B, h_C);

  timer->stop();
  std::cout << "CPU time: " << timer->getTime() << " ms." << std::endl;

// ------------------------ Calculations on the GPU ------------------------- //

  // FANFA: Calculate the dimension of the grid of blocks (2D).
  const dim3 GRID_DIM(grid_dim_1, grid_dim_2);

  timer->reset();
  timer->start();
  GpuMatrixAdd<<<GRID_DIM, BLOCK_DIM>>>(ROWS, COLS, d_A, d_B, d_C);
  checkCudaErrors(cudaDeviceSynchronize());

  timer->stop();
  std::cout << "GPU time: " << timer->getTime() << " ms." << std::endl;

  // FANFA: Download the resulting matrix d_C from the device and store it in h_A.
  checkCudaErrors(cudaMemcpy(h_A, d_C, ROWS*COLS*sizeof(float), cudaMemcpyDeviceToHost));

  // Now let's check if the results are the same.
  float diff = 0.0f;
  for (int row = 0; row < ROWS; ++row) {
    for (int col = 0; col < COLS; ++col) {
      int idx = row * COLS + col;
      diff = std::max(diff, std::abs(h_A[idx] - h_C[idx]));
    }
  }
  std::cout << "Max diff = " << diff << std::endl;  // Should be (very close to) zero.

// ------------------------------- Cleaning up ------------------------------ //

  delete timer;

  checkCudaErrors(cudaFree(d_A));
  checkCudaErrors(cudaFree(d_B));
  checkCudaErrors(cudaFree(d_C));

  delete[] h_A;
  delete[] h_B;
  delete[] h_C;

  return 0;
}
