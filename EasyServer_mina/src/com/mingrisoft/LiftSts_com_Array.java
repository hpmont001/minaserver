package com.mingrisoft;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;



public class LiftSts_com_Array {
	boolean success;
	int status;
	String msg;
	public  List<LiftSts_com> obj; //新闻列表

	public LiftSts_com_Array() {
	}

	public boolean getSuccess() {
		return success;
	}

	public LiftSts_com_Array(boolean success, int status, String msg, List<LiftSts_com> obj) {
		super();
		this.success = success;
		this.status = status;
		this.msg = msg;
		this.obj = obj;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<LiftSts_com> getObj() {
		return obj;
	}

	public void setObj(List<LiftSts_com> obj) {
		this.obj = obj;
	}
}
