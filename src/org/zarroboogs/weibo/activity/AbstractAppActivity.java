
package org.zarroboogs.weibo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewConfiguration;
import android.widget.Toast;

import org.apache.http.client.CookieStore;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.bean.AccountBean;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.asyncdrawable.TimeLineBitmapDownloader;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;

import java.lang.reflect.Field;

public class AbstractAppActivity extends TranslucentStatusBarActivity {

    protected int theme = 0;
    public AccountBean mAccountBean;
    private static AsyncHttpClient mAsyncHttoClient;
    private static CookieStore cookieStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            theme = SettingUtils.getAppTheme();
        } else {
            theme = savedInstanceState.getInt("theme");
        }
        setTheme(theme);

        super.onCreate(savedInstanceState);

        if (cookieStore == null) {
            cookieStore = new PersistentCookieStore(getApplicationContext());
        }

        if (mAsyncHttoClient == null) {
            mAsyncHttoClient = new AsyncHttpClient();
        }

        mAsyncHttoClient.setCookieStore(cookieStore);

        forceShowActionBarOverflowMenu();
        GlobalContext.getInstance().setActivity(this);

    }

    public AsyncHttpClient getAsyncHttpClient() {
        if (mAsyncHttoClient == null) {
            mAsyncHttoClient = new AsyncHttpClient();
        }
        return mAsyncHttoClient;
    }

    public CookieStore getCookieStore() {
        if (cookieStore == null) {
            cookieStore = new PersistentCookieStore(getApplicationContext());
        }
        return cookieStore;
    }

    public AccountBean getAccount() {
        return mAccountBean;
    }

    @Override
    protected void onResume() {
        super.onResume();
        GlobalContext.getInstance().setCurrentRunningActivity(this);

        if (theme != SettingUtils.getAppTheme()) {
            reload();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (GlobalContext.getInstance().getCurrentRunningActivity() == this) {
            GlobalContext.getInstance().setCurrentRunningActivity(null);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("theme", theme);

    }

    private void forceShowActionBarOverflowMenu() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ignored) {

        }
    }

    public void reload() {

        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();

        overridePendingTransition(0, 0);
        startActivity(intent);
        TimeLineBitmapDownloader.refreshThemePictureBackground();
    }

    public TimeLineBitmapDownloader getBitmapDownloader() {
        return TimeLineBitmapDownloader.getInstance();
    }

    protected void dealWithException(WeiboException e) {
        Toast.makeText(this, e.getError(), Toast.LENGTH_SHORT).show();
    }
}
