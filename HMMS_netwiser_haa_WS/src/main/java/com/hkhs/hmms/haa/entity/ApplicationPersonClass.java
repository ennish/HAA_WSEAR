package com.hkhs.hmms.haa.entity;

import net.sf.json.JSONObject;

public class ApplicationPersonClass {
	/**
	 * The Relationship between tenant and person
	 *
	 */
	public enum RP_RELATIONSHIP{
		TENANT("TENANT"),WIFE("WIFE"),SON("SON");
		
		private String value; 
		RP_RELATIONSHIP(String value){
			this.value = value;
		}
		
		public boolean equals(String str) {
			if(str==null) {
				return false;
			}
			return this.value.toUpperCase().equals(str.toUpperCase());
		}
	}
	private String raNO;
	private String rpSeqNo;
	private String rpEngName;
	private String rpChnName;
	private String rpRelationship;
	private String rpCreateBy;
	private String rpCreateDate;
	private String rpContact;
	private String rpUPdateDate;
	private String rpUpdateBy;
	 
	public String getRaNO() {
		return raNO;
	}
	public void setRaNO(String raNO) {
		this.raNO = raNO;
	}
	public String getRpSeqNo() {
		return rpSeqNo;
	}
	public void setRpSeqNo(String rpSeqNo) {
		this.rpSeqNo = rpSeqNo;
	}
	public String getRpEngName() {
		return rpEngName;
	}
	public void setRpEngName(String rpEngName) {
		this.rpEngName = rpEngName;
	}
	 
	public String getRpChnName() {
		return rpChnName;
	}
	public void setRpChnName(String rpChnName) {
		this.rpChnName = rpChnName;
	}
	public String getRpRelationship() {
		return rpRelationship;
	}
	public void setRpRelationship(String rpRelationship) {
		this.rpRelationship = rpRelationship;
	}
	public String getRpCreateBy() {
		return rpCreateBy;
	}
	public void setRpCreateBy(String rpCreateBy) {
		this.rpCreateBy = rpCreateBy;
	}
	public String getRpCreateDate() {
		return rpCreateDate;
	}
	public void setRpCreateDate(String rpCreateDate) {
		this.rpCreateDate = rpCreateDate;
	}
	public String getRpContact() {
		return rpContact;
	}
	public void setRpContact(String rpContact) {
		this.rpContact = rpContact;
	}
	public String getRpUPdateDate() {
		return rpUPdateDate;
	}
	public void setRpUPdateDate(String rpUPdateDate) {
		this.rpUPdateDate = rpUPdateDate;
	}
	public String getRpUpdateBy() {
		return rpUpdateBy;
	}
	public void setRpUpdateBy(String rpUpdateBy) {
		this.rpUpdateBy = rpUpdateBy;
	}
	
	@Override
	public String toString() {
		return  JSONObject.fromObject(this).toString();
	}
	
	public static void main(String args[]) {
		
		ApplicationPersonClass app = new ApplicationPersonClass();
		 
		System.out.println(app.toString());
	}
	
}
