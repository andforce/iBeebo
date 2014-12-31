package com.evgenii.jsevaluator;

import com.evgenii.jsevaluator.interfaces.CallJavaResultInterface;

/**
 * Passed in addJavascriptInterface of WebView to allow web views's JS execute
 * Java code
 */
public class JavaScriptInterface {
	private final CallJavaResultInterface mCallJavaResultInterface;

	public JavaScriptInterface(CallJavaResultInterface callJavaResult) {
		mCallJavaResultInterface = callJavaResult;
	}

	public void returnResultToJava(String value, int callIndex) {
		mCallJavaResultInterface.jsCallFinished(value, callIndex);
	}
}