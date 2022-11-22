package com.hkhs.hmms.haa.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.hkhs.hmms.haa.util.DataUtil;
import com.hkhs.hmms.haa.util.DBConnection;

public class ReportBean {

	private Connection conn = null;
	private PreparedStatement psmt = null;
	private ResultSet rs = null;

	private static final String SQL_CRS_REPORT_QUERY = "select r.RPT_MIS_SERVER,r.RPT_MIS_FOLDER_BASE,r.RPT_MIS_FOLDER_LV1,"
			+ "r.RPT_MIS_FOLDER_LV2,r.RPT_MIS_FOLDER_LV3,r.RPT_MIS_FOLDER_LV4,r.RPT_MIS_FOLDER_LV5,r.RPT_MIS_REPORT_NAME,r.RPT_MIS_TYPE,"
			+ "r.RPT_MIS_REFRESH,r.RPT_MIS_OUTPUTFORMAT from HST_MIS_REPORTS r WHERE RPT_ID='!'";

	private static final String SQL_MIS_RPT_KEY_INSERT_NEW = "insert into HST_MIS_RPT_KEY(RPTK_RPT_ID,RPTK_KEY,RPTK_LOGIN) values(?,?,?)";

	private static final String SQL_RMS_REPORTS_PARAMS_QUERY = "select p.RPTP_MULTI_SELECT,p.RPTP_DESCRIPTION "
			+ "from HST_MIS_RPT_PARAMS p " + "where  p.RPTP_RPT_ID = ? order by p.RPTP_SEQ asc ";

	private static final String SQL_RMS_REPORTS_KEY_QUERY = "SELECT LOVD_VALUE_CHAR FROM HST_HMMS_LIST_OF_VALUE_DETAIL WHERE LOVD_LOVH_CODE = '99 report generation key'";

	private String genrateReportParams(String reportId, String loginId, String params) throws SQLException {
		int i = 0;

		String[] arr = params.split("`");
		psmt = conn.prepareStatement(SQL_RMS_REPORTS_PARAMS_QUERY);
		psmt.setString(1, reportId);
		rs = psmt.executeQuery();
		StringBuilder buff = new StringBuilder();
		while (rs != null && rs.next()) {
			if (!DataUtil.isEmpty(rs.getString(1))) {
				if ("Y".equals(rs.getString(1).trim()))
					buff.append("&lsM" + rs.getString(2).trim() + "=" + arr[i].trim());
				else if ("N".equals(rs.getString(1).trim()))
					buff.append("&lsS" + rs.getString(2).trim() + "=" + arr[i].trim());
			}
			i++;
		}
		return buff.toString();
	}

	private String getReportKeyName() throws SQLException {
		String key = "";

		psmt = conn.prepareStatement(SQL_RMS_REPORTS_KEY_QUERY);
		rs = psmt.executeQuery();
		if (rs != null && rs.next()) {
			if (!DataUtil.isEmpty(rs.getString(1)))
				key = rs.getString(1).trim();
		}
		DBConnection.closeResultSet(rs);
		DBConnection.closePreparedStatement(psmt);
		return key;
	}

	public String genrateBaseUrl(String reportId) {
		String url = "";
		boolean haveParameter = false;
		String loginId = "";
		String estateCode = "";
		String reportKeyName = "";

		if (reportId.startsWith("`")) {
			haveParameter = true;

			reportId = reportId.substring(1);
			String[] params = reportId.split("`");
			loginId = params[0];
			estateCode = params[1];
			reportId = params[2];
		}
		try {
			conn = DBConnection.getConnection();
			DBConnection.beginTransaction(conn);

			reportKeyName = getReportKeyName();
			if (reportKeyName == "")
				reportKeyName = "RPTK_KEY";
			String sql = SQL_CRS_REPORT_QUERY;
			sql = DataUtil.strReplace(sql, "!", DataUtil.PS(reportId));
			psmt = conn.prepareStatement(sql);
			rs = psmt.executeQuery();
			StringBuilder buff = new StringBuilder();
			if (rs != null && rs.next()) {
				if (!DataUtil.isEmpty(rs.getString(1)))
					buff.append(rs.getString(1).trim());
				if (!DataUtil.isEmpty(rs.getString(2)))
					buff.append(rs.getString(2).trim());
				if (!DataUtil.isEmpty(rs.getString(3)))
					buff.append(",[" + rs.getString(3).trim() + "]");
				if (!DataUtil.isEmpty(rs.getString(4)))
					buff.append(",[" + rs.getString(4).trim() + "]");
				if (!DataUtil.isEmpty(rs.getString(5)))
					buff.append(",[" + rs.getString(5).trim() + "]");
				if (!DataUtil.isEmpty(rs.getString(6)))
					buff.append(",[" + rs.getString(6).trim() + "]");
				if (!DataUtil.isEmpty(rs.getString(7)))
					buff.append(",[" + rs.getString(7).trim() + "]");
				if (!DataUtil.isEmpty(rs.getString(8)))
					buff.append("&sDocName=" + rs.getString(8).trim());
				if (!DataUtil.isEmpty(rs.getString(9)))
					buff.append("&sType=" + rs.getString(9).trim());
				if (!DataUtil.isEmpty(rs.getString(10)))
					buff.append("&sRefresh=" + rs.getString(10).trim());
				if (!DataUtil.isEmpty(rs.getString(11)))
					buff.append("&sOutputFormat=" + rs.getString(11).trim());
			}

			url = buff.toString();

			if (haveParameter) {
				String rptKey = saveReportKey(reportId, loginId);
				String param = estateCode + "`" + rptKey;
				String reportParam = genrateReportParams(reportId, loginId, param);

				url += "&lsSRPT_ID=" + reportId;
				url += "&lsS" + reportKeyName + "=" + rptKey;

				url = url + reportParam;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			url = "FAIL:SQL:" + e.getErrorCode() + ":" + e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
			url = "FAIL:SYS:" + e.getMessage();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		return url;
	}

	private String saveReportKey(String reportId, String loginId) throws SQLException {
		String rptKey = "";
		// psmt = conn.prepareStatement(SQL_MIS_RPT_KEY_QUERY);
		// rs = psmt.executeQuery();
		// if(rs!=null&&rs.next()){
		// rptKey = rs.getString(1).trim();
		// }
		rptKey = "" + System.currentTimeMillis();
		psmt = conn.prepareStatement(SQL_MIS_RPT_KEY_INSERT_NEW);
		psmt.setString(1, reportId);
		psmt.setString(3, loginId.toLowerCase());
		psmt.setString(2, rptKey);
		psmt.executeUpdate();

		return rptKey;
	}
}
