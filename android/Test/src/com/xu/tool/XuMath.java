package com.xu.tool;

import android.graphics.Point;
import android.graphics.Rect;

public class XuMath {

	public static Point getXB(int x, int y) {
		Point point;
		double xb = Math.sqrt(x * x + y * y);
		double xx = 10 * x / xb;
		double xy = 10 * y / xb;
		point = new Point((int) xx, (int) xy);
		return point;
	}

	public static Point getXY(Point old,Point now){
		Point mydir;
		double myw=now.x-old.x;
		double myh=now.y-old.y;
		double dw,dh;
		double dd=Math.sqrt(myw*myw+myh*myh);
	dw=myw/dd;
	dh=myh/dd;
	mydir=new Point((int)dw,(int)dh);
	return mydir;
	}
	
	public static boolean hit_test(Point now, Rect rect) {
		boolean re = false;
		if (now.x >= rect.left && now.x <= rect.right) {
			if (now.y >= rect.top && now.y <= rect.bottom) {
				re = true;
			}
		}
		return re;
	}

	public static Point rect_sub(Point now, Point old) {
		int x = 0;
		int y = 0;

		x = now.x - old.x;
		y = now.y - old.y;
		return new Point(x, y);
	}
	
	public static Rect rect_add(Rect rect, Point point) {
		int x = rect.left+point.x;
		int y = rect.top+point.y;
int w=rect.right+point.x;
int h=rect.bottom+point.y;
		return new Rect(x, y,w,h);
	}
}
