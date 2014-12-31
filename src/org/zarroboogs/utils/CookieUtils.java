package org.zarroboogs.utils;

import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

public class CookieUtils {

	public static String getHttpSessionId(CookieStore mCookieStore) {
		String httpSessionId = "";
		List<Cookie> cookies = mCookieStore.getCookies();
		for (Cookie cookie : cookies) {
			String cookieString = cookie.getName() + cookie.getValue();
			if (cookieString.contains("NTESSTUDYSI")) {
				httpSessionId = cookie.getValue();
			}
		}
		return httpSessionId;
	}
}
