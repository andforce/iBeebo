
package org.zarroboogs.weibo.activity;

import android.os.Bundle;

import com.umeng.analytics.MobclickAgent;

public class MyInfoActivity extends UserInfoActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getToolbar().setTitle("我的主页");
        disPlayHomeAsUp(getToolbar());
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
}
