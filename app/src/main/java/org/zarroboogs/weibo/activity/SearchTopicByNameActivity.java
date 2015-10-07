
package org.zarroboogs.weibo.activity;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.fragment.SearchTopicByNameFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;

public class SearchTopicByNameActivity extends AbstractAppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_topic_byname_activity_layout);
        Toolbar searchTopicToolbar = (Toolbar) findViewById(R.id.searchTopicToolbar);

        String q = getIntent().getStringExtra("q");
        if (TextUtils.isEmpty(q)) {
            Uri data = getIntent().getData();
            String d = data.toString();
            int index = d.indexOf("#");
            q = d.substring(index + 1, d.length() - 1);
        }
        searchTopicToolbar.setTitle("话题浏览");
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content, new SearchTopicByNameFragment(q)).commit();
        }
        
        disPlayHomeAsUp(searchTopicToolbar);

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
