package com.bk.bands.serializer;

import com.bk.bands.paper.Paper;
import com.bk.bands.properties.Dimension;
import com.bk.bands.properties.Point;

import java.io.Serializable;


/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/26/2014
 * Time: 8:18 PM
 */
public abstract class FieldBean implements Serializable {

    private Point position = new Point();
    private Dimension size = new Dimension();

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public Dimension getSize() {
        return size;
    }

    public void setSize(Dimension size) {
        this.size = size;
    }

    public abstract void print(Paper paper);

}
