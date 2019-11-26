package com.xu.bitmap;

import java.util.ArrayList;

public class XuBitmap {
private int width;
private int height;
private XuPix[][] pixs;

public XuBitmap(int width,int height,XuPix pixs[][]) {
	this.width=width;
	this.height=height;
	this.pixs=pixs;
}

public int getWidth() {
	return width;
}
public void setWidth(int width) {
	this.width = width;
}
public int getHeight() {
	return height;
}
public void setHeight(int height) {
	this.height = height;
}
public XuPix[][] getPixs() {
	return pixs;
}
public void setPixs(XuPix[][] pixs) {
	this.pixs = pixs;
}
}
