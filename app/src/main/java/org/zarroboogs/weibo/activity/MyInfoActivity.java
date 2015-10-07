
package org.zarroboogs.weibo.activity;

import android.os.Bundle;

public class MyInfoActivity extends UserInfoActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getToolbar().setTitle("我的主页");
        disPlayHomeAsUp(getToolbar());
    }
}
