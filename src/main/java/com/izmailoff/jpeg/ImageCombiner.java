package com.izmailoff.jpeg;

import java.awt.*;
import java.awt.image.*;
/**
 *  ImageCombiner.java
 *  A component that reconstructs an image from its red, green, and blue
 *  color components.
 *
 *  Input(s):
 *      red - The red component of the image.
 *      green - The green component of the image.
 *      blue - The blue component of the image.
 *  Ouput(s):
 *      in - The reconstructed image.
 *
 *  @see ImageSplitter
 *  @author James S. Young, based on original source by Florian Raemy &
 *  LCAV, Swiss Federal Institute of Technology, Lausanne, Switzerland
 */

public class ImageCombiner
{
    public ImageCombiner() {
    }

    int count = 0;

    public void combine(int[][][] RGB) {
            int[][] r = RGB[0];
            int[][] g = RGB[1];
            int[][] b = RGB[2];
            convertRGB(r,g,b);
    }

    private int[][] convertRGB(int[][] R, int[][] G, int[][] B)
    {
        int imageWidth = R.length;
        int imageHeight = R[0].length;

        int array[][] = new int[imageWidth][imageHeight];

        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                array[x][y]= ((0xff << 24) |
                        ((R[x][y] << 16) & 0x00ff0000) |
                        ((G[x][y] << 8) & 0x0000ff00) |
                        (B[x][y] & 0x000000ff));
            }
        }
        return array;
    }
}
