package com.xu.cam;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.FaceDetector;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.widget.FrameLayout;

/**
 * use 自定义相机Camera2 author 孙尚磊 create time 2017-4-25
 */
public class XuCam2 extends FrameLayout {
	private static final int MAX_PREVIEW_WIDTH = 1920;

	/**
	 * Max preview height that is guaranteed by Camera2 API
	 */
	private static final int MAX_PREVIEW_HEIGHT = 1080;

	public final static int FRONT = CameraCharacteristics.LENS_FACING_FRONT;// 前置摄像头
	public final static int BACK = CameraCharacteristics.LENS_FACING_BACK;// 后置摄像头
	private int front_back = FRONT;
	private static final int STATE_PREVIEW = 0;
	/**
	 * Camera state: Waiting for the focus to be locked.
	 */
	private static final int STATE_TAKEPHOTO = 1;
	private int mState = STATE_PREVIEW;
	Context context;
	private XuAutoFitTextureView tv;
	private XuCamSur sv;
	private String mCameraId = "0";// 摄像头id（通常0代表后置摄像头，1代表前置摄像头）
	private final int RESULT_CODE_CAMERA = 1;// 判断是否有拍照权限的标识码
	private CameraDevice cameraDevice;
	private CameraCaptureSession mPreviewSession;
	private CaptureRequest.Builder mCaptureRequestBuilder, captureRequestBuilder;
	private CaptureRequest mCaptureRequest;
	private ImageReader imageReader;
	// public int height=0,width=0;
	public Size previewSize;
	private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
	private Semaphore mCameraOpenCloseLock = new Semaphore(1);// 锁定
	CameraManager manager;
	private HandlerThread mBackgroundThread;
	private Handler mBackgroundHandler;
	XuCamCallBack xuCamCallBack;

	private  int mTakeTime=3000;
	private Thread mTakeThread;
	private Boolean mIsTake=true;
	private SurfaceHolder sh;
	Size largest;

	static {
		ORIENTATIONS.append(Surface.ROTATION_0, 90);
		ORIENTATIONS.append(Surface.ROTATION_90, 0);
		ORIENTATIONS.append(Surface.ROTATION_180, 270);
		ORIENTATIONS.append(Surface.ROTATION_270, 180);
	}

	public XuCam2(Context context, int front_back, XuCamCallBack xuCamCallBack, XuSurfaceCallBack xuSurfaceCallBack) {
		super(context);
		this.context = context;
		this.front_back = front_back;
		this.xuCamCallBack = xuCamCallBack;
		init(xuSurfaceCallBack);
	}

	private void init(XuSurfaceCallBack xuSurfaceCallBack) {
		this.tv = new XuAutoFitTextureView(context);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		tv.setLayoutParams(lp);
		this.addView(tv);
		this.sv = new XuCamSur(context, xuSurfaceCallBack);
		this.sv.setLayoutParams(lp);

		this.addView(sv);

		start();
	}

	public void start() {
		manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
		Log.d("start", "ok");

	}

	// 111111111111111111111111111111111获取摄像头参数
	private void get_par(int width, int height) {
		getCameraId();
		try {
			// 获取指定摄像头的特性
			CameraCharacteristics characteristics = manager.getCameraCharacteristics(mCameraId);
			// 获取摄像头支持的配置属性
			StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
			// 获取摄像头支持的最大尺寸
			largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());

			// 创建一个ImageReader对象，用于获取摄像头的图像数据
			Log.d("get_par", "big:" + largest.toString());

			previewSize = chooseOptimalSize(map.getOutputSizes(ImageFormat.JPEG), width, height, MAX_PREVIEW_WIDTH,
					MAX_PREVIEW_HEIGHT, largest);
			tv.setAspectRatio(previewSize.getHeight(), previewSize.getWidth());

			Log.d("get_par", "texview 尺寸" + tv.getWidth() + ":" + tv.getHeight());
			Log.d("get_par", "最优显示尺寸" + previewSize.toString());

		} catch (CameraAccessException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
		}

	}

	// 2222222222222222222222222222222启动摄像头
	public void resume() {
		Log.d("app", "resum");

		// 如果不存在相机
		if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			return;
		}

		startBackgroundThread();
		if (tv.isAvailable()) {
			Log.d("resum", "open cam");
			if (cameraDevice == null) {
				openCamera(tv.getWidth(), tv.getHeight());
			}
		} else {
			Log.d("resum", "add tex listenner");

			tv.setSurfaceTextureListener(surfaceTextureListener);

		}
	}

	////// 333333333333333333333333
	/** 打开摄像头 */
	private void openCamera(int width, int height) {

		// 设置摄像头特性
		get_par(width, height);

		Log.d("openCamera", "window width:" + width + ":height:" + height);
		sv.imageSize = largest;
		imageReader = ImageReader.newInstance(previewSize.getWidth(), previewSize.getHeight(), ImageFormat.JPEG, 2);
		Log.d("openCamera", "image width:" + largest.getWidth() + ":height:" + largest.getHeight());
		// 设置获取图片的监听
		imageReader.setOnImageAvailableListener(imageAvailableListener, mBackgroundHandler);
		try {
			manager.openCamera(mCameraId, stateCallback, mBackgroundHandler);
			Log.d("openCamera", "ok");
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("openCamera", "no");
		}
	}

	private void startBackgroundThread() {
		mBackgroundThread = new HandlerThread("CameraBackground");
		mBackgroundThread.start();
		mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
		mTakeThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.d("takeThread", "已开启拍照线程");
				while (true) {
					try {
						Thread.sleep(mTakeTime);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
if(mIsTake) {
	try {
Bitmap bitmap=	tv.getBitmap();
xuCamCallBack.OnTake(bitmap);
	//takePicture();
}catch(Exception e) {
	Log.d("bug", e.toString());
}
}
				}
			}
		});
		mTakeThread.start();
	}

	public void pause() {
		Log.d("app", "pause");
		sv.pause();
		stopCamera();
		stopBackgroundThread();
	}

	private void configureTransform(int viewWidth, int viewHeight) {

		if (null == tv || null == previewSize) {
			return;
		}

		Matrix matrix = new Matrix();
		RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
		RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
		float centerX = viewRect.centerX();
		float centerY = viewRect.centerY();

		bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
		matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
		float scale = Math.max((float) viewHeight / previewSize.getHeight(),
				(float) viewWidth / previewSize.getWidth());
		matrix.postScale(scale, scale, centerX, centerY);

		tv.setTransform(matrix);
		Log.d("configureTransform", "tv size:" + tv.getWidth() + ":" + tv.getHeight());
	}

	/** TextureView的监听 */
	private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {

		// 可用
		@Override
		public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
			Log.d("surfaceTextureListener Available", "width:" + width + ":height:" + height);
			openCamera(width, height);
		}

		@Override
		public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
			Log.d("surfaceTextureListener resize", "width:" + width + ":height:" + height);
			configureTransform(width, height);

		}

		// 释放
		@Override
		public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
			stopCamera();
			return true;
		}

		// 更新
		@Override
		public void onSurfaceTextureUpdated(SurfaceTexture surface) {

		}
	};

	// 得到摄像头
	private void getCameraId() {
		try {
			// 循环camlist
			for (String camid : manager.getCameraIdList()) {
				// 获取相机特性
				CameraCharacteristics cc = manager.getCameraCharacteristics(camid);
				int mycam = cc.get(CameraCharacteristics.LENS_FACING);
				if (mycam == front_back) {
					mCameraId = camid;
					Log.d("getcam", mCameraId);
					break;
				}

			}
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}

	private void stopBackgroundThread() {
		mIsTake=false;
		if(mTakeThread!=null) {
			mTakeThread=null;
		}
		mBackgroundThread.quitSafely();
		try {
			mBackgroundThread.join();
			mBackgroundThread = null;
			mBackgroundHandler = null;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// 为Size定义一个比较器Comparator
	static class CompareSizesByArea implements Comparator<Size> {
		@Override
		public int compare(Size lhs, Size rhs) {
			// 强转为long保证不会发生溢出
			return Long.signum((long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
		}
	}

	/** 摄像头状态的监听 */
	private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
		// 摄像头被打开时触发该方法
		@Override
		public void onOpened(CameraDevice cameraDevice) {
			XuCam2.this.cameraDevice = cameraDevice;
			// 开始预览
			Log.d("camera stateCallback", "opened");
			takePreview();
		}

		// 摄像头断开连接时触发该方法
		@Override
		public void onDisconnected(CameraDevice cameraDevice) {
			Log.d("stateCallback", "disconnected");
			stopCamera();

		}

		// 打开摄像头出现错误时触发该方法
		@Override
		public void onError(CameraDevice cameraDevice, int error) {
			Log.d("stateCallback", "error");
			stopCamera();

		}
	};

	/**
	 * 开始预览
	 */
	private void takePreview() {
		Log.d("takePreview", "start preview");
		SurfaceTexture mSurfaceTexture = tv.getSurfaceTexture();
		// 设置TextureView的缓冲区大小
		mSurfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());

		Log.d("takePreview", "set preview buffer:" + previewSize.toString());
		// 获取Surface显示预览数据
		Surface mSurface = new Surface(mSurfaceTexture);
		
		try {
			// 创建预览请求
			mCaptureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
			// 设置自动对焦模式
			mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
					CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
			// 设置Surface作为预览数据的显示界面
			mCaptureRequestBuilder.addTarget(mSurface);

			// mCaptureRequestBuilder.addTarget(imageReader.getSurface());// 预览流
			// 输出到imagereader

			// 创建相机捕获会话，第一个参数是捕获数据的输出Surface列表，第二个参数是CameraCaptureSession的状态回调接口，当它创建好后会回调onConfigured方法，第三个参数用来确定Callback在哪个线程执行，为null的话就在当前线程执行
			cameraDevice.createCaptureSession(Arrays.asList(mSurface, imageReader.getSurface()),
					new CameraCaptureSession.StateCallback() {
						@Override
						public void onConfigured(CameraCaptureSession session) {
							try {

								// 开始预览
								mCaptureRequest = mCaptureRequestBuilder.build();
								mPreviewSession = session;
								// 设置反复捕获数据的请求，这样预览界面就会一直有数据显示
								mPreviewSession.setRepeatingRequest(mCaptureRequest, null, mBackgroundHandler);
								sv.start(previewSize);
								Log.d("takePreview", "preview config ok");
							} catch (CameraAccessException e) {
								e.printStackTrace();
							}

						}

						@Override
						public void onConfigureFailed(CameraCaptureSession session) {

						}
					}, mBackgroundHandler);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}

	}

	private Size chooseOptimalSize(Size[] choices, int textureViewWidth, int textureViewHeight, int maxWidth,
			int maxHeight, Size aspectRatio) {

		// Collect the supported resolutions that are at least as big as the preview
		// Surface
		List<Size> bigEnough = new ArrayList<>();
		// Collect the supported resolutions that are smaller than the preview Surface
		List<Size> notBigEnough = new ArrayList<>();
		int w = aspectRatio.getWidth();
		int h = aspectRatio.getHeight();
		for (Size option : choices) {
			if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight
					&& option.getHeight() == option.getWidth() * h / w) {
				if (option.getWidth() >= textureViewWidth && option.getHeight() >= textureViewHeight) {
					bigEnough.add(option);
				} else {
					notBigEnough.add(option);
				}
			}
		}

		// Pick the smallest of those big enough. If there is no one big enough, pick
		// the
		// largest of those not big enough.
		if (bigEnough.size() > 0) {
			return Collections.min(bigEnough, new CompareSizesByArea());
		} else if (notBigEnough.size() > 0) {
			return Collections.max(notBigEnough, new CompareSizesByArea());
		} else {
			Log.d("chooseOptimalSize", "Couldn't find any suitable preview size");
			return choices[0];
		}
	}

	/** 拍照 */
	public void takePicture() {
		Log.d("thread", "take"+Thread.currentThread().getName());
		Log.d("takePicture", "start takephoto");
		try {
			// 如果正在拍照退出
			if (!mCameraOpenCloseLock.tryAcquire()) {
				Log.d("takePicture", "当前任务未完成");
				return;
			}
			if (cameraDevice == null) {
				Log.d("takePicture", "相机未打开");
				return;
			}
			// 创建拍照请求
			captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
			// 设置自动对焦模式
			captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
					CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
			// 将imageReader的surface设为目标
			captureRequestBuilder.addTarget(imageReader.getSurface());
			// 获取设备方向
			int rotation = ((Activity) context).getWindowManager().getDefaultDisplay().getRotation();
			// 根据设备方向计算设置照片的方向
			captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
			// 停止连续取景
			// mPreviewSession.stopRepeating();
			// mState=STATE_TAKEPHOTO;
			// 拍照
			CaptureRequest captureRequest = captureRequestBuilder.build();
			// 设置拍照监听
			mPreviewSession.capture(captureRequest, captureCallback, mBackgroundHandler);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 监听拍照结果 */
	private CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
		// 拍照成功
		@Override
		public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request,
				TotalCaptureResult result) {
			// 重设自动对焦模式
			// captureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
			// CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
			// 设置自动曝光模式
			// captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
			// CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
			try {
				// 重新进行预览
				// mState=STATE_PREVIEW;
				// mPreviewSession.setRepeatingRequest(mCaptureRequest, null,
				// mBackgroundHandler);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.d("captureCallback", "拍照捕获成功！");
			mCameraOpenCloseLock.release();
		}

		@Override
		public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
			super.onCaptureFailed(session, request, failure);
			Log.d("captureCallback", "拍照捕获失败！");
			mCameraOpenCloseLock.release();
		}
	};

	/** 监听拍照的图片 */
	private ImageReader.OnImageAvailableListener imageAvailableListener = new ImageReader.OnImageAvailableListener() {
		// 当照片数据可用时激发该方法
		@Override
		public void onImageAvailable(ImageReader reader) {

			// 获取捕获的照片数据
			Image image = reader.acquireNextImage();
			if (image == null) {
				return;
			}
			try {
				Log.d("thread", "image"+Thread.currentThread().getName());
				// jpeg格式返回处理
				// 因为是ImageFormat.JPEG格式，所以 image.getPlanes()返回的数组只有一个，也就是第0个。
				ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
				byte[] bytes = new byte[byteBuffer.remaining()];
				byteBuffer.get(bytes);
				// ImageFormat.JPEG格式直接转化为Bitmap格式。
				Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
				Log.d("ImageReader", "开始处理图片");
				xuCamCallBack.OnTake(bitmap);

			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				image.close();
				image = null;
			}

		}

	};

	/**
	 * 停止拍照释放资源
	 */
	private void stopCamera() {

		Log.d("paush", "close");
		if (mPreviewSession != null) {
			mPreviewSession.close();
			mPreviewSession = null;
		}
		if (cameraDevice != null) {
			cameraDevice.close();
			cameraDevice = null;
		}
		if (imageReader != null) {
			imageReader.close();
			imageReader = null;
		}

	}

	public void isdebug(boolean isdebug) {
		sv.isDebug = isdebug;
	}

	public void isScan(boolean isScan) {
		sv.isScan = isScan;
	}

	public void setScanDir(int dir) {
		if (dir >= 0 && dir <= 1) {
			sv.scanDir = dir;
		} else {
			sv.scanDir = XuCamSur.SCAN_Y;
		}
	}

	public void setRotate(int rotate) {
		sv.Rotate = rotate;
	}
	
	public void startTake(Boolean istake,int takeTime) {
		
mTakeTime=takeTime;
		mIsTake=istake;
	}
	
	public void setFace(Bitmap bitmap) {
		 Log.d("start", "face");
FaceDetector mFaceDetector;
 FaceDetector.Face[] mFace = new FaceDetector.Face[1];
 mFaceDetector = new FaceDetector(bitmap.getWidth(), bitmap.getHeight(), 1);
 mFaceDetector.findFaces(bitmap, mFace);
 sv.FaceRe=mFace;

	}
	

	

	

}