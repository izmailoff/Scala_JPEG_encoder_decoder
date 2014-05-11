package com.izmailoff.jpeg;

import java.lang.*;
import java.util.*;

/**
 *  DCT.java
 *  A component that performs a Discrete Cosine Transform.
 *
 *  Input(s):
 *      in - Takes an 8x8 matrix of integers as input.
 *  Ouput(s):
 *      out - Outputs an 8x8 matrix of integers that is the DCT transform of the input.
 */

public class DCT {
    // Block size
    private final static int N = 8;

    // DCT coefficients
    private double[][] c;

    public DCT() {
        c = new double[N][N];
        initMatrix();
    }

    int output[][] = new int[N][N];
    double temp[][] = new double[N][N];
    double temp1;

    public int[][] forwardDCT(int input[][])
    {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                temp[i][j] = 0.0;
                for (int k = 0; k < N; k++) {
                    temp[i][j] += (((int)(input[i][k]) - 128) * c[j][k]);
                }
            }
        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                temp1 = 0.0;

                for (int k = 0; k < N; k++) {
                    temp1 += (c[i][k] * temp[k][j]);
                }

                output[i][j] = (int)Math.round(temp1);
            }
        }

        return output;
    }

    private void initMatrix()
    {
        for (int i = 0; i < N; i++) {
            double nn = (double)(N);
            c[0][i]  = 1.0 / Math.sqrt(nn);
        }

        for (int i = 1; i < N; i++) {
            for (int j = 0; j < N; j++) {
                double jj = (double)j;
                double ii = (double)i;
                c[i][j]  = Math.sqrt(2.0/8.0) * Math.cos(((2.0 * jj + 1.0) * ii * Math.PI) / (2.0 * 8.0));
            }
        }
    }
}
