package com.xu.net;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import com.xu.tool.XuFile;
import com.xu.tool.XuMD5;
import com.xu.tool.XuMath;

import android.R.integer;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;

public class XuHttp {

	private static final String BOUNDARY = "----XuHttp";

	/**
	 * 执行网络请求操作,返回数据会解析成字符串String
	 *
	 * @param method
	 *            请求方式(需要传入String类型的参数:"GET","POST")
	 * @param url
	 *            请求的url
	 * @param params
	 *            请求的参数
	 */
	public static void httpStr(final String method, final String url, final Map<String, String> params,
			final XuStringCB callback) {

		new AsyncTask<String, Void, XuReString>() {
			XuReString re = new XuReString();

			@Override
			protected XuReString doInBackground(String... params1) {

				HttpURLConnection connection = null;
				OutputStream outputStream = null;
				try {
					URL u = new URL(url);
					connection = (HttpURLConnection) u.openConnection();
					// 设置输入可用
					connection.setDoInput(true);
					// 设置输出可用
					connection.setDoOutput(true);
					// 设置请求方式
					connection.setRequestMethod(method);
					// 设置连接超时
					connection.setConnectTimeout(5000);
					// 设置读取超时
					connection.setReadTimeout(5000);
					// 设置缓存不可用
					connection.setUseCaches(false);
					// 开始连接
					connection.connect();

					// 只有当POST请求时才会执行此代码段
					if (params != null) {
						// 获取输出流,connection.getOutputStream已经包含了connect方法的调用
						outputStream = connection.getOutputStream();
						StringBuilder sb = new StringBuilder();
						Set<Map.Entry<String, String>> sets = params.entrySet();
						// 将Hashmap转换为string
						for (Map.Entry<String, String> entry : sets) {
							sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
						}
						String param = sb.substring(0, sb.length() - 1);
						// 使用输出流将string类型的参数写到服务器
						outputStream.write(param.getBytes());
						outputStream.flush();
					}

					int responseCode = connection.getResponseCode();
					re.code = responseCode;
					if (responseCode == 200) {
						InputStream inputStream = connection.getInputStream();
						String result = inputStream2String(inputStream);
						if (result != null && callback != null) {
							re.re = result;
						}
					}

				} catch (final Exception e) {
					e.printStackTrace();
					re.e = e;

				} finally {
					if (connection != null) {
						connection.disconnect();
					}
					if (outputStream != null) {
						try {
							outputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				return re;
			}

			protected void onPostExecute(XuReString result) {
				if (callback != null) {
					if (result.code == 200) {
						callback.onSuccess(result.re);
					} else {
						callback.onFaileure(result.code, result.e);
					}
				}
			};

		}.execute(url);

	}

	/**
	 * 执行网络请求操作,返回数据是Bitmap
	 *
	 * @param method
	 *            请求方式(需要传入String类型的参数:"GET","POST")
	 * @param url
	 *            请求的url
	 * @param params
	 *            请求的参数
	 */
	public static void httpImg(final String method, final String url, final Map<String, String> params,
			final XuBitmapCB callback) {

		new AsyncTask<String, Void, XuReBitmap>() {

			@Override
			protected XuReBitmap doInBackground(String... params1) {
				XuReBitmap re = new XuReBitmap();

				HttpURLConnection connection = null;
				OutputStream outputStream = null;
				InputStream inputStream = null;
				try {
					URL u = new URL(url);
					connection = (HttpURLConnection) u.openConnection();
					// 设置输入可用
					connection.setDoInput(true);
					// 设置输出可用,不能设置为true，true会自动更改为post请求，将无法下载图片
					connection.setDoOutput(false);
					// 设置请求方式
					connection.setRequestMethod(method);
					// 设置连接超时
					connection.setConnectTimeout(5000);
					// 设置读取超时
					connection.setReadTimeout(5000);
					// 设置缓存不可用
					connection.setUseCaches(false);
					// 开始连接
					connection.connect();

					// 只有当POST请求时才会执行此代码段
					if (params != null) {
						// 获取输出流,connection.getOutputStream已经包含了connect方法的调用
						outputStream = connection.getOutputStream();
						StringBuilder sb = new StringBuilder();
						Set<Map.Entry<String, String>> sets = params.entrySet();
						// 将Hashmap转换为string
						for (Map.Entry<String, String> entry : sets) {
							sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
						}
						String param = sb.substring(0, sb.length() - 1);
						// 使用输出流将string类型的参数写到服务器
						outputStream.write(param.getBytes());
						outputStream.flush();
					}

					int responseCode = connection.getResponseCode();
					re.code = responseCode;
					if (responseCode == 200) {
						inputStream = connection.getInputStream();
						Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
						if (bitmap != null && callback != null) {
							re.re = bitmap;
						}
					}

				} catch (final Exception e) {
					e.printStackTrace();
					re.e = e;

				} finally {
					if (connection != null) {
						connection.disconnect();
					}
					if (outputStream != null) {
						try {
							outputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				return re;
			}

			protected void onPostExecute(XuReBitmap result) {
				super.onPostExecute(result);
				if (callback != null) {
					if (result.code == 200) {
						callback.onSuccess(result.re);
					} else {
						callback.onFaileure(result.code, result.e);
					}
				}

			};

		}.execute(url);

	}

	/**
	 * 执行网络请求操作,返回数据是byte[]
	 *
	 * @param method
	 *            请求方式(需要传入String类型的参数:"GET","POST")
	 * @param url
	 *            请求的url
	 * @param params
	 *            请求的参数
	 */
	public static void httpDown(final Context context, final String method, final String url, final Map<String, String> params,
			final ProgressBar pb,final Boolean 
			cache, final XuStringCB callback) {
		int ii=url.lastIndexOf(".");
			String ext=url.substring(ii);
		final String fileName=XuMD5.getMD5(url)+ext;
	final String path=XuFile.getFilePath(context, "down")+"/";

		new AsyncTask<String, Integer, XuReString>() {
			XuReString re = new XuReString();
			int tlen = 0;

			protected void onPreExecute() {
				pb.setProgress(0);

			};

			@Override
			protected XuReString doInBackground(String... params1) {
				HttpURLConnection connection = null;
				OutputStream outputStream = null;//post 流
				FileOutputStream fos = null;//文件输出流
				BufferedOutputStream bos=null;//文件写入流

				try {
					if(cache) {
					boolean isCache=	XuFile.deleteFile(path+fileName);		
					if(isCache) {
						publishProgress(0, 100);
						publishProgress(1, 100);
						Log.d("cache", "true");
						re.code=200;
						re.re=path+fileName;
						return re;
					}
					}
					
					
					URL u = new URL(url);
					connection = (HttpURLConnection) u.openConnection();
					// 设置输入可用
					connection.setDoInput(true);
					// 设置输出可用
					connection.setDoOutput(false);
					// 设置请求方式
					connection.setRequestMethod(method);
					// 设置连接超时
					connection.setConnectTimeout(5000);
					// 设置读取超时
					connection.setReadTimeout(5000);
					// 设置缓存不可用
					connection.setUseCaches(false);
					// 开始连接
					connection.connect();
					// 只有当POST请求时才会执行此代码段
					if (params != null) {
						// 获取输出流,connection.getOutputStream已经包含了connect方法的调用
						outputStream = connection.getOutputStream();

						StringBuilder sb = new StringBuilder();
						Set<Map.Entry<String, String>> sets = params.entrySet();
						// 将Hashmap转换为string
						for (Map.Entry<String, String> entry : sets) {
							sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
						}
						String param = sb.substring(0, sb.length() - 1);
						// 使用输出流将string类型的参数写到服务器
						outputStream.write(param.getBytes());
						outputStream.flush();
					}

					int responseCode = connection.getResponseCode();
					re.code = responseCode;
					if (responseCode == 200) {
						InputStream inputStream = connection.getInputStream();
fos=new FileOutputStream(path+fileName);
bos=new BufferedOutputStream(fos);										
						tlen = connection.getContentLength();
						publishProgress(0, tlen);
						int len = 0;

						byte[] bt = new byte[1024];

						while ((len = inputStream.read(bt)) != -1) {
							bos.write(bt, 0, len);
							publishProgress(1, len);
						}
				
						
						re.re = path+fileName;
					}
					
					
					
					

				} catch (final Exception e) {
					e.printStackTrace();
					re.e = e;

				} finally {
					if (connection != null) {
						connection.disconnect();
					}
					if (outputStream != null) {
						try {
							outputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					if(bos!=null) {
						try {
							bos.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					if(fos!=null) {
						try {
							fos.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				return re;
			}

			protected void onProgressUpdate(Integer[] values) {
				if (pb != null) {
					if (values[0] == 0) {
						pb.setMax(values[1]);
					} else if (values[0] == 1) {
						pb.incrementProgressBy(values[1]);
					}
				}

			};

			@Override
			protected void onPostExecute(XuReString result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);

				if (callback != null) {
					if (result.code == 200) {
						callback.onSuccess(result.re);
					} else {
						callback.onFaileure(result.code, result.e);
					}
				}
			}

		}.execute(url);

	}

	/*
	 * 上传文件
	 */

	public static void httpUp(final String method, final String url, final Map<String, String> params,
			final ProgressBar pb, final String file, final XuStringCB callback) {

		new AsyncTask<String, Integer, XuReString>() {
			XuReString re = new XuReString();

			protected void onPreExecute() {
				if (pb != null) {
					pb.setProgress(0);
				}
			};

			@Override
			protected XuReString doInBackground(String... params1) {
				
				HttpURLConnection conn =null;
				OutputStream out=null;
				
				
				try {
					File myFile = new File(file);
					String ext = MimeTypeMap.getFileExtensionFromUrl(file);
					String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext.toLowerCase());
					String fileName = myFile.getName();

					StringBuilder sb = new StringBuilder();
					/**
					 * 普通的表单数据
					 */
					if (params != null) {
						for (String key : params.keySet()) {
							sb.append("--" + BOUNDARY + "\r\n");
							sb.append("Content-Disposition: form-data; name=\"" + key + "\"" + "\r\n");
							sb.append("\r\n");
							sb.append(params.get(key) + "\r\n");
						}
					}

					/**
					 * 上传文件的头
					 */
					sb.append("--" + BOUNDARY + "\r\n");
					sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"" + "\r\n");
					sb.append("Content-Type:" + mime + "\r\n");// 如果服务器端有文件类型的校验，必须明确指定ContentType
					sb.append("\r\n");

					byte[] headerInfo = sb.toString().getBytes("UTF-8");
					byte[] endInfo = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("UTF-8");
					URL u = new URL(url);
					 conn = (HttpURLConnection) u.openConnection();
					conn.setRequestMethod("POST");
					// 设置传输内容的格式，以及长度
					conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
					conn.setRequestProperty("Content-Length",
							String.valueOf(headerInfo.length + myFile.length() + endInfo.length));
					conn.setDoOutput(true);

					int tlen = (int) (myFile.length());
					publishProgress(0, tlen);

					 out = conn.getOutputStream();
					InputStream in = new FileInputStream(myFile);
					// 写入头部 （包含了普通的参数，以及文件的标示等）
					out.write(headerInfo);
					// 写入文件
					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) != -1) {
						out.write(buf, 0, len);
						publishProgress(1, len);
					}
					// 写入尾部
					out.write(endInfo);
					in.close();
					out.close();
					int responseCode = conn.getResponseCode();
					re.code = responseCode;
					if (responseCode == 200) {
						Log.d("up", fileName + ":文件上传成功");
						InputStream inputStream = conn.getInputStream();
						String result = inputStream2String(inputStream);
						if (result != null && callback != null) {
							re.re = result;
						}

					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally {
					if (conn != null) {
						conn.disconnect();
					}
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

				return re;
			}

			@Override
			protected void onProgressUpdate(Integer[] values) {
				if (pb != null) {
					if (values[0] == 0) {
						pb.setMax(values[1]);
					} else if (values[0] == 1) {
						pb.incrementProgressBy(values[1]);
					}
				}

			};

			@Override
			protected void onPostExecute(XuReString result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if (callback != null) {
					if (result.code == 200) {
						callback.onSuccess(result.re);
					} else {
						callback.onFaileure(result.code, result.e);
					}
				}
			}

		}.execute(url);

	}

	//////////////////////////////////////////////////////////////////////// 转换方法
	/**
	 * 字节流转换成字符串
	 *
	 * @param inputStream
	 * @return
	 */
	private static String inputStream2String(InputStream inputStream) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] bytes = new byte[1024];
		int len = 0;
		try {
			while ((len = inputStream.read(bytes)) != -1) {
				baos.write(bytes, 0, len);
			}
			return new String(baos.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 字节流转换成字节数组
	 *
	 * @param inputStream
	 *            输入流
	 * @return
	 */
	public static byte[] inputStream2ByteArray(InputStream inputStream) {
		byte[] result = null;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		// 缓冲区
		byte[] bytes = new byte[1024];
		int len = -1;
		try {
			// 使用字节数据输出流来保存数据
			while ((len = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, len);
			}
			result = outputStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	
	
	/**
	 * 判断网络是否连接
	 * 
	 * @param context
	 * @return
	 */
	 public static boolean isConnected(Context context)
	 {
	 ConnectivityManager connectivity = (ConnectivityManager) context
	 .getSystemService(Context.CONNECTIVITY_SERVICE);
	 if (null != connectivity)
	 {
	 NetworkInfo info = connectivity.getActiveNetworkInfo();
	 if (null != info && info.isConnected())
	 {
	 if (info.getState() == NetworkInfo.State.CONNECTED)
	 {
	  return true;
	 }
	 }
	 }
	 return false;
	 }
	 /**
	 * 判断是否是wifi连接
	 */
	 public static boolean isWifi(Context context)
	 {
	 ConnectivityManager cm = (ConnectivityManager) context
	 .getSystemService(Context.CONNECTIVITY_SERVICE);
	 if (cm == null)
	 return false;
	 return cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
	 }
	 /**
	 * 打开网络设置界面
	 */
	 public static void openSetting(Activity activity)
	 {
	 Intent intent = new Intent("/");
	 ComponentName cm = new ComponentName("com.android.settings",
	 "com.android.settings.WirelessSettings");
	 intent.setComponent(cm);
	 intent.setAction("android.intent.action.VIEW");
	 activity.startActivityForResult(intent, 0);
	 }

}