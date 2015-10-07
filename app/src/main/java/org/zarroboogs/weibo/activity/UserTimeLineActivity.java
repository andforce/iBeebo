
package org.zarroboogs.weibo.activity;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.fragment.StatusesByIdTimeLineFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class UserTimeLineActivity extends AbstractAppActivity {

    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.setting_activity_layout);
        mToolBar = (Toolbar) findViewById(R.id.settingToolBar);

        String token = getIntent().getStringExtra(Constants.TOKEN);
        UserBean bean = getIntent().getParcelableExtra("user");

        mToolBar.setTitle(bean.getScreen_name());

        if (getSupportFragmentManager().findFragmentByTag(StatusesByIdTimeLineFragment.class.getName()) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, StatusesByIdTimeLineFragment.newInstance(bean, token),
                            StatusesByIdTimeLineFragment.class.getName())
                    .commit();
        }

        disPlayHomeAsUp(mToolBar);
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

    public static Intent newIntent(String token, UserBean userBean) {
        Intent intent = new Intent(BeeboApplication.getInstance(), UserTimeLineActivity.class);
        intent.putExtra(Constants.TOKEN, token);
        intent.putExtra("user", userBean);
        return intent;
    }
}
