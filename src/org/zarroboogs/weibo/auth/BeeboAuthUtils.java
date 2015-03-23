package org.zarroboogs.weibo.auth;

public class BeeboAuthUtils {

	static {
		System.loadLibrary("BeeboAuthUtils");
	}
	public native String getAppKey();

	public native String getAppSecret();

	public native String getRedirectUrl();

}
