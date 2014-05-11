package com.izmailoff.jpeg;

import javax.imageio.ImageIO;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class ImageWriter {
    private String filename;
    private int[][] data;

    public ImageWriter(String imgFilename, int[][] imgData) {
        filename = imgFilename;
        data = imgData;
    }

    public void saveToFile() throws IOException {
        int width = data.length;
        int height = data[0].length;
        int imageType = ColorModel.getRGBdefault().getColorSpace().getType(); // ??? FIXME // or ColorSpace.CS_sRGB
        BufferedImage img = new BufferedImage(width, height, imageType);
        //img.setPixels somehow
        File file = new File(filename);
        String format = "png"; //"jpg";
        ImageIO.write(img, format, file);
    }

    public void saveToStream() throws IOException {
        OutputStream outStream = new ByteArrayOutputStream();
        //writeHeaders(outStream);
        writeCompressedData(outStream);
        writeEOI(outStream);
    }

    public void writeCompressedData(OutputStream outStream) throws IOException {
        //bufferIt(outStream, );
        for(int i = 0; i < data.length; i++)
            for(int j = 0; j < data[i].length; j++)
            outStream.write(data[i][j]);
    }

    public void writeEOI(OutputStream out) {
        byte[] EOI = {(byte) 0xFF, (byte) 0xD9};
        writeMarker(EOI, out);
    }

    public static int[] jpegNaturalOrder = {
            0,  1,  8, 16,  9,  2,  3, 10,
            17, 24, 32, 25, 18, 11,  4,  5,
            12, 19, 26, 33, 40, 48, 41, 34,
            27, 20, 13,  6,  7, 14, 21, 28,
            35, 42, 49, 56, 57, 50, 43, 36,
            29, 22, 15, 23, 30, 37, 44, 51,
            58, 59, 52, 45, 38, 31, 39, 46,
            53, 60, 61, 54, 47, 55, 62, 63,
    };

    /*
    public void writeHeaders(OutputStream out) {
        int i, j, index, offset, length;
        int tempArray[];

// the SOI marker
        byte[] SOI = {(byte) 0xFF, (byte) 0xD8};
        writeMarker(SOI, out);

// The order of the following headers is quiet inconsequential.
// the JFIF header
        byte JFIF[] = new byte[18];
        JFIF[0] = (byte) 0xff;
        JFIF[1] = (byte) 0xe0;
        JFIF[2] = (byte) 0x00;
        JFIF[3] = (byte) 0x10;
        JFIF[4] = (byte) 0x4a;
        JFIF[5] = (byte) 0x46;
        JFIF[6] = (byte) 0x49;
        JFIF[7] = (byte) 0x46;
        JFIF[8] = (byte) 0x00;
        JFIF[9] = (byte) 0x01;
        JFIF[10] = (byte) 0x00;
        JFIF[11] = (byte) 0x00;
        JFIF[12] = (byte) 0x00;
        JFIF[13] = (byte) 0x01;
        JFIF[14] = (byte) 0x00;
        JFIF[15] = (byte) 0x01;
        JFIF[16] = (byte) 0x00;
        JFIF[17] = (byte) 0x00;
        writeArray(JFIF, out);

// Comment Header
        String comment = new String();
        comment = "this is a comment";
        length = comment.length();
        byte COM[] = new byte[length + 4];
        COM[0] = (byte) 0xFF;
        COM[1] = (byte) 0xFE;
        COM[2] = (byte) ((length >> 8) & 0xFF);
        COM[3] = (byte) (length & 0xFF);
        java.lang.System.arraycopy(comment.getBytes(), 0, COM, 4, comment.length());
        writeArray(COM, out);

// The DQT header
// 0 is the luminance index and 1 is the chrominance index
        byte DQT[] = new byte[134];
        DQT[0] = (byte) 0xFF;
        DQT[1] = (byte) 0xDB;
        DQT[2] = (byte) 0x00;
        DQT[3] = (byte) 0x84;
        offset = 4;
        for (i = 0; i < 2; i++) {
            DQT[offset++] = (byte) ((0 << 4) + i);
            tempArray = (int[]) dct.quantum[i];
            for (j = 0; j < 64; j++) {
                DQT[offset++] = (byte) tempArray[jpegNaturalOrder[j]];
            }
        }
        writeArray(DQT, out);

// Start of Frame Header
        byte SOF[] = new byte[19];
        SOF[0] = (byte) 0xFF;
        SOF[1] = (byte) 0xC0;
        SOF[2] = (byte) 0x00;
        SOF[3] = (byte) 17;
        SOF[4] = (byte) JpegObj.Precision;
        SOF[5] = (byte) ((JpegObj.imageHeight >> 8) & 0xFF);
        SOF[6] = (byte) ((JpegObj.imageHeight) & 0xFF);
        SOF[7] = (byte) ((JpegObj.imageWidth >> 8) & 0xFF);
        SOF[8] = (byte) ((JpegObj.imageWidth) & 0xFF);
        SOF[9] = (byte) JpegObj.NumberOfComponents;
        index = 10;
        for (i = 0; i < SOF[9]; i++) {
            SOF[index++] = (byte) JpegObj.CompID[i];
            SOF[index++] = (byte) ((JpegObj.HsampFactor[i] << 4) + JpegObj.VsampFactor[i]);
            SOF[index++] = (byte) JpegObj.QtableNumber[i];
        }
        writeArray(SOF, out);

// The DHT Header
        byte DHT1[], DHT2[], DHT3[], DHT4[];
        int bytes, temp, oldindex, intermediateindex;
        length = 2;
        index = 4;
        oldindex = 4;
        DHT1 = new byte[17];
        DHT4 = new byte[4];
        DHT4[0] = (byte) 0xFF;
        DHT4[1] = (byte) 0xC4;
        for (i = 0; i < 4; i++ ) {
            bytes = 0;
            DHT1[index++ - oldindex] = (byte) ((int[]) Huf.bits.elementAt(i))[0];
            for (j = 1; j < 17; j++) {
                temp = ((int[]) Huf.bits.elementAt(i))[j];
                DHT1[index++ - oldindex] =(byte) temp;
                bytes += temp;
            }
            intermediateindex = index;
            DHT2 = new byte[bytes];
            for (j = 0; j < bytes; j++) {
                DHT2[index++ - intermediateindex] = (byte) ((int[]) Huf.val.elementAt(i))[j];
            }
            DHT3 = new byte[index];
            java.lang.System.arraycopy(DHT4, 0, DHT3, 0, oldindex);
            java.lang.System.arraycopy(DHT1, 0, DHT3, oldindex, 17);
            java.lang.System.arraycopy(DHT2, 0, DHT3, oldindex + 17, bytes);
            DHT4 = DHT3;
            oldindex = index;
        }
        DHT4[2] = (byte) (((index - 2) >> 8)& 0xFF);
        DHT4[3] = (byte) ((index -2) & 0xFF);
        writeArray(DHT4, out);


// Start of Scan Header
        byte SOS[] = new byte[14];
        SOS[0] = (byte) 0xFF;
        SOS[1] = (byte) 0xDA;
        SOS[2] = (byte) 0x00;
        SOS[3] = (byte) 12;
        SOS[4] = (byte) JpegObj.NumberOfComponents;
        index = 5;
        for (i = 0; i < SOS[4]; i++) {
            SOS[index++] = (byte) JpegObj.CompID[i];
            SOS[index++] = (byte) ((JpegObj.DCtableNumber[i] << 4) + JpegObj.ACtableNumber[i]);
        }
        SOS[index++] = (byte) JpegObj.Ss;
        SOS[index++] = (byte) JpegObj.Se;
        SOS[index++] = (byte) ((JpegObj.Ah << 4) + JpegObj.Al);
        writeArray(SOS, out);

    }

    void bufferIt(OutputStream outStream, int code,int size)
    {
        int PutBuffer = code;
        int PutBits = bufferPutBits;

        PutBuffer &= (1 << size) - 1;
        PutBits += size;
        PutBuffer <<= 24 - PutBits;
        PutBuffer |= bufferPutBuffer;

        while(PutBits >= 8) {
            int c = ((PutBuffer >> 16) & 0xFF);
            try
            {
                outStream.write(c);
            }
            catch (IOException e) {
                System.out.println("IO Error: " + e.getMessage());
            }
            if (c == 0xFF) {
                try
                {
                    outStream.write(0);
                }
                catch (IOException e) {
                    System.out.println("IO Error: " + e.getMessage());
                }
            }
            PutBuffer <<= 8;
            PutBits -= 8;
        }
        bufferPutBuffer = PutBuffer;
        bufferPutBits = PutBits;

    }
    */

    void writeMarker(byte[] data, OutputStream out) {
        try {
            out.write(data, 0, 2);
        } catch (IOException e) {
            System.out.println("IO Error: " + e.getMessage());
        }
    }

    void writeArray(byte[] data, OutputStream out) {
        int i, length;
        try {
            length = (((int) (data[2] & 0xFF)) << 8) + (int) (data[3] & 0xFF) + 2;
            out.write(data, 0, length);
        } catch (IOException e) {
            System.out.println("IO Error: " + e.getMessage());
        }
    }



}
