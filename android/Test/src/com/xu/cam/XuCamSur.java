package com.xu.cam;

import com.xu.bitmap.XuBitmapConvert;
import com.xutest.CamTest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class XuCamSur extends SurfaceView implements SurfaceHolder.Callback, Runnable {
	public static final int SCAN_X = 0;//横向扫描
	public static final int SCAN_Y = 1;//纵向扫描
	private SurfaceHolder mHolder;
	// 画布
	private Canvas mCanvas;
	private Paint mPaint;
	private boolean mIsRun=false;
	private boolean mIsDraw=false;
	public boolean isDebug=true;
	private Thread mThread=null;
	private Size mWindowSize=new Size(0, 0);
	private Size mCamPreviewSize=new Size(0, 0);
	public Size imageSize=new Size(0, 0);
	private int scanPosX=0;//x方向扫描位置
	private int scanPosY=0;//y方向扫描位置
	public int scanDir=SCAN_Y;//扫描方向
	public boolean isScan=true;//是否扫描
	private Rect mScan;
	private XuSurfaceCallBack surfaceCallBack;

	private int mFresh=0;
	public int Rotate=90;
	public Face[] FaceRe=null;
	public boolean isFace=true;

	public XuCamSur(Context context,XuSurfaceCallBack surfaceCallBack) {
		super(context);
		this.surfaceCallBack=surfaceCallBack;
		init();
	}

	private void init() {
		this.setZOrderOnTop(true);

		mHolder = this.getHolder();
		mHolder.setFormat(PixelFormat.TRANSLUCENT);
		mHolder.addCallback(this);
		setFocusable(true);
		setFocusableInTouchMode(true);
		this.setKeepScreenOn(true);
		mPaint=new Paint();
		
	}

	@Override
	public void run() {
		while (mIsRun) {
		if(	mIsDraw) {
			draw();
		}
			
		}
	}
	
	public void resume() {
		
	}

	public void pause() {
       mIsRun=false;
    }
public void start(Size size) {
	this.mCamPreviewSize=size;
	mIsDraw = true;
}
	
	private void draw() {
		try {
			mCanvas = mHolder.lockCanvas();
			mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
			Paint(mCanvas, mPaint);

		} finally {
			if (mCanvas != null) {
				mHolder.unlockCanvasAndPost(mCanvas);
			}
		}
	}

	@SuppressLint("WrongCall")
	private void Paint(Canvas canvas, Paint paint) {
		
	
		
		draw_face(canvas,paint);
		
		//绘制扫描线
		draw_scan(canvas,paint);
		//发送绘制信息
		surfaceCallBack.onDraw(canvas, paint);
		
		//绘制调试信息
draw_debug(canvas, paint);
	}
	

	private void draw_face(Canvas canvas, Paint paint) {
		if(FaceRe==null) {
			return;
		}
		int num=0;
		
		 for(FaceDetector.Face face : FaceRe){
			 
		     if (face == null) {
		    	 Log.d("face", "noface");
		         break;
		     }
num++;

		  
		     PointF pointF = new PointF();
		     face.getMidPoint(pointF);
		   canvas.drawCircle(pointF.x, pointF.y, 50, paint);
		  
		 }
		 paint.setTextSize(50);
		 canvas.drawText("num:"+num, 200,200, paint);
	}
	
	private void draw_scan(Canvas canvas, Paint paint) {
		if(isScan) {
			paint.reset();
			paint.setColor(Color.argb(64, 0, 128, 255));
			paint.setStrokeWidth(10);
		if(scanDir==SCAN_Y) {
			scanPosY+=13;
			if(scanPosY>mCamPreviewSize.getWidth()) {
				scanPosY=0;
			}
			canvas.drawLine(0, scanPosY, mCamPreviewSize.getWidth(), scanPosY, paint);
		}else if(scanDir==SCAN_X) {
			scanPosX+=10;
					if(scanPosX>mCamPreviewSize.getHeight()) {
						scanPosX=0;
					}	
					canvas.drawLine(scanPosX, 0, scanPosX, mCamPreviewSize.getWidth(), paint);
		}	
		
		}
	}

	private void draw_debug(Canvas canvas, Paint paint) {
		if(isDebug) {
			int myy=15;
			int myx=15;
			paint.reset();
			paint.setColor(Color.rgb(255, 255, 0));
			paint.setTextSize(18);
			canvas.drawText("debug info:refresh:"+mFresh, myx, myy, paint);
			
			myy+=20;
			canvas.drawText("window width:"+ mWindowSize.getWidth()+" height:"+mWindowSize.getHeight(),myx, myy, paint);
			myy+=20;
			canvas.drawText("cam preview width:"+ mCamPreviewSize.getWidth()+" height:"+mCamPreviewSize.getHeight(),myx, myy, paint);
			myy+=20;
			canvas.drawText("cam max image width:"+ imageSize.getWidth()+" height:"+imageSize.getHeight(),myx, myy, paint);
		}
	}
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		mWindowSize=new Size(arg2, arg3);
		Log.d("cam sur", "顶层surface尺寸改变，width："+arg2+";height:"+arg3);
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		Log.d("cam sur", "创建顶层surface");
		mThread=new Thread(this);
		mIsRun=true;
		mThread.start();
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		Log.d("cam sur", "销毁顶层surface");
		mIsRun = false;
		if(mThread!=null) {
			try {
				mThread.join();
				mThread=null;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	
	

}
