package com.hkhs.hmms.haa.entity;

import net.sf.json.JSONObject;

public class Result {
	/**
	 * success
	 */
	public static final int ERROR_CODE_SUCCESS = 1200;
	
	public static final int ERROR_CODE_WARN = 1201;

	/**
	 * service fail
	 */
	public static final int ERROR_CODE_FAIL = 1500;

	/**
	 * invalid request parameter
	 */
	public static final int ERROR_CODE_INVALID = 1400;

	/**
	 * resource request not found
	 */
	public static final int ERROR_CODE_NOT_EXIST = 1404;

	/**
	 * request resource was forbidden
	 */
	public static final int ERROR_CODE_FORBIDDEN = 1405;

	private int errorCode;

	private String msg;

	// for page divide
	private int total;

	private Object result = new Object();

	public int getErrorCode() {
		return errorCode;
	}

	public Result setErrorCode(int errorCode) {
		this.errorCode = errorCode;
		return this;
	}

	public Object getResult() {
		return result;
	}

	public Result setResult(Object result) {
		this.result = result;
		return this;
	}

	public String getMsg() {
		return msg;
	}

	public Result setMsg(String msg) {
		this.msg = msg;
		return this;
	}

	public int getTotal() {
		return total;
	}

	public Result setTotal(int total) {
		this.total = total;
		return this;
	}

	@Override
	public String toString() {
		return JSONObject.fromObject(this).toString();
	}
}
