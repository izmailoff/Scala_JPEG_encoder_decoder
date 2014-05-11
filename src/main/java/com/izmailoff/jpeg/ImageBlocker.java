package com.izmailoff.jpeg;

/**
 *  ImageBlocker.java
 *  A component that converts an image into a serial stream of 8x8 image blocks. The
 *  image can then be reconstructed using the ImageUnblocker component.
 *
 *  Input(s):
 *      image in - An WxH matrix of integers representing an image.
 *  Ouput(s):
 *      block out - An 8x8 matrix of integers. The first block emitted corresponds to
 *                  the upper-left block of the image. The remainder of the image is
 *                  scanned out in left-right, top-bottom order. Thus, the final block
 *                  corresponds to the bottom-right block of the image.
 *      width - The width of the input image.
 *      height - The height of the input image.
 *      done - A Boolean that is set true after the final block has been emitted.
 *
 *  @see IntMatrix
 *  @see ImageUnblocker
 */

import java.util.LinkedList;
import java.util.List;

/**
 * @author James S. Young, based on original source by Florian Raemy &
 *         LCAV, Swiss Federal Institute of Technology, Lausanne, Switzerland
 */

public class ImageBlocker {
    private int[][] image;

    public ImageBlocker(int[][] imageData) {
        image = imageData;
    }

    public int [][][] splitToBlocks() {

        int [][] block = new int [8][8];
        int width = image.length;
        int height = image[0].length;

        int blockW = width / 8;
        int blockH = height / 8;

        int numBlocks = blockW * blockH;
        int [][][] blocks = new int[numBlocks][8][8];
        int blockIdx = 0;

        for (int i = 0; i < blockH; i++) {
            for (int j = 0; j < blockW; j++) {
                int xpos = j * 8;
                int ypos = i * 8;

                for (int a = 0; a < 8; a++) {
                    for (int b = 0; b < 8; b++) {
                        block[a][b] = image[xpos + a][ypos + b];
                    }
                }

                blocks[blockIdx++] = block;
            }
        }

        return blocks;
    }
}
