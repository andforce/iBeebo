
package org.zarroboogs.weibo.activity;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.fragment.UserTopicListFragment;

import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;

public class UserTopicListActivity extends AbstractAppActivity {

    public static Intent newIntent(UserBean userBean, ArrayList<String> topicList) {
        Intent intent = new Intent(GlobalContext.getInstance(), UserTopicListActivity.class);
        intent.putExtra(Constants.USERBEAN, userBean);
        intent.putStringArrayListExtra("topicList", topicList);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_topic_list_activity_layout);
        UserBean userBean = (UserBean) getIntent().getParcelableExtra(Constants.USERBEAN);
        ArrayList<String> topicList = getIntent().getStringArrayListExtra("topicList");
        // getActionBar().setDisplayHomeAsUpEnabled(true);
        // getActionBar().setTitle(getString(R.string.topic));

        if (savedInstanceState == null) {
            UserTopicListFragment fragment;
            if (topicList != null) {
                fragment = new UserTopicListFragment(userBean, topicList);
            } else {
                fragment = new UserTopicListFragment(userBean);
            }
            getFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
        }
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
        switch (item.getItemId()) {
            case android.R.id.home:

                Intent intent = MainTimeLineActivity.newIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;

        }
        return false;
    }
}
