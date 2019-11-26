package com.kangjiu.xu.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import android.R.integer;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.BitmapShader;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;




/**
 * 图片处理工具�??
 * Created by Administrator on 2015/10/20 0020.
 */
public class BitmapUtils {

    private static final String TAG = "BitmapUtils";
/*
 * 加载本地图片
 */
    private static final int white = 0xffffffff; 
    
    public static Bitmap create_new_bitmap(int widthAndHeight)  {    

    	  int[] pixels = new int[widthAndHeight * widthAndHeight];    
        
       for (int y = 0; y < widthAndHeight; y++) {   
           for (int x = 0; x < widthAndHeight; x++) {   
            
               	 pixels[y * widthAndHeight + x] = white;  
              
           }   
       }   
       Bitmap bitmap = Bitmap.createBitmap(widthAndHeight, widthAndHeight,   
               Bitmap.Config.ARGB_8888 );   
       bitmap.setPixels(pixels, 0, widthAndHeight, 0, 0, widthAndHeight, widthAndHeight);
    	
        return bitmap;   
    }   
    
    
    public static Bitmap loadImg_As(String name,Context context){
    	
		Bitmap bitmap;
		 InputStream inputStream;
			try {
				inputStream=context.getAssets().open("img/"+name+".png");
				  //获得图片的宽、高
		        BitmapFactory.Options tmpOptions = new BitmapFactory.Options();
		        tmpOptions.inJustDecodeBounds = true;
		        BitmapFactory.decodeStream(inputStream, null, tmpOptions);
		        int width = tmpOptions.outWidth;
		        int height = tmpOptions.outHeight;

		        //设置显示图片的中心区�??
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
    public static Bitmap Load_Img_File(String url) {
    	Bitmap bt=null;
    	FileInputStream fis;
    	
    		
		try {
			fis = new FileInputStream(url);
			bt  = BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	
		return bt;
	}
    /**
     *
     * TODO<创建倒影图片>
     * @throw
     * @return Bitmap
     * @param srcBitmap 源图片的bitmap
     * @param reflectionHeight 图片倒影的高�??
     */
    public static Bitmap createReflectedBitmap(Bitmap srcBitmap,int reflectionHeight) {

        if (null == srcBitmap) {
            Log.e(TAG, "the srcBitmap is null");
            return null;
        }

        // The gap between the reflection bitmap and original bitmap.
        final int REFLECTION_GAP = 0;

        int srcWidth = srcBitmap.getWidth();
        int srcHeight = srcBitmap.getHeight();

        if (0 == srcWidth || srcHeight == 0) {
            Log.e(TAG, "the srcBitmap is null");
            return null;
        }

        // The matrix
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        try {

            // The reflection bitmap, width is same with original's, height is
            // half of original's.
            Bitmap reflectionBitmap = Bitmap.createBitmap(srcBitmap, 0, srcHeight - reflectionHeight,
                    srcWidth, reflectionHeight, matrix, false);

            if (null == reflectionBitmap) {
                Log.e(TAG, "Create the reflectionBitmap is failed");
                return null;
            }

            // Create the bitmap which contains original and reflection bitmap.
            Bitmap bitmapWithReflection = Bitmap.createBitmap(srcWidth, srcHeight + reflectionHeight,
                    Bitmap.Config.ARGB_8888);

            if (null == bitmapWithReflection) {
                return null;
            }

            // Prepare the canvas to draw stuff.
            Canvas canvas = new Canvas(bitmapWithReflection);

            // Draw the original bitmap.
            canvas.drawBitmap(srcBitmap, 0, 0, null);

            // Draw the reflection bitmap.
            canvas.drawBitmap(reflectionBitmap, 0, srcHeight + REFLECTION_GAP,
                    null);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            LinearGradient shader = new LinearGradient(0, srcHeight, 0,
                    bitmapWithReflection.getHeight() + REFLECTION_GAP,
                    0x70FFFFFF, 0x00FFFFFF, TileMode.MIRROR);
            paint.setShader(shader);
            paint.setXfermode(new PorterDuffXfermode(
                    android.graphics.PorterDuff.Mode.DST_IN));

            canvas.save();
            // Draw the linear shader.
            canvas.drawRect(0, srcHeight, srcWidth,
                    bitmapWithReflection.getHeight() + REFLECTION_GAP, paint);
            if (reflectionBitmap != null && !reflectionBitmap.isRecycled()){
                reflectionBitmap.recycle();
                reflectionBitmap = null;
            }

            canvas.restore();

            return bitmapWithReflection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(TAG, "Create the reflectionBitmap is failed");
        return null;
    }

    /**
     *
     * TODO<图片圆角处理>
     * @throw
     * @return Bitmap
     * @param srcBitmap 源图片的bitmap
     * @param ret 圆角的度�??
     */
    public static Bitmap getRoundImage(Bitmap srcBitmap, float ret) {

        if(null == srcBitmap){
            Log.e(TAG, "the srcBitmap is null");
            return null;
        }

        int bitWidth = srcBitmap.getWidth();
        int bitHight = srcBitmap.getHeight();

        BitmapShader bitmapShader = new BitmapShader(srcBitmap,
                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(bitmapShader);

        RectF rectf = new RectF(0, 0, bitWidth, bitHight);

        Bitmap outBitmap = Bitmap.createBitmap(bitWidth, bitHight,
                Config.ARGB_8888);
        Canvas canvas = new Canvas(outBitmap);
        canvas.drawRoundRect(rectf, ret, ret, paint);
        canvas.save();
        canvas.restore();

        return outBitmap;
    }


    /**
     *
     * TODO<图片沿着Y轴旋转一定角�??>
     * @throw
     * @return Bitmap
     * @param srcBitmap 源图片的bitmap
     * @param reflectionHeight 图片倒影的高�??
     * @param rotate 图片旋转的角�??
     */
    public static Bitmap skewImage(Bitmap srcBitmap, float rotate, int reflectionHeight) {

        if(null == srcBitmap){
            Log.e(TAG, "the srcBitmap is null");
            return null;
        }

        Bitmap reflecteBitmap = createReflectedBitmap(srcBitmap, reflectionHeight);

        if (null == reflecteBitmap){
            Log.e(TAG, "failed to createReflectedBitmap");
            return null;
        }

        int wBitmap = reflecteBitmap.getWidth();
        int hBitmap = reflecteBitmap.getHeight();
        float scaleWidth = ((float) 180) / wBitmap;
        float scaleHeight = ((float) 270) / hBitmap;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        reflecteBitmap = Bitmap.createBitmap(reflecteBitmap, 0, 0, wBitmap, hBitmap, matrix,
                true);
        Camera localCamera = new Camera();
        localCamera.save();
        Matrix localMatrix = new Matrix();
        localCamera.rotateY(rotate);
        localCamera.getMatrix(localMatrix);
        localCamera.restore();
        localMatrix.preTranslate(-reflecteBitmap.getWidth() >> 1,
                -reflecteBitmap.getHeight() >> 1);
        Bitmap localBitmap2 = Bitmap.createBitmap(reflecteBitmap, 0, 0,
                reflecteBitmap.getWidth(), reflecteBitmap.getHeight(), localMatrix,
                true);
        Bitmap localBitmap3 = Bitmap.createBitmap(localBitmap2.getWidth(),
                localBitmap2.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas localCanvas = new Canvas(localBitmap3);
        Paint localPaint = new Paint();
        localPaint.setAntiAlias(true);
        localPaint.setFilterBitmap(true);
        localCanvas.drawBitmap(localBitmap2, 0.0F, 0.0F, localPaint);
        if (null != reflecteBitmap && !reflecteBitmap.isRecycled()) {
            reflecteBitmap.recycle();
            reflecteBitmap = null;
        }
        if (null != localBitmap2 && !localBitmap2.isRecycled()) {
            localBitmap2.recycle();
            localBitmap2 = null;
        }
        localCanvas.save();
        localCanvas.restore();
        return localBitmap3;
    }


    /**
     *
     * TODO<图片模糊化处�??>
     * @throw
     * @return Bitmap
     * @param bitmap 源图�??
     * @param radius The radius of the blur Supported range 0 < radius <= 25
     * @param context 上下�??
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("NewApi")
    public static Bitmap blurBitmap(Bitmap bitmap,float radius,Context context){

        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);

        //Instantiate a new Renderscript
        RenderScript rs = RenderScript.create(context);

        //Create an Intrinsic Blur Script using the Renderscript
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

        //Set the radius of the blur
        if(radius > 25){
            radius = 25.0f;
        }else if (radius <= 0){
            radius = 1.0f;
        }
        blurScript.setRadius(radius);

        //Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        //Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);

        //recycle the original bitmap
        bitmap.recycle();
        bitmap = null;
        //After finishing everything, we destroy the Renderscript.
        rs.destroy();

        return outBitmap;


    }

    /**
     * TODO<给图片添加指定颜色的边框>
     * @param srcBitmap 原图�??
     * @param borderWidth 边框宽度
     * @param color 边框的颜色�??
     * @return
     */
    public static Bitmap addFrameBitmap(Bitmap srcBitmap,int borderWidth,int color)
    {
        if (srcBitmap == null){
            Log.e(TAG, "the srcBitmap or borderBitmap is null");
            return null;
        }

        int newWidth = srcBitmap.getWidth() + borderWidth ;
        int newHeight = srcBitmap.getHeight() + borderWidth ;

        Bitmap outBitmap = Bitmap.createBitmap(newWidth, newHeight, Config.ARGB_8888);

        Canvas canvas = new Canvas(outBitmap);

        Rect rec = canvas.getClipBounds();
        rec.bottom--;
        rec.right--;
        Paint paint = new Paint();
        //设置边框颜色
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        //设置边框宽度
        paint.setStrokeWidth(borderWidth);
        canvas.drawRect(rec, paint);

        canvas.drawBitmap(srcBitmap, borderWidth/2, borderWidth/2, null);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        if (srcBitmap !=null && !srcBitmap.isRecycled()){
            srcBitmap.recycle();
            srcBitmap = null;
        }

        return outBitmap;
    }

    /**
     * LOMO特效
     * @param bitmap 原图�??
     * @return LOMO特效图片
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

                // ==========边缘黑暗==============//
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
                // ==========边缘黑暗end==============//

                dst[pos] = Color.rgb(newR, newG, newB);
            }
        }

        Bitmap acrossFlushBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        acrossFlushBitmap.setPixels(dst, 0, width, 0, 0, width, height);
        return acrossFlushBitmap;
    }

    /**
     * 旧时光特�??
     * @param bmp 原图�??
     * @return 旧时光特效图�??
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
                int newColor = Color.argb(255, newR > 255 ? 255 : newR, newG > 255 ? 255 : newG, newB > 255 ? 255
                        : newB);
                pixels[width * i + k] = newColor;
            }
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 暖意特效
     * @param bmp 原图�??
     * @param centerX 光源横坐�??
     * @param centerY 光源纵坐�??
     * @return 暖意特效图片
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

        final float strength = 150F; // 光照强度 100~150
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

                // 计算当前点到光照中心的距离，平面座标系中求两点之间的距离
                int distance = (int) (Math.pow((centerY - i), 2) + Math.pow(centerX - k, 2));
                if (distance < radius * radius) {
                    // 按照距离大小计算增加的光照�??
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

    /**
     * 根据饱和度�?�色相�?�亮度调整图�??
     * @param bm 原图�??
     * @param saturation 饱和�??
     * @param hue 色相
     * @param lum 亮度
     * @return 处理后的图片
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

    /**
     * 添加内边�??
     * @param bm 原图�??
     * @param frame 内边框图�??
     * @return 带有边框的图�??
     */
    public static Bitmap addBigFrame(Bitmap bm, Bitmap frame) {
        Bitmap newBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();
        canvas.drawBitmap(bm, 0, 0, paint);
        frame = Bitmap.createScaledBitmap(frame, bm.getWidth(), bm.getHeight(), true);
        canvas.drawBitmap(frame, 0, 0, paint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return newBitmap;

    }

    /**
     * 创建�??个缩放的图片
     * @param path 图片地址
     * @param w 图片宽度
     * @param h 图片高度
     * @return 缩放后的图片
     */
    public static Bitmap createBitmap(String path, int w, int h) {
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            // 这里是整个方法的关键，inJustDecodeBounds设为true时将不为图片分配内存�??
            BitmapFactory.decodeFile(path, opts);
            int srcWidth = opts.outWidth;// 获取图片的原始宽�??
            int srcHeight = opts.outHeight;// 获取图片原始高度
            int destWidth = 0;
            int destHeight = 0;
            // 缩放的比�??
            double ratio = 0.0;
            if (srcWidth < w || srcHeight < h) {
                ratio = 0.0;
                destWidth = srcWidth;
                destHeight = srcHeight;
            } else if (srcWidth > srcHeight) {// 按比例计算缩放后的图片大小，maxLength是长或宽允许的最大长�??
                ratio = (double) srcWidth / w;
                destWidth = w;
                destHeight = (int) (srcHeight / ratio);
            } else {
                ratio = (double) srcHeight / h;
                destHeight = h;
                destWidth = (int) (srcWidth / ratio);
            }
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            // 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能�?�过inSampleSize来进行缩放，其�?�表明缩放的倍数，SDK中建议其值是2的指数�??
            newOpts.inSampleSize = (int) ratio + 1;
            // inJustDecodeBounds设为false表示把图片读进内存中
            newOpts.inJustDecodeBounds = false;
            // 设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
            newOpts.outHeight = destHeight;
            newOpts.outWidth = destWidth;
            // 获取缩放后图�??
            return BitmapFactory.decodeFile(path, newOpts);
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }
    }

    /**
     * 创建�??个缩放的图片
     * @param context 上下�??
     * @param id 图片id
     * @param w 图片宽度
     * @param h 图片高度
     * @return 缩放后的图片
     */
    public static Bitmap createBitmap2(Context context, int id, int w, int h) {
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            // 这里是整个方法的关键，inJustDecodeBounds设为true时将不为图片分配内存�??
            BitmapFactory.decodeResource(context.getResources(), id, opts);
            int srcWidth = opts.outWidth;// 获取图片的原始宽�??
            int srcHeight = opts.outHeight;// 获取图片原始高度
            int destWidth = 0;
            int destHeight = 0;
            // 缩放的比�??
            double ratio = 0.0;
            if (srcWidth < w || srcHeight < h) {
                ratio = 0.0;
                destWidth = srcWidth;
                destHeight = srcHeight;
            } else if (srcWidth > srcHeight) {// 按比例计算缩放后的图片大小，maxLength是长或宽允许的最大长�??
                ratio = (double) srcWidth / w;
                destWidth = w;
                destHeight = (int) (srcHeight / ratio);
            } else {
                ratio = (double) srcHeight / h;
                destHeight = h;
                destWidth = (int) (srcWidth / ratio);
            }
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            // 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能�?�过inSampleSize来进行缩放，其�?�表明缩放的倍数，SDK中建议其值是2的指数�??
            newOpts.inSampleSize = (int) ratio + 1;
            // inJustDecodeBounds设为false表示把图片读进内存中
            newOpts.inJustDecodeBounds = false;
            // 设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
            newOpts.outHeight = destHeight;
            newOpts.outWidth = destWidth;
            // 获取缩放后图�??
            // return BitmapFactory.decodeFile(path, newOpts);
            return BitmapFactory.decodeResource(context.getResources(), id, newOpts);
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }
    }

    /**
     * 保存图片到本�??(JPG)
     * @param bm 保存的图�??
     * @param path 保存的位�??
     * @return 图片路径
     */
    public static String saveToLocalJPG(Bitmap bm, String path) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }
        FileOutputStream fileOutputStream = null;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        String fileName =  "test.jpg";
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

    /**
     * 保存图片到本�??(PNG)
     * @param bm 保存的图�??
     * @param path 保存的位�??
     * @return 图片路径
     */
    public static String saveToLocalPNG(Bitmap bm, String path) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }
        FileOutputStream fileOutputStream = null;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        String fileName = "test.png";
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
        }else {
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

    /**
     * 获取缩略图图�??
     * @param imagePath 图片的路�??
     * @param width 图片的宽�??
     * @param height 图片的高�??
     * @return 缩略图图�??
     */
    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放�??
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
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }
    
    
    
    public	static Bitmap getViewBitmap(View v) {

		v.clearFocus();

		v.setPressed(false);

 

		//�ܻ�����ͷ���false 

		boolean willNotCache = v.willNotCacheDrawing();

		v.setWillNotCacheDrawing(false);

		int color = v.getDrawingCacheBackgroundColor();

		v.setDrawingCacheBackgroundColor(0);

		if (color != 0) {

			v.destroyDrawingCache();

		}

		v.buildDrawingCache();

		Bitmap cacheBitmap = v.getDrawingCache();

		if (cacheBitmap == null) {

			
			return null;

		}

		Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

		// Restore the view 

		v.destroyDrawingCache();

		v.setWillNotCacheDrawing(willNotCache);

		v.setDrawingCacheBackgroundColor(color);

		return bitmap;

	}

 

	/**

	* ����ɫͼת��Ϊ�ڰ�ͼ

	* 

	* @param λͼ

	* @return ����ת���õ�λͼ

	*/

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

	 * ��Bitmap��Ϊ .bmp��ʽͼƬ

	 * @param bitmap

	 */

	public void saveBmp(Bitmap bitmap) {

		if (bitmap == null)

			return;

		// λͼ��С

		int nBmpWidth = bitmap.getWidth();

		int nBmpHeight = bitmap.getHeight();

		// ͼ�����ݴ�С

		int bufferSize = nBmpHeight * (nBmpWidth * 3 + nBmpWidth % 4);

		try {

			// �洢�ļ���

			String filename = "/sdcard/test.bmp";

			File file = new File(filename);

			if (!file.exists()) {

				file.createNewFile();

			}

			FileOutputStream fileos = new FileOutputStream(filename);

			// bmp�ļ�ͷ

			int bfType = 0x4d42;

			long bfSize = 14 + 40 + bufferSize;

			int bfReserved1 = 0;

			int bfReserved2 = 0;

			long bfOffBits = 14 + 40;

			// ����bmp�ļ�ͷ

			writeWord(fileos, bfType);

			writeDword(fileos, bfSize);

			writeWord(fileos, bfReserved1);

			writeWord(fileos, bfReserved2);

			writeDword(fileos, bfOffBits);

			// bmp��Ϣͷ

			long biSize = 40L;

			long biWidth = nBmpWidth;

			long biHeight = nBmpHeight;

			int biPlanes = 1;

			int biBitCount = 24;

			long biCompression = 0L;

			long biSizeImage = 0L;

			long biXpelsPerMeter = 0L;

			long biYPelsPerMeter = 0L;

			long biClrUsed = 0L;

			long biClrImportant = 0L;

			// ����bmp��Ϣͷ

			writeDword(fileos, biSize);

			writeLong(fileos, biWidth);

			writeLong(fileos, biHeight);

			writeWord(fileos, biPlanes);

			writeWord(fileos, biBitCount);

			writeDword(fileos, biCompression);

			writeDword(fileos, biSizeImage);

			writeLong(fileos, biXpelsPerMeter);

			writeLong(fileos, biYPelsPerMeter);

			writeDword(fileos, biClrUsed);

			writeDword(fileos, biClrImportant);

			// ����ɨ��

			byte bmpData[] = new byte[bufferSize];

			int wWidth = (nBmpWidth * 3 + nBmpWidth % 4);

			for (int nCol = 0, nRealCol = nBmpHeight - 1; nCol < nBmpHeight; ++nCol, --nRealCol)

				for (int wRow = 0, wByteIdex = 0; wRow < nBmpWidth; wRow++, wByteIdex += 3) {

					int clr = bitmap.getPixel(wRow, nCol);

					bmpData[nRealCol * wWidth + wByteIdex] = (byte) Color.blue(clr);

					bmpData[nRealCol * wWidth + wByteIdex + 1] = (byte) Color.green(clr);

					bmpData[nRealCol * wWidth + wByteIdex + 2] = (byte) Color.red(clr);

				}

 

			fileos.write(bmpData);

			fileos.flush();

			fileos.close();

 

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

 

	protected void writeWord(FileOutputStream stream, int value) throws IOException {

		byte[] b = new byte[2];

		b[0] = (byte) (value & 0xff);

		b[1] = (byte) (value >> 8 & 0xff);

		stream.write(b);

	}

 

	protected void writeDword(FileOutputStream stream, long value) throws IOException {

		byte[] b = new byte[4];

		b[0] = (byte) (value & 0xff);

		b[1] = (byte) (value >> 8 & 0xff);

		b[2] = (byte) (value >> 16 & 0xff);

		b[3] = (byte) (value >> 24 & 0xff);

		stream.write(b);

	}

 

	protected void writeLong(FileOutputStream stream, long value) throws IOException {

		byte[] b = new byte[4];

		b[0] = (byte) (value & 0xff);

		b[1] = (byte) (value >> 8 & 0xff);

		b[2] = (byte) (value >> 16 & 0xff);

		b[3] = (byte) (value >> 24 & 0xff);

		stream.write(b);

	}
    
    
	
	
	
	
	
	
	
	
	
	
	/**
	 * 文字转图�?
	 * 
	 * @param str
	 * @return
	 */
	public static Bitmap word2bitmap(String str) {

		Bitmap bMap = Bitmap.createBitmap(380, 60, Config.ARGB_8888);
		Canvas canvas = new Canvas(bMap);
		canvas.drawColor(Color.WHITE);
		TextPaint textPaint = new TextPaint();
		textPaint.setStyle(Paint.Style.FILL);
		textPaint.setColor(Color.BLACK);
		textPaint.setTextSize(28.0F);
		StaticLayout layout = new StaticLayout(str, textPaint, bMap.getWidth(),
				Alignment.ALIGN_NORMAL, (float) 1.0, (float) 0.0, true);
		layout.draw(canvas);

		return bMap;

	}

	/**
	 * 两张图片上下合并成一�?
	 * 
	 * @param bitmap1
	 * @param bitmap2
	 * @return
	 */
	public static Bitmap twoBtmap2One(Bitmap bitmap1, Bitmap bitmap2) {
		Bitmap bitmap3 = Bitmap.createBitmap(bitmap1.getWidth(),
				bitmap1.getHeight() + bitmap2.getHeight(), bitmap1.getConfig());
		Canvas canvas = new Canvas(bitmap3);
		canvas.drawBitmap(bitmap1, new Matrix(), null);
		canvas.drawBitmap(bitmap2, 0, bitmap1.getHeight(), null);
		return bitmap3;
	}


	/**
	 * 两张图片左右合并成一�?
	 * 
	 * @param bitmap1
	 * @param bitmap2
	 * @return
	 */
	public static Bitmap twoBtmap2One1(Bitmap bitmap1, Bitmap bitmap2,int x,int y) {
		Bitmap bitmap3 = Bitmap.createBitmap(bitmap1.getWidth(),
				bitmap1.getHeight() , bitmap1.getConfig());
		Canvas canvas = new Canvas(bitmap3);
		canvas.drawBitmap(bitmap1, new Matrix(), null);
		canvas.drawBitmap(bitmap2,x, y, null);
		return bitmap3;
	}

	
	/**
	 * 文字转图�?
	 * 
	 * @param text
	 *            将要生成图片的内�?
	 * @param textSize
	 *            文字大小
	 * @return
	 */
	public static Bitmap textAsBitmap(int width, String text, float textSize,int align) {

		TextPaint textPaint = new TextPaint();

		textPaint.setColor(Color.BLACK);

		textPaint.setTextSize(textSize);
		StaticLayout layout;
		if(align==1) {
			layout = new StaticLayout(text, textPaint, width,
					Alignment.ALIGN_NORMAL, 1.3f, 0.0f, true);
		}else if(align==2) {
			layout = new StaticLayout(text, textPaint, width,
					Alignment.ALIGN_CENTER, 1.3f, 0.0f, true);
		}else {
			layout = new StaticLayout(text, textPaint, width,
					Alignment.ALIGN_OPPOSITE, 1.3f, 0.0f, true);
		}
		
		
		Bitmap bitmap = Bitmap.createBitmap(layout.getWidth() + 20,
				layout.getHeight() + 20, Bitmap.Config.ARGB_8888);
		
		Canvas canvas = new Canvas(bitmap);
		canvas.translate(10, 10);
		canvas.drawColor(Color.WHITE);

		layout.draw(canvas);
	
		return bitmap;
	}

	

	/**
	 * 图片旋转
	 * 
	 * @param bm
	 * @param orientationDegree
	 * @return
	 */
public static	Bitmap  adjustPhotoRotation(Bitmap bm, final int orientationDegree) {

		Matrix m = new Matrix();
		m.setRotate(orientationDegree, (float) bm.getWidth() / 2,
				(float) bm.getHeight() / 2);
		float targetX, targetY;
		if (orientationDegree == 90) {
			targetX = bm.getHeight();
			targetY = 0;
		} else {
			targetX = bm.getHeight();
			targetY = bm.getWidth();
		}

		final float[] values = new float[9];
		m.getValues(values);

		float x1 = values[Matrix.MTRANS_X];
		float y1 = values[Matrix.MTRANS_Y];

		m.postTranslate(targetX - x1, targetY - y1);

		Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(),
				Bitmap.Config.ARGB_8888);
		Paint paint = new Paint();
		Canvas canvas = new Canvas(bm1);
		canvas.drawBitmap(bm, m, paint);

		return bm1;
	}
	
	
	
	
	
	
	
	
}