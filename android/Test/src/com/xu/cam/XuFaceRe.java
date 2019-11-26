package com.xu.cam;

import android.media.FaceDetector.Face;

public class XuFaceRe{
private int faceNum=0;
private Face[] face;
public int getFaceNum() {
	return faceNum;
}
public void setFaceNum(int faceNum) {
	this.faceNum = faceNum;
}
public Face[] getFace() {
	return face;
}
public void setFace(Face[] face) {
	this.face = face;
}
}
