package com.hkhs.hmms.haa.queryEntity;

import net.sf.json.JSONObject;

public class BallotImportClass {

	private String projectCode;
	private String balloteDate;
	private String appFileName;
	private String flatFileName;
	private String seedNo;
	private String batchRemark;

	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	public String getBalloteDate() {
		return balloteDate;
	}

	public void setBalloteDate(String balloteDate) {
		this.balloteDate = balloteDate;
	}

	public String getAppFileName() {
		return appFileName;
	}

	public void setAppFileName(String appFileName) {
		this.appFileName = appFileName;
	}

	public String getFlatFileName() {
		return flatFileName;
	}

	public void setFlatFileName(String flatFileName) {
		this.flatFileName = flatFileName;
	}

	public String getSeedNo() {
		return seedNo;
	}

	public void setSeedNo(String seedNo) {
		this.seedNo = seedNo;
	}

	public String getBatchRemark() {
		return batchRemark;
	}

	public void setBatchRemark(String batchRemark) {
		this.batchRemark = batchRemark;
	}

	@Override
	public String toString() {
		return JSONObject.fromObject(this).toString();
	}

	public static void main(String args[]) {

		BallotImportClass ballot = new BallotImportClass();

		System.out.println(ballot.toString());
	}

}
