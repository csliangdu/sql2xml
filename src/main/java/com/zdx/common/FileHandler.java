package com.zdx.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
	 * ��ȡ������,ת��ΪBase64�ַ��� 
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
			return new String(Base64.encodeBase64(data));// ����Base64��������ֽ������ַ���  
		}catch (FileNotFoundException e) {
			logger.warn("Url connection failed! URL = " + destUrl);
			//e.printStackTrace();
			return null;
		}catch (Exception e) {  
			e.printStackTrace();
			return null;
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
	 * ͼƬת����base64�ַ��� ��ͼƬ�� ��ת��Ϊ�ֽ������ַ��������������Base64���봦�� 
	 *  
	 * @return 
	 */  
	public static String getImageStrByFile(File file) {  
		InputStream in = null;  
		byte[] data = null;  
		// ��ȡͼƬ�ֽ�����  
		try {  
			in = new FileInputStream(file);  
			data = new byte[in.available()];  
			in.read(data);  
			in.close();  
		} catch (IOException e) {  
			e.printStackTrace();  
		}  
		// ���ֽ�����Base64����
		return new String(Base64.encodeBase64(data));// ����Base64��������ֽ������ַ��� 
	}  

	/** 
	 * base64�ַ���ת����ͼƬ ���ֽ������ַ�������Base64���벢����ͼƬ 
	 *  
	 * @param imgStr 
	 *            ��������(�ַ���) 
	 * @param path 
	 *            ���·�� 
	 * @return 
	 */  
	public static boolean getImageByStr(String imgStr, String path) {  
		if (imgStr == null) // ͼ������Ϊ��  
			return false;  
		try {  
			byte[] b = Base64.decodeBase64(imgStr);// Base64����  
			for (int i = 0; i < b.length; ++i) {  
				if (b[i] < 0) {// �����쳣����  
					b[i] += 256;  
				}  
			}  
			// ����jpegͼƬ  
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
	 * ��ͼƬת����Base64����
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
	 * ���ļ�ת����Base64����
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
		String str = FileHandler.getImageStrByUrl(url);  //��ȡ������,ת��ΪBase64�ַ�  
		System.out.println(str);  
		FileHandler.getImageByStr(str, path);           //��Base64�ַ�ת��ΪͼƬ  
		FileHandler.closeHttpConn();  
	}
}