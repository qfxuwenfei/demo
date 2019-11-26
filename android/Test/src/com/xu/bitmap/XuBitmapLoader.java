package com.xu.bitmap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.os.Environment;

public class XuBitmapLoader {
	
	/*
	 * 已整理 加载assets资源目录下的图片 name："img/img.png" return Bitmap
	 */
	public static Bitmap loadImg_As(String name, Context context) {

		Bitmap bitmap;
		InputStream inputStream;
		try {
			inputStream = context.getAssets().open(name);
			// 鑾峰緱鍥剧墖鐨勫銆侀珮
			BitmapFactory.Options tmpOptions = new BitmapFactory.Options();
			tmpOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(inputStream, null, tmpOptions);
			int width = tmpOptions.outWidth;
			int height = tmpOptions.outHeight;

			// 璁剧疆鏄剧ず鍥剧墖鐨勪腑蹇冨尯鍩�
			BitmapRegionDecoder bitmapRegionDecoder = BitmapRegionDecoder.newInstance(inputStream, false);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			bitmap = bitmapRegionDecoder.decodeRegion(new Rect(0, 0, width, height), options);

			return bitmap;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
	/*
	 * 已整理 加载文件目录图片 系统目录+"/图片目录/image.jpg" 返回Bitmap 4种加载图片的方法 Bitmap bitmap=
	 * BitmapFactory.decodeByteArray(bytes,0,bytes.length);//方法一：通过字节数组得到Bitmap对象
	 * Bitmap bitmap2= BitmapFactory.decodeFile("");//方法二：通过本地文件得到Bitmap对象 Bitmap
	 * bitmap3=
	 * BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);//方法三：
	 * 通过资源文件得到Bitmap对象 Bitmap bitmap4= BitmapFactory.decodeStream(new InputStream()
	 * {//方法四：通过输入流得到Bitmap对象
	 * 
	 */
	public static Bitmap LoadImageFile(String url) {
		Bitmap bt = null;
		FileInputStream fis;
		try {
			fis = new FileInputStream(url);
			bt = BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bt;
	}
	
	

	/*
	 * 已整理 从文件加载图片，并指定宽高 return bitmap
	 */
	public static Bitmap createBitmap(String path, int w, int h) {
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			// 杩欓噷鏄暣涓柟娉曠殑鍏抽敭锛宨nJustDecodeBounds璁句负true鏃跺皢涓嶄负鍥剧墖鍒嗛厤鍐呭瓨銆�
			BitmapFactory.decodeFile(path, opts);
			int srcWidth = opts.outWidth;// 鑾峰彇鍥剧墖鐨勫師濮嬪搴�
			int srcHeight = opts.outHeight;// 鑾峰彇鍥剧墖鍘熷楂樺害
			int destWidth = 0;
			int destHeight = 0;
			// 缂╂斁鐨勬瘮渚�
			double ratio = 0.0;
			if (srcWidth < w || srcHeight < h) {
				ratio = 0.0;
				destWidth = srcWidth;
				destHeight = srcHeight;
			} else if (srcWidth > srcHeight) {// 鎸夋瘮渚嬭绠楃缉鏀惧悗鐨勫浘鐗囧ぇ灏忥紝maxLength鏄暱鎴栧鍏佽鐨勬渶澶ч暱搴�
				ratio = (double) srcWidth / w;
				destWidth = w;
				destHeight = (int) (srcHeight / ratio);
			} else {
				ratio = (double) srcHeight / h;
				destHeight = h;
				destWidth = (int) (srcWidth / ratio);
			}
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			// 缂╂斁鐨勬瘮渚嬶紝缂╂斁鏄緢闅炬寜鍑嗗鐨勬瘮渚嬭繘琛岀缉鏀剧殑锛岀洰鍓嶆垜鍙彂鐜板彧鑳介�氳繃inSampleSize鏉ヨ繘琛岀缉鏀撅紝鍏跺�艰〃鏄庣缉鏀剧殑鍊嶆暟锛孲DK涓缓璁叾鍊兼槸2鐨勬寚鏁板��
			newOpts.inSampleSize = (int) ratio + 1;
			// inJustDecodeBounds璁句负false琛ㄧず鎶婂浘鐗囪杩涘唴瀛樹腑
			newOpts.inJustDecodeBounds = false;
			// 璁剧疆澶у皬锛岃繖涓竴鑸槸涓嶅噯纭殑锛屾槸浠nSampleSize鐨勪负鍑嗭紝浣嗘槸濡傛灉涓嶈缃嵈涓嶈兘缂╂斁
			newOpts.outHeight = destHeight;
			newOpts.outWidth = destWidth;
			// 鑾峰彇缂╂斁鍚庡浘鐗�
			return BitmapFactory.decodeFile(path, newOpts);
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	
	/*
	 * 保存jpg图像
	 */
	public static String saveToLocalJPG(Bitmap bm, String path,String fileName) {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return null;
		}
		FileOutputStream fileOutputStream = null;
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}

		String filePath = path + fileName;
		File f = new File(filePath);
		if (!f.exists()) {
			try {
				f.createNewFile();
				fileOutputStream = new FileOutputStream(filePath);
				bm.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
			} catch (IOException e) {
				return null;
			} finally {
				try {
					fileOutputStream.flush();
					fileOutputStream.close();
				} catch (IOException e) {
					return null;
				}
			}
		}
		return filePath;
	}
	
	
	

	/*
	 * 保存png图像
	 */
	public static String saveToLocalPNG(Bitmap bm, String path,String fileName) {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return null;
		}
		FileOutputStream fileOutputStream = null;
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		String filePath = path + fileName;
		File f = new File(filePath);
		if (!f.exists()) {
			try {
				f.createNewFile();
				fileOutputStream = new FileOutputStream(filePath);
				bm.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
			} catch (IOException e) {
				return null;
			} finally {
				try {
					fileOutputStream.flush();
					fileOutputStream.close();
				} catch (IOException e) {
					return null;
				}
			}
		} else {
			try {
				f.delete();
				fileOutputStream = new FileOutputStream(filePath);
				bm.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
			} catch (IOException e) {
				return null;
			} finally {
				try {
					fileOutputStream.flush();
					fileOutputStream.close();
				} catch (IOException e) {
					return null;
				}
			}
		}
		return filePath;
	}

}
