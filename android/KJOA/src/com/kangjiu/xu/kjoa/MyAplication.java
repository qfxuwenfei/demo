package com.kangjiu.xu.kjoa;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;


import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;
import com.zhy.http.okhttp.https.HttpsUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import okhttp3.OkHttpClient;

public class MyAplication extends Application {
	private static Context mAppContext;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.d("myAplication", "creat");
		 mAppContext = getApplicationContext();
	 initOkHttp();//����OkhttpClient

	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		Log.d("myAplication", "end");
	}
	
	private void initOkHttp() {

        CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(getApplicationContext()));//�޸ĳ��Դ���cookie�־û������Խ���������ʱ���ص�
        //ClearableCookieJar cookieJar1 = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getApplicationContext()));

        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);//���ÿɷ������е�https��վ

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60000L, TimeUnit.MILLISECONDS)
                .readTimeout(60000L, TimeUnit.MILLISECONDS)
                //����Log,ͨ������������ʵ�֣�������ṩ��һ��LoggerInterceptor����Ȼ���������ʵ��һ��Interceptor
                .addInterceptor(new LoggerInterceptor("TAG"))
                //���ó־û�Cookie(����Session)
                .cookieJar(cookieJar)
                .hostnameVerifier(new HostnameVerifier()
                {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        // TODO Auto-generated method stub
                        return false;
                    }
                })
                //����Https
                .sslSocketFactory(sslParams.sSLSocketFactory)
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

	 public static Context getAppContext()
	    {
	        return mAppContext;
	    }
}
