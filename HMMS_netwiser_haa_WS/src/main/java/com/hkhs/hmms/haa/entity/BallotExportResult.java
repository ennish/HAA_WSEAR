package com.hkhs.hmms.haa.entity;

import net.sf.json.JSONObject;

public class BallotExportResult {
	private String BallotNo;
	private String appFileName;
	private String flatFileName;

	public String getBallotNo() {
		return BallotNo;
	}

	public void setBallotNo(String ballotNo) {
		BallotNo = ballotNo;
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

	@Override
	public String toString() {
		return JSONObject.fromObject(this).toString();
	}

	public static void main(String[] args) {

		BallotExportResult app = new BallotExportResult();

		System.out.println(app.toString());
	}
}
