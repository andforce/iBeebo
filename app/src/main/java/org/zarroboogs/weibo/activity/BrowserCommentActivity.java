
package org.zarroboogs.weibo.activity;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.bean.CommentBean;
import org.zarroboogs.weibo.fragment.BrowserCommentFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class BrowserCommentActivity extends AbstractAppActivity {

    private String token;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.TOKEN, token);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        CommentBean bean = intent.getParcelableExtra("comment");
        token = intent.getStringExtra(Constants.TOKEN);

        if (getFragmentManager().findFragmentByTag(BrowserCommentActivity.class.getName()) == null) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new BrowserCommentFragment(bean), BrowserCommentFragment.class.getName())
                    .commit();
        }

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
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
        return super.onOptionsItemSelected(item);
    }
}
