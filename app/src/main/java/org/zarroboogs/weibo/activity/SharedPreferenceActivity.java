
package org.zarroboogs.weibo.activity;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.bean.WeiboWeiba;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.WindowManager;

@TargetApi(VERSION_CODES.KITKAT)
public class SharedPreferenceActivity extends AbstractAppActivity implements OnSharedPreferenceChangeListener {
    private SharedPreferences mSharedPreferences = null;
    private String mCookie = "";
    private static final String KEY_COOKIE = "cookie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mSharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        mCookie = mSharedPreferences.getString(KEY_COOKIE, "");
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // TODO Auto-generated method stub
        if (KEY_COOKIE.equals(key)) {
            mCookie = mSharedPreferences.getString(KEY_COOKIE, "");
        }
    }

    // public String getWeiboCookie() {
    // return mCookie;
    // }
    //
    // public void setWeiboCookie(String cookie) {
    // mCookieSP.edit().putString(KEY_COOKIE, cookie).commit();
    // }

    public SharedPreferences getSPs() {
        return mSharedPreferences;
    }

    public void saveWeiba(WeiboWeiba weiba) {
        Editor mEditor = mSharedPreferences.edit();
        mEditor.putString(Constants.KEY_NAME, weiba.getText());
        mEditor.putString(Constants.KEY_CODE, weiba.getCode());
        mEditor.commit();

    }

    public WeiboWeiba getWeiba() {
        WeiboWeiba weiba = new WeiboWeiba();
        weiba.setText(mSharedPreferences.getString(Constants.KEY_NAME, "iBeebo"));
        weiba.setCode(mSharedPreferences.getString(Constants.KEY_CODE, "507Tbr"));
        return weiba;
    }
}
