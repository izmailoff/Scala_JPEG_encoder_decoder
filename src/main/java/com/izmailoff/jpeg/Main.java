package com.izmailoff.jpeg;

import java.io.IOException;
import java.util.List;

// http://embedded.eecs.berkeley.edu/research/javatime/projects/jpeg/jt_jpeg/
public class Main {

    public static void main(String[] args) throws IOException {
        ImageInputFile imageData = new ImageInputFile("/tmp/rgbimage.png");
        int [][] image = imageData.readFile();
        System.out.println(image.length + " by " + image[0].length);

        ImageSplitter splitter = new ImageSplitter();
        int[][][] rgb = splitter.split(image);

        ImageBlocker blocker = new ImageBlocker(rgb[0]); // neeed to loop for each channel
        int[][][] blocks = blocker.splitToBlocks();

        ImageWriter writer = new ImageWriter("/tmp/fuckit.png", image);
        writer.saveToFile();
        /*
        // process blocks somehow
        List<Integer[][]> processedBlocks = blocks;
        ImageUnblocker unblocker = new ImageUnblocker(); // pass blocks back somehow
        int[][] channelBlocks = unblocker.combineBlocks();

        ImageCombiner combiner = new ImageCombiner();
        jpegImage = combiner.combine(channelBlocks *** 3);
        */

    }

}
