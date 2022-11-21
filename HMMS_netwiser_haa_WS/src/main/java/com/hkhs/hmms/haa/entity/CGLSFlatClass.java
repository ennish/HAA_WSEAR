package com.hkhs.hmms.haa.entity;

import net.sf.json.JSONObject;

public class CGLSFlatClass {
	
	private String raNo;;
	private int rfSeqNo;
	private String rfFlatSize;
	private String rfOtherReq;
	private Integer rfTotalPerson;
	private String rfCreateBy;
	private String rfCreateDate;
	private String rfUpdateBy;
	private String rfUpdateDate;
	private Integer rfFlatPersons;
	
	public String getRaNo() {
		return raNo;
	}
	public void setRaNo(String raNo) {
		this.raNo = raNo;
	}
	public int getRfSeqNo() {
		return rfSeqNo;
	}
	public void setRfSeqNo(int rfSeqNo) {
		this.rfSeqNo = rfSeqNo;
	}
	public String getRfFlatSize() {
		return rfFlatSize;
	}
	public void setRfFlatSize(String rfFlatSize) {
		this.rfFlatSize = rfFlatSize;
	}
	public String getRfOtherReq() {
		return rfOtherReq;
	}
	public void setRfOtherReq(String rfOtherReq) {
		this.rfOtherReq = rfOtherReq;
	}
	public Integer getRfTotalPerson() {
		return rfTotalPerson;
	}
	public void setRfTotalPerson(Integer rfTotalPerson) {
		this.rfTotalPerson = rfTotalPerson;
	}
	public String getRfCreateBy() {
		return rfCreateBy;
	}
	public void setRfCreateBy(String rfCreateBy) {
		this.rfCreateBy = rfCreateBy;
	}
	public String getRfCreateDate() {
		return rfCreateDate;
	}
	public void setRfCreateDate(String rfCreateDate) {
		this.rfCreateDate = rfCreateDate;
	}
	public String getRfUpdateBy() {
		return rfUpdateBy;
	}
	public void setRfUpdateBy(String rfUpdateBy) {
		this.rfUpdateBy = rfUpdateBy;
	}
	public String getRfUpdateDate() {
		return rfUpdateDate;
	}
	public void setRfUpdateDate(String rfUpdateDate) {
		this.rfUpdateDate = rfUpdateDate;
	}
	
	public Integer getRfFlatPersons() {
		return rfFlatPersons;
	}
	public void setRfFlatPersons(Integer rfFlatPersons) {
		this.rfFlatPersons = rfFlatPersons;
	}
	@Override
	public String toString() {
		return  JSONObject.fromObject(this).toString();
	}
	
	public static void main(String args[]) {
		
		CGLSFlatClass app = new CGLSFlatClass();
		
		System.out.println(app.toString());
	}
}
