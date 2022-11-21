package com.hkhs.hmms.haa.entity;

import net.sf.json.JSONObject;

public class OfferClass {
	/**
	 * Offer Action
	 */
	public static enum OFFER_ACTION {
		ACCEPT,REJECT;
	}
	
	
	private String roNo;
	
	private int count;
	private String raNo;
	private String rbNo;
	private String rmRalNo;
	/**
	 * 'Y' OR 'N' if 'Y',justification must be given
	 */
	private String directOffer;
	private String propRef;
	/**
	 * Same as Application Status Refer to HST_HAA_REDEV_APPLICATION
	 */
	private String status;
	private String acceptDate;
	private String rejectDate;
	private String createBy;
	private String createDate;
	private String updateBy;
	private String updateDate;
	private String justification;
	private String roRemark;

	public String getRoNo() {
		return roNo;
	}

	public void setRoNo(String roNo) {
		this.roNo = roNo;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getRaNo() {
		return raNo;
	}

	public void setRaNo(String raNo) {
		this.raNo = raNo;
	}

	public String getRmRalNo() {
		return rmRalNo;
	}

	public void setRmRalNo(String rmRalNo) {
		this.rmRalNo = rmRalNo;
	}

	public String getDirectOffer() {
		return directOffer;
	}

	public void setDirectOffer(String directOffer) {
		this.directOffer = directOffer;
	}

	public String getPropRef() {
		return propRef;
	}

	public void setPropRef(String propRef) {
		this.propRef = propRef;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAcceptDate() {
		return acceptDate;
	}

	public void setAcceptDate(String acceptDate) {
		this.acceptDate = acceptDate;
	}

	public String getRejectDate() {
		return rejectDate;
	}

	public void setRejectDate(String rejectDate) {
		this.rejectDate = rejectDate;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public String getJustification() {
		return justification;
	}

	public void setJustification(String justification) {
		this.justification = justification;
	}

	public String getRoRemark() {
		return roRemark;
	}

	public void setRoRemark(String roRemark) {
		this.roRemark = roRemark;
	}

	@Override
	public String toString() {
		return JSONObject.fromObject(this).toString();
	}
}
