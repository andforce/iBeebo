
package org.zarroboogs.weibo.activity;

import org.zarroboogs.weibo.bean.WeiboWeiba;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.WindowManager;

public class SharedPreferenceActivity extends AbstractAppActivity implements OnSharedPreferenceChangeListener {
    private SharedPreferences mCookieSP = null;
    private String mCookie = "";
    private static final String KEY_COOKIE = "cookie";
    private static final String KEY_NAME = "appname";
    private static final String KEY_CODE = "code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        super.onCreate(savedInstanceState);
        mCookieSP = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        mCookie = mCookieSP.getString(KEY_COOKIE, "");
        mCookieSP.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mCookieSP.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // TODO Auto-generated method stub
        if (KEY_COOKIE.equals(key)) {
            mCookie = mCookieSP.getString(KEY_COOKIE, "");
        }
    }

    // public String getWeiboCookie() {
    // return mCookie;
    // }
    //
    // public void setWeiboCookie(String cookie) {
    // mCookieSP.edit().putString(KEY_COOKIE, cookie).commit();
    // }

    public SharedPreferences getAppSrcSharedPreference() {
        return mCookieSP;
    }

    public void saveWeiba(WeiboWeiba weiba) {
        Editor mEditor = mCookieSP.edit();
        mEditor.putString(KEY_NAME, weiba.getText());
        mEditor.putString(KEY_CODE, weiba.getCode());
        mEditor.commit();

    }

    public WeiboWeiba getWeiba() {
        WeiboWeiba weiba = new WeiboWeiba();
        weiba.setText(mCookieSP.getString(KEY_NAME, "Smartisan T1"));
        weiba.setCode(mCookieSP.getString(KEY_CODE, "1GEU4g"));
        return weiba;
    }
}
