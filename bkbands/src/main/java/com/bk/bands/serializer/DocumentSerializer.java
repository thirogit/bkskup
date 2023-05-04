package com.bk.bands.serializer;

import com.bk.bands.print.FieldVisitor;
import com.bk.bands.properties.Border;
import com.bk.bands.properties.Dimension;
import com.bk.bands.properties.Point;
import com.bk.bands.properties.Rectangle;
import com.bk.bands.runtime.BandsException;
import com.bk.bands.properties.TextStyle;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/26/2014
 * Time: 9:57 PM
 */
public class DocumentSerializer implements FieldVisitor {

    private Stack<Point> viewPortStack = new Stack<Point>();
    private List<FieldBean> fields = new LinkedList<FieldBean>();

    public List<FieldBean> getFields() {
        return fields;
    }

    @Override
    public void rectangle(Rectangle rect, Border border) throws BandsException {

        Point viewPortOrigin = calculateViewPort();

        Point position = new Point(viewPortOrigin.x + rect.x,viewPortOrigin.y + rect.y);
        Dimension dimension = rect.getSize();

        RectangleBean rectBean = new RectangleBean();
        rectBean.setPosition(position);
        rectBean.setSize(dimension);
        if(border != null) {
            rectBean.setBorder(border.clone());
        }

        fields.add(rectBean);

    }

    @Override
    public void text(String text, TextStyle style, Rectangle rect, Border border) throws BandsException {
        Point viewPortOrigin = calculateViewPort();

        Point position = new Point(viewPortOrigin.x + rect.x,viewPortOrigin.y + rect.y);
        Dimension dimension = rect.getSize();

        TextBean textBean = new TextBean();
        textBean.setPosition(position);
        textBean.setSize(dimension);
        if(border != null) {
            textBean.setBorder(border.clone());
        }
        textBean.setStyle(style.clone());
        textBean.setText(text);
        fields.add(textBean);
    }

    @Override
    public void oval(Rectangle rect, Border border) throws BandsException {
        Point viewPortOrigin = calculateViewPort();

        Point position = new Point(viewPortOrigin.x + rect.x,viewPortOrigin.y + rect.y);
        Dimension dimension = rect.getSize();

        OvalBean ovalBean = new OvalBean();
        ovalBean.setPosition(position);
        ovalBean.setSize(dimension);
        if(border != null) {
            ovalBean.setBorder(border.clone());
        }

        fields.add(ovalBean);
    }

    public Point popViewPort() {
        return viewPortStack.pop();
    }

    public void pushViewPort(Point viewPort) {
        viewPortStack.push(new Point(viewPort));
    }

    private Point calculateViewPort() {
        Point viewPort = new Point(0, 0);
        for (Point newOrigin : viewPortStack) {
            viewPort.translate(newOrigin.x, newOrigin.y);
        }
        return viewPort;
    }

}
