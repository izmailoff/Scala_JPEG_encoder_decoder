package com.izmailoff.jpeg;

import java.awt.*;
import java.awt.image.*;

/**
 *  ImageSplitter.java
 *  A component that takes an image input and outputs
 *  its red, green, and blue components as IntMatrix signals.
 *
 *  Input(s):
 *      in - The input image, represented as a WxH array of integers.
 *  Ouput(s):
 *      red - The red component of the image.
 *      green - The green component of the image.
 *      blue - The blue component of the image.
 *
 *  @see ImageCombiner
 *  @author James S. Young, based on original source by Florian Raemy &
 *  LCAV, Swiss Federal Institute of Technology, Lausanne, Switzerland
 */

public class ImageSplitter
{

    private int width = -1;
    private int height = -1;

    public ImageSplitter() {
    }

    public int[][][] split(int[][] image) {
        int[][][] channels = new int[][][]{
                getMaskedArray(image, 0x00ff0000, 16), // R
                getMaskedArray(image, 0x0000ff00, 8), // G
                getMaskedArray(image, 0x000000ff, 0) //B
        };
        return channels;
    }

    private int[][] getMaskedArray(int[][] image, int mask, int offset)
    {
        width = image.length;
        height = image[0].length;

        int r[][] = new int[width][height];

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int value = (image[x][y] & mask) >> offset;
                r[x][y] = value;
                // r[x][y] = (0xff000000 | (value << 16) | (value << 8) | value);
            }
        }

        return r;
    }
}
