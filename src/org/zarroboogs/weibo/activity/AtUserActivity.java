
package org.zarroboogs.weibo.activity;

import android.os.Bundle;
import android.view.MenuItem;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.fragment.AtUserFragment;

import com.umeng.analytics.MobclickAgent;

public class AtUserActivity extends AbstractAppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.setting_activity_layout);
        // ActionBar actionBar = getActionBar();
        // actionBar.setDisplayHomeAsUpEnabled(true);
        // actionBar.setTitle(R.string.at_other);

        String token = getIntent().getStringExtra(Constants.TOKEN);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame, new AtUserFragment(token)).commit();
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
