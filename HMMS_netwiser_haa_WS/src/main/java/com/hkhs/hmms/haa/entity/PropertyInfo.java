package com.hkhs.hmms.haa.entity;

import net.sf.json.JSONObject;

public class PropertyInfo {
 
	private String tenancyRef;
	private String propRef;
	private String propRef2;
	private String cotDate;
	private String propEngAddr1;
	private String propChnAddr1;
	private String propEngAddr2;
	private String propChnAddr2;
	private String cglsFlag;
	public String getTenancyRef() {
		return tenancyRef;
	}
	public void setTenancyRef(String tenancyRef) {
		this.tenancyRef = tenancyRef;
	}
	public String getPropRef() {
		return propRef;
	}
	public void setPropRef(String propRef) {
		this.propRef = propRef;
	}
	public String getPropRef2() {
		return propRef2;
	}
	public void setPropRef2(String propRef2) {
		this.propRef2 = propRef2;
	}
	public String getCotDate() {
		return cotDate;
	}
	public void setCotDate(String cotDate) {
		this.cotDate = cotDate;
	}
	public String getPropEngAddr1() {
		return propEngAddr1;
	}
	public void setPropEngAddr1(String propEngAddr1) {
		this.propEngAddr1 = propEngAddr1;
	}
	public String getPropChnAddr1() {
		return propChnAddr1;
	}
	public void setPropChnAddr1(String propChnAddr1) {
		this.propChnAddr1 = propChnAddr1;
	}
	public String getPropEngAddr2() {
		return propEngAddr2;
	}
	public void setPropEngAddr2(String propEngAddr2) {
		this.propEngAddr2 = propEngAddr2;
	}
	public String getPropChnAddr2() {
		return propChnAddr2;
	}
	public void setPropChnAddr2(String propChnAddr2) {
		this.propChnAddr2 = propChnAddr2;
	}
	public String getCglsFlag() {
		return cglsFlag;
	}
	public void setCglsFlag(String cglsFlag) {
		this.cglsFlag = cglsFlag;
	}
	@Override
	public String toString() {
		return JSONObject.fromObject(this).toString();
	}
	
	
	
}
