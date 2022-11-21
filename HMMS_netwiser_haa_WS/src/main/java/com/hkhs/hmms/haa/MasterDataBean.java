package com.hkhs.hmms.haa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import com.hkhs.hmms.haa.entity.ChargeItemClass;
import com.hkhs.hmms.haa.util.DBConnection;
import com.hkhs.hmms.haa.util.DataUtil;

import net.sf.json.JSONObject;

@Service
public class MasterDataBean {

	@Autowired
	private DataSource dataSource;

	private Connection conn = null;
	private PreparedStatement psmt = null;
	private ResultSet rs = null;
	private boolean bInit = false;

	private final static String SQL_QUERY_HAA_STATUS = " select LOVD_VALUE_CHAR, LOVD_VALUE_DES_ENG, LOVD_VALUE_DES_CHT,LOVD_DEFAULT_IND,LOVD_DISP_SEQ_NO "
			+ " from HST_HMMS_LIST_OF_VALUE_DETAIL where LOVD_LOVH_CODE ='HAA_STATUS' " + "  AND  LOVD_ACTIVE_IND='Y' "
			+ " ORDER BY LOVD_DISP_SEQ_NO ";

	private final static String SQL_QUERY_HAA_FLAT_STATUS = "select LOVD_VALUE_CHAR, LOVD_VALUE_DES_ENG, LOVD_VALUE_DES_CHT,LOVD_DEFAULT_IND,LOVD_DISP_SEQ_NO "
			+ "	FROM HST_HMMS_LIST_OF_VALUE_DETAIL WHERE LOVD_LOVH_CODE = 'HAA_FLAT_STATUS' ORDER BY LOVD_DISP_SEQ_NO DESC";

	private final static String SQL_QUERY_HAA_REFUSE_REASON = "select LOVD_VALUE_CHAR,LOVD_VALUE_DES_ENG FROM HST_HMMS_LIST_OF_VALUE_DETAIL"
			+ " WHERE LOVD_LOVH_CODE = 'HAA_REFUSE_REASON' ORDER BY LOVD_DISP_SEQ_NO DESC";

	private final static String SQL_QUERY_HAA_REDEV_PROJECT = "SELECT RP_PRJ_CODE, RP_DESCRIPTION FROM  HST_HAA_REDEV_PROJECT ORDER BY  rp_update_date desc, rp_create_date desc";

	private final static String SQL_QUERY_HAA_APPLICATION_CATEGORY = "SELECT RDC_PRJ_CODE,RDC_CATEGORY,RDC_DESCRIPTION FROM HST_HAA_REDEV_CATEGORY WHERE RDC_PRJ_CODE = ? ORDER BY RDC_CREATE_DATE";

	private final static String SQL_QUERY_HAA_APPLICATION_FLAT_SIZE = " SELECT RDS_PRJ_CODE, RDS_SIZE, RDS_DESCRIPTION, RDS_MIN_ALLOC, RDS_MAX_ALLOC "
			+ "	  FROM  HST_HAA_REDEV_FLAT_SIZE " + "	WHERE RDS_PRJ_CODE=? " + "	  ORDER BY RDS_DISPLAY_SEQ ";

	public void init() {

		if (!bInit) {
			bInit = true;
			conn = DBConnection.getConnection(dataSource);
		}

	}

	public void done() throws SQLException {
		if (bInit) {
			bInit = false;
			conn.close();
		}
	}

	public void initByParent(Connection con) throws SQLException {
		bInit = false;
		this.conn = con;
	}

	public String getFlatSizeList(String prjCode) {
		String xml = "";
		try {
			Boolean isFirst = true;
			StringBuilder buff = new StringBuilder();
			String sql = SQL_QUERY_HAA_APPLICATION_FLAT_SIZE;
			conn = DBConnection.getConnection(dataSource);
			psmt = conn.prepareStatement(sql);
			if (DataUtil.isEmpty(prjCode)) {
				return "";
			}
			psmt.setString(1, prjCode.trim().toUpperCase());
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
				jsonObj.element("prjcode", rs.getString(1));
				jsonObj.element("size", rs.getString(2));
				jsonObj.element("description", rs.getString(3));
				jsonObj.element("minAlloc", rs.getString(4));
				jsonObj.element("maxAlloc", rs.getString(5));
				buff.append(jsonObj.toString());
			}
			buff.append("]");
			xml = buff.toString();
		} catch (SQLException e) {
			e.printStackTrace();
			xml = "FAIL:SQL:" + e.getErrorCode() + ":" + e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
			xml = "FAIL:SYS:" + e.getMessage();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		return DataUtil.escXml(xml);
	}

	public String getApplicationCategoryList(String prjCode, String raNO) {
		String xml = "";
		try {
			Boolean isFirst = true;
			StringBuilder buff = new StringBuilder();
			String sql = SQL_QUERY_HAA_APPLICATION_CATEGORY;
			// StringBuilder cond = new StringBuilder(" 1=1 ");
			// if (DataUtil.isNotEmpty(prjCode)) {
			// cond.append(" AND A.RDC_PRJ_CODE =
			// '").append(prjCode.toUpperCase()).append("'");
			// }
			// if (DataUtil.isNotEmpty(raNO)) {
			// cond.append(" AND B.RC_RA_NO = '").append(raNO).append("'");
			// }
			// sql = DataUtil.strReplaceAll(sql, "<`cond`>", cond.toString());

			conn = DBConnection.getConnection(dataSource);
			psmt = conn.prepareStatement(sql);
			if (prjCode == null) {
				return "";
			}
			psmt.setString(1, prjCode);
			rs = psmt.executeQuery();

			buff.append("[");
			while (rs.next()) {
				if (isFirst) {
					isFirst = false;
				} else {
					buff.append(",");
				}
				buff.append("{");

				buff.append("\"prjcode\":\"" + DataUtil.nvl(rs.getString(1), "") + "\"");
				buff.append(",\"category\":\"" + DataUtil.nvl(rs.getString(2), "") + "\"");
				buff.append(",\"description\":\"" + DataUtil.nvl(rs.getString(3), "") + "\"");
				buff.append("}");
			}
			buff.append("]");
			xml = buff.toString();
		} catch (SQLException e) {
			e.printStackTrace();
			xml = "FAIL:SQL:" + e.getErrorCode() + ":" + e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
			xml = "FAIL:SYS:" + e.getMessage();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		return DataUtil.escXml(xml);
	}

	public String getFlatStatusList(String parameter) {
		String xml = "";
		try {
			Boolean isFirst = true;
			StringBuilder buff = new StringBuilder();
			String sql = SQL_QUERY_HAA_FLAT_STATUS;
			conn = DBConnection.getConnection(dataSource);
			psmt = conn.prepareStatement(sql);
			rs = psmt.executeQuery();
			buff.append("[");
			while (rs.next()) {
				if (isFirst) {
					isFirst = false;
				} else {
					buff.append(",");
				}
				buff.append("{");
				buff.append("\"status\":\"" + DataUtil.nvl(rs.getString(1), "") + "\"");
				buff.append(",\"description\":\"" + DataUtil.nvl(rs.getString(2), "") + "\"");
				buff.append("}");
			}
			buff.append("]");
			xml = buff.toString();
		} catch (SQLException e) {
			e.printStackTrace();
			xml = "FAIL:SQL:" + e.getErrorCode() + ":" + e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
			xml = "FAIL:SYS:" + e.getMessage();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		return DataUtil.escXml(xml);
	}

	public String getRefuseReasons() {
		String xml = "";
		try {
			Boolean isFirst = true;
			StringBuilder buff = new StringBuilder();
			String sql = SQL_QUERY_HAA_REFUSE_REASON;
			conn = DBConnection.getConnection(dataSource);
			psmt = conn.prepareStatement(sql);
			rs = psmt.executeQuery();
			buff.append("[");
			while (rs.next()) {
				if (isFirst) {
					isFirst = false;
				} else {
					buff.append(",");
				}
				buff.append("{");
				buff.append("\"code\":\"" + DataUtil.nvl(rs.getString(1), "") + "\"");
				buff.append(",\"description\":\"" + DataUtil.nvl(rs.getString(2), "") + "\"");
				buff.append("}");
			}
			buff.append("]");
			xml = buff.toString();
		} catch (SQLException e) {
			e.printStackTrace();
			xml = "FAIL:SQL:" + e.getErrorCode() + ":" + e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
			xml = "FAIL:SYS:" + e.getMessage();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		return DataUtil.escXml(xml);
	}

	public String getApplicationStatusList(String parameter) {
		String xml = "";
		try {
			Boolean isFirst = true;
			StringBuilder buff = new StringBuilder();
			String sql = SQL_QUERY_HAA_STATUS;
			conn = DBConnection.getConnection(dataSource);
			psmt = conn.prepareStatement(sql);
			rs = psmt.executeQuery();
			buff.append("[");
			while (rs.next()) {
				if (isFirst) {
					isFirst = false;
				} else {
					buff.append(",");
				}
				buff.append("{");
				buff.append("\"status\":\"" + DataUtil.nvl(rs.getString(1), "") + "\"");
				buff.append(",\"description\":\"" + DataUtil.nvl(rs.getString(2), "") + "\"");
				buff.append("}");
			}
			buff.append("]");
			xml = buff.toString();
		} catch (SQLException e) {
			e.printStackTrace();
			xml = "FAIL:SQL:" + e.getErrorCode() + ":" + e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
			xml = "FAIL:SYS:" + e.getMessage();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		return DataUtil.escXml(xml);
	}

	/**
	 * Query Flat list
	 * 
	 * @param parameter
	 * @return
	 */
	public String getFlstStatusList(String prjCode) {
		String xml = "";
		try {
			Boolean isFirst = true;
			StringBuilder buff = new StringBuilder();
			String sql = SQL_QUERY_HAA_STATUS;
			conn = DBConnection.getConnection(dataSource);
			psmt = conn.prepareStatement(sql);
			rs = psmt.executeQuery();
			buff.append("[");
			while (rs.next()) {
				if (isFirst) {
					isFirst = false;
				} else {
					buff.append(",");
				}
				buff.append("{");
				buff.append("\"status\":\"" + DataUtil.nvl(rs.getString(1), "") + "\"");
				buff.append(",\"description\":\"" + DataUtil.nvl(rs.getString(2), "") + "\"");
				buff.append("}");
			}
			buff.append("]");
			xml = buff.toString();
		} catch (SQLException e) {
			e.printStackTrace();
			xml = "FAIL:SQL:" + e.getErrorCode() + ":" + e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
			xml = "FAIL:SYS:" + e.getMessage();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		return DataUtil.escXml(xml);
	}

	public String getRedevProjectList(String parameter) {
		String xml = "";
		Boolean isFirst = true;
		try {
			StringBuilder buff = new StringBuilder();
			String sql = SQL_QUERY_HAA_REDEV_PROJECT;
			// generate condition
			// String cond = "";

			conn = DBConnection.getConnection(dataSource);
			psmt = conn.prepareStatement(sql);
			rs = psmt.executeQuery();
			buff.append("[");
			while (rs.next()) {
				if (isFirst) {
					isFirst = false;
				} else {
					buff.append(",");
				}
				buff.append("{");
				buff.append("\"prjcode\":\"" + DataUtil.nvl(rs.getString(1), "") + "\"");
				buff.append(",\"description\":\"" + DataUtil.nvl(rs.getString(2), "") + "\"");

				buff.append("}");
			}
			buff.append("]");

			xml = buff.toString();
		} catch (SQLException e) {
			e.printStackTrace();
			xml = "FAIL:SQL:" + e.getErrorCode() + ":" + e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
			xml = "FAIL:SYS:" + e.getMessage();
		} finally {
			DBConnection.closeResultSet(rs);
			DBConnection.closePreparedStatement(psmt);
			DBConnection.closeConnection(conn);
		}
		return DataUtil.escXml(xml);
	}

}
