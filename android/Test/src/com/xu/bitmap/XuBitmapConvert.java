package com.xu.bitmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Base64;
import android.util.Log;

/**
 * bitmap操作相关类
 */
public class XuBitmapConvert {

	private static final String TAG = "BitmapUtils";

	/*
	 * 已整理 base64文本转图片 return bitmap
	 */

	public static Bitmap Base64ToBitmap(String string) {
		Bitmap bitmap = null;
		try {
			// byte[] bitmapArray = Base64.decode(string.split(",")[1], Base64.DEFAULT);
			byte[] bitmapArray = Base64.decode(string, Base64.DEFAULT);
			bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/*
	 * 水平垂直镜像图片
	 */

	public static Bitmap horverBitmap(Bitmap bitmap, boolean H, boolean V) {
		int bmpWidth = bitmap.getWidth();
		int bmpHeight = bitmap.getHeight();
		Matrix matrix = new Matrix();

		if (H)
			matrix.postScale(-1, 1); // 水平翻转H

		if (V)
			matrix.postScale(1, -1); // 垂直翻转V

		if (H && V)
			matrix.postScale(-1, -1); // 水平&垂直翻转HV

		return Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true);

	}

	/*
	 * 已整理 图片装base64文本 return string
	 */

	public static String bitmapToBase64(Bitmap bitmap) {

		String result = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

				baos.flush();
				baos.close();

				byte[] bitmapBytes = baos.toByteArray();
				result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	



	public static Bitmap RGB888ToRGB565(Bitmap bitmap) {
		int width=bitmap.getWidth();
		int height=bitmap.getHeight();
		Bitmap newbt=Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		int colors[]=new int[width * height];;
		bitmap.getPixels(colors, 0, width, 0, 0, width, height);
		newbt.setPixels(colors, 0, width, 0, 0, width, height);
		return newbt;
	}
	
	
	/*
	 * 已整理 创建圆角图片 return bitmap
	 */
	public static Bitmap getRoundImage(Bitmap srcBitmap, float ret) {

		if (null == srcBitmap) {
			Log.e(TAG, "the srcBitmap is null");
			return null;
		}

		int bitWidth = srcBitmap.getWidth();
		int bitHight = srcBitmap.getHeight();

		BitmapShader bitmapShader = new BitmapShader(srcBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setShader(bitmapShader);

		RectF rectf = new RectF(0, 0, bitWidth, bitHight);

		Bitmap outBitmap = Bitmap.createBitmap(bitWidth, bitHight, Config.ARGB_8888);
		Canvas canvas = new Canvas(outBitmap);
		canvas.drawRoundRect(rectf, ret, ret, paint);
		canvas.save();
		canvas.restore();

		return outBitmap;
	}

	/*
	 * 已整理 图形模糊 范围 1-25 返回bitmap
	 */

	public static Bitmap blurBitmap(Bitmap bitmap, float radius, Context context) {

		// Let's create an empty bitmap with the same size of the bitmap we want to blur
		Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);

		// Instantiate a new Renderscript
		RenderScript rs = RenderScript.create(context);

		// Create an Intrinsic Blur Script using the Renderscript
		ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

		// Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
		Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
		Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

		// Set the radius of the blur
		if (radius > 25) {
			radius = 25.0f;
		} else if (radius <= 0) {
			radius = 1.0f;
		}
		blurScript.setRadius(radius);

		// Perform the Renderscript
		blurScript.setInput(allIn);
		blurScript.forEach(allOut);

		// Copy the final bitmap created by the out Allocation to the outBitmap
		allOut.copyTo(outBitmap);

		// recycle the original bitmap
		bitmap.recycle();
		bitmap = null;
		// After finishing everything, we destroy the Renderscript.
		rs.destroy();

		return outBitmap;

	}

	/*
	 * 已整理 添加图形纯色边框 返回bitmap
	 */
	public static Bitmap addFrameBitmap(Bitmap srcBitmap, int borderWidth, int color) {
		if (srcBitmap == null) {
			Log.e(TAG, "the srcBitmap or borderBitmap is null");
			return null;
		}

		int newWidth = srcBitmap.getWidth() + borderWidth;
		int newHeight = srcBitmap.getHeight() + borderWidth;

		Bitmap outBitmap = Bitmap.createBitmap(newWidth, newHeight, Config.ARGB_8888);

		Canvas canvas = new Canvas(outBitmap);

		Rect rec = canvas.getClipBounds();
		rec.bottom--;
		rec.right--;
		Paint paint = new Paint();
		// 璁剧疆杈规棰滆壊
		paint.setColor(color);
		paint.setStyle(Paint.Style.STROKE);
		// 璁剧疆杈规瀹藉害
		paint.setStrokeWidth(borderWidth);
		canvas.drawRect(rec, paint);

		canvas.drawBitmap(srcBitmap, borderWidth / 2, borderWidth / 2, null);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		if (srcBitmap != null && !srcBitmap.isRecycled()) {
			srcBitmap.recycle();
			srcBitmap = null;
		}

		return outBitmap;
	}

	/*
	 * 已整理 柠檬滤镜 返回bitmap
	 */
	public static Bitmap lomoFilter(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int dst[] = new int[width * height];
		bitmap.getPixels(dst, 0, width, 0, 0, width, height);

		int ratio = width > height ? height * 32768 / width : width * 32768 / height;
		int cx = width >> 1;
		int cy = height >> 1;
		int max = cx * cx + cy * cy;
		int min = (int) (max * (1 - 0.8f));
		int diff = max - min;

		int ri, gi, bi;
		int dx, dy, distSq, v;

		int R, G, B;

		int value;
		int pos, pixColor;
		int newR, newG, newB;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				pos = y * width + x;
				pixColor = dst[pos];
				R = Color.red(pixColor);
				G = Color.green(pixColor);
				B = Color.blue(pixColor);

				value = R < 128 ? R : 256 - R;
				newR = (value * value * value) / 64 / 256;
				newR = (R < 128 ? newR : 255 - newR);

				value = G < 128 ? G : 256 - G;
				newG = (value * value) / 128;
				newG = (G < 128 ? newG : 255 - newG);

				newB = B / 2 + 0x25;

				// ==========杈圭紭榛戞殫==============//
				dx = cx - x;
				dy = cy - y;
				if (width > height)
					dx = (dx * ratio) >> 15;
				else
					dy = (dy * ratio) >> 15;

				distSq = dx * dx + dy * dy;
				if (distSq > min) {
					v = ((max - distSq) << 8) / diff;
					v *= v;

					ri = newR * v >> 16;
					gi = newG * v >> 16;
					bi = newB * v >> 16;

					newR = ri > 255 ? 255 : (ri < 0 ? 0 : ri);
					newG = gi > 255 ? 255 : (gi < 0 ? 0 : gi);
					newB = bi > 255 ? 255 : (bi < 0 ? 0 : bi);
				}
				// ==========杈圭紭榛戞殫end==============//

				dst[pos] = Color.rgb(newR, newG, newB);
			}
		}

		Bitmap acrossFlushBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		acrossFlushBitmap.setPixels(dst, 0, width, 0, 0, width, height);
		return acrossFlushBitmap;
	}

	/*
	 * 已整理 复旧照片 返回bitmap
	 */
	public static Bitmap oldTimeFilter(Bitmap bmp) {
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		int pixColor = 0;
		int pixR = 0;
		int pixG = 0;
		int pixB = 0;
		int newR = 0;
		int newG = 0;
		int newB = 0;
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 0; i < height; i++) {
			for (int k = 0; k < width; k++) {
				pixColor = pixels[width * i + k];
				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);
				newR = (int) (0.393 * pixR + 0.769 * pixG + 0.189 * pixB);
				newG = (int) (0.349 * pixR + 0.686 * pixG + 0.168 * pixB);
				newB = (int) (0.272 * pixR + 0.534 * pixG + 0.131 * pixB);
				int newColor = Color.argb(255, newR > 255 ? 255 : newR, newG > 255 ? 255 : newG,
						newB > 255 ? 255 : newB);
				pixels[width * i + k] = newColor;
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/*
	 * 已整理 添加光晕，类似小太阳 x，y为小太阳位置 return bitmap
	 */
	public static Bitmap warmthFilter(Bitmap bmp, int centerX, int centerY) {
		final int width = bmp.getWidth();
		final int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;
		int radius = Math.min(centerX, centerY);

		final float strength = 150F; // 鍏夌収寮哄害 100~150
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		int pos = 0;
		for (int i = 1, length = height - 1; i < length; i++) {
			for (int k = 1, len = width - 1; k < len; k++) {
				pos = i * width + k;
				pixColor = pixels[pos];

				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);

				newR = pixR;
				newG = pixG;
				newB = pixB;

				// 璁＄畻褰撳墠鐐瑰埌鍏夌収涓績鐨勮窛绂伙紝骞抽潰搴ф爣绯讳腑姹備袱鐐逛箣闂寸殑璺濈
				int distance = (int) (Math.pow((centerY - i), 2) + Math.pow(centerX - k, 2));
				if (distance < radius * radius) {
					// 鎸夌収璺濈澶у皬璁＄畻澧炲姞鐨勫厜鐓у��
					int result = (int) (strength * (1.0 - Math.sqrt(distance) / radius));
					newR = pixR + result;
					newG = pixG + result;
					newB = pixB + result;
				}

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				pixels[pos] = Color.argb(255, newR, newG, newB);
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/*
	 * 已整理 图片颜色调整 saturation饱和度 hue色相 lum亮度
	 */
	public static Bitmap handleImage(Bitmap bm, int saturation, int hue, int lum) {
		Bitmap bmp = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		ColorMatrix mLightnessMatrix = new ColorMatrix();
		ColorMatrix mSaturationMatrix = new ColorMatrix();
		ColorMatrix mHueMatrix = new ColorMatrix();
		ColorMatrix mAllMatrix = new ColorMatrix();
		float mSaturationValue = saturation * 1.0F / 127;
		float mHueValue = hue * 1.0F / 127;
		float mLumValue = (lum - 127) * 1.0F / 127 * 180;
		mHueMatrix.reset();
		mHueMatrix.setScale(mHueValue, mHueValue, mHueValue, 1);

		mSaturationMatrix.reset();
		mSaturationMatrix.setSaturation(mSaturationValue);
		mLightnessMatrix.reset();

		mLightnessMatrix.setRotate(0, mLumValue);
		mLightnessMatrix.setRotate(1, mLumValue);
		mLightnessMatrix.setRotate(2, mLumValue);

		mAllMatrix.reset();
		mAllMatrix.postConcat(mHueMatrix);
		mAllMatrix.postConcat(mSaturationMatrix);
		mAllMatrix.postConcat(mLightnessMatrix);

		paint.setColorFilter(new ColorMatrixColorFilter(mAllMatrix));
		canvas.drawBitmap(bm, 0, 0, paint);
		return bmp;
	}

	

	public static Bitmap rotate(Bitmap bitmap, int angle) {
		Matrix matrix = new Matrix();
		matrix.preRotate(angle);
		Bitmap nbitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return nbitmap;
	}

	/**
	 * 使用Matrix
	 * 
	 * @param bitmap
	 *            原始的Bitmap
	 * @param width
	 *            目标宽度
	 * @param height
	 *            目标高度
	 * @return 缩放后的Bitmap
	 */
	public static Bitmap scale(Bitmap bitmap, int width, int height) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		float scaleW = width * 10000 / w * 0.0001f;
		float scaleH = height * 10000 / h * 0.0001f;
		Matrix matrix = new Matrix();
		matrix.setScale(scaleW, scaleH); // 长和宽放大缩小的比例
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
	}

	/*
	 * 获取指定路径图片文件图标
	 */
	public static Bitmap getImageThumbnail(String imagePath, int width, int height) {

		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 鑾峰彇杩欎釜鍥剧墖鐨勫鍜岄珮锛屾敞鎰忔澶勭殑bitmap涓簄ull
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false; // 璁句负 false
		// 璁＄畻缂╂斁姣�
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		// 閲嶆柊璇诲叆鍥剧墖锛岃鍙栫缉鏀惧悗鐨刡itmap锛屾敞鎰忚繖娆¤鎶妎ptions.inJustDecodeBounds 璁句负 false
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		// 鍒╃敤ThumbnailUtils鏉ュ垱寤虹缉鐣ュ浘锛岃繖閲岃鎸囧畾瑕佺缉鏀惧摢涓狟itmap瀵硅薄
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	/*
	 * 新增 图片到数组
	 */
	public static byte[] BitmapToBytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/*
	 * 新增 数组转图片
	 */
	public static Bitmap BytesToBimap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}

	public static Bitmap convertToBlackWhite(Bitmap bmp) {

		int width = bmp.getWidth(); // ��ȡλͼ�Ŀ�

		int height = bmp.getHeight(); // ��ȡλͼ�ĸ�

		int[] pixels = new int[width * height]; // ͨ��λͼ�Ĵ�С�������ص�����

		bmp.getPixels(pixels, 0, width, 0, 0, width, height);

		int alpha = 0xFF << 24;

		for (int i = 0; i < height; i++) {

			for (int j = 0; j < width; j++) {

				int grey = pixels[width * i + j];

				int red = ((grey & 0x00FF0000) >> 16);

				int green = ((grey & 0x0000FF00) >> 8);

				int blue = (grey & 0x000000FF);

				grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);

				grey = alpha | (grey << 16) | (grey << 8) | grey;

				pixels[width * i + j] = grey;

			}

		}

		Bitmap newBmp = Bitmap.createBitmap(width, height, Config.RGB_565);

		newBmp.setPixels(pixels, 0, width, 0, 0, width, height);

		Bitmap resizeBmp = ThumbnailUtils.extractThumbnail(newBmp, 380, 460);

		return resizeBmp;

	}

	/**
	 * 两张图片上下合并成一张
	 * 
	 * @param bitmap1
	 * @param bitmap2
	 * @return
	 */
	public static Bitmap twoBtmapToOne(Bitmap bitmap1, Bitmap bitmap2, int x, int y) {
		Bitmap bitmap3 = Bitmap.createBitmap(bitmap1.getWidth(), bitmap1.getHeight(), bitmap1.getConfig());
		Canvas canvas = new Canvas(bitmap3);
		canvas.drawBitmap(bitmap1, new Matrix(), null);
		canvas.drawBitmap(bitmap2, x, y, null);
		return bitmap3;
	}
	
	
	
	
	
	
	
	
	
}