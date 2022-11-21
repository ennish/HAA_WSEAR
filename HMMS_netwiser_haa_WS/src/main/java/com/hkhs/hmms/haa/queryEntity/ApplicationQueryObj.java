package com.hkhs.hmms.haa.queryEntity;

import net.sf.json.JSONObject;

public class ApplicationQueryObj {
	
	private String category;
	private String propertyRef;
	private String prjCode;
	private String caseNo;
	private String raName;
	private String offerLetterReplyDate;
	private String tenancyRef;
	private String status;
	 
	public String getPrjCode() {
		return prjCode;
	}
	public void setPrjCode(String prjCode) {
		this.prjCode = prjCode;
	}
	public String getCaseNo() {
		return caseNo;
	}
	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}
	public String getRaName() {
		return raName;
	}
	public void setRaName(String raName) {
		this.raName = raName;
	}
	public String getOfferLetterReplyDate() {
		return offerLetterReplyDate;
	}
	public void setOfferLetterReplyDate(String offerLetterReplyDate) {
		this.offerLetterReplyDate = offerLetterReplyDate;
	}
	public String getTenancyRef() {
		return tenancyRef;
	}
	public void setTenancyRef(String tenancyRef) {
		this.tenancyRef = tenancyRef;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getPropertyRef() {
		return propertyRef;
	}
	public void setPropertyRef(String propertyRef) {
		this.propertyRef = propertyRef;
	}
	@Override
	public String toString() {
		return  JSONObject.fromObject(this).toString();
	}
	
	public static void main(String args[]) {
		
		ApplicationQueryObj app = new ApplicationQueryObj();
		 
		System.out.println(app.toString());
	}
	
}
