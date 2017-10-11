package com.zdx.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.*;


public class FileHandler {
	private static HttpURLConnection httpUrl = null;
	private static final Logger logger = LogManager.getLogger();
	//private static Logger logger = Logger..getLogger(FileHandler.class);

	public static void closeHttpConn(){  
		httpUrl.disconnect();  
	}  
	/** 
	 * 读取输入流,转换为Base64字符串 
	 * @param input 
	 * @return 
	 */  
	public static String getImageStrByUrl(String destUrl) {  
		URL url = null;  
		InputStream in = null; 
		byte[] data = null;  
		try{  
			url = new URL(destUrl);  
			httpUrl = (HttpURLConnection) url.openConnection();  
			httpUrl.connect();
			in = httpUrl.getInputStream();
			data = readInputStream(in);
			httpUrl.disconnect();
			FileHandler.closeHttpConn();
			return new String(Base64.encodeBase64(data));// 返回Base64编码过的字节数组字符串  
		}catch (FileNotFoundException e) {
			logger.warn("Dead link URL = " + destUrl);
			return "";
		}catch (ConnectException e) {
			logger.warn("Connection Refused, please wait... URL = " + destUrl);
			logger.warn("*********************************Wait 1 Minute *********************************");
			try {
				TimeUnit.MINUTES.sleep(1);
				return getImageStrByUrl(destUrl);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				logger.warn("*********************************Wait failed *********************************");
				e1.printStackTrace();
				return "";
			}
			//e.printStackTrace();
			//return "";
			
		}catch (Exception e) {  
			e.printStackTrace();
			return "";
		}
	}  

	public static byte[] readInputStream(InputStream inStream) throws Exception{  
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];  
		int len = 0; 
		while( (len=inStream.read(buffer)) != -1 ){  
			outStream.write(buffer, 0, len);  
		}
		inStream.close();  
		return outStream.toByteArray();  
	} 

	/** 
	 * 图片转化成base64字符串 将图片文 件转化为字节数组字符串，并对其进行Base64编码处理 
	 *  
	 * @return 
	 */  
	public static String getImageStrByFile(File file) {  
		InputStream in = null;  
		byte[] data = null;  
		// 读取图片字节数组  
		try {  
			in = new FileInputStream(file);  
			data = new byte[in.available()];  
			in.read(data);  
			in.close();  
		} catch (IOException e) {  
			e.printStackTrace();  
		}  
		// 对字节数组Base64编码
		return new String(Base64.encodeBase64(data));// 返回Base64编码过的字节数组字符串 
	}  

	/** 
	 * base64字符串转化成图片 对字节数组字符串进行Base64解码并生成图片 
	 *  
	 * @param imgStr 
	 *            数据内容(字符串) 
	 * @param path 
	 *            输出路径 
	 * @return 
	 */  
	public static boolean getImageByStr(String imgStr, String path) {  
		if (imgStr == null) // 图像数据为空  
			return false;  
		try {  
			byte[] b = Base64.decodeBase64(imgStr);// Base64解码  
			for (int i = 0; i < b.length; ++i) {  
				if (b[i] < 0) {// 调整异常数据  
					b[i] += 256;  
				}  
			}  
			// 生成jpeg图片  
			OutputStream out = new FileOutputStream(path);  
			out.write(b);  
			out.flush();  
			out.close();  
			return true;  
		} catch (Exception e) {  
			return false;  
		}  
	}  

	/**
	 * 将图片转换成Base64编码
	 */
	public static String getBase64FromImg(String imgFile){
		InputStream in = null;
		byte[] data = null;
		try	{
			in = new FileInputStream(imgFile);        
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String(Base64.encodeBase64(data));
	}
	/**
	 * 将文件转换成Base64编码
	 */
	public static String getBase64FromFile(String path) throws Exception {  
		File file = new File(path);;  
		FileInputStream inputFile = new FileInputStream(file);  
		byte[] buffer = new byte[(int) file.length()];  
		inputFile.read(buffer);  
		inputFile.close();  
		return new String(Base64.encodeBase64(buffer)); 

	}

	public void testFileHandler(String url, String path){
		String str = FileHandler.getImageStrByUrl(url);  //读取输入流,转换为Base64字符  
		System.out.println(str);  
		FileHandler.getImageByStr(str, path);           //将Base64字符转换为图片  
		FileHandler.closeHttpConn();  
	}
}
