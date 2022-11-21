package com.hkhs.hmms.haa.entity;

import net.sf.json.JSONObject;

public class FlatInfo {
	 
	private int minAlloc;
	private int maxAlloc;
	private String flatSize;
	private Double ifa;

	public int getMinAlloc() {
		return minAlloc;
	}

	public void setMinAlloc(int minAlloc) {
		this.minAlloc = minAlloc;
	}

	public int getMaxAlloc() {
		return maxAlloc;
	}

	public void setMaxAlloc(int maxAlloc) {
		this.maxAlloc = maxAlloc;
	}

	public String getFlatSize() {
		return flatSize;
	}

	public void setFlatSize(String flatSize) {
		this.flatSize = flatSize;
	}
 
	public Double getIfa() {
		return ifa;
	}

	public void setIfa(Double ifa) {
		this.ifa = ifa;
	}

	@Override
	public String toString() {
		return JSONObject.fromObject(this).toString();
	}
}
