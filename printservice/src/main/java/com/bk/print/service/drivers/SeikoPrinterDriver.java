package com.bk.print.service.drivers;

import com.bk.print.service.ImageStrip;
import com.bk.print.service.PrinterDriver;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Created by SG0891787 on 9/22/2017.
 */

public class SeikoPrinterDriver implements PrinterDriver {

    private static final int PRINTER_MAX_X_RESOLUTION = 832;

    private OutputStream mStream;

    public SeikoPrinterDriver(OutputStream stream) {
        this.mStream = stream;
    }





    @Override
    public void writeStrip(ImageStrip strip) throws IOException {

        byte setLSBCmd[] = { 0x12, 0x3d, 0 };
        byte rasterBitImgPrintCmd[] = { 0x1d, 0x76, 0x30, 0, 0, 0, 0, 0 };

        int xDots = strip.getWidth();
        int yDots = strip.getHeight();


        if ((xDots >= 0x10000) || (yDots > 0x10000))
        {
            throw new IOException("image_too_big");
        }

        int lineBytes = (xDots + 7) / 8;

        int yMaxChunkDots = 0x2080 / lineBytes;
        if (yMaxChunkDots > 400)
        {
            yMaxChunkDots = 400;
        }

        int yChunks = ((yDots + yMaxChunkDots) - 1) / yMaxChunkDots;

        byte lineDots[] = new byte[lineBytes];


        int y = 0;
        CHUNK_LOOP:for (int iChunk = 0; iChunk < yChunks; iChunk++)
        {
            int yChunkDots = (yDots > yMaxChunkDots) ? yMaxChunkDots : yDots;
            rasterBitImgPrintCmd[4] = (byte)(lineBytes % 0x100);
            rasterBitImgPrintCmd[5] = (byte)(lineBytes / 0x100);
            rasterBitImgPrintCmd[6] = (byte)(yChunkDots % 0x100);
            rasterBitImgPrintCmd[7] = (byte)(yChunkDots / 0x100);

            mStream.write(setLSBCmd);
            mStream.write(rasterBitImgPrintCmd);

            for (int j = iChunk * yMaxChunkDots; j < ((iChunk * yMaxChunkDots) + yChunkDots); j++)
            {
                Arrays.fill(lineDots, (byte) 0);
                int[] bitmapLine = strip.getLine(j);

                for(int jj = 0;jj < xDots && jj < PRINTER_MAX_X_RESOLUTION;jj++)
                {
                    int bit = jj%8;
                    if((bitmapLine[jj] & 0x00FFFFFF) < 180)
                    {
                        lineDots[jj/8] |= (1 << bit);
                    }
                }
                mStream.write(lineDots);
                y++;
            }
            yDots -= yMaxChunkDots;
        }



        }

    @Override
    public void feedPaper(int lines) throws IOException {
        byte feedPaperCmd[] = {0x1b, 0x4a, (byte)lines};
        mStream.write(feedPaperCmd);
    }
}
