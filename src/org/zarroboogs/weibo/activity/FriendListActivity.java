package org.zarroboogs.weibo.activity;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.fragment.FriendsListFragment;

import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;


public class FriendListActivity extends AbstractAppActivity {

	private UserBean bean;


	private Toolbar mToolBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_activity_layout);
		mToolBar = (Toolbar) findViewById(R.id.settingToolBar);
		
//		getActionBar().setDisplayHomeAsUpEnabled(true);
//		getActionBar().setTitle(getString(R.string.following_list));
//		getActionBar().setIcon(R.drawable.ic_ab_friendship);
		mToolBar.setLogo(R.drawable.ic_ab_friendship);
		mToolBar.setTitle(R.string.following_list);
		bean = (UserBean) getIntent().getParcelableExtra("user");
		if (getSupportFragmentManager().findFragmentByTag(FriendsListFragment.class.getName()) == null) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, FriendsListFragment.newInstance(bean), FriendsListFragment.class.getName()).commit();
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
	   public UserBean getUser() {
	        return bean;
	    }

	    public static Intent newIntent(String token, UserBean userBean) {
	        Intent intent = new Intent(GlobalContext.getInstance(), FriendListActivity.class);
	        intent.putExtra(Constants.TOKEN, token);
	        intent.putExtra("user", userBean);
	        return intent;
	    }
}
