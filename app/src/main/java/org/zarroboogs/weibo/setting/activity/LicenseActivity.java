
package org.zarroboogs.weibo.setting.activity;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.AbstractAppActivity;
import org.zarroboogs.weibo.support.utils.Utility;

import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebView;

public class LicenseActivity extends AbstractAppActivity {

    private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.license_activity_layout);
        webView = (WebView) findViewById(R.id.licenseWebview);

        if (getWindow().isFloating()) {
            WindowManager.LayoutParams layout = new WindowManager.LayoutParams();
            layout.copyFrom(getWindow().getAttributes());
            layout.height = WindowManager.LayoutParams.MATCH_PARENT;
            getWindow().setAttributes(layout);
        }
        webView.loadUrl("file:///android_asset/licenses.html");
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getName());
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getName());
        MobclickAgent.onPause(this);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Utility.isKK()) {
            getMenuInflater().inflate(R.menu.actionbar_menu_licenseactivity, menu);
            return true;
        } else {
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            intent = new Intent(this, AboutActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.menu_print) {
            PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
            PrintDocumentAdapter adapter = webView.createPrintDocumentAdapter();
            printManager.print(getString(R.string.app_name), adapter, null);
            return true;
        }
        return false;
    }

}
