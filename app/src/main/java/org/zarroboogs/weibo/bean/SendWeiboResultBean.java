package org.zarroboogs.weibo.bean;

public class SendWeiboResultBean {
	private String code;
	private String msg;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isSuccess(){
		return "100000".equals(code);
	}
}
