package org.zarroboogs.weibo.helper;

import android.content.Context;
import android.content.SharedPreferences;

import org.zarroboogs.weibo.BeeboApplication;

import java.util.Set;

/**
 * Created by wangdiyuan on 15-5-4.
 */
public class SharedPreferenceHelper {
    private static Context sContent = BeeboApplication.getAppContext();
    private static SharedPreferences sPreference;

    public SharedPreferences getPreference() {
        if (sPreference == null) {
            sPreference = sContent.getSharedPreferences(sContent.getPackageName(), Context.MODE_PRIVATE);
        }
        return sPreference;
    }

    public boolean commitValue(String key, Object value) {
        if (value instanceof String) {
            return getPreference().edit().putString(key, (String) value).commit();
        } else if (value instanceof Integer) {
            return getPreference().edit().putInt(key, (Integer) value).commit();
        } else if (value instanceof Boolean) {
            return getPreference().edit().putBoolean(key, (Boolean) value).commit();
        } else if (value instanceof Float) {
            return getPreference().edit().putFloat(key, (Float) value).commit();
        } else if (value instanceof Long) {
            return getPreference().edit().putLong(key, (Long) value).commit();
        } else {
            return getPreference().edit().putStringSet(key, (Set) value).commit();
        }
    }

    public SharedPreferences.Editor putValue(String key, Object value) {
        if (value instanceof String) {
            return getPreference().edit().putString(key, (String) value);
        } else if (value instanceof Integer) {
            return getPreference().edit().putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            return getPreference().edit().putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            return getPreference().edit().putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            return getPreference().edit().putLong(key, (Long) value);
        } else {
            return getPreference().edit().putStringSet(key, (Set) value);
        }
    }
}
