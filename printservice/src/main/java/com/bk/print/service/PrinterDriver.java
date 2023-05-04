package com.bk.print.service;

import java.io.IOException;

/**
 * Created by SG0891787 on 9/22/2017.
 */

public interface PrinterDriver {
    void writeStrip(ImageStrip strip) throws IOException;
    void feedPaper(int lines) throws IOException;
}
