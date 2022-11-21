package com.hkhs.hmms.haa.api;

import javax.jws.WebService;

@WebService
public interface HaaWS {

/*** MasterData Bean ***/
	public String helloWorld();

	public String version();

	public String getApplicationStatusList(String parameter);

	public String queryOfferHistory(String rano);

	public String getFlatStatusList(String parameter);

	public String getRefuseReasonList();

	public String getRedevProjectList(String parameter);

	public String getApplicationCategoryList(String prjcode);

	public String getFlatSizeList(String prjcode);

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
	public String queryApplicationListByPage(String parameter, String propertyRef, int pageIndex, int pageSize);

	/**
	 * Query application
	 * 
	 * @param propertyRef Fuzzy query
	 * @param tenancyRef
	 * @return
	 */
	public String queryApplication(String propertyRef, String tenancyRef, String raNo);

	/**
	 * Generate application
	 * 
	 * @param parameter
	 * @param adminName
	 * @return
	 */
	public String createApplication(String parameter, String adminName);

	/**
	 * Update application
	 * 
	 * @param parameter Json string of ApplicationClass
	 * @return
	 */
	public String updateApplication(String parameter, String adminName);
	/**
	 * Query flat capacity by prjcode and size
	 * 
	 * @param prjcode
	 * @param size
	 * @return
	 */
	public String queryFlatCapacityByPrjCodeAndSize(String prjcode, String size);

	/**
	 * query data for create application
	 */
	public String queryPropertyInfo(String tenancyRef, String propRef);

	/**
	 * Query tenant's household person
	 * 
	 * @param tenancyRef
	 * @return
	 */
	public String queryHouseholdPerson(String tenancyRef);

	/**
	 * Batch cancel application
	 */
	public String batchChangeStatus(String ranos, String status, String raRemark, String adminName);

	/*** Ballot Bean ***/
	/**
	 * Count flat amount according to types
	 */
	public String queryFlatCount(String parameter);

	/**
	 * Count application amount according to categories
	 */
	public String queryAppCount(String parameter);

	/**
	 * Query flat list
	 * 
	 * @param parameter Query condition
	 */
	public String queryFlatListForBallotByPage(String parameter);

	/**
	 * Check if its outstanding ballot
	 * 
	 * @param prjcode
	 * @return
	 */
	public String outstandingBallotCheck(String prjcode);

	/**
	 * Export application and flat files for ballot.
	 * 
	 * @param parameter1 Application categories
	 * @param parameter2 Flat categories
	 * @param adminName  Administrator name
	 * @return
	 */
	public String ballotExport(String parameter1, String parameter2, String adminName);

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
	public String ballotImport(String ballotImport, byte[] appF, byte[] flatF, String adminName);
	/**
	 * Query ballot list
	 */
	public String queryBallotList(String prjCode);

	/*** Offer Bean ***/
	/**
	 * Accept offer(s)
	 * 
	 * @param ranos
	 * @param adminName
	 * @return
	 */
	public String offerAccept(String ranos, int index, String acDate, String raRemark, String adminName);
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
			String adminName);

	/**
	 * Query offer list
	 * 
	 * @param rbno
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public String queryOfferList(String prjCode, String rbno);
	
    /**
	 * Get flat allocation report
	 */
	public String getFlatAllocationReport(String reportId, String prjCode, String rbNo);
	/**
	 * Get flat detail by propNo
	 */
	public String getFlatDetail(String prjCode, String propNo);

	/**
	 * Direct assign offer
	 */
	public String directAssign(String raNo, String propNo, String remark, String justification, String authorizedBy,
			String approvalDate, String admin);

	public String generateReport(String prjCode, String rbno, String fileName);

	public String checkIfReportExists(String fileName);
}