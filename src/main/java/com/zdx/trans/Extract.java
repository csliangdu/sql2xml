package com.zdx.trans;

import java.io.File;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.zdx.common.DataSourceFactory;
import com.zdx.common.FileHandler;
import com.zdx.common.GenerateXML;
import com.zdx.common.News;

public class Extract {
	private static final Logger logger = LogManager.getLogger(Extract.class);
	private static String rootUrl = "http://www.sxdygbjy.com";
	public static void main(String[] args) {		
		ExtractArticle();
		ExtractInfo();
	}

	public static void ExtractInfo(){
		try {
			Connection conn = DataSourceFactory.getInstance().getConnection();	

			int n1 = 166000;// maxId = 165928, [0, maxId]
			PreparedStatement ps = conn.prepareStatement("SELECT MIN(INFO_ID) as minId, MAX(INFO_ID) as maxId, COUNT(*) as count FROM dbo.AT_INFO where INFO_ID < " + n1);
			ResultSet rs = ps.executeQuery();
			int nSmp = 0, maxId = 0;
			while(rs.next())
			{
				maxId = rs.getInt("maxId");	
				nSmp = rs.getInt("count");
			}

			if (nSmp > 0){
				ExtractSubInfo(conn, nSmp, maxId, 0);
			}

			int nStart = 10000000;//[nStart, maxId]
			ps = conn.prepareStatement("SELECT MIN(INFO_ID) as minId, MAX(INFO_ID) as maxId, COUNT(*) as count FROM dbo.AT_INFO where INFO_ID >= " + nStart);
			rs = ps.executeQuery();
			while(rs.next())
			{
				maxId = rs.getInt("maxId");	
				nSmp = rs.getInt("count");
			}
			if (nSmp > 0){
				ExtractSubInfo(conn, nSmp, maxId, nStart);
			}


		} catch ( Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} 
	}


	public static void ExtractSubInfo(Connection conn, int nSmp, int maxId, int nStart){
		int mBatch = 10;
		int nBatch = (int)Math.ceil(((double)(maxId - nStart))/mBatch);
		int x = 0;
		for (int i = 0; i < nBatch; i++){
			try {
				String sql1 = "SELECT * FROM dbo.AT_INFO where INFO_ID < " + ((i+1) * mBatch + nStart) + " AND INFO_ID >= " + (i * mBatch + nStart);
				PreparedStatement ps_t = conn.prepareStatement(sql1);
				logger.info("sql1 = " + sql1);
				String sql2 = "SELECT count(*) as currentCount FROM dbo.AT_INFO where INFO_ID < " + ((i+1) * mBatch + nStart) + " AND INFO_ID >= " + (i * mBatch + nStart);
				PreparedStatement ps_tmp = conn.prepareStatement(sql2);
				logger.info("sql2 = " + sql2);
				//for (int i = 0; i < 1; i++){
				//PreparedStatement ps_t = conn.prepareStatement("SELECT * FROM dbo.cms_Article where Id = 304057; ");
				ResultSet rs_tmp = ps_tmp.executeQuery();
				int currentCount = 0;
				while(rs_tmp.next())
				{
					currentCount = rs_tmp.getInt("currentCount");
				}
				if (currentCount > 0){
					ResultSet rs_t = ps_t.executeQuery();
					int y = 0;
					while(rs_t.next())
					{
						x = x + 1;
						y = y + 1;
						logger.info("Handle " + x + "/" + nSmp + "...");
						News ns = new News();
						//����������Ŀ¼
						Date cDate = rs_t.getDate("INFO_TIME");//ע������ֶ��Ƿ�=null
						SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMdd"); 
						ns.dateDir = formatter.format(cDate);
						ns.sourceTable = "AT_INFO";
						ns.channelId = rs_t.getInt("INFO_TYPE");

						//����URL������
						ns.fileName = "" + rs_t.getInt("INFO_ID") + ".xml";
						File f = new File(ns.dateDir + File.separator + ns.fileName);
						if (f.exists()){
							break;
						}
						ns.siteId = 0; //վ��ID-----------------------------------------------

						ns.subtitle = ""; //������� ------------------------------------------
						ns.artAbstract = ""; //���ժҪ---------------------------------------
						ns.keyword = ""; //�ؼ���1 �ؼ���2...----------------------------------
						ns.tag = ""; //��ǩ1 ��ǩ2...						
						ns.nsDate = rs_t.getString("INFO_TIME"); //�������ʱ��
						ns.source = rs_t.getString("INFO_AUTHOR");//�����Դ����
						ns.regionId = 1;
						ns.author = rs_t.getString("INFO_EXPLAIN");//����
						ns.editor = "";//�༭----------------
						ns.liability = "";//���α༭
						ns.title = rs_t.getString("INFO_TITLE"); //�������

						ns.smallTitlePic = ""; //����ͼƬС-----------------------------------
						ns.middleTitlePic = ""; //����ͼƬ��----------------------------------
						ns.bigTitlePic = ""; //����ͼƬ��-------------------------------------

						ns.columnID = rs_t.getInt("INFO_TYPE"); //ԭϵͳ����ĿID
						ns.content = rs_t.getString("INFO_VALUE"); //����(���·���;���·��)-------

						String destDir = System.getProperty("user.dir") + File.separator + ns.dateDir;
						String html = "<html><head><title> </title></head>" + ns.content + "</html>";
						Document doc = Jsoup.parse(html.toLowerCase());  
						HashSet<String> urls = new HashSet<String>();
						Elements links = doc.getElementsByTag("a");
						for (Element link : links) {
							logger.warn("URL size = " + links.size());
							//������
							String linkHref = link.attr("href");
							String tmp = linkHref.toLowerCase();
							boolean flag1 = tmp.indexOf("jpg") > 0 || tmp.indexOf("png") > 0 || tmp.indexOf("jpeg") > 0;
							boolean flag2 = tmp.indexOf("doc") > 0 || tmp.indexOf("xls") > 0 || tmp.indexOf("docx") > 0  || tmp.indexOf("xlsx") > 0 || tmp.indexOf("rar") > 0;
							if (flag1 || flag2){
								if (tmp.indexOf(":") < 0 && tmp.indexOf("www") < 0 && tmp.indexOf("sxdygbjy") < 0){
									tmp = rootUrl + linkHref;
								}
								if (tmp.indexOf("sxdygbjy") > 0){
									urls.add(tmp);
								}else {
									//��վ����
									logger.warn("Outer link URL = " + linkHref + "; DBId = " + rs_t.getInt("INFO_ID"));
								}
							} 
						}
						Elements imgs = doc.getElementsByTag("img");
						for (Element img : imgs) { 
							//����ͼƬͼ��

							String imgHref = img.attr("src");
							String tmp = imgHref.toLowerCase();
							boolean flag1 = tmp.indexOf("jpg") > 0 || tmp.indexOf("png") > 0 || tmp.indexOf("jpeg") > 0;
							boolean flag2 = tmp.indexOf("doc") > 0 || tmp.indexOf("xls") > 0 || tmp.indexOf("docx") > 0  || tmp.indexOf("xlsx") > 0 || tmp.indexOf("rar") > 0;
							if (flag1 || flag2){
								if (tmp.indexOf(":") < 0 && tmp.indexOf("www") < 0 && tmp.indexOf("sxdygbjy") < 0){
									tmp = rootUrl + imgHref;
								}
								if (tmp.indexOf("sxdygbjy") > 0){
									urls.add(tmp);
								} else {
									//��վ����
									logger.warn("Outer link URL = " + tmp + "; DBId = " + rs_t.getInt("INFO_ID"));
								}
							}
						}
						for (String url : urls){
							Map<String, String> map = FileHandler.getImageStrByUrl(url);
							if (Integer.parseInt(map.get("status")) != 202){
								//�����ļ��������Ѿ���ɾ��(������)
								logger.warn("Useless src URL = " + url + "; DBId = " + rs_t.getInt("Id") + "; status = " + map.get("status"));
							} else {
								ns.fileNameList.add(url);
								ns.attdescList.add("");
								ns.filecodeList.add(map.get("code64"));								
								logger.warn("Test src URL = " + url + "; DBId = " + rs_t.getInt("Id"));
								url = url.substring(url.indexOf(".com")+4);
								FileHandler.getImageByStr(map.get("code64"), destDir + File.separator + url.replace("/", "_"));
							}
						}
						GenerateXML.writeXML(ns);
					}
				}

			} catch ( Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			} 
		}
	}

	public static void ExtractArticle(){
		try {
			Connection conn = DataSourceFactory.getInstance().getConnection();	
			PreparedStatement ps = conn.prepareStatement("SELECT MIN(Id) as minId, MAX(Id) as maxId, COUNT(*) as count FROM dbo.cms_Article");
			ResultSet rs = ps.executeQuery();
			int nSmp = 0, maxId = 0;
			while(rs.next())
			{
				maxId = rs.getInt("maxId");	
				nSmp = rs.getInt("count");
			}
			int x = 0;
			int mBatch = 10;
			int nBatch = (int)Math.ceil(((double)maxId)/mBatch);
			//for (int i = 0; i < 1000; i++){
			for (int i = 0; i < nBatch; i++){
				PreparedStatement ps_t = conn.prepareStatement("SELECT * FROM dbo.cms_Article where Id < " + (i+1) * mBatch + " AND ID >= " + i * mBatch);
				PreparedStatement ps_tmp = conn.prepareStatement("SELECT count(*) as currentCount FROM dbo.cms_Article where Id < " + (i+1) * mBatch + " AND ID >= " + i * mBatch);

				//for (int i = 0; i < 1; i++){
				//PreparedStatement ps_t = conn.prepareStatement("SELECT * FROM dbo.cms_Article where Id = 304057; ");
				ResultSet rs_tmp = ps_tmp.executeQuery();
				int currentCount = 0;
				while(rs_tmp.next())
				{
					currentCount = rs_tmp.getInt("currentCount");
				}
				if (currentCount > 0){
					ResultSet rs_t = ps_t.executeQuery();
					int y = 0;
					while(rs_t.next())
					{
						x = x + 1;
						y = y + 1;
						logger.info("Handle " + x + "/" + nSmp + "...");
						News ns = new News();
						//����������Ŀ¼
						Date cDate = rs_t.getDate("Dateline");//ע������ֶ��Ƿ�=null
						SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMdd"); 
						ns.dateDir = formatter.format(cDate);
						//����URL������
						ns.fileName = "" + rs_t.getInt("Id") + ".xml";
						File f = new File(ns.dateDir + File.separator + ns.fileName);
						if (f.exists()){
							break;
						}
						ns.siteId = 0; //վ��ID-----------------------------------------------
						ns.channelId = rs_t.getInt("ChannelId");
						ns.sourceTable = "cms_Article";
						ns.subtitle = ""; //������� ------------------------------------------
						ns.artAbstract = ""; //���ժҪ---------------------------------------
						ns.keyword = ""; //�ؼ���1 �ؼ���2...----------------------------------
						ns.tag = rs_t.getString("Tags"); //��ǩ1 ��ǩ2...
						String s = rs_t.getString("Dateline");
						ns.nsDate = s.substring(0, s.lastIndexOf(".")); //�������ʱ��
						ns.source = rs_t.getString("Source");//�����Դ����
						ns.regionId = 1;
						ns.author = rs_t.getString("Author");//����
						ns.editor = rs_t.getString("Author");//�༭----------------
						ns.liability = rs_t.getString("ApprovedBy");//���α༭
						ns.title = rs_t.getString("Subject"); //�������

						ns.smallTitlePic = ""; //����ͼƬС-----------------------------------
						ns.middleTitlePic = ""; //����ͼƬ��----------------------------------
						ns.bigTitlePic = ""; //����ͼƬ��-------------------------------------

						ns.columnID = rs_t.getInt("ChannelId"); //ԭϵͳ����ĿID
						ns.content = rs_t.getString("Body"); //����(���·���;���·��)-------

						String html = "<html><head><title> </title></head>" + ns.content + "</html>";
						logger.warn("CONTENT = " + ns.content );
						Document doc = Jsoup.parse(html.toLowerCase());  
						Elements links = doc.getElementsByTag("a");
						String destDir = System.getProperty("user.dir") + File.separator + ns.dateDir;
						HashSet<String> urls = new HashSet<String>();
						for (Element link : links) {
							//������
							String linkHref = link.attr("href");
							logger.warn("Handling URL = " + linkHref );
							String tmp = linkHref.toLowerCase();
							boolean flag1 = tmp.indexOf("jpg") > 0 || tmp.indexOf("png") > 0 || tmp.indexOf("jpeg") > 0;
							boolean flag2 = tmp.indexOf("doc") > 0 || tmp.indexOf("xls") > 0 || tmp.indexOf("docx") > 0  || tmp.indexOf("xlsx") > 0;
							if (flag1 || flag2){
								if (tmp.indexOf(":") < 0 && tmp.indexOf("www") < 0 && tmp.indexOf("sxdygbjy") < 0){
									tmp = rootUrl + linkHref;
								}
								if (tmp.indexOf("sxdygbjy") > 0){
									urls.add(tmp);									
								} else {
									//��վ����
									logger.warn("    Outer link URL = " + tmp + "; DBId = " + rs_t.getInt("Id"));
								}
							}
						}

						Elements imgs = doc.getElementsByTag("img");
						for (Element img : imgs) { 
							//����ͼƬͼ��
							String imgHref = img.attr("src");
							String tmp = imgHref.toLowerCase();
							boolean flag1 = tmp.indexOf("jpg") > 0 || tmp.indexOf("png") > 0 || tmp.indexOf("jpeg") > 0;
							boolean flag2 = tmp.indexOf("doc") > 0 || tmp.indexOf("xls") > 0 || tmp.indexOf("docx") > 0  || tmp.indexOf("xlsx") > 0 || tmp.indexOf("rar") > 0;
							if (flag1 || flag2){
								if (tmp.indexOf(":") < 0 && tmp.indexOf("www") < 0 && tmp.indexOf("sxdygbjy") < 0){
									tmp = rootUrl + imgHref;
								}
								if (tmp.indexOf("sxdygbjy") > 0){
									urls.add(tmp);
								} else {
									//��վ����
									logger.warn("Outer src URL = " + tmp + "; DBId = " + rs_t.getInt("Id"));
								}
							}
						}

						for (String url : urls){
							Map<String, String> map = FileHandler.getImageStrByUrl(url);
							if (Integer.parseInt(map.get("status")) != 202){
								//�����ļ��������Ѿ���ɾ��(������)
								logger.warn("Useless src URL = " + url + "; DBId = " + rs_t.getInt("Id") + "; status = " + map.get("status"));
							} else {
								ns.fileNameList.add(url);
								ns.attdescList.add("");
								ns.filecodeList.add(map.get("code64"));								
								logger.warn("Test src URL = " + url + "; DBId = " + rs_t.getInt("Id"));
								url = url.substring(url.indexOf(".com")+4);
								FileHandler.getImageByStr(map.get("code64"), destDir + File.separator + url.replace("/", "_"));
							}
						}

						GenerateXML.writeXML(ns);
					}
				}
			}
		} catch ( Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} 
	}
	/*
	public static String getImgBase64(String rootDir, String imgUrl){
		String str = "";
		if (rootDir.length() == 0){
			//����·��Ϊ�գ���Զ�̻�ȡ����
			if (imgUrl.indexOf("sxdygbjy") >= 0){
				//��վ����·��				
				str = FileHandler.getImageStrByUrl(imgUrl);
			} else if (imgUrl.indexOf(":") < 0 && imgUrl.indexOf("www") < 0 && imgUrl.indexOf("sxdygbjy") < 0){
				//��վ���·��
				String rootUrl = "http://www.sxdygbjy.com";
				String imgPath = rootUrl + imgUrl;
				str = FileHandler.getImageStrByUrl(imgPath);
			} else {
				////��վͼƬ����ץȡ;
			}						
		} else {
			String imgDir = rootDir;
			String[] localPaths = imgUrl.split("/");
			for (int i = 0; i< localPaths.length; i++){
				if (localPaths[i].length() > 0){
					imgDir = imgDir + File.separator + localPaths[i];
				}
			}
			File file = new File(imgDir);
			if (file.exists()){
				str = FileHandler.getImageStrByFile(file); 
			}
		}
		return str;
	}*/
}
