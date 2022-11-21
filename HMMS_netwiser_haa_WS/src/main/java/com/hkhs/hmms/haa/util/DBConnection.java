package com.hkhs.hmms.haa.util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection { 

	public static Connection getConnection(){
		return null;
	}

    public static void closeResultSet(ResultSet rs ){
    	try {
    		if(rs != null){
    			rs.close();
    		}
    	}catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    public static void closeProc(CallableStatement statement) {
    	try {
    		if(statement != null){
    			statement.close();
    		}
    	}catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    public static void closePreparedStatement(PreparedStatement psmt ){
    	try {
    		if(psmt != null){
				psmt.close();
			}
    	}catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    public static void closeConnection(Connection conn){
    	try {
    		if(conn != null){
    			conn.close();
    		}
    	}catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    public static void beginTransaction(Connection conn){
    	try {
    		if(conn != null){
    			conn.setAutoCommit(false);
    		}
    	}catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    public static void rollback(Connection conn){
    	try {
    		if(conn != null){
    			conn.rollback();
    		}
    	}catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    public static void commit(Connection conn){
    	try {
    		if(conn != null){
    			conn.commit();
    		}
    	}catch (SQLException e) {
			e.printStackTrace();
		}
    }
}
