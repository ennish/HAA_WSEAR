package com.hkhs.hmms.haa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hkhs.hmms.haa.config.DataSourceConfig;
import com.hkhs.hmms.haa.entity.ApplicationCategoryClass;
import com.hkhs.hmms.haa.entity.ApplicationClass;
import com.hkhs.hmms.haa.entity.ApplicationClass.CGLS_FLAG;
import com.hkhs.hmms.haa.entity.ApplicationPersonClass;
import com.hkhs.hmms.haa.entity.CGLSFlatClass;
import com.hkhs.hmms.haa.entity.Result;
import com.hkhs.hmms.haa.entity.ResultBuilder;
import com.hkhs.hmms.haa.queryEntity.ApplicationQueryObj;
import com.hkhs.hmms.haa.util.DBConnection;
import com.hkhs.hmms.haa.util.DataUtil;

import net.sf.json.JSONObject;

/**
 * @author TuWei 14/2/2019
 */
@Service
public class ApplicationBean {
	@Autowired
	private DataSourceConfig dataSourceConfig;

	private final static String SQL_QUERY_PROPERTY_INFO = "select HSK_HAA_RA.get_property_info(?, ?) from dual";

	private final static String SQL_QUERY_HOUSEHOLD_PERSON = "select HSK_HAA_RA.get_household_person (?) from dual";

	private final static String SQL_QUERY_OFFER_HISTORY = "select HSK_HAA_RA.get_offer_history (?) from dual";

	private final static String SQL_UPDATE_STATUS_APPLICATION = "select HSK_HAA_RA.change_status(?,?,?) from dual";

	private final static String SQL_INSERT_REDEV_APPLICATION = "INSERT INTO HST_HAA_REDEV_APPLICATION ( RA_NO , RA_PRJ_CODE  , RA_ESTATE_CODE  , RA_STATUS  , RA_TENANCY_REF  , RA_NAME ,"
			+ " RA_PROP_REF , RA_PROP_ENG_ADDRESS  , RA_PROP_CHN_ADDRESS , RA_PROP_REF_2ND , RA_PROP_ENG_ADDRESS_2ND, RA_PROP_CHN_ADDRESS_2ND, RA_COT_DATE  , RA_CGLS_FLAG    , "
			+ "	RA_EXPECT_BABY  , RA_TEMP_STAY    , RA_TOTAL_PERSON , RA_RECEIVE_DATE , RA_CREATE_BY  , RA_CREATE_DATE , RA_GENERAL_REMARK)VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?, to_date(?,'dd/MM/yyyy'),?,?,?,?,to_date(?,'dd/MM/yy'),?,sysdate,?)";

	private final static String SQL_INSERT_RA_CATEGORY = "INSERT INTO HST_HAA_RA_CATEGORY(RC_RA_NO,RC_CATEGORY,RC_CREATE_BY,RC_CREATE_DATE,RC_UPDATE_BY,RC_UPDATE_DATE) "
			+ " VALUES (?,?,?,SYSDATE,?,SYSDATE)";

	private final static String SQL_INSERT_RA_CGLS_FALT = "INSERT INTO HST_HAA_RA_CGLS_FLAT(RF_RA_NO,RF_SEQ_NO,RF_FLAT_SIZE,RF_OTHER_REQ,RF_TOTAL_PERSON,RF_CREATE_BY,RF_CREATE_DATE,RF_UPDATE_BY,RF_UPDATE_DATE)"
			+ " VALUES(?,?,?,?,?,?,SYSDATE,?,SYSDATE)";

	private final static String SQL_INSERT_RA_PERSON = "INSERT INTO HST_HAA_RA_PERSON(RP_RA_NO,RP_SEQ_NO,RP_ENG_NAME,RP_CHN_NAME,RP_RELATIONSHIP,RP_CREATE_BY,RP_CREATE_DATE,RP_UPDATE_BY,RP_UPDATE_DATE) VALUES(?,?,?,?,?,?,SYSDATE,?,SYSDATE)";

	private final static String SQL_UPDATE_REDEV_APPLICATION = "UPDATE HST_HAA_REDEV_APPLICATION SET RA_EXPECT_BABY = ?,RA_TEMP_STAY = ?,RA_CGLS_FLAG = ? ,RA_RECEIVE_DATE = to_date(?,'dd/MM/yyyy'),RA_UPDATE_BY=?,RA_TOTAL_PERSON = ?,RA_GENERAL_REMARK = ?, RA_UPDATE_DATE=sysdate WHERE RA_NO = ?";

	private final static String SQL_UPDATE_COMMENT = "UPDATE HST_HAA_REDEV_APPLICATION SET RA_GENERAL_REMARK = ? ,RA_UPDATE_BY = ? ,RA_UPDATE_DATE = SYSDATE WHERE RA_NO = ?";

	private final static String SQL_DELETE_RA_CATEGORY = "DELETE FROM HST_HAA_RA_CATEGORY WHERE RC_RA_NO = ?";

	private final static String SQL_DELETE_RA_CGLS_FLAT = "DELETE FROM HST_HAA_RA_CGLS_FLAT WHERE RF_RA_NO = ?";

	// private final static String SQL_CHANGE_STATUS_RA_APPLICATION = "UPDATE
	// HST_HAA_REDEV_APPLICATION SET RA_STATUS = ?, RA_UPDATE_BY=?,
	// RA_UPDATE_DATE=sysdate WHERE RA_NO = ? AND <`cond`> ";

	private final static String SQL_QUERY_REDEV_APPLICATION_LIST = "select * from (select A.*,ROWNUM RN from "
			+ " (SELECT app.RA_NO, app.RA_TENANCY_REF, app.RA_NAME, per.RP_CHN_NAME ,app.RA_PROP_REF,  app.RA_PROP_ENG_ADDRESS, "
			+ " hsk_haa_ra.get_application_categories_des(app.RA_NO) as categories_des , app.RA_OFFER_REPLY_DATE, app.RA_STATUS, app.RA_CGLS_FLAG, app.RA_PROP_REF_2ND, "
			+ " hsk_haa_ra.get_status_desp('HAA_STATUS',app.RA_STATUS) as des,hsk_haa_ra.get_application_categories(app.RA_NO) as categories,  "
			+ " app.ra_prj_code, RP_DESCRIPTION"
			+ " from HST_HAA_REDEV_APPLICATION app, HST_HAA_RA_PERSON per, HST_HAA_REDEV_PROJECT where per.RP_RA_NO = app.RA_NO AND RP_PRJ_CODE=app.RA_PRJ_CODE AND UPPER(per.RP_RELATIONSHIP) = '"
			+ TENANCY_RELATIONSHIP.TENANT.name() + "' AND <`cond`> order by app.RA_NO desc) A "
			+ "  where <`cond2`> AND ROWNUM <= ? ) WHERE RN > ? ";

	private final static String SQL_QUERY_REDEV_APPLICATION_COUNT = " select count(1) from "
			+ " (SELECT app.RA_NO , hsk_haa_ra.get_application_categories(app.RA_NO) as categories "
			+ " from HST_HAA_REDEV_APPLICATION app, HST_HAA_RA_PERSON per where per.RP_RA_NO = app.RA_NO AND UPPER(per.RP_RELATIONSHIP) = '"
			+ TENANCY_RELATIONSHIP.TENANT.name() + "' AND <`cond`> ) A " + "  where <`cond2`> ";

	private final static String SQL_QUERY_REDEV_APPLICATION = "SELECT RA_NO, RA_PROP_REF, RA_COT_DATE,RA_PROP_ENG_ADDRESS,RA_PROP_CHN_ADDRESS,RA_PROP_ENG_ADDRESS_2ND, "
			+ " RA_PROP_CHN_ADDRESS_2ND, RA_CGLS_FLAG, RA_EXPECT_BABY, RA_TEMP_STAY, RA_TOTAL_PERSON,RA_RECEIVE_DATE ,RA_STATUS ,RA_TENANCY_REF,RA_PROP_REF_2ND, hsk_haa_ra.get_status_description(RA_NO) as des,RA_GENERAL_REMARK "
			+ " FROM HST_HAA_REDEV_APPLICATION WHERE <`cond`>";

	private final static String SQL_QUERY_RA_PERSON_LIST = "SELECT RP_RA_NO,RP_ENG_NAME,RP_CHN_NAME,RP_RELATIONSHIP "
			+ "FROM HST_HAA_RA_PERSON " + "WHERE <`cond`> ORDER BY RP_SEQ_NO";

	private final static String SQL_QUERY_RA_CATEGORY = "SELECT RC_CATEGORY FROM HST_HAA_RA_CATEGORY WHERE <`cond`>";

	private final static String SQL_QUERY_RA_CGLS_FLAT = "SELECT RF_RA_NO,RF_SEQ_NO,RF_FLAT_SIZE,RF_OTHER_REQ FROM HST_HAA_RA_CGLS_FLAT WHERE <`cond`> ";

	private final static String SQL_QUERY_HAA_REDEV_FLAT_CAPACITY = "SELECT  RDS_MIN_ALLOC,RDS_MAX_ALLOC FROM HST_HAA_REDEV_FLAT_SIZE WHERE RDS_PRJ_CODE = ? AND RDS_SIZE = ?";

	private final static String SQL_QUERY_HAA_REDEV_FLAT_CAPACITY_LIST = " SELECT RDS_SIZE, RDS_MIN_ALLOC,RDS_MAX_ALLOC FROM HST_HAA_REDEV_FLAT_SIZE WHERE RDS_PRJ_CODE = ?";

	private final static String SQL_QUERY_HAA_REDEV_APPLICATION_EXISTS = "SELECT COUNT(1) FROM HST_HAA_REDEV_APPLICATION WHERE RA_TENANCY_REF = ? OR RA_PROP_REF = ? ";

	private final static String SQL_UPDATE_APPLICATION = "update HST_HAA_REDEV_APPLICATION set RA_GENERAL_REMARK = ?   where RA_NO = ?";

	/**
	 * Application Statuses
	 * 
	 */
	public static enum APP_STATUS {
		NEW, TEMP_OFFER, PARTIAL, READY_OFFER, ON_HOLD, ESTATE_REJECT, ACCEPT_OFFER, REJECT_OFFER, TA_SIGN, TA_CANCEL,
		CANCEL;
	}

	/**
	 * Operations of Application
	 */
	public static enum OPERATION {
		CANCEL_REINSTATE, HOLD_UNHOLD;
	}

	/**
	 * Relationship between tenancy and RA_PERSON
	 * 
	 * @author Administrator
	 */
	public static enum TENANCY_RELATIONSHIP {
		TENANT, WIFE, CHILD;
	}

	private Connection conn = null;
	private PreparedStatement psmt = null;
	private ResultSet rs = null;

	/**
	 * Data is obtained by db function.
	 * 
	 * @param tenancyRef
	 * @param propRef
	 * @return
	 */
	public String queryPropertyInfo(String tenancyRef, String propRef) {
		String xml = "";
		String sql = SQL_QUERY_PROPERTY_INFO;
		String sqlQueryExists = SQL_QUERY_HAA_REDEV_APPLICATION_EXISTS;
		try {
			conn = dataSourceConfig.getConnection();
			psmt = conn.prepareStatement(sqlQueryExists);
			psmt.setString(1, tenancyRef);
			psmt.setString(2, propRef);
			rs = psmt.executeQuery();
			int resultInt = 0;
			if (rs.next()) {
				resultInt = rs.getInt(1);
			}
			if (resultInt > 0) {
				return "Fail:1401:application of the tenant existes!";
			}
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeResultSet(rs);

			psmt = conn.prepareStatement(sql);
			psmt.setString(1, tenancyRef);
			psmt.setString(2, propRef);
			rs = psmt.executeQuery();
			StringBuilder buff = new StringBuilder("{\"propertyInfo\":");

			String result = "";
			String[] dataArr = null;
			if (rs.next()) {
				result = rs.getString(1);
			}
			if (DataUtil.isEmpty(result)) {
				return ""; // not found
			}
			dataArr = result.split("`");
			if (dataArr == null || dataArr.length < 8)
				return ""; // not found
			if (dataArr != null) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.element("tenancyRef", dataArr[0]);
				jsonObject.element("propRef", dataArr[1]);
				jsonObject.element("propRef2", dataArr[2]);
				jsonObject.element("cotDate", dataArr[3]);
				jsonObject.element("propEndAddr1", dataArr[4]);
				jsonObject.element("propChnAddr1", dataArr[5]);
				jsonObject.element("propEngAddr2", dataArr[6]);
				jsonObject.element("propChnAddr2", dataArr[7]);
				jsonObject.element("cglsFlag", dataArr[8]);
				buff.append(DataUtil.escXml(jsonObject.toString())).append("}");
			}
			xml = buff.toString();
		} catch (SQLException e) {
			e.printStackTrace();
			xml = "FAIL:1500:" + e.getMessage();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		return DataUtil.escXml(xml);
	}

	/**
	 * Data is obtained by db function.
	 * 
	 * @param tenancyRef
	 * @return
	 */
	public String queryHouseholdPerson(String tenancyRef) {
		String xml = "";
		String sql = SQL_QUERY_HOUSEHOLD_PERSON;

		try {
			conn = dataSourceConfig.getConnection();

			psmt = conn.prepareStatement(sql);
			psmt.setString(1, tenancyRef);
			rs = psmt.executeQuery();

			String result = "";
			String[] dataArr = null;

			String[][] dataArr2 = null;
			if (rs.next()) {
				result = rs.getString(1);
			}
			if (DataUtil.isNotEmpty(result)) {
				dataArr = result.split("\\^");
				dataArr2 = new String[dataArr.length][];
				for (int i = 0; i < dataArr.length; i++) {
					dataArr2[i] = dataArr[i].split("`");
				}
			}

			StringBuilder buff = new StringBuilder();
			if (dataArr != null) {
				buff.append("{\"household\":[");
				for (int i = 0; i < dataArr2.length; i++) {
					if (i != 0) {
						buff.append(",");
					}
					JSONObject jsonObject = new JSONObject();
					jsonObject.element("seqNo", dataArr2[i][0]);
					jsonObject.element("tenancyNo", dataArr2[i][1]);
					jsonObject.element("rpEngName", dataArr2[i][2]);
					jsonObject.element("rpChnName", dataArr2[i][3]);
					jsonObject.element("rpRelationship", dataArr2[i][4]);
					buff.append(DataUtil.escXml(jsonObject.toString()));
				}
				buff.append("]}");
			}
			xml = buff.toString();
		} catch (SQLException e) {
			e.printStackTrace();
			xml = "FAIL:1500";
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		System.out.println(xml);
		return DataUtil.escXml(xml);
	}

	/**
	 * Data is obtained by database function.
	 * 
	 * @param raNo
	 * @return
	 */
	public String queryOfferHistory(String raNo) {
		String xml = "";
		String sql = SQL_QUERY_OFFER_HISTORY;

		try {
			conn = dataSourceConfig.getConnection();

			psmt = conn.prepareStatement(sql);
			psmt.setString(1, raNo);
			rs = psmt.executeQuery();

			String result = "";
			String[] dataArr = null;

			if (rs.next()) {
				result = rs.getString(1);
			}
			if (DataUtil.isNotEmpty(result)) {
				dataArr = result.split("\\^");
			}

			StringBuilder buff = new StringBuilder();

			if (dataArr != null) {
				buff.append("{\"household\":[");
				for (int i = 0; i < dataArr.length; i++) {
					if (i != 0) {
						buff.append(",");
					}
					String dataArr2[] = dataArr[i].split("`");
					JSONObject jsonObject = new JSONObject();
					jsonObject.element("offerDate", dataArr2[0]);
					jsonObject.element("offerStatus", dataArr2[1]);
					jsonObject.element("offeredFlag", dataArr2[2]);
					jsonObject.element("index", dataArr2[3]);
					buff.append(DataUtil.escXml(jsonObject.toString()));
				}
				buff.append("]}");
			}
			xml = buff.toString();
		} catch (SQLException e) {
			e.printStackTrace();
			xml = "FAIL:SQL:" + e.getErrorCode() + ":" + e.getMessage();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		return xml;
	}

	// return -1 if data is invalid, 1 if flat size not match and 0 if flat match
	private int checkFlatSize(ApplicationClass application) {
		Map<String, int[]> capacitys = null;
		CGLS_FLAG cglsF = null;
		cglsF = CGLS_FLAG.valueOf(application.getRaCglsFlag());
		// check flat size, if cgls flag is 'Y'
		if (CGLS_FLAG.Y == cglsF) {
			if (application.getCglsFlat1() == null || DataUtil.isEmpty(application.getCglsFlat1().getRfFlatSize())) {
				return -1;
			}
			if (application.getCglsFlat2() == null || DataUtil.isEmpty(application.getCglsFlat2().getRfFlatSize())) {
				return -1;
			}
			capacitys = queryFlatSizeCapacityMap(application.getRaPRJCode());
			// check flat size,if cgls flat is no ,both of two cgls flat could be empty ;if
			// cgls flat is yes ,flat2 is empty!
			int[] cap1 = capacitys.get(application.getCglsFlat1().getRfFlatSize());
			int[] cap2 = capacitys.get(application.getCglsFlat2().getRfFlatSize());
			if (application.getRaTotalPerson() < (cap1[0] + cap2[0])
					|| application.getRaTotalPerson() > (cap1[1] + cap2[1])) {
				return 1;
			}
		}
		return 0;
	}

	public String insertApplication(ApplicationClass application) {

		String result = "";
		try {
			CGLS_FLAG cglsFlag = CGLS_FLAG.valueOf(application.getRaCglsFlag());
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.buildResult().setErrorCode(Result.ERROR_CODE_INVALID).setMsg("cgls flat is invalid")
					.toString();
		}
		if (0 != checkFlatSize(application)) {
			result = "Fail: flat size check failed";
			return ResultBuilder.buildResult().setErrorCode(Result.ERROR_CODE_INVALID).setMsg(result).toString();
		}
		try {
			try {
				String sql = SQL_INSERT_REDEV_APPLICATION;
				conn = dataSourceConfig.getConnection();
				DBConnection.beginTransaction(conn);
				HaaNoBean hb = new HaaNoBean(conn);
				// this method must be in transaction
				String newNo = hb.genRaNo(application.getRaEstateCode());
				application.setRaNO(newNo);
				psmt = conn.prepareStatement(sql);
				psmt.setString(1, application.getRaNO());
				psmt.setString(2, application.getRaPRJCode());
				psmt.setString(3, application.getRaEstateCode());
				psmt.setString(4, APP_STATUS.READY_OFFER.name());
				psmt.setString(5, application.getRaTenancyRef());
				psmt.setString(6, application.getRaName());
				psmt.setString(7, application.getRaPropRef());
				psmt.setString(8, application.getRaPropEngAddress());
				psmt.setString(9, application.getRaPropChnAddress());
				psmt.setString(10, application.getRaPropRef2());
				psmt.setString(11, application.getRaPropEngAddress2());
				psmt.setString(12, application.getRaPropChnAddress2());
				psmt.setString(13, application.getRaCotDate());
				psmt.setString(14, application.getRaCglsFlag());
				psmt.setInt(15, application.getRaExpectBaby());
				psmt.setInt(16, application.getRaTempStay());
				psmt.setInt(17, application.getRaTotalPerson());
				psmt.setString(18, application.getRaReceiveDate());
				psmt.setString(19, application.getRaCreateBy());
				if (DataUtil.isEmpty(application.getRaRemark())) {
					application.setRaRemark(" ");
				}
				psmt.setString(20, application.getRaRemark());
				int resultApp = psmt.executeUpdate();
				if (resultApp <= 0) {
					DBConnection.rollback(conn);
					return ResultBuilder.buildServerFailResult("Application save failed").setResult(result).toString();
				}
				DBConnection.closeResultSet(rs);
				DBConnection.closePreparedStatement(psmt);

				String sql1 = SQL_INSERT_RA_CGLS_FALT;
				CGLSFlatClass flat1 = application.getCglsFlat1();
				CGLSFlatClass flat2 = application.getCglsFlat2();
				if (flat1 != null && DataUtil.isNotEmpty(flat1.getRfFlatSize())) {
					psmt = conn.prepareStatement(sql1);
					psmt.setString(1, application.getRaNO());
					psmt.setInt(2, 1);
					psmt.setString(3, flat1.getRfFlatSize());
					psmt.setString(4, flat1.getRfOtherReq());
					psmt.setInt(5, application.getRaTotalPerson());
					psmt.setString(6, application.getRaCreateBy());

					psmt.setString(7, "");
					int resultFlat1 = psmt.executeUpdate();
					if (resultFlat1 <= 0) {
						DBConnection.rollback(conn);
						return ResultBuilder.buildServerFailResult("Flat1 save failed").setResult(result).toString();
					}
					DBConnection.closeResultSet(rs);
					DBConnection.closePreparedStatement(psmt);
				}

				if (flat2 != null && DataUtil.isNotEmpty(flat2.getRfFlatSize())) {
					psmt = conn.prepareStatement(sql1);
					psmt.setString(1, application.getRaNO());
					psmt.setInt(2, 2);
					psmt.setString(3, flat2.getRfFlatSize());
					psmt.setString(4, flat2.getRfOtherReq());
					psmt.setInt(5, application.getRaTotalPerson());
					psmt.setString(6, application.getRaCreateBy());
					psmt.setString(7, "");

					int resultFlat2 = psmt.executeUpdate();
					if (resultFlat2 <= 0) {
						DBConnection.rollback(conn);
						return ResultBuilder.buildServerFailResult("Flat2 save failed").setResult(result).toString();
					}
					DBConnection.closeResultSet(rs);
					DBConnection.closePreparedStatement(psmt);
				}
				// save categories
				String sql2 = SQL_INSERT_RA_CATEGORY;

				psmt = conn.prepareStatement(sql2);
				if (application.getCategories() != null && application.getCategories().length > 0) {
					for (int i = 0; i < application.getCategories().length; i++) {
						ApplicationCategoryClass category = application.getCategories()[i];
						psmt.setString(1, application.getRaNO());
						psmt.setString(2, category.getCategory());
						psmt.setString(3, application.getRaCreateBy());
						psmt.setString(4, "");
						psmt.addBatch();
						if ((i + 1) % 100 == 0) {
							psmt.executeBatch();
						}
					}
					psmt.executeBatch();
					psmt.clearBatch();
					DBConnection.closePreparedStatement(psmt);
				}
				// save person
				String sql3 = SQL_INSERT_RA_PERSON;
				psmt = conn.prepareStatement(sql3);
				int rpSeqNO = 1;
				int i = 1;
				for (ApplicationPersonClass person : application.getPersons()) {
					psmt.setString(1, application.getRaNO());
					psmt.setInt(2, rpSeqNO++);
					psmt.setString(3, person.getRpEngName() == null ? "" : person.getRpEngName());
					psmt.setString(4, person.getRpChnName() == null ? "" : person.getRpChnName());
					psmt.setString(5, person.getRpRelationship() == null ? "" : person.getRpRelationship());
					psmt.setString(6, application.getRaCreateBy());
					psmt.setString(7, "");
					psmt.addBatch();
					if (i % 500 == 0) {
						psmt.executeBatch();
					}
				}
				psmt.executeBatch();
				psmt.clearBatch();

				DBConnection.commit(conn);
				result = application.getRaNO();

			} catch (SQLException e) {
				e.printStackTrace();
				DBConnection.rollback(conn);
				result = "FAIL:SQL:" + e.getErrorCode() + ":" + e.getMessage();
				return ResultBuilder.buildResult().setErrorCode(Result.ERROR_CODE_FAIL).setMsg(result).toString();
			} catch (Exception e) {
				e.printStackTrace();
				DBConnection.rollback(conn);
				result = "FAIL:SYS:" + e.getMessage();
				return ResultBuilder.buildResult().setErrorCode(Result.ERROR_CODE_FAIL).setMsg(result).toString();
			} finally {
				DBConnection.closePreparedStatement(psmt);
				DBConnection.closeConnection(conn);
			}
		} catch (Exception e) {
			result = "FAIL:SYS:" + e.getMessage();
			return ResultBuilder.buildResult().setErrorCode(Result.ERROR_CODE_FAIL).setMsg(result).toString();
		}
		return ResultBuilder.buildSuccessResult().setMsg(result).setResult(result).toString();
	}

	/**
	 * @param parameter:  Query condition in json : estateCode,
	 *                    caseNO,tenantNameEng(RA_NAME),
	 *                    offerletterReplyDate,tenanceRef, status
	 * @param propertyRef :Fuzzy query condition
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public String queryApplicationListByPage(ApplicationQueryObj application, String propertyRef, int pageIndex,
			int pageSize) {
		String xml = "";
		StringBuilder buff = new StringBuilder();
		String sql = SQL_QUERY_REDEV_APPLICATION_LIST;
		String sqlCount = SQL_QUERY_REDEV_APPLICATION_COUNT;
		pageIndex = pageIndex > 0 ? pageIndex : 1;
		pageSize = pageSize > 0 ? pageSize : 1;

		StringBuilder cond = new StringBuilder(" 1=1 ");
		StringBuilder cond2 = new StringBuilder(" 1=1 ");

		if (DataUtil.isNotEmpty(propertyRef)) {
			cond.append(" AND RA_PROP_REF LIKE '%").append(propertyRef).append("%'");
		}
		if (application != null) {
			if (DataUtil.isEmpty(application.getPrjCode())) {
				return ResultBuilder.buildResult().setErrorCode(Result.ERROR_CODE_INVALID)
						.setMsg("Project code is required!").toString();
			}
			cond.append(" AND RA_PRJ_CODE = '").append(application.getPrjCode()).append("'");
			if (DataUtil.isNotEmpty(application.getCaseNo())) {
				cond.append(" AND RA_NO = '").append(application.getCaseNo()).append("'");
			}

			if (DataUtil.isNotEmpty(application.getRaName())) {
				cond.append(" AND RA_NAME = '").append(application.getRaName()).append("'");
			}

			if (DataUtil.isNotEmpty(application.getOfferLetterReplyDate())) {
				cond.append(" AND RA_OFFER_LETTER_REPLY_DATE = '").append(application.getOfferLetterReplyDate())
						.append("'");
			}

			if (DataUtil.isNotEmpty(application.getTenancyRef())) {
				cond.append(" AND RA_TENANCY_REF = '").append(application.getTenancyRef()).append("'");
			}

			if (DataUtil.isNotEmpty(application.getStatus())) {
				cond.append(" AND RA_STATUS = '").append(application.getStatus()).append("'");
			}

			if (DataUtil.isNotEmpty(application.getPropertyRef())) {
				cond.append(" AND RA_PROP_REF = '").append(application.getPropertyRef()).append("'");
			}

			if (DataUtil.isNotEmpty(application.getCategory())) {
				cond2.append(" AND INSTR(A.categories, '").append(application.getCategory()).append("') > 0");
			}
		}

		sql = DataUtil.strReplaceAll(sql, "<`cond`>", cond.toString());
		sql = DataUtil.strReplaceAll(sql, "<`cond2`>", cond2.toString());

		sqlCount = DataUtil.strReplaceAll(sqlCount, "<`cond`>", cond.toString());
		sqlCount = DataUtil.strReplaceAll(sqlCount, "<`cond2`>", cond2.toString());
		int total = 0;
		Boolean isFirst = true;
		try {
			conn = dataSourceConfig.getConnection();
			psmt = conn.prepareStatement(sql);
			psmt.setInt(1, pageSize * pageIndex);
			psmt.setInt(2, pageSize * (pageIndex - 1));
			rs = psmt.executeQuery();

			buff.append("[");
			JSONObject jsonObj = null;
			while (rs.next()) {
				if (isFirst) {
					isFirst = false;
				} else {
					buff.append(",");
				}
				jsonObj = new JSONObject();
				jsonObj.element("caseNo", rs.getString(1));
				jsonObj.element("tenancyRef", rs.getString(2));
				jsonObj.element("tenancyNameEng", rs.getString(3) == null ? "" : rs.getString(3));
				jsonObj.element("tenancyNameChn", rs.getString(4) == null ? "" : rs.getString(4));
				jsonObj.element("propertyRef", rs.getString(5));
				jsonObj.element("propertyAddr", rs.getString(6));
				jsonObj.element("category", rs.getString(7));
				jsonObj.element("offerLetterReplyDate", rs.getString(8) == null ? "" : rs.getString(8));
				jsonObj.element("status", rs.getString(9));
				jsonObj.element("cglsFlag",
						CGLS_FLAG.S.name().equals(rs.getString(10)) ? CGLS_FLAG.Y.name() : rs.getString(10));
				jsonObj.element("PropRef2", rs.getString(11));
				jsonObj.element("statusDesc", rs.getString(12));
				jsonObj.element("prjCode", rs.getString(14));
				jsonObj.element("prjDesc", rs.getString(15));
				// jsonObj.element("raRemark", rs.getString(13));
				buff.append(jsonObj.toString());
			}

			buff.append("]");
			xml = buff.toString();
			psmt = conn.prepareStatement(sqlCount);
			rs = psmt.executeQuery();
			if (rs.next()) {
				total = rs.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			xml = "FAIL:SQL:" + e.getErrorCode() + ":" + e.getMessage();
			return ResultBuilder.buildServerFailResult(xml).toString();
		} catch (Exception e) {
			e.printStackTrace();
			xml = "FAIL:SYS:" + e.getMessage();
			return ResultBuilder.buildServerFailResult(xml).toString();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		return ResultBuilder.buildSuccessResult(xml).setMsg(total + "").toString();
	}

	public List<ApplicationPersonClass> queryPersonList(String raNo) {
		String sql = SQL_QUERY_RA_PERSON_LIST;
		StringBuilder cond = new StringBuilder(" 1=1 ");
		if (DataUtil.isNotEmpty(raNo)) {
			cond.append(" AND RP_RA_NO = '" + raNo + "'");
		}
		sql = DataUtil.strReplaceAll(sql, "<`cond`>", cond.toString());
		List<ApplicationPersonClass> resultList = new ArrayList<ApplicationPersonClass>(64);
		try {
			conn = dataSourceConfig.getConnection();
			psmt = conn.prepareStatement(sql);
			rs = psmt.executeQuery();
			while (rs.next()) {
				ApplicationPersonClass person = new ApplicationPersonClass();
				person.setRaNO(rs.getString(1));
				person.setRpEngName(rs.getString(2));
				person.setRpChnName(rs.getString(3));
				person.setRpRelationship(rs.getString(4));
				resultList.add(person);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		return resultList;
	}

	public String queryApplication(String propertyRef, String tenancyRef, String raNo) {
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		String xml = "";

		String sql = SQL_QUERY_REDEV_APPLICATION;
		StringBuilder cond = new StringBuilder(" 1=1 ");
		if (DataUtil.isNotEmpty(propertyRef)) {
			cond.append(" AND RA_PROP_REF like '%" + propertyRef + "%'");
		}
		if (DataUtil.isNotEmpty(tenancyRef)) {
			cond.append(" AND RA_TENANCY_REF = '" + tenancyRef + "'");
		}
		if (DataUtil.isNotEmpty(raNo)) {
			cond.append(" AND RA_NO = '" + raNo + "'");
		}
		sql = DataUtil.strReplaceAll(sql, "<`cond`>", cond.toString());
		try {
			conn = dataSourceConfig.getConnection();

			psmt = conn.prepareStatement(sql);
			rs = psmt.executeQuery();
			StringBuilder buff = new StringBuilder("{\"raApplication\":");
			JSONObject jsonObj = null;
			if (!rs.next()) {
				return "";
			}

			jsonObj = new JSONObject();
			raNo = rs.getString(1);

			jsonObj.element("raNo", raNo);
			jsonObj.element("raPropRef", rs.getString(2));
			Date cotDate = rs.getDate(3);
			SimpleDateFormat sdFormatter = new SimpleDateFormat("dd/MM/yyyy");

			jsonObj.element("raCotDate", cotDate == null ? "" : sdFormatter.format(cotDate));
			jsonObj.element("raPropEngAddress", rs.getString(4));
			jsonObj.element("raPropChnAddress", rs.getString(5));
			jsonObj.element("raPropEngAddress2", rs.getString(6));
			jsonObj.element("raPropChnAddress2", rs.getString(7));
			jsonObj.element("raCglsFlag", rs.getString(8));
			jsonObj.element("raExpectBaby", rs.getString(9));
			jsonObj.element("raTempStay", rs.getString(10));
			jsonObj.element("raTotalPerson", rs.getString(11));
			Date rcvDate = rs.getDate(12);
			jsonObj.element("raReceiveDate", rcvDate == null ? "" : sdFormatter.format(rcvDate));
			jsonObj.element("raStatus", rs.getString(13));
			jsonObj.element("raTenancyRef", rs.getString(14));
			jsonObj.element("raPropRef2", rs.getString(15));
			jsonObj.element("raStatusDesc", rs.getString(16));
			jsonObj.element("raRemark", rs.getString(17));
			buff.append(jsonObj.toString());

			String sql1 = SQL_QUERY_RA_PERSON_LIST;

			cond = new StringBuilder();
			if (DataUtil.isNotEmpty(raNo)) {
				cond.append(" RP_RA_NO = '" + raNo + "'");
			} else {
				cond.append(" 1 != 1 ");
			}
			sql1 = DataUtil.strReplaceAll(sql1, "<`cond`>", cond.toString());

			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			psmt = conn.prepareStatement(sql1);
			rs = psmt.executeQuery();
			buff.append(",\"persons\":[");
			Boolean flag = true;
			ApplicationPersonClass person = new ApplicationPersonClass();
			while (rs.next()) {
				if (flag) {
					flag = false;
				} else {
					buff.append(",");
				}
				person = new ApplicationPersonClass();
				person.setRaNO(rs.getString(1));
				person.setRpEngName(rs.getString(2));
				person.setRpChnName(rs.getString(3));
				person.setRpRelationship(rs.getString(4));
				buff.append(JSONObject.fromObject(person).toString());
			}

			String sql2 = SQL_QUERY_RA_CGLS_FLAT;
			cond = new StringBuilder();
			if (DataUtil.isNotEmpty(raNo)) {
				cond.append(" RF_RA_NO = '" + raNo + "'");
			} else {
				cond.append(" 1 != 1 ");
			}
			sql2 = DataUtil.strReplaceAll(sql2, "<`cond`>", cond.toString());
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);

			psmt = conn.prepareStatement(sql2);
			rs = psmt.executeQuery();
			buff.append("],\"flats\":[");
			flag = true;
			CGLSFlatClass flat = new CGLSFlatClass();
			while (rs.next()) {
				if (flag) {
					flag = false;
				} else {
					buff.append(",");
				}
				flat = new CGLSFlatClass();
				flat.setRaNo(rs.getString(1));
				flat.setRfSeqNo(Integer.parseInt(rs.getString(2)));
				flat.setRfFlatSize(rs.getString(3));
				flat.setRfOtherReq(rs.getString(4));
				buff.append(flat.toString());
			}
			buff.append("]");

			// OFFERS
			// buff.append(",\"offers\":[");
			// String sql3 = "";

			cond = new StringBuilder();
			if (DataUtil.isNotEmpty(raNo)) {
				cond.append(" RC_RA_NO = '" + raNo + "'");
			} else {
				cond.append(" 1 != 1 ");
			}
			String sql4 = SQL_QUERY_RA_CATEGORY;
			sql4 = DataUtil.strReplaceAll(sql4, "<`cond`>", cond.toString());

			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			psmt = conn.prepareStatement(sql4);
			rs = psmt.executeQuery();
			buff.append(",\"categories\":[");

			flag = true;
			while (rs.next()) {
				if (flag) {
					flag = false;
				} else {
					buff.append(",");
				}
				ApplicationCategoryClass category = new ApplicationCategoryClass();
				category.setCategory(rs.getString(1));
				buff.append(category.toString());
			}
			buff.append("]");
			buff.append("}");
			xml = buff.toString();
		} catch (SQLException e) {
			e.printStackTrace();
			xml = "FAIL:SQL:" + e.getErrorCode() + ":" + e.getMessage();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		return DataUtil.escXml(xml);
	}

	public String updateApplication(ApplicationClass application) {
		String result = "";
		if (application == null) {
			result = "Fail:empty data is received";
			return ResultBuilder.buildResult(result).setErrorCode(Result.ERROR_CODE_INVALID).toString();
		}
		try {
			CGLS_FLAG cglsF = CGLS_FLAG.valueOf(application.getRaCglsFlag());
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.buildResult().setErrorCode(Result.ERROR_CODE_INVALID).setMsg("cgls flat is invalid")
					.toString();
		}
		if (0 != checkFlatSize(application)) {
			result = "Fail: flat size check failed";
			return ResultBuilder.buildResult().setErrorCode(Result.ERROR_CODE_INVALID).setMsg(result).toString();
		}
		// If status is not READY_OFFER,only comment is allowed to update.
		if (!APP_STATUS.READY_OFFER.name().equals(application.getRaStatus())) {
			int chgResult = 0;
			conn = dataSourceConfig.getConnection();
			try {
				String sql = SQL_UPDATE_COMMENT;
				psmt = conn.prepareStatement(sql);
				psmt.setString(1, application.getRaRemark());
				psmt.setString(2, application.getRaUpdateBy());
				psmt.setString(3, application.getRaNO());
				chgResult = psmt.executeUpdate();

			} catch (SQLException e) {
				e.printStackTrace();
				DBConnection.rollback(conn);
				result = "FAIL:SQL:" + e.getErrorCode() + ":" + e.getMessage();
			} finally {
				DBConnection.closePreparedStatement(psmt);
				DBConnection.closeConnection(conn);
			}
			return ResultBuilder.buildSuccessResult().setResult(chgResult).toString();
		}
		conn = dataSourceConfig.getConnection();
		DBConnection.beginTransaction(conn);
		try {
			// update flat if it's cgls
			String sqlDelFlats = SQL_DELETE_RA_CGLS_FLAT;
			String sqlInsertFlats = SQL_INSERT_RA_CGLS_FALT;

			psmt = conn.prepareStatement(sqlDelFlats);
			psmt.setString(1, application.getRaNO());
			psmt.executeUpdate();
			DBConnection.closePreparedStatement(psmt);

			CGLSFlatClass flat1 = application.getCglsFlat1();
			CGLSFlatClass flat2 = application.getCglsFlat2();
			// if it's cgls,flat need to be update
			if (!CGLS_FLAG.N.name().equals(application.getRaCglsFlag())) {
				if (application.getCglsFlat1() != null
						&& DataUtil.isNotEmpty(application.getCglsFlat1().getRfFlatSize())) {
					psmt = conn.prepareStatement(sqlInsertFlats);
					psmt.setString(1, application.getRaNO());
					psmt.setInt(2, 1);
					psmt.setString(3, flat1.getRfFlatSize());
					psmt.setString(4, flat1.getRfOtherReq());
					psmt.setInt(5, application.getRaTotalPerson());
					psmt.setString(6, application.getRaCreateBy());
					psmt.setString(7, application.getRaUpdateBy());
					psmt.executeUpdate();
					DBConnection.closePreparedStatement(psmt);
				}

				if (application.getCglsFlat2() != null
						&& DataUtil.isNotEmpty(application.getCglsFlat2().getRfFlatSize())) {
					psmt = conn.prepareStatement(sqlInsertFlats);
					psmt.setString(1, application.getRaNO());
					psmt.setInt(2, 2);
					psmt.setString(3, flat2.getRfFlatSize());
					psmt.setString(4, flat2.getRfOtherReq());
					psmt.setInt(5, application.getRaTotalPerson());
					psmt.setString(6, application.getRaCreateBy());
					psmt.setString(7, application.getRaUpdateBy());
					psmt.executeUpdate();
					DBConnection.closePreparedStatement(psmt);
				}
			}
			// update category
			if (application.getCategories() != null && application.getCategories().length > 0) {
				String sqlDelCat = SQL_DELETE_RA_CATEGORY;
				psmt = conn.prepareStatement(sqlDelCat);
				psmt.setString(1, application.getRaNO());
				psmt.executeUpdate();
				DBConnection.closePreparedStatement(psmt);

				String sqlInsertCat = SQL_INSERT_RA_CATEGORY;
				psmt = conn.prepareStatement(sqlInsertCat);
				for (int i = 0; i < application.getCategories().length; i++) {
					ApplicationCategoryClass category = application.getCategories()[i];
					psmt.setString(1, application.getRaNO());
					psmt.setString(2, category.getCategory());
					psmt.setString(3, "");
					psmt.setString(4, application.getRaUpdateBy());
					psmt.addBatch();
					if ((i + 1) % 100 == 0) {
						psmt.executeBatch();
					}
					psmt.executeBatch();
				}
				DBConnection.closePreparedStatement(psmt);

			}
			// update application
			String sql = SQL_UPDATE_REDEV_APPLICATION;
			psmt = conn.prepareStatement(sql);
			psmt.setInt(1, application.getRaExpectBaby());
			psmt.setInt(2, application.getRaTempStay());
			psmt.setString(3, application.getRaCglsFlag());
			psmt.setString(4, application.getRaReceiveDate());
			psmt.setString(5, application.getRaUpdateBy());
			psmt.setInt(6, application.getRaTotalPerson());
			psmt.setString(7, application.getRaRemark());

			psmt.setString(8, application.getRaNO());
			psmt.executeUpdate();
			DBConnection.commit(conn);
			result = "SUCCESS";
			return ResultBuilder.buildSuccessResult().toString();
		} catch (SQLException e) {
			e.printStackTrace();
			DBConnection.rollback(conn);
			result = "FAIL:SQL:" + e.getErrorCode() + ":" + e.getMessage();
		} catch (Exception e) {
			DBConnection.rollback(conn);
			e.printStackTrace();
			result = "FAIL:SYS:" + e.getMessage();
		} finally {
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		return ResultBuilder.buildServerFailResult(result).toString();
	}

	public Map<String, int[]> queryFlatSizeCapacityMap(String prjCode) {
		String sql = SQL_QUERY_HAA_REDEV_FLAT_CAPACITY_LIST;
		Map<String, int[]> capacityMap = new HashMap<String, int[]>(16);
		try {
			conn = dataSourceConfig.getConnection();
			psmt = conn.prepareStatement(sql);
			psmt.setString(1, prjCode);
			rs = psmt.executeQuery();
			int min = 0;
			int max = 0;
			String size = "";
			while (rs.next()) {
				size = rs.getString(1);
				min = rs.getInt(2);
				max = rs.getInt(3);
				capacityMap.put(size, new int[] { min, max });
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		return capacityMap;
	}

	public int[] queryFlatCapacityByPrjCodeAndSize(String prjCode, String size) {
		String sql = SQL_QUERY_HAA_REDEV_FLAT_CAPACITY;
		int result[] = new int[2];
		try {
			conn = dataSourceConfig.getConnection();
			psmt = conn.prepareStatement(sql);
			psmt.setString(1, prjCode);
			psmt.setString(2, size);
			rs = psmt.executeQuery();

			while (rs.next()) {
				result[0] = rs.getInt(1);
				result[1] = rs.getInt(2);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		return result;
	}

	public String changeStatus(String ranos, String status, String raRemark, String adminName) {
		String sql = SQL_UPDATE_STATUS_APPLICATION;
		String sqlUpdApp = SQL_UPDATE_APPLICATION;
		if (DataUtil.isEmpty(ranos)) {
			return ResultBuilder.buildServerFailResult("FAIL: Rano is empty").toString();
		}
		OPERATION statusEnum = null;
		try {
			statusEnum = OPERATION.valueOf(status);

		} catch (Exception e) {
			return ResultBuilder.buildServerFailResult("Illegal status").toString();
		}
		String[] ranoArray = ranos.split(",");

		if (DataUtil.isNotEmpty(raRemark)) {
			try {
				conn = dataSourceConfig.getConnection();
				psmt = conn.prepareStatement(sqlUpdApp);
				psmt.setString(1, raRemark);
				psmt.setString(2, ranoArray[0]);
				psmt.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
				return ResultBuilder.buildServerFailResult("Fail").toString();
			} finally {
				DBConnection.closeResultSet(rs);
				DBConnection.closePreparedStatement(psmt);
				DBConnection.closeConnection(conn);
			}
		}

		conn = dataSourceConfig.getConnection();
		String[] result = new String[ranoArray.length];
		try {
			psmt = conn.prepareStatement(sql);
			for (int i = 0; i < ranoArray.length; i++) {
				psmt.setString(1, ranoArray[i]);
				psmt.setString(2, statusEnum.name());
				psmt.setString(3, adminName);
				rs = psmt.executeQuery();
				if (rs.next()) {
					result[i] = rs.getString(1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return ResultBuilder.buildServerFailResult("Fail").toString();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		return ResultBuilder.buildSuccessResult(result).toString();
	}

}
