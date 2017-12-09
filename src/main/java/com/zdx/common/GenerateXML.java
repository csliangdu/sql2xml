package com.zdx.common;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;  
import org.dom4j.DocumentHelper;  
import org.dom4j.Element;  
import org.dom4j.io.OutputFormat;  
import org.dom4j.io.XMLWriter;  


public class GenerateXML {  
	private static final Logger logger = LogManager.getLogger(GenerateXML.class);
	public static void writeXML(News ns)     
	{   
		Element root = DocumentHelper.createElement("FounderEnpML");  
		Document doucment = DocumentHelper.createDocument(root);
		Element packageElement = root.addElement("Package");
		Element articleTypeElement = packageElement.addElement("ArticleType");
		articleTypeElement.addText("" + ns.articleType);
		Element siteIdElement = packageElement.addElement("SiteId");
		siteIdElement.addText("" + ns.siteId);

		Element articleElement = root.addElement("Article");
		Element subtitleElement = articleElement.addElement("Subtitle");
		subtitleElement.addCDATA(ns.subtitle);
		Element AbstractElement = articleElement.addElement("Abstract");
		AbstractElement.addCDATA(ns.artAbstract);
		Element KeywordElement = articleElement.addElement("Keyword");
		KeywordElement.addCDATA(ns.keyword);
		Element TagElement = articleElement.addElement("Tag");
		TagElement.addCDATA(ns.tag);
		Element NsdateElement = articleElement.addElement("Nsdate");
		NsdateElement.addCDATA(ns.nsDate);
		Element SourceElement = articleElement.addElement("Source");
		SourceElement.addCDATA(ns.source);
		Element RegionElement = articleElement.addElement("Region");
		RegionElement.addText("" + ns.regionId);
		Element AuthorElement = articleElement.addElement("Author");
		AuthorElement.addCDATA(ns.author);
		Element EditorElement = articleElement.addElement("Editor");
		EditorElement.addCDATA(ns.editor);
		Element LiabilityElement = articleElement.addElement("Liability");
		LiabilityElement.addCDATA(ns.liability);
		Element TitleElement = articleElement.addElement("Title");
		TitleElement.addCDATA(ns.title);
		Element SmallTitlePicElement = articleElement.addElement("SmallTitlePic");
		SmallTitlePicElement.addCDATA(ns.smallTitlePic);
		Element MiddleTitlePicElement = articleElement.addElement("MiddleTitlePic");
		MiddleTitlePicElement.addCDATA(ns.middleTitlePic);
		Element BigTitlePicElement = articleElement.addElement("BigTitlePic");
		BigTitlePicElement.addCDATA(ns.bigTitlePic);
		Element ColumnIDElement = articleElement.addElement("ColumnID");
		ColumnIDElement.addText("" + ns.columnID);
		Element ContentElement = articleElement.addElement("Content");
		ContentElement.addCDATA(ns.content);

		Element AttachementElement = articleElement.addElement("Attachement");
		if (ns.articleType == 0 && ns.fileNameList.size() > 0){
			for(int i = 0; i < ns.fileNameList.size(); i++){
				Element fileElement = AttachementElement.addElement("file");
				fileElement.addAttribute("type", "" + ns.articleType);
				fileElement.addAttribute("length", "");
				Element filenameElement = fileElement.addElement("filename");
				filenameElement.addText(ns.fileNameList.get(i));
				Element attdescElement = fileElement.addElement("attdesc");
				attdescElement.addText(ns.attdescList.get(i));
				Element filecodeElement = fileElement.addElement("filecode");
				filecodeElement.addCDATA(ns.filecodeList.get(i));
			}
		} else if (ns.articleType == 1 && ns.attdescList.size() > 0){
			for(int i = 0; i < ns.attdescList.size(); i++){
				Element fileElement = AttachementElement.addElement("file");
				fileElement.addAttribute("type", "" + ns.articleType);
				fileElement.addAttribute("length", "");
				Element attdescElement = fileElement.addElement("attdesc");
				attdescElement.addText(ns.attdescList.get(i));
				Element filecodeElement = fileElement.addElement("filecode");
				filecodeElement.addCDATA(ns.filecodeList.get(i));
			}
		} else if (ns.articleType == 1 && ns.multiattachList.size() > 0){
			for(int i = 0; i < ns.multiattachList.size(); i++){
				Element fileElement = AttachementElement.addElement("file");
				fileElement.addAttribute("type", "" + ns.articleType);
				fileElement.addAttribute("length", "");
				Element MultiattachElement = fileElement.addElement("Multiattach");
				MultiattachElement.addCDATA(ns.multiattachList.get(i));
			}
		}

		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		String destDir = System.getProperty("user.dir") + File.separator + ns.sourceTable + File.separator + ns.channelId + File.separator + ns.dateDir;
		String destFile = destDir + File.separator + ns.fileName;
		File dir = new File(destDir);
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();
			if (!dir.exists() || !dir.isDirectory()){
				logger.error("创建目录失败，请检查是否存在同名文件");
				logger.error("destDir = " + destDir);
				logger.error("destFile = " + destFile);
			}
		}
		try {
			FileOutputStream file = new FileOutputStream(destFile);
			XMLWriter xml = new XMLWriter(file, format);
			xml.write(doucment);
			xml.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error("写入XML失败,系统找不到指定的路径;");
			logger.error("destDir = " + destDir);
			logger.error("destFile = " + destFile);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}  

}