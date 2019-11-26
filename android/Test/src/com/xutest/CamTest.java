package com.xutest;

import java.io.ByteArrayInputStream;

import com.xu.bitmap.XuBitmapConvert;
import com.xu.bitmap.XuBitmapLoader;
import com.xu.cam.XuCam2;
import com.xu.cam.XuCamCallBack;
import com.xu.cam.XuFace;
import com.xu.cam.XuFaceRe;
import com.xu.cam.XuSurfaceCallBack;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CamTest extends Activity {

	private XuCam2 cam;
	private ImageView iv;
private LinearLayout mycam;
Bitmap bt;
	
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);// remove title bar 即隐藏标题栏
	setContentView(R.layout.cam2);
	init();

}

private void init() {
	bt=XuBitmapLoader.loadImg_As("img/my1.jpg",CamTest.this);
	mycam=(LinearLayout)findViewById(R.id.mycam);
	iv=(ImageView)findViewById(R.id.show_iv);
	//iv.setVisibility(View.INVISIBLE);
cam=new XuCam2(CamTest.this,XuCam2.FRONT,new XuCamCallBack() {
	
	@Override
	public void OnTake(Bitmap bitmap) {
		// TODO Auto-generated method stub
		//bitmap=XuBitmap.rotate(bitmap, 270);
		//bitmap=XuBitmap.horverBitmap(bitmap, true, false);
		Log.d("thread", "take:"+Thread.currentThread().getName());
		//bitmap=XuBitmap.RGB888ToRGB565(bitmap);
		iv.setImageBitmap(bitmap);	
	cam.setFace(bitmap);
	}
}, new XuSurfaceCallBack() {
	
	@Override
	public void onDraw(Canvas canvas, Paint paint) {
		// TODO Auto-generated method stub
		
	}
});
cam.isdebug(true);
cam.setRotate(90);
cam.isScan(true);
cam.setScanDir(1);
cam.startTake(true, 1);
mycam.addView(cam);

}

public void paizhao(View v) {	
cam.takePicture();

}


@Override
protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
	cam.resume();

}

@Override
protected void onPause() {
	// TODO Auto-generated method stub
	super.onPause();
	cam.pause();
}

}
