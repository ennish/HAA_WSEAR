package com.hkhs.hmms.haa;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import com.hkhs.hmms.haa.entity.BallotClass;
import com.hkhs.hmms.haa.entity.Result;
import com.hkhs.hmms.haa.entity.ResultBuilder;
import com.hkhs.hmms.haa.util.DBConnection;

/**
 * @author TuWei 17/08/2021
 */
public class ReportForCrystalBean {

	private Log logger = LogFactory.getLog(ReportForCrystalBean.class);
	
	private Connection conn = null;
	private PreparedStatement psmt = null;
	private ResultSet rs = null;
	/**
	 * Get seed no from ballot
	 */
	private final static String SQL_QUERY_BALLOT_DETAIL = "select RB_PRJ_CODE,RB_INPUT_REMARK,TO_CHAR(RB_INPUT_DATE,'dd/mm/yyyy'),RB_INPUT_BY,RB_UPLOAD_REMARK,RB_UPLOAD_DATE,RB_UPLOAD_BY,RB_SEED_NO,RB_BALLOT_DATE from HST_HAA_RA_BALLOT where RB_NO = ?";

	/**
	 * Get Ballot detail by RB NO.
	 * 
	 * @throws SQLException
	 */
	public BallotClass getBallotDetailBy(Connection conn, String rbNo) throws SQLException {
		BallotClass entity = new BallotClass();
		try {
			conn = DBConnection.getConnection();
			String sql = SQL_QUERY_BALLOT_DETAIL;
			psmt = conn.prepareStatement(sql);
			psmt.setString(1, rbNo);
			rs = psmt.executeQuery();
			if (rs.next()) {
				entity.setPrjCode(rs.getString(1));
				entity.setInputRemark(rs.getString(2));
				entity.setInputDate(rs.getString(3));
				entity.setInputBy(rs.getString(4));
				entity.setUploadRemark(rs.getString(5));
				entity.setUploadDate(rs.getString(6));
				entity.setUploadBy(rs.getString(7));
				entity.setSeedNO(rs.getString(8));
				entity.setBallotDate(rs.getString(9));
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
		}
		return entity;
	}

	public Result generateReport(String prjCode, String rbNo, String fileName) {

		String reportId = "";
		String generationUrl = "";
		String inputDate = "";
		String seedNo = "";
		// Get crystal generation URL and reportId
		try {
			conn = DBConnection.getConnection();
			BallotClass ballot = getBallotDetailBy(conn, rbNo);
			if (ballot == null) {
				return ResultBuilder.buildResult(Result.ERROR_CODE_NOT_EXIST, "Ballot not found");
			}
			inputDate = ballot.getBallotDate();
			seedNo = ballot.getSeedNO();
			psmt = conn.prepareStatement(
					"select LOVD_VALUE_CHAR from HST_HMMS_LIST_OF_VALUE_DETAIL where lovd_lovh_code = 'HAA2_CRYSTAL_REPORT_GENERATION_URL'");
			rs = psmt.executeQuery();
			if (rs.next()) {
				generationUrl = rs.getString(1);
			}
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			psmt = conn.prepareStatement(
					"select LOVD_VALUE_CHAR from HST_HMMS_LIST_OF_VALUE_DETAIL where lovd_lovh_code = 'HAA2_CRYSTAL_REPORT_REPORT_ID'");
			rs = psmt.executeQuery();
			if (rs.next()) {
				reportId = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.buildServerFailResult(e.getMessage());
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		try {
			doGetRequestForReport(generationUrl, reportId, prjCode, rbNo, seedNo, inputDate, fileName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return ResultBuilder.buildServerFailResult(e.getMessage());
			
		}
		return ResultBuilder.buildSuccessResult(generationUrl);
	}

	private void doGetRequestForReport(String generationUrl,String ReportID, String Param1, String Param2, String Param3, String Param4,
			String fileName) throws Exception {
		URI uri = null;
		try {
			uri = new URIBuilder(generationUrl).addParameter("ReportID", ReportID).addParameter("Param1", Param1).addParameter("Param2", Param2)
					.addParameter("Param3", Param3).addParameter("Param4", Param4).addParameter("FileName", fileName)
					.build();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			throw e1;
		}
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpGet = new HttpGet(uri);
			CloseableHttpResponse response = null;
			
			try {
				response = httpclient.execute(httpGet);
				if(response.getStatusLine().getStatusCode() != org.apache.http.HttpStatus.SC_OK) {
					logger.warn(String.format("Request for (%s) return a status code: %d ",uri.getRawQuery(),response.getStatusLine().getStatusCode()));
				}
				System.out.println(response.getStatusLine());
				logger.info(uri);
			} catch (Exception e) {
				throw e;
			} finally {
				response.close();
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (httpclient != null) {
					httpclient.close();
				}
			} catch (IOException e) {
				throw e;
			}
		}
	}
 
	public static void main(String[] args) {
		ReportForCrystalBean rptBean = new ReportForCrystalBean();
		try {
			rptBean.doGetRequestForReport("http://www.baidu.com", "a", "b", "c", "d", "e", "e");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Result checkReportGenerated(String fileName) {
		// Get crystal generation URL and reportId
		String rptPath = "";
		try {
			conn = DBConnection.getConnection();
			psmt = conn.prepareStatement(
					"select LOVD_VALUE_CHAR from  HST_HMMS_LIST_OF_VALUE_DETAIL where lovd_lovh_code = 'HAA2_CRYSTAL_REPORT_GENERATION_PATH'");
			rs = psmt.executeQuery();
			if (rs.next()) {
				rptPath = rs.getString(1);
			}
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.buildServerFailResult(e.getMessage());
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		if (!StringUtils.endsWith(rptPath, File.separator)) {
			rptPath += File.separator;
		}
		File file = new File(rptPath + fileName);
		if (!file.exists()) {
			return ResultBuilder.buildResult(Result.ERROR_CODE_NOT_EXIST, "Report not exists");
		}
		return ResultBuilder.buildSuccessResult();
	}
}
