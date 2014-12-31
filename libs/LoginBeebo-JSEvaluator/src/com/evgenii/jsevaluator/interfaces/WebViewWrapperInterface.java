package com.evgenii.jsevaluator.interfaces;

import android.webkit.WebView;

public interface WebViewWrapperInterface {
	public void loadJavaScript(String javascript);

    public WebView getWebView();
}
