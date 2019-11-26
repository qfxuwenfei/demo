package com.xutest;



import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.xu.bitmap.XuBitmap;
import com.xu.bitmap.XuBitmap2D;
import com.xu.bitmap.XuBitmapConvert;
import com.xu.bitmap.XuBitmapLoader;
import com.xu.tool.XuFile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;


public class MainActivity extends Activity {
TextView tip;
ImageView img;
Bitmap bitmap;




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 
		setContentView(R.layout.activity_main);
	init();
	
	}
	
	private void init() {
		tip=(TextView)findViewById(R.id.tip);
		img=(ImageView)findViewById(R.id.img);
	}

	public void opencam(View v) {
	XuBitmap xuBitmap=XuBitmap2D.bitmap_to_XuMap(bitmap);
	Bitmap bt=XuBitmap2D.XuMap_to_Bitmap(xuBitmap);
	Log.d("xubitmap", xuBitmap.getWidth()+":"+xuBitmap.getHeight());
	img.setImageBitmap(bt);
	}
	
	
	
	
	public void opencam3(View v) {
		Intent intent=new Intent(MainActivity.this,CamTest.class);
		startActivity(intent);
	}
	
	
public void test(View v) {

EventLoopGroup boss=new NioEventLoopGroup();
EventLoopGroup work=new NioEventLoopGroup();
ServerBootstrap bootstrap=new ServerBootstrap();
bootstrap.group(boss, work)
.channel(NioServerSocketChannel.class)
.childHandler(null);



}


}
