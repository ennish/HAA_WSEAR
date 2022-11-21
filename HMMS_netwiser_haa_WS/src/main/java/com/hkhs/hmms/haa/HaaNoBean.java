package com.hkhs.hmms.haa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.hkhs.hmms.haa.util.DBConnection;

public class HaaNoBean {
	private Connection conn = null;
	private PreparedStatement psmt = null;
	private ResultSet rs = null;

	public HaaNoBean(Connection conn) {
		this.conn = conn;
	}

	public String genRaNo(String estateCode) throws SQLException {
		int maxid = 0;
		estateCode = estateCode.trim();
		String sql = ("UPDATE HST_HAA_REDEV_PROJECT SET RP_APP_NEXT_SEQ = NVL(RP_APP_NEXT_SEQ,0) + 1 WHERE RTRIM(RP_ESTATE_CODE)='"
				+ estateCode + "'");
		psmt = conn.prepareStatement(sql);
		psmt.executeUpdate();
		DBConnection.closePreparedStatement(psmt);
		
		String sqlQ = ("SELECT RP_APP_NEXT_SEQ FROM HST_HAA_REDEV_PROJECT WHERE RTRIM (RP_ESTATE_CODE)='" + estateCode
				+ "'");
		psmt = conn.prepareStatement(sqlQ);
		rs = psmt.executeQuery();
		if (rs != null && rs.next()) {
			String s = rs.getString(1);
			if (s != null) {
				maxid = Integer.parseInt(s);
			}
		}
		// $$$2018 maxid++;
		String id = Integer.toString(maxid);
		int len = id.length();
		for (int i = 0; i < 7 - len; i++)
			id = "0" + id;
		DBConnection.closeResultSet(rs);
		DBConnection.closePreparedStatement(psmt);
		return estateCode + "-RA-" + id;
	}

	public String genRaNoByPrjCode(String prjCode) throws SQLException {
		int maxid = 0;
		prjCode = prjCode.trim();
		String sql = ("UPDATE HST_HAA_REDEV_PROJECT SET RP_APP_NEXT_SEQ = NVL(RP_APP_NEXT_SEQ,0) + 1 WHERE RTRIM(RP_PRJ_CODE)='"
				+ prjCode + "'");
		psmt = conn.prepareStatement(sql);
		psmt.executeUpdate();
		String sqlQ = ("SELECT RP_APP_NEXT_SEQ FROM HST_HAA_REDEV_PROJECT WHERE RTRIM (RP_PRJ_CODE)='" + prjCode + "'");
		psmt = conn.prepareStatement(sqlQ);
		rs = psmt.executeQuery();
		if (rs != null && rs.next()) {
			String s = rs.getString(1);
			if (s != null) {
				maxid = Integer.parseInt(s);
			}
		}
		// $$$2018 maxid++;
		String id = Integer.toString(maxid);
		int len = id.length();
		for (int i = 0; i < 7 - len; i++)
			id = "0" + id;
		DBConnection.closeResultSet(rs);
		DBConnection.closePreparedStatement(psmt);
		return prjCode + "-RA-" + id;
	}
}