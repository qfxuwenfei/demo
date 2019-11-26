package com.xu.cam;

import android.graphics.Bitmap;
import android.media.FaceDetector;

public class XuFace {
	FaceDetector mFaceDetector;
	FaceDetector.Face[] mFace;
public XuFace(int width,int height,int face) {
	mFaceDetector=new FaceDetector(width, height, face);
mFace = new FaceDetector.Face[face];
}

public XuFaceRe getFace(Bitmap bitmap) {
	 mFaceDetector.findFaces(bitmap, mFace);
	 int num=0;
	 for(FaceDetector.Face face : mFace){
	     if (face != null) {
	      num++;
	     }else {
	    	   break; 
	     }
	
	 }
	 XuFaceRe faceRe=new XuFaceRe();
	 faceRe.setFaceNum(num);
	 faceRe.setFace(mFace);
     return faceRe;
}

}

