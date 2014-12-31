package org.zarroboogs.weibo.setting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.AbstractAppActivity;

import com.umeng.analytics.MobclickAgent;

public class PerformanceActivity extends AbstractAppActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
//		getActionBar().setDisplayShowHomeEnabled(false);
//		getActionBar().setDisplayShowTitleEnabled(true);
//		getActionBar().setDisplayHomeAsUpEnabled(true);
//		getActionBar().setTitle(getString(R.string.pref_performance_title));

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().replace(android.R.id.content, new PerformanceFragment()).commit();
		}
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
	
	public static class PerformanceFragment extends PreferenceFragment {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setRetainInstance(false);

			addPreferencesFromResource(R.xml.performance_pref);
		}

	}
}
