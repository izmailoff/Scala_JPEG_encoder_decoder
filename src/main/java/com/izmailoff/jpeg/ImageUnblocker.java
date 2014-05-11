package com.izmailoff.jpeg;

/**
 *  ImageUnblocker.java
 *  A component that reconstructs a single image from a stream of 8x8 image blocks.
 *
 *  Input(s):
 *      block in - An 8x8 matrix of integers representing an image block.
 *      width - The width of the input image.
 *      height - The height of the input image.
 *      done - A Boolean that is set true after the final block has been received.
 *  Ouput(s):
 *      image out - A WxH matrix representing the reconstructed image.
 *
 *  @see IntMatrix
 *  @see ImageBlocker
 */

/**
 * @author James S. Young, based on original source by Florian Raemy &
 *         LCAV, Swiss Federal Institute of Technology, Lausanne, Switzerland
 */

public class ImageUnblocker {
    private int[][] image;

    public ImageUnblocker() {
    }

    public int[][] combineBlocks() {

        int width = 0; //get from image ???
        int height = 0; // ????

        image = new int[width][height];
        int[][] block = new int[8][8];
        int xpos = 0;
        int ypos = 0;
        int blockCount = 0;

        block = null;//readsomehow;

        for (int a = 0; a < 8; a++) {
            for (int b = 0; b < 8; b++) {
                image[xpos + a][ypos + b] = block[a][b];
            }
        }

        xpos = xpos + 8;
        if (xpos >= image.length) {
            xpos = 0;
            ypos = (ypos + 8);
            if (ypos >= image[0].length) {
                ypos = 0;
            }
        }

        blockCount++;

        return image;
    }
}