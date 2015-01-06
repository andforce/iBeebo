
package org.zarroboogs.weibo.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.fragment.AtUserFragment;

import com.umeng.analytics.MobclickAgent;

public class AtUserActivity extends AbstractAppActivity {

    private Toolbar mAtUserToolBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.at_user_activity_layout);

        mAtUserToolBar = (Toolbar) findViewById(R.id.atUserToolbar);

        String token = getIntent().getStringExtra(Constants.TOKEN);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(R.id.at_content_frame, new AtUserFragment(token, mAtUserToolBar)).commit();
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
                finish();
                break;
        }
        return true;
    }
}
