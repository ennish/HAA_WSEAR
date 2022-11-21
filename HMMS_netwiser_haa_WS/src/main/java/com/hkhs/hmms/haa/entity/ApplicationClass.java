package com.hkhs.hmms.haa.entity;

import net.sf.json.JSONObject;

public class ApplicationClass {
	public static final String YES  = "Y";
	public static final String NO  = "N";
	
	public enum CGLS_FLAG{
		//both flat size will be saved
		Y,
		//NOT APPLIABLE
		S,
		//
		N
	};
	
	private String raNO;
	private String raPRJCode;
	private String raEstateCode;
	private String raStatus;
	private String raTenancyRef;
	private String raOfferLetterDate;
	private String raName;
	private String raPropRef;
	private String raPropEngAddress;
	private String raPropChnAddress;
	private String raPropRef2;
	private String raPropEngAddress2;
	private String raPropChnAddress2;
	private String raCotDate;
	private String raCglsFlag;
	private String raNotApplicable;
	private int raExpectBaby;
	private int raTempStay;
	private int raTotalPerson;
	private String raReceiveDate;
	private String raCreateBy;
	private String raUpdateDate;
	private String raUpdateBy;
	private String raApproveBy;
	private String raApproveDate;
	private String raRemark;
	
	private CGLSFlatClass cglsFlat1 ;
	private CGLSFlatClass cglsFlat2 ;
	private ApplicationCategoryClass[] categories ;
	private ApplicationPersonClass[] persons;
	
	public String getRaNO() {
		return raNO;
	}
	public void setRaNO(String raNO) {
		this.raNO = raNO;
	}
	public String getRaPRJCode() {
		return raPRJCode;
	}
	public void setRaPRJCode(String raPRJCode) {
		this.raPRJCode = raPRJCode;
	}
	public String getRaStatus() {
		return raStatus;
	}
	
	public String getRaEstateCode() {
		return raEstateCode;
	}
	public void setRaEstateCode(String raEstateCode) {
		this.raEstateCode = raEstateCode;
	}
	public void setRaStatus(String raStatus) {
		this.raStatus = raStatus;
	}
	public String getRaTenancyRef() {
		return raTenancyRef;
	}
	public void setRaTenancyRef(String raTenancyRef) {
		this.raTenancyRef = raTenancyRef;
	}
	public String getRaName() {
		return raName;
	}
	public void setRaName(String raName) {
		this.raName = raName;
	}
	public String getRaPropRef() {
		return raPropRef;
	}
	public void setRaPropRef(String raPropRef) {
		this.raPropRef = raPropRef;
	}
	public String getRaPropEngAddress() {
		return raPropEngAddress;
	}
	public void setRaPropEngAddress(String raPropEngAddress) {
		this.raPropEngAddress = raPropEngAddress;
	}
	public String getRaPropChnAddress() {
		return raPropChnAddress;
	}
	public void setRaPropChnAddress(String raPropChnAddress) {
		this.raPropChnAddress = raPropChnAddress;
	}
	public String getRaPropEngAddress2() {
		return raPropEngAddress2;
	}
	public void setRaPropEngAddress2(String raPropEngAddress2) {
		this.raPropEngAddress2 = raPropEngAddress2;
	}
	public String getRaPropChnAddress2() {
		return raPropChnAddress2;
	}
	public void setRaPropChnAddress2(String raPropChnAddress2) {
		this.raPropChnAddress2 = raPropChnAddress2;
	}
	public String getRaCotDate() {
		return raCotDate;
	}
	public void setRaCotDate(String raCotDate) {
		this.raCotDate = raCotDate;
	}
	public int getRaExpectBaby() {
		return raExpectBaby;
	}
	public void setRaExpectBaby(int raExpectBaby) {
		this.raExpectBaby = raExpectBaby;
	}
	public int getRaTempStay() {
		return raTempStay;
	}
	public void setRaTempStay(int raTempStay) {
		this.raTempStay = raTempStay;
	}
	public int getRaTotalPerson() {
		return raTotalPerson;
	}
	public void setRaTotalPerson(int raTotalPerson) {
		this.raTotalPerson = raTotalPerson;
	}
	public String getRaReceiveDate() {
		return raReceiveDate;
	}
	public void setRaReceiveDate(String raReceiveDate) {
		this.raReceiveDate = raReceiveDate;
	}
	public String getRaCreateBy() {
		return raCreateBy;
	}
	public void setRaCreateBy(String raCreateBy) {
		this.raCreateBy = raCreateBy;
	}
	public String getRaUpdateDate() {
		return raUpdateDate;
	}
	public void setRaUpdateDate(String raUpdateDate) {
		this.raUpdateDate = raUpdateDate;
	}
	public String getRaUpdateBy() {
		return raUpdateBy;
	}
	public void setRaUpdateBy(String raUpdateBy) {
		this.raUpdateBy = raUpdateBy;
	}
	public String getRaApproveBy() {
		return raApproveBy;
	}
	public void setRaApproveBy(String raApproveBy) {
		this.raApproveBy = raApproveBy;
	}
	public String getRaApproveDate() {
		return raApproveDate;
	}
	public void setRaApproveDate(String raApproveDate) {
		this.raApproveDate = raApproveDate;
	}
	public String getRaCglsFlag() {
		return raCglsFlag;
	}
	public void setRaCglsFlag(String raCglsFlag) {
		this.raCglsFlag = raCglsFlag;
	}
	public String getRaPropRef2() {
		return raPropRef2;
	}
	public void setRaPropRef2(String raPropRef2) {
		this.raPropRef2 = raPropRef2;
	}
	
	public String getRaOfferLetterDate() {
		return raOfferLetterDate;
	}
	public void setRaOfferLetterDate(String raOfferLetterDate) {
		this.raOfferLetterDate = raOfferLetterDate;
	}
	public CGLSFlatClass getCglsFlat1() {
		return cglsFlat1;
	}
	public void setCglsFlat1(CGLSFlatClass cglsFlat1) {
		this.cglsFlat1 = cglsFlat1;
	}
	public CGLSFlatClass getCglsFlat2() {
		return cglsFlat2;
	}
	public void setCglsFlat2(CGLSFlatClass cglsFlat2) {
		this.cglsFlat2 = cglsFlat2;
	}
	 
	public ApplicationCategoryClass[] getCategories() {
		return categories;
	}
	public void setCategories(ApplicationCategoryClass[] categories) {
		this.categories = categories;
	}
	public ApplicationPersonClass[] getPersons() {
		return persons;
	}
	public void setPersons(ApplicationPersonClass[] persons) {
		this.persons = persons;
	}
	
	public String getRaNotApplicable() {
		return raNotApplicable;
	}
	public void setRaNotApplicable(String raNotApplicable) {
		this.raNotApplicable = raNotApplicable;
	}
	
	public String getRaRemark() {
		return raRemark;
	}
	public void setRaRemark(String raRemark) {
		this.raRemark = raRemark;
	}
	@Override
	public String toString() {
		return  JSONObject.fromObject(this).toString();
	}
	
	public static void main(String args[]) {
		
		ApplicationClass app = new ApplicationClass();
		 
		System.out.println(app.toString());
	}
}
