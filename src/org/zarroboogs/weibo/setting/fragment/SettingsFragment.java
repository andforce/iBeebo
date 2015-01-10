package org.zarroboogs.weibo.setting.fragment;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.AccountActivity;
import org.zarroboogs.weibo.setting.activity.SettingActivity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setting_activity_pref);
		Preference myPref = (Preference) findPreference(SettingActivity.CHANGE_WEIBO_ACCOUNT);
		myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub
				showAccountSwitchPage();
				return false;
			}

		});
	}

	private void showAccountSwitchPage() {
		Intent intent = AccountActivity.newIntent();
		startActivity(intent);
	}
}
