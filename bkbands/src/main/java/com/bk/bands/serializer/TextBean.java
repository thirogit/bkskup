package com.bk.bands.serializer;

import com.bk.bands.paper.Paper;
import com.bk.bands.properties.*;
import com.google.common.base.Strings;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/26/2014
 * Time: 8:33 PM
 */
public class TextBean extends FieldBean {

    private String text;
    private Border border;
    private TextStyle style;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Border getBorder() {
        return border;
    }

    public void setBorder(Border border) {
        this.border = border;
    }

    public TextStyle getStyle() {
        return style;
    }

    public void setStyle(TextStyle style) {
        this.style = style;
    }

    @Override
    public void print(Paper paper) {
        paper.drawText(Strings.nullToEmpty(text),getStyle(),new Rectangle(getPosition(),getSize()),getBorder());
    }
}
