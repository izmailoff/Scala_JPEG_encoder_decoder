package com.izmailoff.jpeg;

/**
 *  Quantize.java
 *  A component that quantizes an image block.
 *
 *  Input(s):
 *      in - Takes an 8x8 matrix of integers as input.
 *      quality - An Integer that specifies the quantization quality. This number is used
 *                in generating a quantization matrix. The default value is 10.
 *  Ouput(s):
 *      out - Outputs an 8x8 matrix of the quantized image block.
 *
 *  @see IntMatrix
 */

public class Quantize
{
    // Block size
    private final static int N = 8;

    // Quantization matrix
    private int[][] quantum = new int[N][N];

    // Quality
    private int quality = 10;

    public Quantize() {
        initMatrix(quality);
        // new inst.InstMathOps (this);
    }

    public void go() {
            int[][] matrix = null; //????
            quantize(matrix);
    }

    private int outputData[][] = new int[N][N];

    public int[][] quantize(int inputData[][])
    {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                double result = (inputData[i][j] / quantum[i][j]);
                outputData[i][j] = (int)(Math.round(result));
            }
        }
        return outputData;
    }

    private void initMatrix(int quality)
    {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                quantum[i][j] = (1 + ((1 + i + j) * quality));
            }
        }
    }
}
