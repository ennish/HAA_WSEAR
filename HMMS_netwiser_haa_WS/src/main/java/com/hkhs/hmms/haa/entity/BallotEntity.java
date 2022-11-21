package com.hkhs.hmms.haa.entity;

import net.sf.json.JSONObject;

public class BallotEntity {

	private String rbNo;
	private String seedNo;
	private String ballotDate;
	public String getRbNo() {
		return rbNo;
	}
	public void setRbNo(String rbNo) {
		this.rbNo = rbNo;
	}
	public String getSeedNo() {
		return seedNo;
	}
	public void setSeedNo(String seedNo) {
		this.seedNo = seedNo;
	}
	public String getBallotDate() {
		return ballotDate;
	}
	public void setBallotDate(String ballotDate) {
		this.ballotDate = ballotDate;
	}

	@Override
	public String toString() {
		return JSONObject.fromObject(this).toString();
	}
}
