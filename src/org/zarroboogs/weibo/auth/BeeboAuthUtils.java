package org.zarroboogs.weibo.auth;

public class BeeboAuthUtils {

	static {
		System.loadLibrary("BeeboAuthUtils");
	}

	public static native String getAppKey();

	public static native String getAppSecret();

	public static native String getRedirectUrl();
	
	
	public static native String getHackAppKey();

	public static native String getHackAppSecret();

	public static native String getHackRedirectUrl();
	
	public static native String getHackPackageName();

	public static native String getHackKeyHash();
	
}
