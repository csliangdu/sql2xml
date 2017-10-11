package com.zdx.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.zdx.common.DataSourceFactory;
 
 
public class SQLSeverLink {
    private Connection conn;
     
    public void link()
    {
        try {
            conn = DataSourceFactory.getInstance().getConnection();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
     
    public void closeLink()
    {
        if(conn!=null)
            try {
                conn.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }
     
    public ResultSet select(String sql)
    {
        ResultSet rs= null;
         
        try {
            PreparedStatement partnames=conn.prepareStatement(sql);
            rs=partnames.executeQuery();
             
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
         
        return rs;
    }
     
    public void update(String sql)
    {
        try {
            Statement partnames=conn.createStatement();
            partnames.executeUpdate(sql);
            System.out.println("update success!");
             
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    } 
}
