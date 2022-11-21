package com.hkhs.hmms.haa.entity;

import net.sf.json.JSONObject;

public class RedevCategoryClass {

	private String prjCode;
	private String category;
	private String description;
	private String createBy;
	private String createDate;
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
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
	public String getPrjCode() {
		return prjCode;
	}
	public void setPrjCode(String prjCode) {
		this.prjCode = prjCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return  JSONObject.fromObject(this).toString();
	}
	
	public static void main(String args[]) {
		
		RedevCategoryClass app = new RedevCategoryClass();
		 
		System.out.println(app.toString());
	}
}