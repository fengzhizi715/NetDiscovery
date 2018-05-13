package com.cv4j.netdiscovery.admin.dto;

import com.cv4j.netdiscovery.admin.config.Constant;

import java.util.HashMap;

/**
 * 返回结果对象
 */
public class ResultMap extends HashMap<String, Object>{

	private static final long serialVersionUID = 1333253070110384732L;
	
	private ResultMap() { }

	/**
	 * 返回成功
	 */
	public static ResultMap ok() {
		return ok("操作成功！");
	}

	public static ResultMap addSuccess() {
		return ok("新增成功！");
	}

	public static ResultMap updateSuccess() {
		return ok("更新成功！");
	}

	public static ResultMap deleteSuccess() {
		return ok("删除成功！");
	}


	/**
	 * 返回成功
	 */
	public static ResultMap ok(String message) {
		return ok(Constant.STATUS_SUCCESS, message);
	}
	
	/**
	 * 返回成功
	 */
	public static ResultMap ok(int code, String message) {
		ResultMap resultMap = new ResultMap();
		resultMap.put("code", code);
		resultMap.put("msg", message);
		return resultMap;
	}
	
	/**
	 * 返回失败
	 */
	public static ResultMap error() {
		return error("操作失败！");
	}

	public static ResultMap addFailure() {
		return ok("新增失败！");
	}

	public static ResultMap updateFailure() {
		return ok("更新失败！");
	}

	public static ResultMap deleteFailure() {
		return ok("删除成失败！");
	}
	
	/**
	 * 返回失败
	 */
	public static ResultMap error(String messag) {
		return error(Constant.STATUS_ERROR, messag);
	}

	/**
	 * 返回失败
	 */
	public static ResultMap error(int code, String message) {
		ResultMap resultMap = new ResultMap();
		resultMap.put("code", code);
		resultMap.put("msg", message);
		return resultMap;
	}
	
	/**
	 * 设置code
	 */
	public ResultMap setCode(int code){
		super.put("code", code);
		return this;
	}
	
	/**
	 * 设置message
	 */
	public ResultMap setMessage(String message){
		super.put("msg", message);
		return this;
	}
	
	/**
	 * 放入object
	 */
	@Override
	public ResultMap put(String key, Object object){
		super.put(key, object);
		return this;
	}
}