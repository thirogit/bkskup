package com.bk.bands.properties;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/6/12
 * Time: 8:19 PM
 */
public class Border implements Serializable {
    private int lineWidth;
    private Color lineColor;

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    public Color getLineColor() {
        return lineColor;
    }

    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }

    public Border clone() {
        Border copy = new Border();
        copy.setLineColor(lineColor);
        copy.setLineWidth(lineWidth);
        return copy;
    }
}
