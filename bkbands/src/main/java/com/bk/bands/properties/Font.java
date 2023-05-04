package com.bk.bands.properties;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/7/12
 * Time: 12:28 AM
 */
public class Font implements Serializable {
    protected int height;
    protected String face;
    protected boolean bold;
    protected boolean italic;
    protected boolean underline;
    protected boolean strikeout;

    public Font clone() {
        Font copy = new Font();
        copy.setBold(bold);
        copy.setFaceName(face);
        copy.setHeight(height);
        copy.setItalic(italic);
        copy.setStrikeout(strikeout);
        copy.setUnderline(underline);
        return copy;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getFaceName() {
        return face;
    }

    public void setFaceName(String face) {
        this.face = face;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public boolean isUnderline() {
        return underline;
    }

    public void setUnderline(boolean underline) {
        this.underline = underline;
    }

    public boolean isStrikeout() {
        return strikeout;
    }

    public void setStrikeout(boolean strikeout) {
        this.strikeout = strikeout;
    }
}
