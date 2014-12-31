package org.zarroboogs.weibo.bean;

import java.util.ArrayList;
import java.util.List;

public class WeibaGson implements java.io.Serializable {
	/**
     * 
     */
	private static final long serialVersionUID = 5810057029890495586L;
	// "code": "0000",
	// "data":
	private String code = "";
	private List<WeibaTree> data = new ArrayList<WeibaTree>();

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<WeibaTree> getData() {
		return data;
	}

	public void setData(List<WeibaTree> data) {
		this.data = data;
	}

}
