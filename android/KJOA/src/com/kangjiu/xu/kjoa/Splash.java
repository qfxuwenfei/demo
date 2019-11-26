package com.kangjiu.xu.kjoa;

import com.xu.kangjiu.kjoa.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

public class Splash extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// remove title bar 即隐藏标题栏
		
		setContentView(R.layout.splash);
		 handler.sendMessageDelayed(Message.obtain(), 2000);
	}
	
	private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //界面转载
            Intent intent = new Intent(Splash.this,Login.class);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	handler.removeCallbacksAndMessages(null);
    }
}
