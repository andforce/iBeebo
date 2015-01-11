
package org.zarroboogs.weibo.setting.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.othercomponent.AppNewMsgAlarm;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.setting.activity.SettingActivity;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Preference frequency;
    private Preference ringtone;
    private List<Preference> preferenceList = new ArrayList<Preference>(9);

    private Uri uri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);

        addPreferencesFromResource(R.xml.notification_pref);

        preferenceList.add(findPreference(SettingActivity.DISABLE_FETCH_AT_NIGHT));
        preferenceList.add(findPreference(SettingActivity.FREQUENCY));
        preferenceList.add(findPreference(SettingActivity.ENABLE_COMMENT_TO_ME));
        preferenceList.add(findPreference(SettingActivity.ENABLE_MENTION_COMMENT_TO_ME));
        preferenceList.add(findPreference(SettingActivity.ENABLE_MENTION_TO_ME));
        preferenceList.add(findPreference(SettingActivity.ENABLE_VIBRATE));
        preferenceList.add(findPreference(SettingActivity.ENABLE_LED));
        preferenceList.add(findPreference(SettingActivity.ENABLE_RINGTONE));

        // View title =
        // getActivity().getLayoutInflater().inflate(R.layout.filteractivity_title_layout, null);
        // Switch switchBtn = (Switch) title.findViewById(R.id.switchBtn);
        //
        // switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        // @Override
        // public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // SettingUtils.setEnableFetchMSG(isChecked);
        // switchPre(isChecked);
        // }
        // });

        // switchBtn.setChecked(SettingUtils.getEnableFetchMSG());
        switchPre(SettingUtils.getEnableFetchMSG());

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        frequency = findPreference(SettingActivity.FREQUENCY);
        ringtone = findPreference(SettingActivity.ENABLE_RINGTONE);
        ringtone.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.pref_ringtone_title));
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, uri);
                startActivityForResult(intent, 1);
                return true;
            }
        });

        String path = SettingUtils.getRingtone();
        if (!TextUtils.isEmpty(SettingUtils.getRingtone())) {
            uri = Uri.parse(path);
        }

    }

    // confirm getActivity() is not null
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        buildSummary();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String ringTonePath = "";
            uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                ringTonePath = uri.toString();
            }

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            sharedPref.edit().putString(SettingActivity.ENABLE_RINGTONE, ringTonePath).commit();
            buildSummary();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SettingActivity.ENABLE_FETCH_MSG)) {
            boolean value = sharedPreferences.getBoolean(key, false);
            buildSummary();
            if (value) {
                AppNewMsgAlarm.startAlarm(AppNewMsgAlarm.DEBUG, getActivity(), false);
            } else {
                AppNewMsgAlarm.stopAlarm(getActivity(), true);
            }
        }

        if (key.equals(SettingActivity.FREQUENCY)) {

            AppNewMsgAlarm.startAlarm(AppNewMsgAlarm.DEBUG, getActivity(), false);
            buildSummary();
        }

        if (key.equals(SettingActivity.JBNOTIFICATION_STYLE)) {

            buildSummary();
        }
    }

    private void buildSummary() {
        if (SettingUtils.getEnableFetchMSG()) {
            String value = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(SettingActivity.FREQUENCY,
                    "1");
            frequency.setSummary(getActivity().getResources().getStringArray(R.array.frequency)[Integer.valueOf(value) - 1]);
        } else {
            frequency.setSummary(getString(R.string.stopped));

        }

        if (uri != null) {
            Ringtone r = RingtoneManager.getRingtone(getActivity(), uri);
            ringtone.setSummary(r.getTitle(getActivity()));
        } else {
            ringtone.setSummary(getString(R.string.silent));
        }

    }

    private void switchPre(boolean value) {
        for (Preference p : preferenceList) {
            p.setEnabled(value);
        }

    }

}
