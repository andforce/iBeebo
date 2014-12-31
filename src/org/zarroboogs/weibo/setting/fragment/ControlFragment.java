package org.zarroboogs.weibo.setting.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.setting.activity.SettingActivity;
import org.zarroboogs.weibo.support.utils.Utility;

/**
 * User: qii Date: 12-10-19
 */
public class ControlFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

	private Preference msgCount = null;
	private Preference commentRepostListAvatar = null;
	private Preference uploadPicQuality = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(false);

		addPreferencesFromResource(R.xml.control_pref);

		msgCount = findPreference(SettingActivity.MSG_COUNT);
		commentRepostListAvatar = findPreference(SettingActivity.COMMENT_REPOST_AVATAR);
		uploadPicQuality = findPreference(SettingActivity.UPLOAD_PIC_QUALITY);

		buildSummary();

		PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);

	}

	private void buildSummary() {
		String value = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(SettingActivity.MSG_COUNT, "3");
		msgCount.setSummary(getActivity().getResources().getStringArray(R.array.msg_count_title)[Integer.valueOf(value) - 1]);

		value = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(SettingActivity.COMMENT_REPOST_AVATAR, "3");
		commentRepostListAvatar.setSummary(getActivity().getResources().getStringArray(R.array.comment_repost_list_avatar_mode)[Integer.valueOf(value) - 1]);

		value = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(SettingActivity.UPLOAD_PIC_QUALITY, "1");
		uploadPicQuality.setSummary(getActivity().getResources().getStringArray(R.array.upload_pic_quality_hack_bug)[Integer.valueOf(value) - 1]);

	}

	@Override
	public void onDetach() {
		super.onDetach();
		PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(SettingActivity.DISABLE_DOWNLOAD_AVATAR_PIC)) {

		}

		if (key.equals(SettingActivity.COMMENT_REPOST_AVATAR)) {
			switch (SettingUtils.getCommentRepostAvatar()) {
			case 1:
				SettingUtils.setEnableCommentRepostAvatar(true);
				break;
			case 2:
				SettingUtils.setEnableCommentRepostAvatar(false);
				break;
			case 3:
				SettingUtils.setEnableCommentRepostAvatar(Utility.isWifi(getActivity()));
				break;

			}

		}

		buildSummary();
	}
}
