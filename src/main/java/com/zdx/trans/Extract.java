package com.zdx.trans;

import java.io.File;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

	public static void main(String[] args) {
		// TODO Auto-generated method stub
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
				PreparedStatement ps_t = conn.prepareStatement("SELECT * FROM dbo.cms_Article where Id < " + (i+1) * mBatch + " AND ID > " + i * mBatch);
				PreparedStatement ps_tmp = conn.prepareStatement("SELECT count(*) as currentCount FROM dbo.cms_Article where Id < " + (i+1) * mBatch + " AND ID > " + i * mBatch);

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
					while(rs_t.next())
					{
						x = x + 1;
						logger.info("Handle " + x + "/" + nSmp + "...");
						News ns = new News();
						//新闻日期做目录
						Date cDate = rs_t.getDate("Dateline");//注意检查该字段是否=null
						SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMdd"); 
						ns.dateDir = formatter.format(cDate);
						//新闻URL做标题
						//String t = rs_t.getString("PageUrl").replaceAll("/", "_");
						//ns.fileName = t.substring(0,t.lastIndexOf(".")) + ".xml";
						ns.fileName = "" + rs_t.getInt("Id") + ".xml";

						ns.siteId = 0; //站点ID-----------------------------------------------

						ns.subtitle = ""; //稿件副题 ------------------------------------------
						ns.artAbstract = ""; //稿件摘要---------------------------------------
						ns.keyword = ""; //关键字1 关键字2...----------------------------------
						ns.tag = rs_t.getString("Tags"); //标签1 标签2...
						String s = rs_t.getString("Dateline");
						ns.nsDate = s.substring(0, s.lastIndexOf(".")); //稿件发布时间
						ns.source = rs_t.getString("Source");//稿件来源名称
						ns.regionId = 1;
						ns.author = rs_t.getString("Author");//作者
						ns.editor = rs_t.getString("Author");//编辑----------------
						ns.liability = rs_t.getString("ApprovedBy");//责任编辑
						ns.title = rs_t.getString("Subject"); //稿件标题

						ns.smallTitlePic = ""; //标题图片小-----------------------------------
						ns.middleTitlePic = ""; //标题图片中----------------------------------
						ns.bigTitlePic = ""; //标题图片大-------------------------------------

						ns.columnID = rs_t.getInt("ChannelId"); //原系统中栏目ID
						ns.content = rs_t.getString("Body"); //正文(相对路径和绝对路径)-------

						String destDir = System.getProperty("user.dir") + File.separator + ns.dateDir;
						String html = "<html><head><title> </title></head>" + ns.content + "</html>";
						Document doc = Jsoup.parse(html.toLowerCase());  
						Elements links = doc.getElementsByTag("a");
						for (Element link : links) {
							//处理附件
							String linkHref = link.attr("href");
							//System.out.println("ID = " + rs_t.getInt("Id") + "; link = " + linkHref);
							String linkText = link.text().trim();
							int f1 = linkHref.indexOf("www") + linkHref.indexOf(":"); //f1 < 0, 相对路径/本地文件
							int f2 = linkHref.indexOf("sxdygbjy"); //本站
							if (f1 < 0 || (f1 >= -1 && f2 >=0)) {
								String fileCode64 = getImgBase64("", linkHref);
								if (fileCode64 == ""){
									//本地文件，但是已经被删除(死链接)
									logger.warn("Dead link URL = " + linkHref + "; DBId = " + rs_t.getInt("Id"));
									//FileHandler.getImageByStr(fileCode64, destDir + File.separator + linkHref.replace("/", "_"));
								} else {
									ns.fileNameList.add(linkHref);
									ns.attdescList.add(linkText);
									ns.filecodeList.add(fileCode64);
								}
							} else {
								//外站链接
								logger.warn("Outer link URL = " + linkHref + "; DBId = " + rs_t.getInt("Id"));
							}
						}
						Elements imgs = doc.getElementsByTag("img");
						for (Element img : imgs) { 
							//处理图片图标
							String imgHref = img.attr("src");
							ns.fileNameList.add(imgHref);
							ns.attdescList.add("");
							String fileCode64 = getImgBase64("", imgHref);
							ns.filecodeList.add(fileCode64);
							FileHandler.getImageByStr(fileCode64, destDir + File.separator + imgHref.replace("/", "_"));
						}
						if (doc.text().length() > 20){
							ns.articleType = 1;//稿件类型，0：文章/1：组图/2：视频------------------								
						}
						GenerateXML.writeXML(ns);
					}
				}
				logger.warn("*********************************Wait 1 Minute ");
				TimeUnit.MINUTES.sleep(1);
			}
		} catch ( Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} 
	}

	public static String getImgBase64(String rootDir, String imgUrl){
		String str = "";
		if (rootDir.length() == 0){
			//本地路径为空，从远程获取数据
			if (imgUrl.indexOf("sxdygbjy") >= 0){
				//本站绝对路径				
				str = FileHandler.getImageStrByUrl(imgUrl);
			} else if (imgUrl.indexOf(":") < 0 && imgUrl.indexOf("www") < 0 && imgUrl.indexOf("sxdygbjy") < 0){
				//本站相对路径
				String rootUrl = "http://www.sxdygbjy.com";
				String imgPath = rootUrl + imgUrl;
				str = FileHandler.getImageStrByUrl(imgPath);
			} else {
				////外站图片，不抓取;
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
	}
}
