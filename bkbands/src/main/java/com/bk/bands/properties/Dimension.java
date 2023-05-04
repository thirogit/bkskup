package com.bk.bands.properties;

import java.io.Serializable;

public class Dimension implements Serializable {
    
    public int width;

    public int height;

    public Dimension() {
	this(0, 0);
    }

    public Dimension(Dimension d) {
	this(d.width, d.height);
    }

    public Dimension(int width, int height) {
	this.width = width;
	this.height = height;
    }

    public double getWidth() {
	return width;
    }

    public double getHeight() {
	return height;
    }

    public void setSize(double width, double height) {
	this.width = (int) Math.ceil(width);
	this.height = (int) Math.ceil(height);
    }

    public Dimension getSize() {
	return new Dimension(width, height);
    }	

    public void setSize(Dimension d) {
	setSize(d.width, d.height);
    }	

    public void setSize(int width, int height) {
    	this.width = width;
    	this.height = height;
    }	

    public boolean equals(Object obj) {
	if (obj instanceof Dimension) {
	    Dimension d = (Dimension)obj;
	    return (width == d.width) && (height == d.height);
	}
	return false;
    }

    public int hashCode() {
        int sum = width + height;
        return sum * (sum + 1)/2 + width;
    }

    public String toString() {
	return getClass().getName() + "[width=" + width + ",height=" + height + "]";
    }
}
