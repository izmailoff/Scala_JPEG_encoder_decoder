package com.izmailoff.jpeg;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.Point;
import java.awt.Dimension;

/**
 *  ImageInputFile.java
 *
 *  A utility component that reads in an image file and converts it into
 *  an IntMatrix representing that image.
 *  Input(s):
 *      poke - An event on this input causes the component to prompt the user
 *             to select an image file.
 *  Ouput(s):
 *      out - An IntMatrix representing the selected image.
 */

public class ImageInputFile implements ImageObserver
{
    private Image image;
    private String filename;

    public ImageInputFile(String filePath) {
        filename = filePath;
    }

    public int[][] readFile() {
        loadFile(filename);
        if (image != null) { //???
            return convertToMatrix(image);
        } else return null; // FIXME
    }

    public synchronized boolean imageUpdate(Image img, int infoflags, int x, int y,
                                            int width, int height) {
        if ((width != -1) && (height != -1)) {
            notify();
            return false;
        } else {
            return true;
        }
    }

    private synchronized int[][] convertToMatrix(Image image) {
        int width = -1;
        int height = -1;

        while ((width == -1) || (height == -1)) {
            width = image.getWidth(this);
            height = image.getHeight(this);
            try { // WTF????
                wait(1000);
            } catch (InterruptedException e) {}
        }

        int values[] = new int[width * height];
        PixelGrabber grabber = new PixelGrabber(image.getSource(), 0, 0,
                width, height, values, 0, width);

        try {
            if (!grabber.grabPixels()) {
                try {
                    throw new AWTException("Grabber returned false: " + grabber.getStatus());
                } catch (Exception e) {}
            }
        } catch (InterruptedException e) {}

        int matrix[][] = new int[width][height];
        int index = 0;
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                matrix[x][y] = values[index];
                index++;
            }
        }

        return matrix;
    }

    private void loadFile(String filename) {

        int height, width;

        image = Toolkit.getDefaultToolkit().getImage(filename);
        MediaTracker tracker = new MediaTracker(new Frame());
        tracker.addImage(image, 0);
        try {
            tracker.waitForID(0);
        } catch (Exception e) {
            System.err.println("Can't load input image:"+e.toString());
        }
        System.out.println("Loaded image.");
    }


}
