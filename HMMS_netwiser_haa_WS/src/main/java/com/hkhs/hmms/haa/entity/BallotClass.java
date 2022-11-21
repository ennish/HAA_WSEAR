package com.hkhs.hmms.haa.entity;

import net.sf.json.JSONObject;

public class BallotClass {
	private String rbNO;
	private String prjCode;
	private String inputRemark;
	private String inputAppFile;
	private String inputDate;
	private String inputBy;
	private String uploadRemark;
	private String uploadDate;
	private String uploadBy;
//	private String uploadFlatFile;
//	private String uploadAppFile;
	private String seedNO;
	private String ballotDate;
	
	public String getRbNO() {
		return rbNO;
	}

	public void setRbNO(String rbNO) {
		this.rbNO = rbNO;
	}

	public String getPrjCode() {
		return prjCode;
	}

	public void setPrjCode(String prjCode) {
		this.prjCode = prjCode;
	}

	public String getInputRemark() {
		return inputRemark;
	}

	public void setInputRemark(String inputRemark) {
		this.inputRemark = inputRemark;
	}

	public String getInputAppFile() {
		return inputAppFile;
	}

	public void setInputAppFile(String inputAppFile) {
		this.inputAppFile = inputAppFile;
	}

	public String getInputDate() {
		return inputDate;
	}

	public void setInputDate(String inputDate) {
		this.inputDate = inputDate;
	}

	public String getInputBy() {
		return inputBy;
	}

	public void setInputBy(String inputBy) {
		this.inputBy = inputBy;
	}

	public String getUploadBy() {
		return uploadBy;
	}

	public void setUploadBy(String uploadBy) {
		this.uploadBy = uploadBy;
	}

	public String getUploadRemark() {
		return uploadRemark;
	}

	public void setUploadRemark(String uploadRemark) {
		this.uploadRemark = uploadRemark;
	}

	public String getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(String uploadDate) {
		this.uploadDate = uploadDate;
	}

	public String getSeedNO() {
		return seedNO;
	}

	public void setSeedNO(String seedNO) {
		this.seedNO = seedNO;
	}

	public String getBallotDate() {
		return ballotDate;
	}

	public void setBallotDate(String ballotDate) {
		this.ballotDate = ballotDate;
	}

	@Override
	public String toString() {
		return  JSONObject.fromObject(this).toString();
	}
	
	public static void main(String args[]) {
		
		BallotClass c = new BallotClass();
		System.out.println(c.toString());
	}
}
