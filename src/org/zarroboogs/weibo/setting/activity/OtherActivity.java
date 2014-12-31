
package org.zarroboogs.weibo.setting.activity;

import org.zarroboogs.utils.file.FileManager;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.AbstractAppActivity;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.setting.SettingUtils;

import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.MenuItem;
import android.widget.Toast;

public class OtherActivity extends AbstractAppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // getActionBar().setDisplayShowHomeEnabled(false);
        // getActionBar().setDisplayShowTitleEnabled(true);
        // getActionBar().setDisplayHomeAsUpEnabled(true);
        // getActionBar().setTitle(getString(R.string.pref_other_title));

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(android.R.id.content, new OtherFragment()).commit();
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

    public static class OtherFragment extends PreferenceFragment {

        private static final String DEBUG_INFO = "pref_debug_info_key";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(false);

            addPreferencesFromResource(R.xml.other_pref);

            final Preference cleanCachePre = findPreference(SettingActivity.CLICK_TO_CLEAN_CACHE);

            if (FileManager.isExternalStorageMounted()) {
                new CalcCacheSize(cleanCachePre).executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                cleanCachePre.setSummary(R.string.please_insert_sd_card);
            }
            cleanCachePre.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (FileManager.isExternalStorageMounted()) {
                        new CleanCacheTask(cleanCachePre).executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.please_insert_sd_card), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });

            detectDebugPreference();
        }

        private void detectDebugPreference() {
            Preference debugPreferenceCategory = (PreferenceCategory) findPreference(DEBUG_INFO);

            if (!SettingUtils.isBlackMagicEnabled()) {
                PreferenceScreen screen = getPreferenceScreen();
                screen.removePreference(debugPreferenceCategory);
            }
        }

        private class CalcCacheSize extends MyAsyncTask<Void, Void, String> {

            Preference preference;

            public CalcCacheSize(Preference preference) {
                this.preference = preference;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                this.preference.setSummary(R.string.getting_cache_size);
            }

            @Override
            protected String doInBackground(Void... params) {
                return FileManager.getPictureCacheSize();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (getActivity() == null) {
                    return;
                }

                preference.setSummary(getString(R.string.pref_max_file_cache_size_is_300mb_current_size_is, s));
            }

        }

        private class CleanCacheTask extends MyAsyncTask<Void, Void, Void> {

            Preference preference;

            public CleanCacheTask(Preference preference) {
                this.preference = preference;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Toast.makeText(getActivity(), getString(R.string.start_clean_cache), Toast.LENGTH_SHORT).show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                FileManager.deletePictureCache();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (getActivity() == null) {
                    return;
                }

                Toast.makeText(getActivity(), getString(R.string.clean_cache_finish), Toast.LENGTH_SHORT).show();

                new CalcCacheSize(preference).executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
            }
        }

    }
}
