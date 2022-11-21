package com.hkhs.hmms.haa;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.hkhs.hmms.haa.BallotBean.FLAT_STATUS;
import com.hkhs.hmms.haa.entity.BallotClass;
import com.hkhs.hmms.haa.entity.FlatClass;
import com.hkhs.hmms.haa.entity.OfferEntityClass;
import com.hkhs.hmms.haa.entity.ReportBean;
import com.hkhs.hmms.haa.entity.Result;
import com.hkhs.hmms.haa.entity.ResultBuilder;
import com.hkhs.hmms.haa.util.DBConnection;
import com.hkhs.hmms.haa.util.DataUtil;

/**
 * @author TuWei 14/2/2019
 */
public class OfferBean {

	private final static String SQL_QUERY_OFFER_RESULT = "select RA_NO,RA_PROP_REF,RA_PROP_REF_2ND,RA_NAME,RA_TOTAL_PERSON,"
			+ "hsk_haa_ra.get_application_categories(RA_NO) as categories," + "RO_PROP_REF as NEW_FLAT,"
			+ "hsk_haa_ra.get_new_flat_size(RA_NO,?,1) as NEW_FLAT_SIZE," + "decode(RA_CGLS_FLAG,'S','Y',RA_CGLS_FLAG),"
			+ "hsk_haa_ra.get_new_flat_prop(RA_NO,?,2) as NEW_FLAT2,"
			+ "hsk_haa_ra.get_new_flat_size(RA_NO,?,2) as NEW_FLAT2_SIZE," + "RO_REMARK "
			+ "from HST_HAA_RA_OFFER, HST_HAA_REDEV_APPLICATION "
			+ "WHERE RO_RA_NO = RA_NO AND RO_RM_CGLS_FLAT_SEQ_NO <> 2 AND RA_PRJ_CODE = ? AND RO_RB_NO = ? ";

	private final static String SQL_OFFER_ACCEPT = "call HSK_HAA_RA.accept_offer(?,?,?,?,?) ";

	private final static String SQL_OFFER_REJECT = "call HSK_HAA_RA.reject_offer(?,?,?,?,?,?,?) ";

	private final static String SQL_OFFER_DIRECT_ASSIGN = "call HSK_HAA_RA.direct_assign(?,?,?,?,?,?,?,?) ";

	private final static String SQL_UPDATE_APPLICATION = "update HST_HAA_REDEV_APPLICATION set RA_GENERAL_REMARK = ? ,RA_UPDATE_BY =?,RA_UPDATE_DATE = sysdate where RA_NO = ?";

	/**
	 * Query flat for direct assign
	 */
	private final static String SQL_QUERY_RA_FLAT_FOR_DIRECT_ASSIGN = "select UNIQUE(PROPERTY_REFNO),ESTATE_CODE,FLATTYPE,BLOCK,FLOOR,UNIT,ADDRESS from hst_haa_redev_flat_view "
			+ " where PROPERTY_REFNO = ? and PROPERTY_REFNO not in (select FLS_PROP_REF from hst_haa_ra_flat_sequence where FLS_PRJ_CODE = ? AND FLS_STATUS = '"
			+ FLAT_STATUS.ACCEPT_OFFER + "')";

	/**
	 * Get seed no from ballot
	 */
	private final static String SQL_QUERY_BALLOT_DETAIL = "select RB_PRJ_CODE,RB_INPUT_REMARK,TO_CHAR(RB_INPUT_DATE,'dd/mm/yyyy'),RB_INPUT_BY,RB_UPLOAD_REMARK,RB_UPLOAD_DATE,RB_UPLOAD_BY,RB_SEED_NO,RB_BALLOT_DATE from HST_HAA_RA_BALLOT where RB_NO = ?";

	private Connection conn = null;
	private PreparedStatement psmt = null;
	private ResultSet rs = null;
	private CallableStatement proc = null;

	/**
	 * Query flat allocation report by page.
	 * 
	 * @param prjCode
	 * @param rbNo
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public String queryOfferList(String prjCode, int rbNo) {
		List<OfferEntityClass> offerList = new ArrayList<OfferEntityClass>(64);
		try {
			conn = DBConnection.getConnection();
			String sql = SQL_QUERY_OFFER_RESULT;
			psmt = conn.prepareStatement(sql);
			psmt.setInt(1, rbNo);
			psmt.setInt(2, rbNo);
			psmt.setInt(3, rbNo);
			psmt.setString(4, prjCode);
			psmt.setInt(5, rbNo);
			rs = psmt.executeQuery();
			while (rs.next()) {
				OfferEntityClass entity = new OfferEntityClass();
				entity.setRaNo(rs.getString(1));
				entity.setExistingFlatUnit1(rs.getString(2));
				entity.setExistingFlatUnit2(rs.getString(3));
				entity.setRaName(rs.getString(4));
				entity.setRequiredHeads(rs.getInt(5));
				entity.setCategories(rs.getString(6));
				entity.setNewFlatUnit(rs.getString(7));
				entity.setNewFlatSize(rs.getString(8));
				entity.setCgls(rs.getString(9));
				entity.setNewFlat2Unit(rs.getString(10));
				entity.setNewFlat2Size(rs.getString(11));
				entity.setRemark(rs.getString(12));
				offerList.add(entity);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return ResultBuilder.buildServerFailResult(e.getMessage()).toString();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		return ResultBuilder.buildSuccessResult(offerList).toString();
	}

	/**
	 * Get Ballot detail by RB NO.
	 */
	public BallotClass getBallotDetailBy(String rbNo) {
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
			e.printStackTrace();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		return entity;
	}

	/**
	 * Get flat allocation report url
	 * 
	 * @param reportId
	 * @param prjCode
	 * @param rbNo
	 * @return
	 */
	public String generateFlatAllocationReport(String reportId, String prjCode, String rbNo) {

		BallotClass ballot = getBallotDetailBy(rbNo);

		ReportBean reportBean = new ReportBean();

		String baseUrl = reportBean.genrateBaseUrl(reportId);

		baseUrl += "&lsSPRJCODE=" + prjCode + "&lsSRBNO=" + rbNo + "&lsSINPUTDATE=" + ballot.getInputDate()
				+ "&lsSSEEDNO=" + ballot.getSeedNO();

		return baseUrl;
	}

	/**
	 * Accept offer(s). Bulk offers was accepted in loop and index was default 0.
	 * 
	 * @param ranos
	 * @param adminName
	 * @return
	 */
	public String acceptOffer(String[] ranos, int index, String acDate, String raRemark, String adminName) {

		if (DataUtil.isEmpty(ranos)) {
			return ResultBuilder.buildResult("Application no can not be empty").setErrorCode(Result.ERROR_CODE_INVALID)
					.toString();
		}
		StringBuffer resultStr = new StringBuffer();
		
		if (DataUtil.isNotEmpty(raRemark)) {
			try {
				String sqlUpdApp = SQL_UPDATE_APPLICATION;
				conn = DBConnection.getConnection();
				psmt = conn.prepareStatement(sqlUpdApp);
				psmt.setString(1, raRemark);
				psmt.setString(2, adminName);
				psmt.setString(3, ranos[0]);
				psmt.executeUpdate();
				DBConnection.closeConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
				return ResultBuilder.buildServerFailResult("Fail").toString();
			} finally {
				DBConnection.closeResultSet(rs);
				DBConnection.closePreparedStatement(psmt);
				DBConnection.closeConnection(conn);
			}
		}
		
		try {
			conn = DBConnection.getConnection();
			DBConnection.beginTransaction(conn);
			String sql = SQL_OFFER_ACCEPT;
			DBConnection.beginTransaction(conn);
			proc = conn.prepareCall(sql);
			for (int i = 0; i < ranos.length; i++) {
				proc.setString(1, ranos[i]);
				proc.setInt(2, index);
				proc.setString(3, adminName);
				proc.setString(4, acDate);
				proc.registerOutParameter(5, Types.VARCHAR);
				proc.execute();
				if (proc.getString(5) != null) {
					resultStr.append(proc.getString(5));
				}
			}
			DBConnection.commit(conn);
			if (DataUtil.isEmpty(resultStr.toString())) {
				return ResultBuilder.buildSuccessResult().toString();
			}
		} catch (Exception e) {
			DBConnection.rollback(conn);
			e.printStackTrace();
			return ResultBuilder.buildServerFailResult(e.getMessage()).toString();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(proc);
			DBConnection.closeConnection(conn);
		}
		return ResultBuilder.buildResult("Accept fail" + resultStr.toString()).setErrorCode(Result.ERROR_CODE_FAIL)
				.toString();
	}

	/**
	 * Reject offer(s),bulk offers was rejected in loop.
	 * 
	 * @param ranos
	 * @param adminName
	 * @param rjCode
	 * @param rjDes
	 * @return
	 */
	public String rejectOffer(String[] ranos, int index, String rjCode, String rjDes, String rjDate,
			String raRemark, String adminName) {
		if (DataUtil.isEmpty(ranos)) {
			return ResultBuilder.buildResult("Application no can not be empty").setErrorCode(Result.ERROR_CODE_INVALID)
					.toString();
		}
		if (DataUtil.isNotEmpty(raRemark)) {
			try {
				String sqlUpdApp = SQL_UPDATE_APPLICATION;
				conn = DBConnection.getConnection();
				psmt = conn.prepareStatement(sqlUpdApp);
				psmt.setString(1, raRemark);
				psmt.setString(2, adminName);
				psmt.setString(3, ranos[0]);
				psmt.executeUpdate();
				DBConnection.closeConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
				return ResultBuilder.buildServerFailResult("Fail").toString();
			} finally {
				DBConnection.closeResultSet(rs);
				DBConnection.closePreparedStatement(psmt);
				DBConnection.closeConnection(conn);
			}
		}
		StringBuffer resultStr = new StringBuffer();
		try {
			conn = DBConnection.getConnection();
//			DBConnection.beginTransaction(conn);
		    String sql = SQL_OFFER_REJECT;
			
			proc = conn.prepareCall(sql);
			String tmpResult = null;
			for (int i = 0; i < ranos.length; i++) {
				proc.setString(1, ranos[i]);
				proc.setInt(2, index);
				proc.setString(3, adminName);
				proc.setString(4, rjCode);
				proc.setString(5, rjDes);
				proc.setString(6, rjDate);
				proc.registerOutParameter(7, Types.VARCHAR);
				proc.execute();
//				DBConnection.commit(conn);
				tmpResult = proc.getString(7);
				if (tmpResult != null) {
					System.out.println(tmpResult);
					resultStr.append(tmpResult);
				}
			}
			
			if (DataUtil.isEmpty(resultStr.toString())) {
				return ResultBuilder.buildSuccessResult().toString();
			}
		} catch (Exception e) {
			DBConnection.rollback(conn);
			e.printStackTrace();
			return ResultBuilder.buildServerFailResult(e.getMessage()).toString();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		return ResultBuilder.buildResult(resultStr.toString()).setErrorCode(Result.ERROR_CODE_FAIL).toString();
	}

	/**
	 * Query flat for direct assign
	 */
	public String getFlatDetail(String prjCode, String propNo) {
		String sql = SQL_QUERY_RA_FLAT_FOR_DIRECT_ASSIGN;
		if (DataUtil.isEmpty(propNo)) {
			return ResultBuilder.buildResult("Property no can not be empty").setErrorCode(Result.ERROR_CODE_INVALID)
					.toString();
		}
		FlatClass resultBean = new FlatClass();
		try {
			conn = DBConnection.getConnection();
			psmt = conn.prepareStatement(sql);
			psmt.setString(1, propNo);
			psmt.setString(2, prjCode);
			rs = psmt.executeQuery();
			if (rs.next()) {
				resultBean.setFlPropRef(rs.getString(1));
				resultBean.setFlEstateCode(rs.getString(2));
				resultBean.setFlBlock(rs.getString(3));
				resultBean.setFlFloor(rs.getString(4));
				resultBean.setFlUnit(rs.getString(5));
				resultBean.setFlAddress(rs.getString(6));
				return ResultBuilder.buildSuccessResult(resultBean).toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.buildServerFailResult(e.getMessage()).toString();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		return ResultBuilder.buildResult().setErrorCode(Result.ERROR_CODE_NOT_EXIST).toString();
	}

	/**
	 * Direct assign
	 * 
	 * @param rano
	 * @return
	 */
	public String directOffer(String rano, String propNo, String remark, String justification, String authorizedBy,
			String approvalDate, String admin) {
		String sql = SQL_OFFER_DIRECT_ASSIGN;
		if (DataUtil.isEmpty(rano)) {
			return ResultBuilder.buildResult("Application no can not be empty").setErrorCode(Result.ERROR_CODE_INVALID)
					.toString();
		}
		String resultProc = "";
		try {
			conn = DBConnection.getConnection();
			proc = conn.prepareCall(sql);
			proc.setString(1, rano);
			proc.setString(2, propNo);
			proc.setString(3, remark);
			proc.setString(4, justification);
			proc.setString(5, authorizedBy);
			proc.setString(6, approvalDate);
			proc.setString(7, admin);
			proc.registerOutParameter(8, Types.VARCHAR);
			proc.execute();
			// Get out parameter.
			resultProc = proc.getString(8);

			if (!DataUtil.isEmpty(resultProc) && !resultProc.startsWith("FAIL")) {
				return ResultBuilder.buildSuccessResult(resultProc).toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.buildServerFailResult(e.getMessage()).toString();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closeProc(proc);
			DBConnection.closeConnection(conn);
		}
		return ResultBuilder.buildServerFailResult(resultProc).toString();
	}

}
