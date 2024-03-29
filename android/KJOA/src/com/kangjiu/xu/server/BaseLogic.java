package com.kangjiu.xu.server;

import java.util.List;

import com.kangjiu.xu.set.Pubset;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.CookieStore;

import android.content.Context;
import okhttp3.Cookie;
import okhttp3.CookieJar;

public class BaseLogic {

    protected Context context;
    public final String charset = "GBK";//例如：URLEncoder.encode("中途结束", charset)
    
    /**拼接完整的URL地址*/
    public static String getUrl(String serverUrl){
        String url = Pubset.SERVER_HOME + serverUrl;
        return url;
    }

    /**获取cookies*/
    public static String getCookiesStr(){
        String cookiesInfo = "";
        CookieJar cookieJar = OkHttpUtils.getInstance().getOkHttpClient().cookieJar();
        if (cookieJar instanceof CookieJarImpl)
        {
            CookieStore cookieStore = ((CookieJarImpl) cookieJar).getCookieStore();
            List<Cookie> cookies = cookieStore.getCookies();
            for(Cookie cookie : cookies){
                cookiesInfo = cookiesInfo + cookie.name() + ":" + cookie.value() + ";";
            }
        }

        return cookiesInfo;
    }

    /**清空cookie缓存*/
    public static void clearCookies()
    {
        CookieJar cookieJar = OkHttpUtils.getInstance().getOkHttpClient().cookieJar();
        if (cookieJar instanceof CookieJarImpl)
        {
            ((CookieJarImpl) cookieJar).getCookieStore().removeAll();
        }
    }

}