
package org.zarroboogs.weibo.support.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;

import org.zarroboogs.weibo.activity.BrowserWebActivity;
import org.zarroboogs.weibo.setting.SettingUtils;

/**
 * User: qii Date: 13-9-17
 */
public class WebBrowserSelector {

    public static void openLink(Context context, Uri uri) {
        if (SettingUtils.allowInternalWebBrowser()) {
            Intent intent = new Intent(context, BrowserWebActivity.class);
            intent.putExtra("url", uri.toString());
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
            context.startActivity(intent);
        }
    }
}
