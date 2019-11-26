package com.kangjiu.xu.view;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kangjiu.xu.kjoa.ErpMain;
import com.kangjiu.xu.kjoa.Login;
import com.kangjiu.xu.server.APIServer;
import com.kangjiu.xu.server.BaseLogic;
import com.xu.kangjiu.kjoa.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;


public class ErpPage1 extends Fragment implements OnClickListener{
	ErpMain ma;

	public ErpPage1(ErpMain ma) {
		this.ma = ma;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.erppage1, null);
		return view;

	}

private void init_view() {
	
		
}
	


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		init_view();
		
	}
	
	

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
	
		default:
			break;
		}
	}

	
	class MyWebViewClient extends WebViewClient{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			view.loadUrl(url);  
			return true;
		}
		
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
		}
		
		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);
		}
		
		
	}
	
	
	
}



