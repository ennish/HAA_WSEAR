package com.hkhs.hmms.haa.queryEntity;

import net.sf.json.JSONObject;

public class BallotFlatQueryObj {
	
	private String project;
	private String propRef;
	private String[] categories;
	private String type;
	private String status;
	
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public String getPropRef() {
		return propRef;
	}
	public void setPropRef(String propRef) {
		this.propRef = propRef;
	} 
	
	public String[] getCategories() {
		return categories;
	}
	public void setCategories(String[] categories) {
		this.categories = categories;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return  JSONObject.fromObject(this).toString();
	}
	public static void main(String[] args) {
		System.out.println(new BallotFlatQueryObj());
		
	}
}
