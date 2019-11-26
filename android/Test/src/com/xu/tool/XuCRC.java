package com.xu.tool;

public class XuCRC {
	/**
     * 计算CRC16校验�?
     *
     * @param bytes 字节数组
     * @return {@link String} 校验�?
     * @since 1.0
     */
    public static String CRC_CHECK(byte[] bytes) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        return Integer.toHexString(CRC);
    }

    public static boolean is_CRC(byte [] bytes) {
    	int size=bytes.length-4;
    	byte[] temp=new byte[size];
    	boolean re=false;
    	
    	for(int i=0;i<size;i++) {
    		temp[i]=bytes[i];
    	}
    	
    String	str=CRC_CHECK(temp);
   
    byte	crc1=getCRC(str, 1);
    byte	crc2=getCRC(str, 2);
   
    	if(bytes[size+1]==crc1 && bytes[size]==crc2) {
    		re=true;
    	}
    	
    	return re;
    }
    
    public static byte[] getCRC(byte[] bytes) {
    	int size=bytes.length-4;
    	String str;
    	byte crc1,crc2;
    	byte[] re=new byte[size];
    	byte[] re1=bytes;
    	for(int i=0;i<size;i++) {
    		re[i]=bytes[i];
    	}
    	str=CRC_CHECK(re);
    	crc1=getCRC(str, 1);
    	crc2=getCRC(str, 2);
    	re=bytes;
    	re[size+1]=crc1;
    	re[size]=crc2;
    	
    	return re;
    }
   
    public  static byte getCRC(String str,int i) {
    	byte re;
    	String hex;
    	if(i==1) {
    		hex=str.substring(0, 2);
    	}else {
    		hex=str.substring(2);
    	}
    re=	(byte) Integer.parseInt(hex, 16);
    	
    	return re;
    }
}
