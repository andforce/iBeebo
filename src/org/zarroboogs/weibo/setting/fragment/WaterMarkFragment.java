package org.zarroboogs.weibo.setting.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.setting.activity.SettingActivity;

import java.util.ArrayList;
import java.util.List;

public class WaterMarkFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

	private Preference frequency;
	private List<Preference> preferenceList = new ArrayList<Preference>(4);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(false);

		addPreferencesFromResource(R.xml.water_mark_pref);

		preferenceList.add(findPreference(SettingActivity.WATER_MARK_SCREEN_NAME));
		preferenceList.add(findPreference(SettingActivity.WATER_MARK_WEIBO_ICON));
		preferenceList.add(findPreference(SettingActivity.WATER_MARK_WEIBO_URL));
		
		
		preferenceList.add(findPreference(SettingActivity.WATER_MARK_POS));

		View title = getActivity().getLayoutInflater().inflate(R.layout.filteractivity_title_layout, null);
		Switch switchBtn = (Switch) title.findViewById(R.id.switchBtn);
//		getActivity().getActionBar().setCustomView(title, new ActionBar.LayoutParams(Gravity.RIGHT));
//		getActivity().getActionBar().setDisplayShowCustomEnabled(true);
//		getActivity().getActionBar().setTitle("图片水印");

		switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SettingUtils.setEnableWaterMark(isChecked);
				switchPre(isChecked);
			}
		});

		switchBtn.setChecked(SettingUtils.getEnableWaterMark());
		switchPre(SettingUtils.getEnableWaterMark());

		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

		frequency = findPreference(SettingActivity.WATER_MARK_POS);
		buildSummary();
	}

	// confirm getActivity() is not null
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		buildSummary();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

		if (key.equals(SettingActivity.WATER_MARK_POS)) {
			buildSummary();
		}
	}

	private void buildSummary() {
		if (SettingUtils.getEnableWaterMark()) {
			String value = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(SettingActivity.WATER_MARK_POS, "1");
			frequency.setSummary(getActivity().getResources().getStringArray(R.array.water_mark_pos_array)[Integer.valueOf(value) - 1]);
		}

	}

	private void switchPre(boolean value) {
		for (Preference p : preferenceList) {
			p.setEnabled(value);
		}

	}

}
