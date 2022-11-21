package com.hkhs.hmms.haa.entity;

import net.sf.json.JSONObject;

public class FlatClass {
	
	private String flPropRef;
	private String flPrjCode;
	private String flEstateCode;
	private String flBlock;
	private String flFloor;
	private String flUnit;
	private String flIFA;
	private String flType;
	private String flFacing;
	private String flUDF;
	private String flElderlyWing;
	private String flStatus;
	private String flAddress;
	private String flDescription;
	private int flSeq;
	
	public String getFlPropRef() {
		return flPropRef;
	}
	public void setFlPropRef(String flPropRef) {
		this.flPropRef = flPropRef;
	}
	public String getFlPrjCode() {
		return flPrjCode;
	}
	public void setFlPrjCode(String flPrjCode) {
		this.flPrjCode = flPrjCode;
	}
	public String getFlEstateCode() {
		return flEstateCode;
	}
	public void setFlEstateCode(String flEstateCode) {
		this.flEstateCode = flEstateCode;
	}
	public String getFlBlock() {
		return flBlock;
	}
	public void setFlBlock(String flBlock) {
		this.flBlock = flBlock;
	}
	public String getFlFloor() {
		return flFloor;
	}
	public void setFlFloor(String flFloor) {
		this.flFloor = flFloor;
	}
	public String getFlUnit() {
		return flUnit;
	}
	public void setFlUnit(String flUnit) {
		this.flUnit = flUnit;
	}
	public String getFlIFA() {
		return flIFA;
	}
	public void setFlIFA(String flIFA) {
		this.flIFA = flIFA;
	}
	public String getFlType() {
		return flType;
	}
	public void setFlType(String flType) {
		this.flType = flType;
	}
	public String getFlFacing() {
		return flFacing;
	}
	public void setFlFacing(String flFacing) {
		this.flFacing = flFacing;
	}
	public String getFlUDF() {
		return flUDF;
	}
	public void setFlUDF(String flUDF) {
		this.flUDF = flUDF;
	}
	public String getFlElderlyWing() {
		return flElderlyWing;
	}
	public void setFlElderlyWing(String flElderlyWing) {
		this.flElderlyWing = flElderlyWing;
	}
	
	public String getFlStatus() {
		return flStatus;
	}
	public void setFlStatus(String flStatus) {
		this.flStatus = flStatus;
	}

	
	public String getFlAddress() {
		return flAddress;
	}
	
	public void setFlAddress(String flAddress) {
		this.flAddress = flAddress;
	}
	
	public String getFlDescription() {
		return flDescription;
	}
	public void setFlDescription(String flDescription) {
		this.flDescription = flDescription;
	}
	
	public int getFlSeq() {
		return flSeq;
	}
	public void setFlSeq(int flSeq) {
		this.flSeq = flSeq;
	}
	@Override
	public String toString() {
		return  JSONObject.fromObject(this).toString();
	}
	 
}
