package org.zarroboogs.weibo.activity;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.fragment.FanListFragment;

import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * User: Jiang Qi Date: 12-8-16
 */
public class FanListActivity extends AbstractAppActivity {

	private String token;

	private UserBean bean;
	private Toolbar mFanToolbar;

	public UserBean getUser() {
		return bean;
	}

	public static Intent newIntent(String token, UserBean userBean) {
		Intent intent = new Intent(GlobalContext.getInstance(), FanListActivity.class);
		intent.putExtra(Constants.TOKEN, token);
		intent.putExtra("user", userBean);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fan_list_activity_layout);
		mFanToolbar = (Toolbar) findViewById(R.id.fanListToolbar);
		
//		getActionBar().setDisplayHomeAsUpEnabled(true);
//		getActionBar().setTitle(getString(R.string.fan_list));
//		getActionBar().setIcon(R.drawable.ic_ab_friendship);
		mFanToolbar.setTitle(getString(R.string.fan_list));

		token = getIntent().getStringExtra(Constants.TOKEN);
		bean = (UserBean) getIntent().getParcelableExtra("user");

		if (getSupportFragmentManager().findFragmentByTag(FanListFragment.class.getName()) == null) {
			getSupportFragmentManager().beginTransaction().replace(R.id.content, FanListFragment.newInstance(bean), FanListFragment.class.getName())
					.commit();
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
}
