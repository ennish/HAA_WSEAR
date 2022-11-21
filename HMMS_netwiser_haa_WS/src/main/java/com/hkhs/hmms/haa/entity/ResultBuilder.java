package com.hkhs.hmms.haa.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.hkhs.hmms.haa.util.DataUtil;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

/**
 * @author tw
 */
public class ResultBuilder {

	public static Result buildResult() {
		Result result = new Result();
		return result;
	}

	public static Result buildResult(Result result) {
		return result;
	}

	public static Result buildResult(String msg) {
		Result result = new Result();
		result.setMsg(msg);
		return result;
	}

	public static Result buildSuccessResult() {
		Result result = new Result();
		result.setErrorCode(Result.ERROR_CODE_SUCCESS);
		return result;
	}

	public static Result buildSuccessResult(Object data) {
		Result result = new Result();
		result.setErrorCode(Result.ERROR_CODE_SUCCESS).setResult(data);
		return result;
	}

	public static Result buildSuccessResult(Object data, int total) {
		Result result = new Result();
		result.setErrorCode(Result.ERROR_CODE_SUCCESS).setResult(data).setTotal(total);
		return result;
	}

	public static Result buildServerFailResult(String msg) {
		Result result = new Result();
		result.setErrorCode(Result.ERROR_CODE_FAIL).setMsg(msg);
		return result;
	}

	public static Result buildResult(int errorCode, String message) {
		Result result = new Result();
		result.setErrorCode(errorCode).setMsg(message);
		return result;
	}
}
