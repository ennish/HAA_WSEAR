package com.hkhs.hmms.haa.entity;

import net.sf.json.JSONObject;

public class BallotImportResult {
	private String ballotCode;
	private String seedNo;
	private String projectCode;
	private String ballotDate;
	private String errorLog;
	private String unMatchAppFile;
	private String unMatchFlatFile;
	private String message;

	public String getBallotCode() {
		return ballotCode;
	}

	public void setBallotCode(String ballotCode) {
		this.ballotCode = ballotCode;
	}

	public String getSeedNo() {
		return seedNo;
	}

	public void setSeedNo(String seedNo) {
		this.seedNo = seedNo;
	}

	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	public String getErrorLog() {
		return errorLog;
	}

	public void setErrorLog(String errorLog) {
		this.errorLog = errorLog;
	}

	public String getBallotDate() {
		return ballotDate;
	}

	public void setBallotDate(String ballotDate) {
		this.ballotDate = ballotDate;
	}

	public String getUnMatchAppFile() {
		return unMatchAppFile;
	}

	public void setUnMatchAppFile(String unMatchAppFile) {
		this.unMatchAppFile = unMatchAppFile;
	}

	public String getUnMatchFlatFile() {
		return unMatchFlatFile;
	}

	public void setUnMatchFlatFile(String unMatchFlatFile) {
		this.unMatchFlatFile = unMatchFlatFile;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return JSONObject.fromObject(this).toString();
	}

}
