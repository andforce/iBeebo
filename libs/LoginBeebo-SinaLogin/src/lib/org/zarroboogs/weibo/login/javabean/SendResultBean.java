package lib.org.zarroboogs.weibo.login.javabean;

import java.io.Serializable;

public class SendResultBean implements Serializable {
	private static final long serialVersionUID = -2502238369242634465L;
	// {"code":"100000",
	// "othercode":null,
	// "msg":"\u6210\u529f",
	// "data":"<li class=\"feed_item S_separate_l\">\n <div
	// class=\"user_avatar_container\">\n <a
	// title=\"\u5c0f\u5f1f\u738b\u632f\u8d85\" target=\"_blank\"
	// href=\"http:\/\/weibo.com\/u\/2294141594\"
	// class=\"user_avatar_link\"><img width=\"50\" height=\"50\"
	// alt=\"\u5c0f\u5f1f\u738b\u632f\u8d85\"
	// src=\"http:\/\/tp3.sinaimg.cn\/2294141594\/50\/5704514671\/1\"
	// class=\"user_avatar\"><\/a>\n <\/div>\n <div class=\"weibo_content\">\n
	// <div class=\"weibo_text\">\n <a title=\"\u5c0f\u5f1f\u738b\u632f\u8d85\"
	// target=\"_blank\" href=\"http:\/\/weibo.com\/u\/2294141594\"
	// class=\"user_name\">\u5c0f\u5f1f\u738b\u632f\u8d85<\/a><cite
	// class=\"user_words\">: 111416703800645<\/cite>\n <\/div>\n <div
	// class=\"weibo_bottom\">\n <div class=\"weibo_info S_text_l\">\n <a
	// title=\"\" target=\"_blank\"
	// href=\"http:\/\/weibo.com\/2294141594\/BxyXst9u2\"
	// class=\"weibo_time\">10\u79d2\u949f\u524d<\/a><cite
	// class=\"weibo_from\">\u6765\u81ea<a
	// href=\"http:\/\/app.weibo.com\/t\/feed\/6gBvZH\" rel=\"nofollow\"
	// target=\"_blank\">\u95ea\u95ea\u7684\u725b\u903c<\/a><\/cite>\n <\/div>\n
	// <div class=\"weibo_handle\">\n <a title=\"\" href=\"#\" onclick=\"return
	// false;\" action-type=\"forward\" action-data=\"mid=3779992066947970\"
	// class=\"weibo_handler_a\">\u8f6c\u53d1<\/a>\n <cite
	// class=\"WB_vline\">|<\/cite>\n <a title=\"\" href=\"#\" onclick=\"return
	// false;\" action-type=\"favorite\" action-data=\"mid=3779992066947970\"
	// class=\"weibo_handler_a\">\u6536\u85cf<\/a>\n <cite
	// class=\"WB_vline\">|<\/cite>\n <a title=\"\" href=\"#\" onclick=\"return
	// false;\" action-type=\"comment\" action-data=\"mid=3779992066947970\"
	// class=\"weibo_handler_a\">\u8bc4\u8bba<\/a>\n <\/div>\n <\/div>\n
	// <\/div>\n<\/li>"}
	private String code;
	private String othercode;
	private String msg;
	private String data;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getOthercode() {
		return othercode;
	}

	public void setOthercode(String othercode) {
		this.othercode = othercode;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
