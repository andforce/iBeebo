
package org.zarroboogs.weibo.activity;

import lib.org.zarroboogs.weibo.login.utils.LogTool;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.setting.SettingUtils;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

public class TranslucentStatusBarActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(true);
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintEnabled(true);
			// R.color.actionbar_dark
			tintManager.setStatusBarTintResource(R.color.md_actionbar_bg_color);

			int height = getNavigationBarHeight(this.getApplicationContext(),Configuration.ORIENTATION_PORTRAIT);

			boolean hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey();
			boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

			if (!(isMeiZu() || isHuaWei())) {
				if (!hasMenuKey && !hasBackKey) {
					// Do whatever you need to do, this device has a navigation bar
					LogTool.D("getNavigationBarHeight_:" + height + "有虚拟按键");
					if (SettingUtils.isNaviGationBarIm()) {
						tintManager.setNavigationBarTintEnabled(true);
						tintManager.setNavigationBarTintResource(R.color.md_actionbar_bg_color);
					} else {
						tintManager.setNavigationBarTintEnabled(true);
						tintManager.setNavigationBarTintResource(R.color.black);
					}
				} else {
					LogTool.D("getNavigationBarHeight_:" + height + "没有虚拟按键");
					tintManager.setNavigationBarTintEnabled(false);
				}
			}
		}

    }

	public static boolean isMeiZu() {
		return "Meizu".equalsIgnoreCase(android.os.Build.MANUFACTURER);
	}

	public static boolean isHuaWei() {
		return "HUAWEI".equalsIgnoreCase(android.os.Build.MANUFACTURER);
	}
    private int getNavigationBarHeight(Context context, int orientation) {
        Resources resources = context.getResources();

        int id = resources.getIdentifier(
                orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_height_landscape",
                "dimen", "android");
        if (id > 0) {
            return resources.getDimensionPixelSize(id);
        }
        return 0;
    }
    
    @TargetApi(19)
    public void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

}
