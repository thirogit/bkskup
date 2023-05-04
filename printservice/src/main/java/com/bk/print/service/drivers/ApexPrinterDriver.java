package com.bk.print.service.drivers;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.bk.print.service.ImageStrip;
import com.bk.print.service.PrinterDriver;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Created by SG0891787 on 9/22/2017.
 */

public class ApexPrinterDriver implements PrinterDriver {
    private static final String TAG = ApexPrinterDriver.class.getName();
    private static char ESC = 27;
    private static final int PRINTER_MAX_HEAD_WIDTH = 832;

    private OutputStream mStream;

    public ApexPrinterDriver(OutputStream stream) {
        this.mStream = stream;
    }

    private void writeChar(char c) throws IOException {


        if (c <= 255) {
            mStream.write(new byte[]{(byte) c});
        } else {
            mStream.write(new byte[]{(byte) (c >> 8), (byte) (c & 255)});
        }
    }

    private void writeCmd(char c1, char c2) throws IOException {
        writeChar(c1);
        writeChar(c2);
    }

    private void writeCmd(char c, byte b) throws IOException {
        writeChar(c);
        mStream.write(new byte[]{(byte) b});
    }

    private void writeCmd(char c, byte[] bytes) throws IOException {
        writeChar(c);
        mStream.write(bytes);
    }


    private boolean isBlack(int rgbPixelValue) {
        return (rgbPixelValue & 0x00FFFFFF) < 180;
    }

    @Override
    public void writeStrip(ImageStrip strip) throws IOException {

        int height = strip.getHeight();
        int width = strip.getWidth();

        int monoBitmapStride = (width + 31 & -32) / 8;
        int printHeadStride = (PRINTER_MAX_HEAD_WIDTH + 31 & -32) / 8;
        int biSizeImage = monoBitmapStride * height;
        byte[] newBitmapData = new byte[biSizeImage];

        int row;
        int nCol;
        int col;
        int pixel;

        for (row = 0; row < height; ++row) {
            int bitmapLine[] = strip.getLine(row);

            for (nCol = 0; nCol < width; ++nCol) {
                col = 128 >> (nCol & 7);

                pixel = bitmapLine[nCol];
                int R = Color.red(pixel);
                int G = Color.green(pixel);
                int B = Color.blue(pixel);
                int A = Color.alpha(pixel);
                if (A < 128) {
                    R = 255;
                } else {
                    R = (int) (0.299D * (double) R + 0.587D * (double) G + 0.114D * (double) B);
                }

                if (R < 185) {
                    int index = (height - row - 1) * monoBitmapStride + nCol / 8;
                    newBitmapData[index] = (byte) (newBitmapData[index] | col);
                }
            }
        }

        for (row = height - 1; row >= 0; --row) {
            mStream.write(new byte[] { 27, 86, 1, 0 });
            nCol = monoBitmapStride > printHeadStride ? monoBitmapStride : printHeadStride;
            for (col = 0; col < nCol; ++col) {
                pixel = monoBitmapStride < nCol ? row * monoBitmapStride + col : row * nCol + col;
                if (col < printHeadStride) {
                    if (col >= monoBitmapStride) {
                        mStream.write(new byte[] {  0 });
                    } else {
                        mStream.write(new byte[] { newBitmapData[pixel] });
                    }
                }
            }
        }
    }


    @Override
    public void feedPaper(int lines) throws IOException {
        mStream.write(new byte[] { 27, 74, (byte)lines });
    }


}
