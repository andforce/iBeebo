package org.zarroboogs.weibo.setting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import org.zarroboogs.weibo.activity.AbstractAppActivity;
import org.zarroboogs.weibo.setting.fragment.DraftFragment;

import com.umeng.analytics.MobclickAgent;

public class DraftActivity extends AbstractAppActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
//		getActionBar().setDisplayShowHomeEnabled(false);
//		getActionBar().setDisplayShowTitleEnabled(true);
//		getActionBar().setDisplayHomeAsUpEnabled(true);
//		getActionBar().setTitle(getString(R.string.draft));

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().replace(android.R.id.content, new DraftFragment()).commit();
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
			intent = new Intent(this, SettingActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			return true;
		}
		return false;
	}

}
