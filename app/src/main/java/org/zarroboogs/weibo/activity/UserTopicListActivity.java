
package org.zarroboogs.weibo.activity;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.fragment.UserTopicListFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;

public class UserTopicListActivity extends AbstractAppActivity {

    public static Intent newIntent(UserBean userBean, ArrayList<String> topicList) {
        Intent intent = new Intent(BeeboApplication.getInstance(), UserTopicListActivity.class);
        intent.putExtra(Constants.USERBEAN, userBean);
        intent.putStringArrayListExtra("topicList", topicList);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_topic_list_activity_layout);
        UserBean userBean = getIntent().getParcelableExtra(Constants.USERBEAN);
        ArrayList<String> topicList = getIntent().getStringArrayListExtra("topicList");

        if (savedInstanceState == null) {
            UserTopicListFragment fragment;
            if (topicList != null) {
                fragment = new UserTopicListFragment(userBean, topicList);
            } else {
                fragment = new UserTopicListFragment(userBean);
            }
            getFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
        }
        
        disPlayHomeAsUp(R.id.userTopicListToolbar);
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
