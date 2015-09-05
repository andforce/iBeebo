
package org.zarroboogs.weibo.activity;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.fragment.FanListFragment;

import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class FanListActivity extends AbstractAppActivity {

    private String token;

    private UserBean bean;

    public UserBean getUser() {
        return bean;
    }

    public static Intent newIntent(String token, UserBean userBean) {
        Intent intent = new Intent(BeeboApplication.getInstance(), FanListActivity.class);
        intent.putExtra(Constants.TOKEN, token);
        intent.putExtra("user", userBean);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fan_list_activity_layout);
        Toolbar mFanToolbar = (Toolbar) findViewById(R.id.fanListToolbar);
        mFanToolbar.setTitle(getString(R.string.fan_list));

        token = getIntent().getStringExtra(Constants.TOKEN);
        bean = getIntent().getParcelableExtra("user");

        if (getSupportFragmentManager().findFragmentByTag(FanListFragment.class.getName()) == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content, FanListFragment.newInstance(bean), FanListFragment.class.getName())
                    .commit();
        }

        disPlayHomeAsUp(mFanToolbar);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                intent = MainTimeLineActivity.newIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
        }
        return false;
    }
}
