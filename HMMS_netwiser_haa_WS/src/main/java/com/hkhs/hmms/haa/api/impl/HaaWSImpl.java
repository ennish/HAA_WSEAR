package com.hkhs.hmms.haa.api.impl;

import com.hkhs.hmms.haa.ApplicationBean;
import com.hkhs.hmms.haa.BallotBean;
import com.hkhs.hmms.haa.MasterDataBean;
import com.hkhs.hmms.haa.OfferBean;
import com.hkhs.hmms.haa.ReportForCrystalBean;
import com.hkhs.hmms.haa.api.HaaWS;
import com.hkhs.hmms.haa.entity.ApplicationClass;
import com.hkhs.hmms.haa.entity.Result;
import com.hkhs.hmms.haa.entity.ResultBuilder;
import com.hkhs.hmms.haa.queryEntity.ApplicationQueryObj;
import com.hkhs.hmms.haa.queryEntity.BallotFlatQueryObj;
import com.hkhs.hmms.haa.queryEntity.BallotImportClass;
import com.hkhs.hmms.haa.util.DataUtil;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

public class HaaWSImpl implements HaaWS{

 /*** MasterData Bean ***/
 public String helloWorld() {
    return "Hello world";
}

public String version() {
    return "Version 2.0.1 Build Date: 2020-01-14 before deployed";
}

public String getApplicationStatusList(String parameter) {
    MasterDataBean masterBean = new MasterDataBean();
    return masterBean.getApplicationStatusList(parameter);
}

public String queryOfferHistory(String rano) {
    ApplicationBean bean = new ApplicationBean();
    String result = bean.queryOfferHistory(rano);
    return result;
}

public String getFlatStatusList(String parameter) {
    MasterDataBean masterBean = new MasterDataBean();
    return masterBean.getFlatStatusList(parameter);
}

public String getRefuseReasonList() {
    MasterDataBean masterBean = new MasterDataBean();
    return masterBean.getRefuseReasons();
}

public String getRedevProjectList(String parameter) {
    MasterDataBean masterBean = new MasterDataBean();
    return masterBean.getRedevProjectList(parameter);
}

public String getApplicationCategoryList(String prjcode) {
    MasterDataBean masterBean = new MasterDataBean();
    return masterBean.getApplicationCategoryList(prjcode, "");
}

public String getFlatSizeList(String prjcode) {
    MasterDataBean masterBean = new MasterDataBean();
    return masterBean.getFlatSizeList(prjcode);
}

/*** Application Bean ***/
/**
 * Query application list by page.
 * 
 * @param parameter   query condition in json { "estateCode":"", "caseNo":"",
 *                    "tenantEngName:"", "offerLetterReplyDate":"",
 *                    "tenancyRef":"", "status":"" }
 * @param propertyRef Fuzzy query
 * @param pageIndex
 * @param pageSize
 * @return
 */
public String queryApplicationListByPage(String parameter, String propertyRef, int pageIndex, int pageSize) {
    ApplicationBean applicationBean = new ApplicationBean();
    JSONObject beanJSON = JSONObject.fromObject(parameter);
    if (DataUtil.CheckCateria(parameter, propertyRef)) {
        return ResultBuilder.buildResult("invalid parameter").setErrorCode(Result.ERROR_CODE_INVALID).toString();
    }
    ApplicationQueryObj bean = (ApplicationQueryObj) JSONObject.toBean(beanJSON, ApplicationQueryObj.class);
    String result = applicationBean.queryApplicationListByPage(bean, propertyRef, pageIndex, pageSize);
    return result;
}

/**
 * Query application
 * 
 * @param propertyRef Fuzzy query
 * @param tenancyRef
 * @return
 */
public String queryApplication(String propertyRef, String tenancyRef, String raNo) {
    ApplicationBean applicationBean = new ApplicationBean();
    if (DataUtil.CheckCateria(tenancyRef, propertyRef)) {
        return ResultBuilder.buildResult("invalid parameter").setErrorCode(Result.ERROR_CODE_INVALID).toString();
    }
    return applicationBean.queryApplication(propertyRef, tenancyRef, raNo);
}

/**
 * Generate application
 * 
 * @param parameter
 * @param adminName
 * @return
 */
public String createApplication(String parameter, String adminName) {
    ApplicationBean applicationBean = new ApplicationBean();
    JSONObject beanJSON = JSONObject.fromObject(parameter);
    ApplicationClass bean = (ApplicationClass) JSONObject.toBean(beanJSON, ApplicationClass.class);
    if (bean == null) {
        return "Fail, parameter can not be empty";
    }
    bean.setRaCreateBy(adminName);
    return applicationBean.insertApplication(bean);
}

/**
 * Update application
 * 
 * @param parameter Json string of ApplicationClass
 * @return
 */
public String updateApplication(String parameter, String adminName) {
    ApplicationBean applicationBean = new ApplicationBean();
    if (DataUtil.CheckCateria(parameter, adminName)) {
        return ResultBuilder.buildResult("invalid parameter").setErrorCode(Result.ERROR_CODE_INVALID).toString();
    }
    JSONObject beanJSON = JSONObject.fromObject(parameter);
    ApplicationClass bean = (ApplicationClass) JSONObject.toBean(beanJSON, ApplicationClass.class);
    if (bean == null) {
        return "Fail, parameter can not be empty";
    }
    bean.setRaUpdateBy(adminName);
    return applicationBean.updateApplication(bean);
}

/**
 * Query flat capacity by prjcode and size
 * 
 * @param prjcode
 * @param size
 * @return
 */
public String queryFlatCapacityByPrjCodeAndSize(String prjcode, String size) {
    ApplicationBean applicationBean = new ApplicationBean();
    int result[] = applicationBean.queryFlatCapacityByPrjCodeAndSize(prjcode, size);
    return "{" + result[0] + "," + result[1] + "}";
}

/**
 * query data for create application
 */
public String queryPropertyInfo(String tenancyRef, String propRef) {
    ApplicationBean applicationBean = new ApplicationBean();
    if (DataUtil.isEmpty(tenancyRef)) {
        tenancyRef = "";
    }
    if (DataUtil.isEmpty(propRef)) {
        propRef = "";
    }
    String result = applicationBean.queryPropertyInfo(tenancyRef, propRef);
    return result;
}

/**
 * Query tenant's household person
 * 
 * @param tenancyRef
 * @return
 */
public String queryHouseholdPerson(String tenancyRef) {
    ApplicationBean applicationBean = new ApplicationBean();

    return applicationBean.queryHouseholdPerson(tenancyRef);
}

/**
 * Batch cancel application
 */
public String batchChangeStatus(String ranos, String status, String raRemark, String adminName) {
    ApplicationBean applicationBean = new ApplicationBean();
    String result = applicationBean.changeStatus(ranos, status, raRemark, adminName);
    return DataUtil.escXml(result);
}

/*** Ballot Bean ***/
/**
 * Count flat amount according to types
 */
public String queryFlatCount(String parameter) {
    BallotBean ballotBean = new BallotBean();
    if (DataUtil.isEmpty(parameter)) {
        return ResultBuilder.buildResult("Parameter can not be empty!").setErrorCode(Result.ERROR_CODE_INVALID)
                .toString();
    }
    String result = ballotBean.countFlat(parameter);
    return result;
}

/**
 * Count application amount according to categories
 */
public String queryAppCount(String parameter) {
    BallotBean ballotBean = new BallotBean();
    String result = ballotBean.countApplication(parameter);
    return result;
}

/**
 * Query flat list
 * 
 * @param parameter Query condition
 */
public String queryFlatListForBallotByPage(String parameter) {
    BallotBean ballotBean = new BallotBean();

    if (DataUtil.CheckCateria(parameter)) {
        return ResultBuilder.buildResult("invalid parameter").setErrorCode(Result.ERROR_CODE_INVALID).toString();
    }
    if (DataUtil.isEmpty(parameter)) {
        return ResultBuilder.buildResult("parameter can not be empty").setErrorCode(Result.ERROR_CODE_INVALID)
                .toString();
    }
    JsonConfig jsonConfig = new JsonConfig();
    jsonConfig.setNewBeanInstanceStrategy(JsonConfig.DEFAULT_NEW_BEAN_INSTANCE_STRATEGY);
    JSONObject beanJSON = JSONObject.fromObject(parameter);
    BallotFlatQueryObj queryObj = (BallotFlatQueryObj) JSONObject.toBean(beanJSON, BallotFlatQueryObj.class);
    String result = ballotBean.queryFlatListForBallotByPage(queryObj);
    return result;
}

/**
 * Check if its outstanding ballot
 * 
 * @param prjcode
 * @return
 */
public String outstandingBallotCheck(String prjcode) {
    BallotBean ballotBean = new BallotBean();
    if (DataUtil.isEmpty(prjcode)) {
        return ResultBuilder.buildResult("project can not be empty").setErrorCode(Result.ERROR_CODE_INVALID)
                .toString();
    }
    return ballotBean.checkOutStanding(prjcode);
}

/**
 * Export application and flat files for ballot.
 * 
 * @param parameter1 Application categories
 * @param parameter2 Flat categories
 * @param adminName  Administrator name
 * @return
 */
public String ballotExport(String parameter1, String parameter2, String adminName) {
    BallotBean ballotBean = new BallotBean();
    JSONObject beanJSON = JSONObject.fromObject(parameter1);
    JSONObject beanJSON2 = JSONObject.fromObject(parameter2);
    BallotFlatQueryObj queryObj = (BallotFlatQueryObj) JSONObject.toBean(beanJSON, BallotFlatQueryObj.class);
    BallotFlatQueryObj queryObj2 = (BallotFlatQueryObj) JSONObject.toBean(beanJSON2, BallotFlatQueryObj.class);
    return ballotBean.ballotExport(queryObj, queryObj2, adminName);
}

/**
 * Upload files for ballot,ballot and generate match result.If any applications
 * or flats weren't matched, Unmatched logs will be generate for download.
 * 
 * @param ballotImport:A          json string of BallotImportClass
 * @param appF:Application        file
 * @param flatF:Flat              file
 * @param adminName:Administrator name
 * @return
 */
public String ballotImport(String ballotImport, byte[] appF, byte[] flatF, String adminName) {
    BallotBean ballotBean = new BallotBean();
    JSONObject beanJSON = JSONObject.fromObject(ballotImport);
    BallotImportClass ballot = (BallotImportClass) JSONObject.toBean(beanJSON, BallotImportClass.class);
    if (ballot == null || DataUtil.isEmpty(ballot.getAppFileName())) {
        return ResultBuilder.buildResult("Query parameter musn't be empty").toString();
    }
    if (DataUtil.isEmpty(ballot.getBalloteDate())) {
        return ResultBuilder.buildResult("Query parameter musn't be empty").toString();
    }
    if (DataUtil.isEmpty(ballot.getFlatFileName())) {
        return ResultBuilder.buildResult("Query parameter musn't be empty").toString();
    }
    if (DataUtil.isEmpty(ballot.getProjectCode())) {
        return ResultBuilder.buildResult("Query parameter musn't be empty").toString();
    }
    if (DataUtil.isEmpty(ballot.getSeedNo())) {
        return ResultBuilder.buildResult("Query parameter musn't be empty").toString();
    }
    return ballotBean.ballotImport(ballot, appF, flatF, adminName);
}

/**
 * Query ballot list
 */
public String queryBallotList(String prjCode) {
    BallotBean bean = new BallotBean();
    String result = bean.queryBallotList(prjCode);
    return result;
}

/*** Offer Bean ***/
/**
 * Accept offer(s)
 * 
 * @param ranos
 * @param adminName
 * @return
 */
public String offerAccept(String ranos, int index, String acDate, String raRemark, String adminName) {
    OfferBean offerBean = new OfferBean();
    if (DataUtil.isEmpty(ranos) || acDate == null) {
        return ResultBuilder.buildResult("Ranos and Accept date can not be empty").toString();
    }
    String[] arr = ranos.split(",");
    return offerBean.acceptOffer(arr, index, acDate, raRemark, adminName);
}

/**
 * Reject offer(s) If one offer is rejected by three times,its status will be
 * set 'REJECT_OFFER',else its status will be set 'READY_OFFER'.
 * 
 * If
 * 
 * @param ranos
 * @param adminName
 * @return
 */
public String offerReject(String ranos, int index, String rjCode, String rjDes, String rjDate, String raRemark,
        String adminName) {
    OfferBean offerBean = new OfferBean();
    if (DataUtil.isEmpty(ranos) || rjDate == null) {
        return ResultBuilder.buildResult("Ranos and Reject date can not be empty").toString();
    }
    String[] arr = ranos.split(",");

    return offerBean.rejectOffer(arr, index, rjCode, rjDes, rjDate, raRemark, adminName);
}

/**
 * Query offer list
 * 
 * @param rbno
 * @param pageIndex
 * @param pageSize
 * @return
 */
public String queryOfferList(String prjCode, String rbno) {
    OfferBean bean = new OfferBean();
    if (DataUtil.isEmpty(rbno) || !rbno.matches("\\d+")) {
        return ResultBuilder.buildResult("invalid rbno").toString();
    }
    int rbNoInt = Integer.parseInt(rbno);
    return bean.queryOfferList(prjCode, rbNoInt);
}

/**
 * Get flat allocation report
 */
public String getFlatAllocationReport(String reportId, String prjCode, String rbNo) {
    OfferBean bean = new OfferBean();
    return bean.generateFlatAllocationReport(reportId, prjCode, rbNo);
}

/**
 * Get flat detail by propNo
 */
public String getFlatDetail(String prjCode, String propNo) {
    OfferBean bean = new OfferBean();
    String result = bean.getFlatDetail(prjCode, propNo);
    return result;
}

/**
 * Direct assign offer
 */
public String directAssign(String raNo, String propNo, String remark, String justification, String authorizedBy,
        String approvalDate, String admin) {
    OfferBean bean = new OfferBean();
    String result = bean.directOffer(raNo, propNo, remark, justification, authorizedBy, approvalDate, admin);
    return result;
}

public String generateReport(String prjCode, String rbno, String fileName) {
    ReportForCrystalBean rptBean = new ReportForCrystalBean();
    return rptBean.generateReport(prjCode, rbno, fileName).toString();
}

public String checkIfReportExists(String fileName) {
    ReportForCrystalBean rptBean = new ReportForCrystalBean();
    return rptBean.checkReportGenerated(fileName).toString();
}
    
}
