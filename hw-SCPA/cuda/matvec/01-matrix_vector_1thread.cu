// 
// Author: Salvatore Filippone salvatore.filippone@cranfield.ac.uk
//

// Computes matrix-vector product. Matrix A is in row-major order
// i.e. A[i, j] is stored in i * COLS + j element of the vector.
//

#include <iostream>

#include <cuda_runtime.h>  // For CUDA runtime API
#include <helper_cuda.h>   // For checkCudaError macro
#include <helper_timer.h>  // For CUDA SDK timers

// Matrix dimensions.
const int ROWS = 4096;
const int COLS = 4096;

// TODO(later) Play a bit with the block size.
// Note: For meaningful time measurements you need sufficiently large matrix.
const dim3 BLOCK_DIM = 256;

// Simple CPU implementation of matrix-vector product.
void CpuMatrixVector(int rows, int cols, const float* A, const float* x, float* y) {
  for (int row = 0; row < rows; ++row) {
    float t=0.0;
    for (int col = 0; col < cols; ++col) {
      int idx = row * cols + col;
      t += A[idx] * x[col];
    }
    y[row] = t;

  }
}

// FANFA: GPU implementation of matrix_vector product: see if you can use
// one thread per row. You'll need to get the addressing right!
__global__ void gpuMatrixVector(int rows, int cols, const float* A,
				const float* x, float* y) {

      if( threadIdx.x == 0 && blockIdx.x == 0 ) {
        int idx;        //iteration counter && vector x index
        int matrixIdx;  //matrix index
        float t=0.0;    //result to be registered into array y

        for(idx=0; idx<cols; idx++) {
          matrixIdx = idx + cols * blockIdx.y;  //cols sarebbe blockDim.x * gridDim.x
          t += A[matrixIdx] * x[idx];

        }
        y[ blockIdx.y ] = t;

      }
  
}

int main(int argc, char** argv) {

// ----------------------- Host memory initialisation ----------------------- //

  float* h_A = new float[ROWS * COLS];
  float* h_x = new float[COLS];
  float* h_y = new float[ROWS];
  float* h_y_d = new float[ROWS];

  srand(time(0));
  for (int row = 0; row < ROWS; ++row) {
    for (int col = 0; col < COLS; ++col) {
      int idx = row * COLS + col;
      h_A[idx] = 100.0f * static_cast<float>(rand()) / RAND_MAX;
    }
    h_x[row] = 100.0f * static_cast<float>(rand()) / RAND_MAX;
  }

// ---------------------- Device memory initialisation ---------------------- //

  // FANFA: Allocate global memory on the GPU.
  float *d_A, *d_x, *d_y;
  checkCudaErrors(cudaMalloc((void **)&d_A, ROWS*COLS*sizeof(float)));
  checkCudaErrors(cudaMalloc((void **)&d_x, COLS*sizeof(float)));
  checkCudaErrors(cudaMalloc((void **)&d_y, ROWS*sizeof(float)));

  // FANFA: Copy matrices from the host (CPU) to the device (GPU).
  checkCudaErrors(cudaMemcpy(d_A, h_A, ROWS*COLS*sizeof(float), cudaMemcpyHostToDevice));
  checkCudaErrors(cudaMemcpy(d_x, h_x, COLS*sizeof(float), cudaMemcpyHostToDevice));
  checkCudaErrors(cudaMemcpy(d_y, h_y, ROWS*sizeof(float), cudaMemcpyHostToDevice));

// ------------------------ Calculations on the CPU ------------------------- //

  // Create the CUDA SDK timer.
  StopWatchInterface* timer = 0;
  sdkCreateTimer(&timer);

  timer->start();
  CpuMatrixVector(ROWS, COLS, h_A, h_x, h_y);

  timer->stop();
  std::cout << "CPU time: " << timer->getTime() << " ms." << std::endl;

// ------------------------ Calculations on the GPU ------------------------- //

  // FANFA: Calculate the dimension of the grid of blocks (2D).
  int grid_dim_1 = COLS/256;
  int grid_dim_2 = ROWS;
  const dim3 GRID_DIM(grid_dim_1, grid_dim_2);

  timer->reset();
  timer->start();
  gpuMatrixVector<<<GRID_DIM, BLOCK_DIM>>>(ROWS, COLS, d_A, d_x, d_y);
  checkCudaErrors(cudaDeviceSynchronize());

  timer->stop();
  std::cout << "GPU time: " << timer->getTime() << " ms." << std::endl;

  // FANFA: Download the resulting vector d_y from the device and store it in h_y_d.
  checkCudaErrors(cudaMemcpy(h_y_d, d_y, ROWS*sizeof(float), cudaMemcpyDeviceToHost));

  // Now let's check if the results are the same.
  float diff = 0.0f;
  for (int row = 0; row < ROWS; ++row) {
    diff = std::max(diff, std::abs(h_y[row] - h_y_d[row]));
  }
  std::cout << "Max diff = " << diff << std::endl;  // Should be (very close to) zero.

// ------------------------------- Cleaning up ------------------------------ //

  delete timer;

  checkCudaErrors(cudaFree(d_A));
  checkCudaErrors(cudaFree(d_x));
  checkCudaErrors(cudaFree(d_y));

  delete[] h_A;
  delete[] h_x;
  delete[] h_y;
  delete[] h_y_d;
  return 0;
}
