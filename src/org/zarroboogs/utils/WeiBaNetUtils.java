package org.zarroboogs.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class WeiBaNetUtils {
	public static boolean isNetworkAvaliable(Context context) {
		ConnectivityManager manager = (ConnectivityManager) (context.getSystemService(Context.CONNECTIVITY_SERVICE));
		NetworkInfo networkinfo = manager.getActiveNetworkInfo();
		return !(networkinfo == null || !networkinfo.isAvailable());
	}
}
