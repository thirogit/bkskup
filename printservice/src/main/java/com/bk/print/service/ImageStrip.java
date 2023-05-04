package com.bk.print.service;

import java.util.Arrays;

/**
 * Created by SG0891787 on 12/30/2017.
 */

public class ImageStrip {
    private int width;
    private int height;
    private int pixels[];

    public ImageStrip(int width, int height, int[] pixels) {
        this.width = width;
        this.height = height;
        this.pixels = pixels;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getLine(int line) {

        if(line >= height)
            return new int[] {};

        int fromIndex = line * width;
        int toIndex = fromIndex+width;
        return Arrays.copyOfRange(pixels, fromIndex,toIndex);
    }


}
