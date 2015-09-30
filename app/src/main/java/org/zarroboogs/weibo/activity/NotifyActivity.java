package org.zarroboogs.weibo.activity;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.fragment.NotifyViewPagerFragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class NotifyActivity extends ToolBarAppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	 setContentView(R.layout.hotweibo_activity_layout);
         Toolbar mToolbar = (Toolbar) findViewById(R.id.hotWeiboToolbar);
         
         buildContent();
         mToolbar.setTitle(R.string.mentions_me);
         disPlayHomeAsUp(mToolbar);
         
         mToolbar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				NotifyViewPagerFragment atme = (NotifyViewPagerFragment) getSupportFragmentManager().findFragmentByTag(NotifyViewPagerFragment.class.getName());
				atme.scrollToTop();
			}
		});
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// TODO Auto-generated method stub
    	getMenuInflater().inflate(R.menu.write_dm_menu, menu);
    	return super.onCreateOptionsMenu(menu);
    }
    
    public static final int REQUEST_CODE = 0x1010;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
		if (itemId == R.id.menu_write_dm) {
			Intent intent = new Intent(NotifyActivity.this, DMSelectUserActivity.class);
			startActivityForResult(intent, REQUEST_CODE);
		}
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (REQUEST_CODE == requestCode) {
            Intent intent = new Intent(NotifyActivity.this, DMActivity.class);
            intent.putExtra("user", data.getParcelableExtra("user"));
            startActivity(intent);
		}

    }
    
    private void buildContent() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (getSupportFragmentManager().findFragmentByTag(NotifyViewPagerFragment.class.getName()) == null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.hotWeiboContent, new NotifyViewPagerFragment(),NotifyViewPagerFragment.class.getName())
                            .commitAllowingStateLoss();
                    getSupportFragmentManager().executePendingTransactions();
                }
            }
        });
    }
}
