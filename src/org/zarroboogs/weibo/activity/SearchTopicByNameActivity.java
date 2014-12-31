
package org.zarroboogs.weibo.activity;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.fragment.SearchTopicByNameFragment;

import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;

public class SearchTopicByNameActivity extends AbstractAppActivity {

    private Toolbar mSearchTopicToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_topic_byname_activity_layout);
        mSearchTopicToolbar = (Toolbar) findViewById(R.id.searchTopicToolbar);

        String q = getIntent().getStringExtra("q");
        if (TextUtils.isEmpty(q)) {
            Uri data = getIntent().getData();
            String d = data.toString();
            int index = d.indexOf("#");
            q = d.substring(index + 1, d.length() - 1);
        }
        // getActionBar().setDisplayHomeAsUpEnabled(true);
        // getActionBar().setDisplayShowHomeEnabled(false);
        // getActionBar().setTitle("#" + q + "#");
        mSearchTopicToolbar.setTitle("#" + q + "#");
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content, new SearchTopicByNameFragment(q)).commit();
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
