package com.zdx.common;

//import org.apache.commons.dbcp2.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.*;
import java.sql.DriverManager;

public class DataSourceFactory {
	private static String PROPERTIES_FILE = "databaseresource.properties";
	private static DataSourceFactory instance = null;
	Properties prop = new Properties();

	private DataSourceFactory() {        
		try {
			File file = new File(System.getProperty("user.dir") + File.separator + PROPERTIES_FILE);
			InputStream inputStream = new FileInputStream(file);
			prop.load(inputStream);
		}  catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static DataSourceFactory getInstance() {
		if(instance == null) {
			instance = new DataSourceFactory();
		}
		return instance;
	}

	public Connection getConnection() {
		Connection conn = null;
		try {
			String driverName = prop.getProperty("driverClassName");
			String dbURL = prop.getProperty("url");
			String userName = prop.getProperty("username");
			String userPwd = prop.getProperty("password");
			Class.forName(driverName);
			conn= DriverManager.getConnection(dbURL,userName,userPwd);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return conn;
	}
}
