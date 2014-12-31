package org.zarroboogs.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLDecoder {
	public static String UnicodeToString(String str) {
		if (str == null) {
			return str;
		}
		Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
		Matcher matcher = pattern.matcher(str);
		char ch;
		while (matcher.find()) {
			ch = (char) Integer.parseInt(matcher.group(2), 16);
			str = str.replace(matcher.group(1), ch + "");
		}
		return str;
	}
}
