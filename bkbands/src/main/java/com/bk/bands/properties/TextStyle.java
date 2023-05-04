package com.bk.bands.properties;


import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/6/12
 * Time: 9:01 PM
 */
public class TextStyle implements Serializable {

    private Color textColor;

    private HorizontalAlign horAlign;

    private VerticalAlign verAlign;

    private Font font;

    private Color backColor;


    public TextStyle clone()
    {
        TextStyle copy = new TextStyle();
        copy.setHorAlign(horAlign);
        copy.setVerAlign(verAlign);
        copy.setBackColor(backColor);
        copy.setFont(font.clone());
        copy.setTextColor(textColor);

        return copy;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public HorizontalAlign getHorAlign() {
        return horAlign;
    }

    public void setHorAlign(HorizontalAlign horAlign) {
        this.horAlign = horAlign;
    }

    public VerticalAlign getVerAlign() {
        return verAlign;
    }

    public void setVerAlign(VerticalAlign verAlign) {
        this.verAlign = verAlign;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Color getBackColor() {
        return backColor;
    }

    public void setBackColor(Color backColor) {
        this.backColor = backColor;
    }
}
