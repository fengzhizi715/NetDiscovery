package com.cv4j.netdiscovery.admin.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 用于存储查询结果
 */
@Setter
@Getter
public class PageResult<T> {
	
	private int code = 0; //状态码,  0 表示成功  注意！！！
	
	private String msg = "";  //提示信息

	private long count; // 总数量,

	private List<T> data; // 当前页数据

	public PageResult(int code) {
		this.code = code;
	}

	public PageResult(List<T> data, long count) {
		this.data = data;
		this.count = count;
	}

}
