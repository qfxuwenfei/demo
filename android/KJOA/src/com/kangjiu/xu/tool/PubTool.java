package com.kangjiu.xu.tool;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

public class PubTool {
	

public static boolean isNumeric(String str){ 

   Pattern pattern = Pattern.compile("[0-9]*"); 

   Matcher isNum = pattern.matcher(str);

   if( !isNum.matches() ){

       return false; 

   } 

   return true; 

}


	public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
	
	
	
}
