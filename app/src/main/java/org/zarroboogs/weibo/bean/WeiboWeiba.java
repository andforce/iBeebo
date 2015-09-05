package org.zarroboogs.weibo.bean;

import java.io.Serializable;

public class WeiboWeiba implements Serializable {
	
	// "text": "iPhone 6 Plus", "code": "5yiHuw"
	private static final long serialVersionUID = -2154985132157129772L;
	private String text = "";
	private String code = "";

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
