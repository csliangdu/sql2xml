package com.zdx.common;

import java.util.ArrayList;

public class News {
	public String sourceTable = "";
	public String dateDir = "";
	public String category = "";
	public String newsId = "";
	public String fileName = "";
	public int channelId = 001;
	public int articleType;
	public int siteId;
	public String subtitle="";
	public String artAbstract = "";
	public String keyword = "";
	public String tag = "";
	public String nsDate = "";
	public String source = "";
	public int regionId;				
	public String author = "";
	public String editor = "";
	public String liability = "";
	public String title = "";
	public String smallTitlePic = "";
	public String middleTitlePic = "";
	public String bigTitlePic = "";
	public int columnID;
	public String content = "";
	public ArrayList<String> fileNameList = new ArrayList<String>();
	public ArrayList<String> attdescList = new ArrayList<String>();
	public ArrayList<String> filecodeList = new ArrayList<String>();
	public ArrayList<String> multiattachList = new ArrayList<String>();
	
	public News(){
		
	}
}
