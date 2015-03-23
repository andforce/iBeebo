
package org.zarroboogs.weibo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.fragment.AtUserFragment;

import com.umeng.analytics.MobclickAgent;

public class AtUserActivity extends AbstractAppActivity {

    private Toolbar mAtUserToolBar;
    
    public static final String TOKEN_HACK = "token_hack";
    
    public static Intent atUserIntent(Activity activity, String token) {
		Intent intent = new Intent(activity, AtUserActivity.class);
		intent.putExtra(TOKEN_HACK, token);
		return intent;
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.at_user_activity_layout);

        mAtUserToolBar = (Toolbar) findViewById(R.id.atUserToolbar);

        String token = getIntent().getStringExtra(TOKEN_HACK);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.at_content_frame, new AtUserFragment(token, mAtUserToolBar)).commit();
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
