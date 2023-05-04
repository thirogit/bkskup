package com.bk.bands.serializer;

import com.bk.bands.paper.Paper;
import com.bk.bands.properties.Border;
import com.bk.bands.properties.Rectangle;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/26/2014
 * Time: 10:10 PM
 */
public class OvalBean extends FieldBean {
    private Border border;

    public Border getBorder() {
        return border;
    }

    public void setBorder(Border border) {
        this.border = border;
    }

    @Override
    public void print(Paper paper) {
        paper.drawOval(new Rectangle(getPosition(),getSize()), getBorder());
    }
}
