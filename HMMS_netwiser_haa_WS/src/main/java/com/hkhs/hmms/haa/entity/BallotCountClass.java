package com.hkhs.hmms.haa.entity;

import net.sf.json.JSONObject;

public class BallotCountClass {

	private String name;
	private String description;
	private int num;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return JSONObject.fromObject(this).toString();
	}
}
