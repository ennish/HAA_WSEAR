package com.hkhs.hmms.haa;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.hkhs.hmms.haa.ApplicationBean.APP_STATUS;
import com.hkhs.hmms.haa.entity.BallotCountClass;
import com.hkhs.hmms.haa.entity.BallotEntity;
import com.hkhs.hmms.haa.entity.BallotExportResult;
import com.hkhs.hmms.haa.entity.BallotImportResult;
import com.hkhs.hmms.haa.queryEntity.BallotImportClass;
import com.hkhs.hmms.haa.entity.FlatClass;
import com.hkhs.hmms.haa.entity.FlatInfo;
import com.hkhs.hmms.haa.entity.Result;
import com.hkhs.hmms.haa.entity.ResultBuilder;
import com.hkhs.hmms.haa.queryEntity.BallotFlatQueryObj;
import com.hkhs.hmms.haa.util.DBConnection;
import com.hkhs.hmms.haa.util.DataUtil;

/**
 * @author TuWei 14/2/2019
 */
public class BallotBean {

	private final static String SQL_GET_HAA_FILE_PATH = "select HSK_HAA_RA.get_download_path(?) from dual";

	private final static String SQL_QUERY_RA_APPLICATION_FOR_BALLOT = "SELECT UNIQUE(RA_NO) FROM HST_HAA_REDEV_APPLICATION ,HST_HAA_RA_CATEGORY  "
			+ "WHERE RC_RA_NO = RA_NO AND " + "( RA_STATUS='" + APP_STATUS.READY_OFFER.name() + "'"
			+ "  OR (RA_STATUS = '" + APP_STATUS.PARTIAL.name() + "' AND "
			+ " (SELECT COUNT(RO_OFFER_COUNT) FROM HST_HAA_RA_OFFER WHERE RO_RA_NO = RA_NO GROUP BY RO_RA_NO)<3 ) "
			+ ") AND <`cond`> ";

	private final static String SQL_QUERY_ELDERLY_SELECTED = "SELECT count(*) from HST_HAA_MATCH_CAT WHERER RAM_RB_NO=? and RAM_category='Elderly'";

	private final static String SQL_QUERY_RA_APPLICATION_FOR_BALLOT2 = "SELECT UNIQUE(RA_NO) FROM HST_HAA_REDEV_APPLICATION "
			+ "WHERE  (RA_STATUS='READY_OFFER'" + "OR" + "  ( RA_STATUS = 'PARTIAL' AND"
			+ "	 ( ? =0 and  ( (SELECT COUNT(*) FROM HST_HAA_RA_OFFER WHERE RO_RA_NO = RA_NO AND ro_status='REFUSE')<3 AND  (SELECT COUNT(*) FROM HST_HAA_RA_OFFER WHERE RO_RA_NO = RA_NO AND ro_status='"
			+ APP_STATUS.ACCEPT_OFFER.name() + "')>0 or"
			+ "	  (SELECT COUNT(*) FROM HST_HAA_RA_OFFER WHERE RO_RA_NO = RA_NO AND ro_status='"
			+ APP_STATUS.REJECT_OFFER.name()
			+ "')=3 AND (SELECT COUNT(*) FROM HST_HAA_RA_OFFER WHERE RO_RA_NO = RA_NO AND ro_status='"
			+ APP_STATUS.ACCEPT_OFFER.name() + "')=0" + "	  OR"
			+ "	   ? > 0 and (SELECT COUNT(*) FROM HST_HAA_RA_OFFER WHERE RO_RA_NO = RA_NO AND ro_status='"
			+ APP_STATUS.REJECT_OFFER.name()
			+ "')<3 AND (SELECT COUNT(*) FROM HST_HAA_RA_OFFER WHERE RO_RA_NO = RA_NO AND ro_status='"
			+ APP_STATUS.ACCEPT_OFFER.name() + "')=0 )" + "	)" + "  )" + ")"
			+ "AND  1=1  AND RA_PRJ_CODE = ? AND  (select count(*) from hst_haa_ra_category where RC_RA_NO= RA_NO AND <`cond`> ) > 0";

	private final static String SQL_QUERY_RA_APPLICATION_COUNT = "select distinct c.*, d.RDC_DESCRIPTION, d.rdc_display_seq from (select rc_category as category, count(*) as cnt from hst_haa_redev_application a, hst_haa_ra_category b "
			+ " where a.ra_no=b.rc_ra_no and a.ra_prj_code=? and (a.ra_status  = '" + APP_STATUS.READY_OFFER.name()
			+ "' OR ( a.ra_prj_code=? and a.ra_status = '" + APP_STATUS.PARTIAL.name()
			+ "' and (SELECT COUNT(*) FROM HST_HAA_RA_OFFER WHERE RO_RA_NO = a.RA_NO AND ro_status='"
			+ APP_STATUS.REJECT_OFFER.name() + "') <3)) "
			+ " group by rc_category) c left join HST_HAA_REDEV_CATEGORY d on c.category = d.RDC_CATEGORY where rdc_prj_code = ? order by rdc_display_seq";

	private final static String SQL_QUERY_TEMP_OFFER_FLAT = "select FLS_RB_NO, FLS_PROP_REF, FLS_SEQ from HST_HAA_RA_FLAT_SEQUENCE where FLS_STATUS ='"
			+ FLAT_STATUS.TEMP_OFFER.name() + "' and FLS_PRJ_CODE = ?";

	/**
	 * Order of flat list export
	 */
	private final static String SQL_QUERY_FLAT_EXPORT_ORDER = "select rp_flat_sort_config from hst_haa_redev_project where rp_prj_code = ?";

	/**
	 * Query flat list imported into haa as READY_OFFER
	 */

	private final static String SQL_QUERY_RA_FLAT_FOR_BALLOT_EXPORT = "select UNIQUE(PROPERTY_REFNO), FLATTYPE, MIN_ALLOC, MAXALLOC, IFA from hst_haa_redev_flat_view "
			+ " where (regexp_like(PROPERTY_REFNO, (SELECT RP_PROP_RANGE FROM HST_HAA_REDEV_PROJECT WHERE RP_PRJ_CODE = ? )) "
			+ "	 or property_refno in (select rfl_prop_ref from hs_haa.hst_haa_redev_flat_list where rfl_prj_code = ?)) "
			+ " <`cond`> "
			+ "  and property_refno not in (select FLS_PROP_REF from hst_haa_ra_flat_sequence  "
			+ "	 where FLS_PRJ_CODE = ? AND FLS_STATUS NOT IN ('" + FLAT_STATUS.READY_OFFER + "', '" + FLAT_STATUS.REJECT_OFFER + "'))  order by PROPERTY_REFNO ";

	/**
	 * Flat Management
	 * 
	 * Query flat list
	 */
//	private final static String SQL_QUERY_RA_FLAT_FOR_BALLOT = "select PROPERTY_REFNO,IFA,FLATTYPE,FACING,ESTATES,UDF,ELDERLY_WING,NVL(FLS_STATUS,'"
//			+ FLAT_STATUS.READY_OFFER + "') from HST_HAA_REDEV_FLAT_VIEW,HST_HAA_RA_FLAT_SEQUENCE "
//			+ " where PROPERTY_REFNO = FLS_PROP_REF and <`cond`>";

	/**
	 * cond1 View prjCode refNo type cond2 sequence status
	 * 
	 * cond.append("and ( regexp_like (PROPERTY_REFNO,'"+queryByPrj+"') ")
 			.append(" OR PROPERTY_REFNO IN (SELECT RFL_PROP_REF FROM HST_HAA_REDEV_FLAT_LIST WHERE RFL_PRJ_CODE = '" + queryObj.getProject() + "')) ");
		
	 */
	  final static String SQL_QUERY_FLAT_FOR_MANAMEGENT = "select distinct(PROPERTY_REFNO),IFA,FLATTYPE,FACING,ESTATES,UDF,ELDERLY_WING,NVL(FLS_STATUS,'READY_OFFER'),hsk_haa_ra.get_status_desp('HAA_FLAT_STATUS',NVL(FLS_STATUS,'READY_OFFER')) "
			+ " from HST_HAA_REDEV_FLAT_VIEW, "
			+ "	(select FLS_PROP_REF, FLS_STATUS , FLS_RB_NO "
			+ "		from HST_HAA_RA_FLAT_SEQUENCE where FLS_PRJ_CODE =  ? AND FLS_RB_NO IN( "
			+ "  		SELECT FLS_RS_NO FROM (SELECT FLS_PRJ_CODE, FLS_PROP_REF , MAX(FLS_RB_NO) FLS_RS_NO "
			+ " 		FROM HST_HAA_RA_FLAT_SEQUENCE GROUP BY FLS_PRJ_CODE,FLS_PROP_REF) A) and FLS_STATUS <> '" +  FLAT_STATUS.REJECT_OFFER + "' ) B "
			+ " where PROPERTY_REFNO = FLS_PROP_REF(+) AND "
			+ " 	( regexp_like (PROPERTY_REFNO,(SELECT RP_PROP_RANGE FROM HST_HAA_REDEV_PROJECT WHERE RP_PRJ_CODE = ?))"
			+ " 	OR PROPERTY_REFNO IN (SELECT RFL_PROP_REF FROM HST_HAA_REDEV_FLAT_LIST WHERE RFL_PRJ_CODE = ?)) <`cond`> ORDER BY PROPERTY_REFNO";

	private final static String SQL_GET_RA_FLAT_CATEGORY_FOR_BALLOT_COUNT = "SELECT hsk_haa_ra.get_flat_category_count(?) FROM DUAL";

	private final static String SQL_UPDATE_SEQUENCE_OF_APP = "UPDATE HST_HAA_RA_SEQUENCE SET RAS_SEQ = ? WHERE RAS_RB_NO = ? AND RAS_RA_NO = ?";

	private final static String SQL_UPDATE_SEQUENCE_OF_FLAT = "UPDATE HST_HAA_RA_FLAT_SEQUENCE SET FLS_SEQ = ? WHERE FLS_RB_NO = ? AND FLS_PROP_REF = ?";

	private final static String SQL_UPDATE_BALLOT = "UPDATE HST_HAA_RA_BALLOT SET RB_INPUT_REMARK = ?,RB_INPUT_DATE = SYSDATE, RB_INPUT_BY = ?, RB_INPUT_FLAT_FILE_NAME = ?, RB_INPUT_APP_FILE_NAME = ?,RB_SEED_NO = ?, RB_BALLOT_DATE = TO_DATE(?,'dd/MM/yy') WHERE RB_NO = ?";

	private final static String SQL_SAVE_BALLOT_FLAT = "INSERT INTO HST_HAA_RA_FLAT_SEQUENCE(FLS_RB_NO,FLS_PROP_REF,FLS_SEQ,FLS_STATUS,FLS_PRJ_CODE,FLS_MIN_ALLOC,FLS_MAX_ALLOC,FLS_FLAT_SIZE,FLS_IFA) VALUES(?,?,?,?,?,?,?,?,?)";

	private final static String SQL_SAVE_BALLOT_FLAT_CAT = "INSERT INTO HST_HAA_RA_FLAT_MATCH_SIZE(RAF_RB_NO,RAF_PRJ_CODE,RAF_SIZE) VALUES(?,?,?)";

	private final static String SQL_SAVE_BALLOT_APPLICATION = "INSERT INTO HST_HAA_RA_SEQUENCE(RAS_RB_NO,RAS_RA_NO,RAS_SEQ,RAS_CGLS_FLAT_SEQ_NO) VALUES(?,?,?,?)";

	private final static String SQL_SAVE_BALLOT_APPLICATION_CAT = "INSERT INTO HST_HAA_RA_MATCH_CAT(RAM_RB_NO,RAM_PRJ_CODE,RAM_CATEGORY) VALUES(?,?,?)";

	private final static String SQL_GET_BALLOT_SEQ = "select SEQ_HST_HAA_RA_BALLOT.Nextval from dual";

	private final static String SQL_SAVE_UPLOAD_BALLOT_LOG = "INSERT INTO HST_HAA_RA_BALLOT(RB_NO,RB_PRJ_CODE,RB_UPLOAD_FLAT_FILE_NAME,RB_UPLOAD_APP_FILE_NAME,RB_UPLOAD_DATE,RB_UPLOAD_BY) "
			+ " VALUES(?,?,?,?,SYSDATE,?)";

	private final static String SQL_EXEC_MATCH = "call hsk_haa_ra.match(?,?,?,?)";

	private final static String SQL_CHECK_OUTSTANDING_BALLOT = "SELECT count(1) FROM HST_HAA_RA_BALLOT WHERE RB_PRJ_CODE = ? "
			+ " AND NVL(RB_UPLOAD_DATE,TO_DATE('01/01/1920','dd/MM/yy')) = TO_DATE('01/01/1920','dd/MM/yy')";

	private final static String SQL_QUERY_APPLICATIONS_BY_FILE_NAME = "SELECT UNIQUE(RAS_RA_NO),RB_NO,RB_PRJ_CODE FROM HST_HAA_RA_BALLOT,HST_HAA_RA_SEQUENCE WHERE RAS_RB_NO = RB_NO AND RB_UPLOAD_APP_FILE_NAME = ? AND RB_INPUT_APP_FILE_NAME IS NULL ORDER BY RAS_RA_NO";

	private final static String SQL_QUERY_FLATS_BY_FILE_NAME = "SELECT UNIQUE(FLS_PROP_REF),RB_NO FROM HST_HAA_RA_BALLOT,HST_HAA_RA_FLAT_SEQUENCE WHERE FLS_RB_NO = RB_NO AND RB_UPLOAD_APP_FILE_NAME = ? AND RB_INPUT_FLAT_FILE_NAME IS NULL ORDER BY FLS_PROP_REF";

	private final static String SQL_QUERY_APPLICATIONS_OF_LAST_ROUND_BALLOT = "SELECT UNIQUE(RAS_RA_NO),RAS_RB_NO, RB_PRJ_CODE FROM HST_HAA_RA_SEQUENCE,HST_HAA_RA_BALLOT WHERE RAS_RB_NO = RB_NO AND RAS_RB_NO = (SELECT MAX(RB_NO) FROM HST_HAA_RA_BALLOT)  AND RB_INPUT_APP_FILE_NAME IS NULL ORDER BY RAS_RA_NO";

	private final static String SQL_QUERY_FLATS_OF_LAST_ROUND_BALLOT = "SELECT UNIQUE(FLS_PROP_REF),FLS_RB_NO, RB_PRJ_CODE FROM HST_HAA_RA_FLAT_SEQUENCE,HST_HAA_RA_BALLOT WHERE FLS_RB_NO = RB_NO AND FLS_RB_NO = (SELECT MAX(RB_NO) FROM HST_HAA_RA_BALLOT ) AND RB_INPUT_FLAT_FILE_NAME IS NULL ORDER BY FLS_PROP_REF";

	private final static String SQL_GENERATE_PROPREFNO_FOR_FLAT_QUERY = "SELECT RP_PROP_RANGE,RP_ESTATE_CODE FROM HST_HAA_REDEV_PROJECT WHERE RP_PRJ_CODE = ?";

	private final static String SQL_QUERY_BALLOT_FLAT_NOT_MATCH = "SELECT FLS_RB_NO,FLS_PROP_REF FROM HST_HAA_RA_FLAT_SEQUENCE WHERE FLS_RB_NO = ? AND FLS_PRJ_CODE = ? AND FLS_STATUS IN ('NEW','REJECT_OFFER','CANCEL') ORDER BY FLS_RB_NO";

	private final static String SQL_QUERY_BALLOT_APPLICATION_NOT_MATCH = "SELECT CASE_NO FROM HST_HAA_RA_NOT_MATCH_LOG WHERE RB_NO = ? ORDER BY RB_NO";

	private final static String SQL_QUERY_BALLOT_LIST = "select RB_NO, RB_SEED_NO, RB_BALLOT_DATE from HST_HAA_RA_BALLOT where RB_PRJ_CODE = ? ORDER BY RB_NO";

	/**
	 * Flat status
	 */
	public static enum FLAT_STATUS {
		READY_OFFER, TEMP_OFFER, ACCEPT_OFFER, REJECT_OFFER, TA_SIGNED, CANCEL
	}

	/**
	 * Flat categories
	 */
	public static enum FLAT_CATEGORIES {
		UDF, ELDERLY_WING, OTHER;
	}

	/**
	 * Get upload or download path
	 */
	public static enum HAA_PATH {
		DOWNLOAD("DOWNLOAD"), UPLOAD("UPLOAD"), LOG("LOG");
		private String val;

		HAA_PATH(String value) {
			this.val = value;
		}

		public String getValue() {
			return this.val;
		}
	}

	/**
	 * To check type of a flat
	 */
	public static enum FLAT_TYPE_CHECK {
		// YES
		Y,
		// NO
		N;
	}

	/**
	 * Separator between columns in file
	 */
	public String FILE_LINE_CONTENT_SEPARATOR = "\t";

	/**
	 * Size of ballot data field
	 */
	public final int SIZE_OF_BALLOT = 194;
	/**
	 * Size of appno in ballot data field
	 */
	public final int SIZE_OF_APPNO = 7;

	public final int SIZE_OF_FAM_CAT = 1;

	/**
	 * Line break in file
	 */
	public String FILE_CONTENT_LINE_BREAK = "\r\n";

	private Connection conn = null;
	private PreparedStatement psmt = null;
	private ResultSet rs = null;
	private CallableStatement proc = null;

	/**
	 * Query amount of flat according to type(category)
	 * 
	 * @param queryobj
	 * @return
	 */
	public String countFlat(String prjcode) {
		String sql = SQL_GET_RA_FLAT_CATEGORY_FOR_BALLOT_COUNT;
		if (DataUtil.isEmpty(prjcode)) {
			return ResultBuilder.buildResult("Project code can not be empty").setErrorCode(Result.ERROR_CODE_INVALID)
					.toString();
		}
		List<BallotCountClass> resultList = new ArrayList<BallotCountClass>();
		String resultStr = "";
		String[] countStr = null;
		try {
			conn = DBConnection.getConnection();
			psmt = conn.prepareStatement(sql);
			psmt.setString(1, prjcode);
			rs = psmt.executeQuery();

			if (rs.next()) {
				resultStr = rs.getString(1);
			}
			if (resultStr != null) {
				countStr = resultStr.split("\\^");
			}
			if (countStr != null) {
				for (int i = 0; i < countStr.length; i++) {
					BallotCountClass countClass = new BallotCountClass();
					String[] data = countStr[i].split("`");
					countClass.setName(data[0]);
					countClass.setNum(Integer.parseInt(data[1] == null ? "0" : data[1]));
					countClass.setDescription(data[2]);
					resultList.add(countClass);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		return ResultBuilder.buildSuccessResult(resultList).toString();
	}

	/**
	 * Query amount of application according to type(category)
	 * 
	 * @param queryobj
	 * @return
	 */
	public String countApplication(String prjcode) {
		String sql = SQL_QUERY_RA_APPLICATION_COUNT;
		if (DataUtil.isEmpty(prjcode)) {
			return ResultBuilder.buildResult("Project code can not be empty").setErrorCode(Result.ERROR_CODE_INVALID)
					.toString();
		}
		List<BallotCountClass> resultList = new ArrayList<BallotCountClass>();
		try {
			conn = DBConnection.getConnection();
			psmt = conn.prepareStatement(sql);
			psmt.setString(1, prjcode);
			psmt.setString(2, prjcode);
			psmt.setString(3, prjcode);

			rs = psmt.executeQuery();

			while (rs.next()) {
				BallotCountClass countClass = new BallotCountClass();
				countClass.setName(rs.getString(1));
				countClass.setNum(rs.getInt(2));
				countClass.setDescription(rs.getString(3));
				resultList.add(countClass);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		return ResultBuilder.buildSuccessResult(resultList).toString();
	}

	/**
	 * Generate flat query condition of project
	 * 
	 * @throws SQLException
	 * 
	 */
	public String generateQueryCondition(String projectCode) throws SQLException {
		String sql = SQL_GENERATE_PROPREFNO_FOR_FLAT_QUERY;
		conn = DBConnection.getConnection();
		String range = "";
		psmt = conn.prepareStatement(sql);
		psmt.setString(1, projectCode);
		rs = psmt.executeQuery();
		if (rs.next()) {
			range = rs.getString(1) == null ? "" : rs.getString(1);
		}
		return range;
	}

	/**
	 * Query flat list For Flat Management
	 * 
	 * @param queryObj Query condition contains project (Project Code),
	 *                 type(S*,M*,L*,UF) status( READY_OFFER,TEMP_OFFER..)
	 * @see FLAT_STATUS
	 * 
	 */
	public String queryFlatListForBallotByPage(BallotFlatQueryObj queryObj) {
		if (queryObj == null || DataUtil.isEmpty(queryObj.getProject())) {
			return ResultBuilder.buildResult("project code can not be null!").setErrorCode(Result.ERROR_CODE_INVALID)
					.toString();
		} 
		String sql = SQL_QUERY_FLAT_FOR_MANAMEGENT;

		// condition of FLAT_SEQUENCE
		StringBuilder cond = new StringBuilder();
 		if (DataUtil.isNotEmpty(queryObj.getStatus())) {
			if (FLAT_STATUS.REJECT_OFFER.name().equals(queryObj.getStatus())) {
				cond.append(" and FLS_STATUS IS NULL ");
			} else if (FLAT_STATUS.READY_OFFER.name().equals(queryObj.getStatus())) {
				cond.append(" and (FLS_STATUS IS NULL or FLS_STATUS = '").append(queryObj.getStatus()).append("')");
			} else {
				cond.append(" and FLS_STATUS = '").append(queryObj.getStatus()).append("' ");
			}
		}
		if (DataUtil.isNotEmpty(queryObj.getPropRef())) {
			cond.append(" and PROPERTY_REFNO like '" + queryObj.getPropRef() + "' ");
		}

		if (DataUtil.isNotEmpty(queryObj.getType())) {
			cond.append(" and FLATTYPE like '" + queryObj.getType().toUpperCase() + "%' ");
		}
		sql = DataUtil.strReplaceAll(sql, "<`cond`>", cond.toString());

		List<FlatClass> resultList = new ArrayList<FlatClass>(64);
		try {
			conn = DBConnection.getConnection();
			psmt = conn.prepareStatement(sql);
			psmt.setString(1, queryObj.getProject());
			psmt.setString(2, queryObj.getProject());
			psmt.setString(3, queryObj.getProject());

			rs = psmt.executeQuery();
			while (rs.next()) {
				FlatClass flat = new FlatClass();
				flat.setFlPropRef(rs.getString(1));
				flat.setFlIFA(rs.getString(2));
				flat.setFlType(rs.getString(3));
				flat.setFlFacing(rs.getString(4));
				flat.setFlEstateCode(rs.getString(5));
				flat.setFlUDF(rs.getString(6));
				flat.setFlElderlyWing(rs.getString(7));
				flat.setFlStatus(rs.getString(8));
				flat.setFlDescription(rs.getString(9));
				resultList.add(flat);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return ResultBuilder.buildServerFailResult(e.getMessage()).toString();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		return ResultBuilder.buildSuccessResult(resultList).toString();
	}

	/**
	 * Ballot Import
	 * 
	 */
	public String ballotImport(BallotImportClass parameter, byte[] appFile, byte[] flatFile, String adminName) {
		System.out.println(parameter.toString());
		System.out.println(adminName);

		String uploadPath = "";
		String logPath = "";
		try {
			String sql = SQL_GET_HAA_FILE_PATH;
			conn = DBConnection.getConnection();
			psmt = conn.prepareStatement(sql);
			psmt.setString(1, HAA_PATH.UPLOAD.getValue());
			rs = psmt.executeQuery();
			if (rs.next()) {
				uploadPath = rs.getString(1);
			}
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);

			psmt = conn.prepareStatement(sql);
			psmt.setString(1, HAA_PATH.LOG.getValue());
			rs = psmt.executeQuery();
			if (rs.next()) {
				logPath = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.buildServerFailResult(e.getMessage()).toString();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}

		if (!uploadPath.endsWith(File.separator)) {
			uploadPath += File.separator;
		}
		if (!logPath.endsWith(File.separator)) {
			logPath += File.separator;
		}

		// 2. Read file content
		Map<String, Integer> appNos = new HashMap<String, Integer>();
		Map<String, Integer> flatNos = new HashMap<String, Integer>();

		InputStream appIn = new ByteArrayInputStream(appFile);
		InputStreamReader appIsReader = new InputStreamReader(appIn);
		BufferedReader appBfReader = new BufferedReader(appIsReader);
		String appInfo = null;
		try {
			while ((appInfo = appBfReader.readLine()) != null) {
				if (DataUtil.isEmpty(appInfo)) {
					continue;
				}

				String rano = appInfo.substring(8, 42).trim();
				String priority = appInfo.substring(98, 103).trim();
				// key(raNo),value(seq)
				appNos.put(rano, Integer.parseInt(priority));
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			return ResultBuilder.buildServerFailResult(e1.getMessage()).toString();
		} finally {
			try {
				appBfReader.close();
				appIsReader.close();
				appIn.close();
			} catch (IOException e) {
				e.printStackTrace();
				return ResultBuilder.buildServerFailResult(e.getMessage()).toString();
			}
		}

		InputStream flatIn = new ByteArrayInputStream(flatFile);
		InputStreamReader flatIsReader = new InputStreamReader(flatIn);
		BufferedReader flatBfReader = new BufferedReader(flatIsReader);
		String flatInfo = null;
		try {
			while ((flatInfo = flatBfReader.readLine()) != null) {
				if (DataUtil.isEmpty(flatInfo)) {
					continue;
				}
				String rfno = flatInfo.substring(8, 42).trim();
				String priority = flatInfo.substring(98, 103).trim();
				flatNos.put(rfno, Integer.parseInt(priority));
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			return ResultBuilder.buildServerFailResult(e1.getMessage()).toString();
		} finally {
			try {
				flatBfReader.close();
				flatIsReader.close();
				flatIn.close();
			} catch (IOException e) {
				e.printStackTrace();
				return ResultBuilder.buildServerFailResult(e.getMessage()).toString();
			}
		}
		// 3. Validate file content
		if (appNos.isEmpty() || flatNos.isEmpty()) {
			// app or flat is empty!
			return ResultBuilder.buildResult("Invalid file:empty file exists!").setErrorCode(Result.ERROR_CODE_INVALID)
					.toString();
		}

		List<String> appNoList = new ArrayList<String>(64);
		List<String> flatPropList = new ArrayList<String>(64);

		String rbNo = "";
		String prjCode = "";

		String sql = SQL_QUERY_APPLICATIONS_OF_LAST_ROUND_BALLOT;
		String sql2 = SQL_QUERY_FLATS_OF_LAST_ROUND_BALLOT;
		/**
		 * Query by last ballot
		 */
		try {
			conn = DBConnection.getConnection();
			psmt = conn.prepareStatement(sql);
			rs = psmt.executeQuery();
			while (rs.next()) {
				appNoList.add(rs.getString(1));
				rbNo = rs.getString(2);
				prjCode = rs.getString(3);
			}
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);

			psmt = conn.prepareStatement(sql2);
			rs = psmt.executeQuery();
			while (rs.next()) {
				flatPropList.add(rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.buildServerFailResult(e.getMessage()).toString();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		
		if (appNoList.isEmpty() || flatPropList.isEmpty()) {
			return ResultBuilder.buildResult("Invalid file:applications or flats for ballot not found!")
					.setErrorCode(Result.ERROR_CODE_INVALID).toString();
		}
		if (appNos.size() != appNoList.size() || flatNos.size() != flatPropList.size()) {
			// : size of application or size of flat doesn't matc
			return ResultBuilder.buildResult("Invalid file:applications or flats imported not match with data in db.")
					.setErrorCode(Result.ERROR_CODE_INVALID).toString();
		}
		
		boolean flagNotExists = false;
		for (int i = 0; i < appNoList.size(); i++) {
			if (!appNos.containsKey(appNoList.get(i))) {
				flagNotExists = true;
				break;
			}
		}
		if (flagNotExists) {
			return ResultBuilder.buildResult("Invalid file:applications or flats imported not match with data in db.")
					.setErrorCode(Result.ERROR_CODE_INVALID).toString();
		}
		flagNotExists = false;
		for (int i = 0; i < flatPropList.size(); i++) {
			if (!flatNos.containsKey(flatPropList.get(i))) {
				flagNotExists = true;
				break;
			}
		}
		if (flagNotExists) {
			// File content not match
			return ResultBuilder.buildResult("Invalid file:applications or flats imported not match with data in db.")
					.setErrorCode(Result.ERROR_CODE_INVALID).toString();
		}
		// 4. Save file
		File destAppFile = new File(uploadPath + parameter.getAppFileName());
		File destFlatFile = new File(uploadPath + parameter.getFlatFileName());
		if (!destAppFile.getParentFile().exists()) {
			destAppFile.getParentFile().mkdirs();
		}
		if (!destAppFile.exists()) {
			try {
				destAppFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return ResultBuilder.buildServerFailResult("Fail to create file:" + e.getMessage()).toString();
			}
		}
		if (!destFlatFile.getParentFile().exists()) {
			destFlatFile.getParentFile().mkdirs();
		}
		
		if (!destFlatFile.exists()) {
			try {
				destFlatFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return ResultBuilder.buildServerFailResult("Fail to create file:" + e.getMessage()).toString();
			}
		}
		OutputStream outputStreamApp = null, outputStreamFlat = null;
		try {
			outputStreamApp = new FileOutputStream(destAppFile);
			outputStreamApp.write(appFile);

			outputStreamFlat = new FileOutputStream(destFlatFile);
			outputStreamFlat.write(flatFile);
		} catch (IOException e) {
			e.printStackTrace();
			return ResultBuilder.buildServerFailResult(e.getMessage()).toString();
		} finally {
			try {
				outputStreamApp.flush();
				outputStreamApp.close();
				outputStreamFlat.flush();
				outputStreamFlat.close();
			} catch (IOException e) {
				e.printStackTrace();
				return ResultBuilder.buildServerFailResult(e.getMessage()).toString();
			}
		}
		// 5.update sequence and ballot log
		BallotImportResult ballotResult = new BallotImportResult();
		List<String> failRaNoList = new ArrayList<String>(64);
		List<String> failPropRefList = new ArrayList<String>(64);
		try {
			conn = DBConnection.getConnection();

			int i = 0;
			Set<String> appNoKeys = appNos.keySet();
			Set<String> flatNoKeys = flatNos.keySet();
			Iterator<String> appIterator = appNoKeys.iterator();
			Iterator<String> flatIterator = flatNoKeys.iterator();
			sql = SQL_UPDATE_SEQUENCE_OF_APP;
			DBConnection.beginTransaction(conn);
			psmt = conn.prepareStatement(sql);

			while (appIterator.hasNext()) {
				i++;
				String rano = appIterator.next();
				psmt.setInt(1, appNos.get(rano));
				psmt.setInt(2, Integer.parseInt(rbNo));
				psmt.setString(3, rano);
				psmt.addBatch();
				if (i % 100 == 0) {
					psmt.executeBatch();
				}
			}
			psmt.executeBatch();
			psmt.clearBatch();
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);

			i = 0;
			sql = SQL_UPDATE_SEQUENCE_OF_FLAT;
			psmt = conn.prepareStatement(sql);
			while (flatIterator.hasNext()) {
				String prop = flatIterator.next();
				psmt.setInt(1, flatNos.get(prop));
				psmt.setInt(2, Integer.parseInt(rbNo));
				psmt.setString(3, prop);
				psmt.addBatch();
				if (i % 100 == 0) {
					psmt.executeBatch();
				}
			}
			psmt.executeBatch();
			psmt.clearBatch();
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);

			sql = SQL_UPDATE_BALLOT;
			psmt = conn.prepareStatement(sql);
			psmt.setString(1, parameter.getBatchRemark());
			psmt.setString(2, adminName);
			psmt.setString(3, parameter.getAppFileName());
			psmt.setString(4, parameter.getFlatFileName());
			psmt.setString(5, parameter.getSeedNo());
			psmt.setString(6, parameter.getBalloteDate());
			psmt.setString(7, rbNo);
			if (psmt.executeUpdate() <= 0) {
				DBConnection.rollback(conn);
				return ResultBuilder.buildResult("Update ballot fail").setErrorCode(Result.ERROR_CODE_FAIL).toString();
			}
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);

			// 6.Call storage procedure of match
			proc = conn.prepareCall(SQL_EXEC_MATCH);
			proc.setInt(1, Integer.parseInt(rbNo));
			proc.setString(2, prjCode);
			proc.setString(3, adminName);
			proc.registerOutParameter(4, Types.VARCHAR);
			proc.execute();

			// Get out parameter.
			String procResult = "";
			procResult = proc.getString(4);
			proc.close();

			ballotResult.setBallotCode(rbNo);
			ballotResult.setProjectCode(prjCode);
			ballotResult.setSeedNo(parameter.getSeedNo());
			ballotResult.setBallotDate(parameter.getBalloteDate());
			if (DataUtil.isEmpty(procResult)) {
				DBConnection.commit(conn);
				return ResultBuilder.buildSuccessResult(ballotResult).toString();
			} else {
				ballotResult.setMessage(procResult);
				// Generate error log
				// Query application not matched and flat not matched
				sql = SQL_QUERY_BALLOT_APPLICATION_NOT_MATCH;
				psmt = conn.prepareStatement(sql);
				psmt.setString(1, rbNo);
				rs = psmt.executeQuery();
				while (rs.next()) {
					failRaNoList.add(rs.getString(1));
				}
				DBConnection.closeResultSet(rs);
				DBConnection.closePreparedStatement(psmt);

				sql = SQL_QUERY_BALLOT_FLAT_NOT_MATCH;
				psmt = conn.prepareStatement(sql);
				psmt.setString(1, rbNo);
				psmt.setString(2, prjCode);
				rs = psmt.executeQuery();
				while (rs.next()) {
					failPropRefList.add(rs.getString(2));
				}
				DBConnection.closeResultSet(rs);
				DBConnection.closePreparedStatement(psmt);
			}
			DBConnection.commit(conn);
		} catch (Exception e) {
			e.printStackTrace();
			DBConnection.rollback(conn);
			return ResultBuilder.buildServerFailResult(e.getMessage()).toString();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closeProc(proc);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		// save fail log file
		if (!failRaNoList.isEmpty()) {
			FileWriter writerApp = null;
			File fileAppLog = new File(logPath + "BallotAppLog_" + System.currentTimeMillis() + ".log");
			if (!fileAppLog.getParentFile().exists()) {
				fileAppLog.getParentFile().mkdirs();
			}
			if (!fileAppLog.exists()) {
				try {
					fileAppLog.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					return ResultBuilder.buildServerFailResult(e.getMessage()).toString();
				}
			}
			try {
				writerApp = new FileWriter(fileAppLog);
				for (String rano : failRaNoList) {
					writerApp.write(rano + FILE_CONTENT_LINE_BREAK);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return ResultBuilder.buildServerFailResult(e.getMessage()).toString();
			} finally {
				try {
					writerApp.flush();
					writerApp.close();
				} catch (IOException e) {
					e.printStackTrace();
					return ResultBuilder.buildServerFailResult(e.getMessage()).toString();
				}
			}
			ballotResult.setUnMatchAppFile(fileAppLog.getName());
		}

		if (!failPropRefList.isEmpty()) {

			FileWriter writerFlat = null;
			File fileFlatLog = new File(logPath + "BallotFlatLog_" + System.currentTimeMillis() + ".log");
			if (!fileFlatLog.getParentFile().exists()) {
				fileFlatLog.getParentFile().mkdirs();
			}
			if (!fileFlatLog.exists()) {
				try {
					fileFlatLog.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					return ResultBuilder.buildServerFailResult(e.getMessage()).toString();
				}
			}
			try {
				writerFlat = new FileWriter(fileFlatLog);
				for (String prop : failPropRefList) {
					writerFlat.write(prop + FILE_CONTENT_LINE_BREAK);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return ResultBuilder.buildServerFailResult(e.getMessage()).toString();
			} finally {
				try {
					writerFlat.flush();
					writerFlat.close();
				} catch (IOException e) {
					e.printStackTrace();
					return ResultBuilder.buildServerFailResult(e.getMessage()).toString();
				}
			}
			ballotResult.setUnMatchFlatFile(fileFlatLog.getName());
		}
		// return saved file name
		return ResultBuilder.buildSuccessResult(ballotResult).setErrorCode(Result.ERROR_CODE_WARN).toString();
	}

	/**
	 * 
	 * @param prjcode
	 * @return
	 */
	public String checkOutStanding(String prjcode) {
		String sql = SQL_CHECK_OUTSTANDING_BALLOT;
		int result = 0;
		conn = DBConnection.getConnection();
		try {
			psmt = conn.prepareStatement(sql);
			psmt.setString(1, prjcode);
			rs = psmt.executeQuery();
			if (rs.next()) {
				result = rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		return ResultBuilder.buildSuccessResult(result > 0 ? 1 : -1).toString();
	}

	/**
	 * Upload file for ballot
	 * 
	 * flat list <Property Ref No> <Group No> Group No default to 1
	 * 
	 * application list <Case No> <Group No> Group No default to 1
	 */
	public String uploadFile(String filename, byte[] fileContent) {
		if (DataUtil.isEmpty(filename)) {
			return ResultBuilder.buildResult("file name can not be empty").setErrorCode(Result.ERROR_CODE_INVALID)
					.toString();
		}

		String basePath = "";
		try {
			String sql = SQL_GET_HAA_FILE_PATH;
			conn = DBConnection.getConnection();
			psmt = conn.prepareStatement(sql);
			psmt.setString(1, "");
			rs = psmt.executeQuery();
			if (rs.next()) {
				basePath = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.buildServerFailResult(e.getMessage()).toString();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		if (!basePath.endsWith(File.separator)) {
			basePath += File.separator;
		}
		File destFile = new File(basePath + "uploadfiles");
		if (!destFile.getParentFile().exists()) {
			destFile.mkdirs();
		}
		if (!destFile.exists()) {
			try {
				destFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return ResultBuilder.buildServerFailResult(e.getMessage()).toString();
			}
		}
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(destFile);
			outputStream.write(fileContent);
		} catch (IOException e) {
			e.printStackTrace();
			return ResultBuilder.buildServerFailResult(e.getMessage()).toString();
		} finally {
			try {
				outputStream.flush();
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
				return ResultBuilder.buildServerFailResult(e.getMessage()).toString();
			}
		}
		return ResultBuilder.buildSuccessResult().setMsg(filename + "upload success").toString();
	}

	/**
	 * Check flat matched in last round has been accepted/refused.New round only
	 * start when all flats matched in last round have been accepted/refused.
	 * 
	 * @return 0 if none flat status of last round( is 'TEMP_OFFER'
	 */
	public List<FlatClass> checkFlatBeforeBallotExport(String prjCode) {
		// "select FLS_PROP_REF, FLS_SEQ, FLS_PRJ_CODE HST_HAA_RA_FLAT_SEQUENCE where
		// FLS_STATUS ='"+ FLAT_STATUS.TEMP_OFFER.name()+"' ";
		String sql = SQL_QUERY_TEMP_OFFER_FLAT;
		List<FlatClass> list = new ArrayList<FlatClass>();
		FlatClass flat = new FlatClass();
		try {
			conn = DBConnection.getConnection();
			psmt = conn.prepareStatement(sql);
			psmt.setString(1, prjCode);

			rs = psmt.executeQuery();
			while (rs.next()) {
				flat.setFlPropRef(rs.getString(2));
				flat.setFlSeq(rs.getInt(3));
				list.add(flat);
			}
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		return list;
	}

	/**
	 * Export application file and flat file for ballot.
	 * 
	 * @param queryObjApp queryObjFlat
	 * @return result with ballot application file name , ballot flat file name and
	 *         rbno.
	 */
	public String ballotExport(BallotFlatQueryObj queryObjApp, BallotFlatQueryObj queryObjFlat, String adminName) {
		List<FlatClass> list = checkFlatBeforeBallotExport(queryObjApp.getProject());
		if (!list.isEmpty()) {
			return ResultBuilder.buildResult().setErrorCode(Result.ERROR_CODE_FORBIDDEN).setMsg(
					"There are matched flat(s) from previous ballot cycle that are either accepted/refused. Subsequent ballot cycle cannot proceed.")
					.toString();
		}
		String basePath = "";
		BallotExportResult result = new BallotExportResult();
		// 1.Query flat list of which status is READY OFFER.
		if (DataUtil.isEmpty(queryObjApp.getProject()) || DataUtil.isEmpty(queryObjFlat.getProject())) {
			return ResultBuilder.buildResult().setErrorCode(Result.ERROR_CODE_INVALID)
					.setMsg("Project code can not be empty").toString();
		}
		// Categories can not be empty
		if (DataUtil.isEmpty(queryObjApp.getCategories()) || DataUtil.isEmpty(queryObjFlat.getCategories())) {
			return ResultBuilder.buildResult().setErrorCode(Result.ERROR_CODE_INVALID)
					.setMsg("Categories code can not be empty").toString();
		}
		// application list info from db
		List<String> listRanos = new ArrayList<String>(64);
		// Store flat list info from db,key is flat rfno and value is flat info object
		Map<String, FlatInfo> mapRfnos = new LinkedHashMap<String, FlatInfo>(64);

		String sql = SQL_GET_HAA_FILE_PATH;
		try {
			conn = DBConnection.getConnection();
			psmt = conn.prepareStatement(sql);
			psmt.setString(1, HAA_PATH.DOWNLOAD.name());
			rs = psmt.executeQuery();
			if (rs.next()) {
				basePath = rs.getString(1);
			}
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);

			// Query if elderly selected
			int elderlySelected = 0;
			if (queryObjApp.getCategories() != null) {
				for (int i = 0; i < queryObjApp.getCategories().length; i++) {
					if ("Elderyly".equals(queryObjApp.getCategories()[i])) {
						elderlySelected = 1;
						break;
					}
				}
			}

			// Query ballot applicatoin list
			sql = SQL_QUERY_RA_APPLICATION_FOR_BALLOT2;
			// Query condition
			StringBuilder cond = new StringBuilder(" 1=1 ");
//			cond.append(" AND RA_PRJ_CODE = '").append(queryObjApp.getProject()).append("'");
			if (DataUtil.isNotEmpty(queryObjApp.getCategories())) {
				cond.append(" AND RC_CATEGORY in (");
				for (int i = 0; i < queryObjApp.getCategories().length; i++) {
					if (i != 0) {
						cond.append(",");
					}
					cond.append("'").append(queryObjApp.getCategories()[i]).append("'");
				}
				cond.append(")");
			}
			sql = DataUtil.strReplaceAll(sql, "<`cond`>", cond.toString());
			psmt = conn.prepareStatement(sql);
			psmt.setInt(1, elderlySelected);
			psmt.setInt(2, elderlySelected);
			psmt.setString(3, queryObjApp.getProject());

			rs = psmt.executeQuery();
			while (rs.next()) {
				listRanos.add(rs.getString(1));
			}
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			if (listRanos.isEmpty()) {
				DBConnection.closeConnection(conn);
				return ResultBuilder.buildResult("Applications meeting the conditions not found")
						.setErrorCode(Result.ERROR_CODE_INVALID).toString();
			}

			// Query Ballot flat list
			sql = SQL_QUERY_RA_FLAT_FOR_BALLOT_EXPORT;
			cond = new StringBuilder();
			if (queryObjFlat.getCategories() != null && queryObjFlat.getCategories().length > 0) {
				cond.append(" and (");
				for (int i = 0; i < queryObjFlat.getCategories().length; i++) {
					if (i != 0) {
						cond.append(" or ");
					}
					// If UDF and ELDERLY_WING are not 'Y' then the record is OTHER.
					if (FLAT_CATEGORIES.OTHER.name().equals(queryObjFlat.getCategories()[i].toUpperCase())) {
						cond.append(" ( nvl(").append(FLAT_CATEGORIES.ELDERLY_WING.name() + ",'N') <>'Y' " + " and nvl("
								+ FLAT_CATEGORIES.UDF.name() + ",'N') <>'Y')");
						continue;
					}
					cond.append(queryObjFlat.getCategories()[i].toUpperCase()).append("='Y'");
				}
				cond.append(")");
			}
			sql = DataUtil.strReplaceAll(sql, "<`cond`>", cond.toString());

			psmt = conn.prepareStatement(sql);
			psmt.setString(1, queryObjFlat.getProject());
 			psmt.setString(2, queryObjFlat.getProject());
			psmt.setString(3, queryObjFlat.getProject());

			rs = psmt.executeQuery();
			while (rs.next()) {
				FlatInfo flat = new FlatInfo();
				flat.setFlatSize(rs.getString(2));
				flat.setMinAlloc(rs.getInt(3));
				flat.setMaxAlloc(rs.getInt(4));
				flat.setIfa(rs.getDouble(5));
				// key: propref_no
				mapRfnos.put(rs.getString(1), flat);
			}
			if (mapRfnos.isEmpty()) {
				DBConnection.closeConnection(conn);
				return ResultBuilder.buildResult("Flats meeting the conditions not found")
						.setErrorCode(Result.ERROR_CODE_INVALID).toString();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return ResultBuilder.buildServerFailResult(e.getMessage()).toString();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		// Generate File
		File fileApp = null, fileFlat = null;
		FileWriter fileWriter = null, fileWriter2 = null;
		try {
			fileApp = new File(basePath + File.separator + "APPLICATIONS_" + System.currentTimeMillis() + ".txt");
			fileFlat = new File(basePath + File.separator + "FLATS_" + System.currentTimeMillis() + ".txt");

			if (!fileApp.getParentFile().exists()) {
				fileApp.getParentFile().mkdirs();
			}
			if (!fileApp.exists()) {
				fileApp.createNewFile();
			}
			if (!fileFlat.getParentFile().exists()) {
				fileFlat.getParentFile().mkdirs();
			}
			if (!fileFlat.exists()) {
				fileFlat.createNewFile();
			}
			fileWriter = new FileWriter(fileApp);
			fileWriter2 = new FileWriter(fileFlat);
			StringBuilder sbuilder;
			for (String rano : listRanos) {
				sbuilder = new StringBuilder();

//				char[] FILLING = new char[186];

				char[] FILLING = new char[SIZE_OF_BALLOT - SIZE_OF_APPNO - SIZE_OF_FAM_CAT];
				Arrays.fill(FILLING, ' ');
				for (int i = 0; i < rano.length(); i++) {
					FILLING[i] = rano.charAt(i);
				}
				sbuilder.append(rano.substring(rano.length() - SIZE_OF_APPNO)).append("1").append(FILLING)
						.append(FILE_CONTENT_LINE_BREAK);
				fileWriter.write(sbuilder.toString());
			}
			
			Iterator<Entry<String, FlatInfo>> iter = mapRfnos.entrySet().iterator();
			while(iter.hasNext()) {
				sbuilder = new StringBuilder();
				Entry<String,FlatInfo> entry = iter.next();
				String rfno = entry.getKey();
				char[] FILLING = new char[SIZE_OF_BALLOT - SIZE_OF_APPNO - SIZE_OF_FAM_CAT];
				Arrays.fill(FILLING, ' ');
				for (int i = 0; i < rfno.length(); i++) {
					FILLING[i] = rfno.charAt(i);
				}
				sbuilder.append(rfno.substring(rfno.length() - SIZE_OF_APPNO)).append("1").append(FILLING)
						.append(FILE_CONTENT_LINE_BREAK);
				fileWriter2.write(sbuilder.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.buildServerFailResult(e.getMessage()).toString();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
				fileWriter2.flush();
				fileWriter2.close();
			} catch (IOException e) {
				e.printStackTrace();
				return ResultBuilder.buildServerFailResult(e.getMessage()).toString();
			}
		}
		result.setAppFileName(fileApp.getName());
		result.setFlatFileName(fileFlat.getName());

		// Save Ballot log,Ballot flat log,Ballot app log,Ballot flat cat log,Ballot app
		// cat log
		try {
			conn = DBConnection.getConnection();
			DBConnection.beginTransaction(conn);
			int rbNo = 0;
			sql = SQL_GET_BALLOT_SEQ;
			psmt = conn.prepareStatement(sql);
			rs = psmt.executeQuery();
			if (rs.next()) {
				rbNo = rs.getInt(1);
			}
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			sql = SQL_SAVE_UPLOAD_BALLOT_LOG;
			psmt = conn.prepareStatement(sql);
			psmt.setInt(1, rbNo);
			psmt.setString(2, queryObjApp.getProject());
			psmt.setString(3, fileFlat.getName());
			psmt.setString(4, fileApp.getName());
			psmt.setString(5, adminName);
			if (psmt.executeUpdate() <= 0) {
				DBConnection.closeConnection(conn);
				return ResultBuilder.buildServerFailResult("Generate ballot record fail!").toString();
			}
			result.setBallotNo(rbNo + "");
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);

			// Save ballot application and ballot flat log
			sql = SQL_SAVE_BALLOT_APPLICATION;
			psmt = conn.prepareStatement(sql);
			for (int i = 0; i < listRanos.size(); i++) {
				psmt.setInt(1, rbNo);
				psmt.setString(2, listRanos.get(i));
				psmt.setInt(3, -1);
				psmt.setInt(4, 0);
				psmt.addBatch();
				if ((i + 1) % 100 == 0) {
					psmt.executeBatch();
					psmt.clearBatch();
				}
			}
			psmt.executeBatch();
			psmt.clearBatch();
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);

			sql = SQL_SAVE_BALLOT_FLAT;
			psmt = conn.prepareStatement(sql);
			Set<String> keySet = mapRfnos.keySet();
			int tempCount = 0;
			for (String key : keySet) {
				tempCount++;
				psmt.setInt(1, rbNo);
				psmt.setString(2, key);
				psmt.setInt(3, -1);
				psmt.setString(4, FLAT_STATUS.READY_OFFER.name());
				psmt.setString(5, queryObjFlat.getProject());
				psmt.setInt(6, mapRfnos.get(key).getMinAlloc());
				psmt.setInt(7, mapRfnos.get(key).getMaxAlloc());
				psmt.setString(8, mapRfnos.get(key).getFlatSize());// flat size
				psmt.setDouble(9,mapRfnos.get(key).getIfa());
				psmt.addBatch();
				if ((tempCount + 1) % 100 == 0) {
					psmt.executeBatch();
				}
			}
			psmt.executeBatch();
			psmt.clearBatch();
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			// Save ballot application cat and ballot flat cat
			sql = SQL_SAVE_BALLOT_APPLICATION_CAT;
			psmt = conn.prepareStatement(sql);
			for (int j = 0; j < queryObjApp.getCategories().length; j++) {
				System.out.println(queryObjApp.getCategories()[j]);
				psmt.setInt(1, rbNo);
				psmt.setString(2, queryObjApp.getProject());
				psmt.setString(3, queryObjApp.getCategories()[j]);
				psmt.addBatch();
			}
			psmt.executeBatch();
			psmt.clearBatch();

			sql = SQL_SAVE_BALLOT_FLAT_CAT;
			psmt = conn.prepareStatement(sql);
			for (int j = 0; j < queryObjFlat.getCategories().length; j++) {
				System.out.println(queryObjFlat.getCategories()[j]);
				psmt.setInt(1, rbNo);
				psmt.setString(2, queryObjFlat.getProject());
				psmt.setString(3, queryObjFlat.getCategories()[j]);
				psmt.addBatch();
			}
			psmt.executeBatch();
			DBConnection.commit(conn);
		} catch (SQLException e) {
			e.printStackTrace();
			DBConnection.rollback(conn);
			return ResultBuilder.buildServerFailResult(e.getMessage()).toString();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		return ResultBuilder.buildSuccessResult(result).toString();
	}

	public String queryBallotList(String prjCode) {
		List<BallotEntity> resultList = new ArrayList<BallotEntity>();
		try {
			conn = DBConnection.getConnection();
			String sql = SQL_QUERY_BALLOT_LIST;
			psmt = conn.prepareStatement(sql);
			psmt.setString(1, prjCode);
			rs = psmt.executeQuery();
			while (rs.next()) {
				BallotEntity entity = new BallotEntity();
				entity.setRbNo(rs.getString(1));
				entity.setSeedNo(rs.getString(2));
				entity.setBallotDate(rs.getString(3));
				resultList.add(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.buildServerFailResult(e.getMessage()).toString();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		return ResultBuilder.buildSuccessResult(resultList).toString();
	}
}
