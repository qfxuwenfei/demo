package com.xu.bitmap;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.util.Log;

public class XuBitmap2D {
public static XuBitmap bitmap_to_XuMap(Bitmap bt) {
	   int R, G, B,A;
	int width=bt.getWidth();
	int height=bt.getHeight();
	XuPix pixs[][]=new XuPix[width][height];
	
	int src[]=new int[width*height];
	int dst[][]=new int[width][height];
	bt.getPixels(src, 0, width, 0, 0, width, height);
	int pos;
	int pixColor;
	 for (int y = 0; y < height; y++) {
         for (int x = 0; x < width; x++) {
        	pos = y * width + x;
        	
        	pixColor = src[pos];
        	 R = Color.red(pixColor);
             G = Color.green(pixColor);
             B = Color.blue(pixColor);
             A=Color.alpha(pixColor);
       
             
             pixs[x][y]=new XuPix();
             pixs[x][y].setPix(pixColor);
             pixs[x][y].setWidth(x);
             pixs[x][y].setHeight(y);
            pixs[x][y].setR(R);
            pixs[x][y].setG(G);
            pixs[x][y].setB(B);
            pixs[x][y].setA(A);           
         }
         }
	
	
	XuBitmap bt2d=new XuBitmap(width, height, pixs);
	return bt2d;
}


public static Bitmap XuMap_to_Bitmap(XuBitmap xuMap) {
int	width=xuMap.getWidth();
int height=xuMap.getHeight();
	 Bitmap bt = Bitmap.createBitmap(width, height, Config.ARGB_8888);
	 int R,G,B,A;
	 
	 for (int y = 0; y < height; y++) {
         for (int x = 0; x < width; x++) {
        	 R=xuMap.getPixs()[x][y].getR();
        	G=xuMap.getPixs()[x][y].getG();
        	 B=xuMap.getPixs()[x][y].getB();
        	 A=xuMap.getPixs()[x][y].getA();     
        	
        	 
        	 // 新的ARGB  
             int newColor =Color.argb(A, R, G, B);
          
             bt.setPixel(x, y, newColor);
         }}
	 return bt;
	
}
}
