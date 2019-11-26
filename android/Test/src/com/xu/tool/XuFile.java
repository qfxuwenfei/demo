package com.xu.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class XuFile {
	 /**
	 * 判断SDCard是否可用
	 * 
	 * @return
	 */
	 public static boolean isSDCardEnable()
	 {
	 return Environment.getExternalStorageState().equals(
	 Environment.MEDIA_MOUNTED);
	 }
	 
	 /**
	  * 获取SD卡路径
	  * 
	  * @return
	  */
	  public static String getSDCardPath()
	  {
	  return Environment.getExternalStorageDirectory().getAbsolutePath()
	  + File.separator;
	  }
	  /**
	  * 获取SD卡的剩余容量 单位byte
	  * 
	  * @return
	  */
	  public static long getSDCardAllSize()
	  {
	  if (isSDCardEnable())
	  {
	  StatFs stat = new StatFs(getSDCardPath());
	  // 获取空闲的数据块的数量
	  long availableBlocks = (long) stat.getAvailableBlocks() - 4;
	  // 获取单个数据块的大小（byte）
	  long freeBlocks = stat.getAvailableBlocks();
	  return freeBlocks * availableBlocks;
	  }
	  return 0;
	  }
	  /**
	  * 获取指定路径所在空间的剩余可用容量字节数，单位byte
	  * 
	  * @param filePath
	  * @return 容量字节 SDCard可用空间，内部存储可用空间
	  */
	  public static long getFreeBytes(String filePath)
	  {
	  // 如果是sd卡的下的路径，则获取sd卡可用容量
	  if (filePath.startsWith(getSDCardPath()))
	  {
	  filePath = getSDCardPath();
	  } else
	  {// 如果是内部存储的路径，则获取内存存储的可用容量
	  filePath = Environment.getDataDirectory().getAbsolutePath();
	  }
	  StatFs stat = new StatFs(filePath);
	  long availableBlocks = (long) stat.getAvailableBlocks() - 4;
	  return stat.getBlockSize() * availableBlocks;
	  }
	  /**
	  * 获取系统存储路径
	  * 
	  * @return
	  */
	  public static String getRootDirectoryPath()
	  {
	  return Environment.getRootDirectory().getAbsolutePath();
	  }
	/*
	 * 已整理
	 * 获取文件夹目录
	 * 如果不存在，则创建
	 * 目录位于内存卡/data/包/files/下
	 * return String
	 */
	public static String getFilePath(Context context,String dir) {
	    String directoryPath="";
	    if (Environment.getExternalStorageState()!=null ) {
	        directoryPath =context.getExternalFilesDir(dir).getAbsolutePath();
	        }else{
	        directoryPath=context.getFilesDir()+File.separator+dir;
	        }
	        File file = new File(directoryPath);
	        if(!file.exists()){
	        file.mkdirs();
	        }
	    return directoryPath;
	}
	

	public static File getFile(String path){
	    if (path == null || path.trim().equals(""))
	        return null;
	    File f = new File(path);
	    return f;
	}

	/** 删锟斤拷锟侥硷拷
	 * @param path 锟侥硷拷路锟斤拷
	 * @return 锟角凤拷删锟斤拷锟缴癸拷
	 */
	public static boolean deleteFile(String path) {
	    File f = getFile(path);
	    if (!f.exists())
	        return true;
	    return f.delete();
	}

	public static void deleteFolder(String path) {
		File file=getFile(path);
		if(file.exists()) {
	    if (file.isDirectory()) {
	        File[] files = file.listFiles();
	        for (int i = 0; i < files.length; i++) {
	            File f = files[i];
	          f.delete();
	        }
	        file.delete();//濡傝淇濈暀鏂囦欢澶癸紝鍙垹闄ゆ枃浠讹紝璇锋敞閲婅繖琛�
	    } else if (file.exists()) {
	        file.delete();
	    }}
	}
	
	
	/** 锟斤拷锟斤拷锟侥硷拷
	 * @param fromFile 原锟侥硷拷
	 * @param toFile 目锟斤拷锟侥硷拷
	 * @param rewrite 锟角否覆革拷
	 * @param isDeleteFromFile 锟斤拷锟斤拷锟斤拷锟斤拷欠锟缴撅拷锟皆拷募锟�
	 * @return 锟角凤拷锟狡成癸拷
	 */
	public static boolean copyFile(File fromFile, File toFile, Boolean rewrite,
	                               Boolean isDeleteFromFile) {
	    if (!fromFile.exists() || !fromFile.isFile() || !fromFile.canRead()) {
	        return false;
	    }
	    if (toFile.exists() && rewrite) {
	        toFile.delete();
	    }
	    FileInputStream from = null;
	    FileOutputStream to = null;
	    boolean isSuccess = true;
	    try {
	        from = new FileInputStream(fromFile);
	        to = new FileOutputStream(toFile);
	        byte[] content = new byte[1024];
	        int reads = 0;
	        while ((reads = from.read(content)) > 0) {
	            to.write(content, 0, reads);
	        }
	        to.flush();
	    } catch (Exception ex) {
	        isSuccess = false;
	    } finally {
	        if (from != null) {
	            try {
	                from.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        if (to != null) {
	            try {
	                to.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        if (isDeleteFromFile && isSuccess) {
	            fromFile.delete();
	        }
	    }
	    return isSuccess;
	}
	/**
	 * 锟斤拷取锟侥硷拷锟叫达拷小
	 * @param file 锟侥硷拷锟斤拷
	 * @return 锟侥硷拷锟叫达拷小锟斤拷锟斤拷位锟街斤拷
	 */
	public static long getFileSize(File file) {
	    long size = 0;
	    File flist[] = file.listFiles();
	    for (int i = 0; i < flist.length; i++) {
	        if (flist[i].isDirectory()) {
	            size = size + getFileSize(flist[i]);
	        } else {
	            size = size + flist[i].length();
	        }
	    }
	    return size;
	}
	/**
	 * 锟斤拷取锟侥硷拷锟斤拷小
	 * @param path 锟侥硷拷
	 * @return 锟侥硷拷锟斤拷小锟斤拷锟斤拷位锟街斤拷
	 */
	public static long getFileSize(String path) {
	    long size = 0;
	    File file = getFile(path);
	    FileInputStream fis;
	    try {
	        fis = new FileInputStream(file);
	        size = fis.available();
	    } catch (Exception e) {
	        Log.e("e","");
	        e.printStackTrace();
	    }
	    return size;
	}


	//F

	    public static boolean fileIsExists(String strFile)  

	    {  

	        try  

	        {  

	            File f=new File(strFile);  

	            if(!f.exists())  

	            {  

	                    return false;  

	            }  

	  

	        }  

	        catch (Exception e)  

	        {  

	            return false;  

	        }  

	  

	        return true;  

	    } 
//鑾峰彇鏂囦欢鍚嶅瓧
	    public static String Get_File_Name(String path) {
	     	String re="";
	    	if(path.length()>0) {
	   
	    	int i=-1;
	    	i=path.lastIndexOf("\\");
	    	if(i>0) {
	    		
	    		re=path.substring(i+1);
	    	}
	    	}
	    	return re;
	    }
	    
	  //鑾峰彇鏂囦欢鍚嶅瓧
	    public static String Get_File_Name1(String path) {
	    	String re="";
	    	if(path.length()>0) {
	    	int i=-1;
	    	i=path.lastIndexOf("/");
	    	if(i>0) {
	    		
	    		re=path.substring(i+1);
	    	}
	    	}
	    	return re;
	    }
//纾佺洏璺緞杞崲涓虹綉缁滆矾寰�
	    public static String Disc_To_Net(String path) {
	    	String re="";
	    	re=path.replace("\\", "/");
	    	return re;
	    }
}
