package com.hkhs.hmms.haa.util;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.lang.StringUtils;

public class DBConnection {

	public static Connection getConnection() {
		Connection connection = null;
		Properties Config = new Properties();
		try {
			Config.load(DBConnection.class.getClassLoader().getResourceAsStream("jdbc.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Configuration Config = new Configuration("/jdbc.properties");
		// Config.initConfig("jdbc.properties");
		String jndi = Config.getProperty("jdbc.jndi");
		String profile = System.getProperty("spring.profiles.active");

		if (StringUtils.isNotBlank(jndi) && StringUtils.startsWithIgnoreCase(profile, "prd")) {
			try {
				/*
				 * Properties props = new Properties();
				 * props.put(Context.INITIAL_CONTEXT_FACTORY,
				 * "org.jboss.naming.remote.client.InitialContextFactory");
				 * props.put(Context.PROVIDER_URL, "remote://localhost:4447");
				 * //props.put(Context.SECURITY_PRINCIPAL, "admin");
				 * //props.put(Context.SECURITY_CREDENTIALS, "password");
				 */
				Context ctx = new InitialContext();
				Object datasourceRef = ctx.lookup(jndi);
				DataSource ds = (DataSource) datasourceRef;

				connection = ds.getConnection();
			} catch (Exception e) {
				System.out.println("fail to get connection by jndi " + e.getMessage());
				e.printStackTrace();
			}
			return connection;
		}

		try {
			System.out.println("-----Use jdbc for test------");
			String driverString = Config.getProperty("jdbc.class");
			String dburl = Config.getProperty("jdbc.url");
			String username = Config.getProperty("jdbc.username");
			String password = Config.getProperty("jdbc.password");

			Class.forName(driverString);
			connection = DriverManager.getConnection(dburl, username, password);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}

	public static void closeResultSet(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void closeProc(CallableStatement statement) {
		try {
			if (statement != null) {
				statement.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void closePreparedStatement(PreparedStatement psmt) {
		try {
			if (psmt != null) {
				psmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void closeConnection(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void beginTransaction(Connection conn) {
		try {
			if (conn != null) {
				conn.setAutoCommit(false);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void rollback(Connection conn) {
		try {
			if (conn != null) {
				conn.rollback();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void commit(Connection conn) {
		try {
			if (conn != null) {
				conn.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
