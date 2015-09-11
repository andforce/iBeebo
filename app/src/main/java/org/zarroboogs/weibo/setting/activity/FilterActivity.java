
package org.zarroboogs.weibo.setting.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.AbstractAppActivity;
import org.zarroboogs.weibo.setting.fragment.FilterKeywordFragment;
import org.zarroboogs.weibo.setting.fragment.FilterSourceFragment;
import org.zarroboogs.weibo.setting.fragment.FilterTopicFragment;
import org.zarroboogs.weibo.setting.fragment.FilterUserFragment;
import org.zarroboogs.weibo.support.lib.AppFragmentPagerAdapter;

import com.andforce.common.view.SlidingTabLayout;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class FilterActivity extends AbstractAppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_activity_layout);

        initLayout();

        disPlayHomeAsUp(R.id.viewPagerToolBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getName());
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getName());
        MobclickAgent.onPause(this);
    }

    private void initLayout() {

        SlidingTabLayout slidingTab = (SlidingTabLayout) findViewById(R.id.filterSTL);
        ViewPager viewPager = (ViewPager) findViewById(R.id.filterViewpager);
        TimeLinePagerAdapter adapter = new TimeLinePagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(adapter);
        slidingTab.setViewPager(viewPager);

    }

    class TimeLinePagerAdapter extends AppFragmentPagerAdapter {

        List<Fragment> list = new ArrayList<>();

        public TimeLinePagerAdapter(FragmentManager fm) {
            super(fm);
            if (getFilterFragment() == null) {
                list.add(new FilterKeywordFragment());
            } else {
                list.add(getFilterFragment());
            }
            if (getFilterUserFragment() == null) {
                list.add(new FilterUserFragment());
            } else {
                list.add(getFilterUserFragment());
            }

            if (getFilterTopicFragment() == null) {
                list.add(new FilterTopicFragment());
            } else {
                list.add(getFilterTopicFragment());
            }

            if (getFilterSourceFragment() == null) {
                list.add(new FilterSourceFragment());
            } else {
                list.add(getFilterSourceFragment());
            }
        }

        @Override
        public Fragment getItem(int i) {
            return list.get(i);
        }

        @Override
        protected String getTag(int position) {
            List<String> tagList = new ArrayList<>();
            tagList.add(FilterKeywordFragment.class.getName());
            tagList.add(FilterUserFragment.class.getName());
            tagList.add(FilterTopicFragment.class.getName());
            tagList.add(FilterSourceFragment.class.getName());
            return tagList.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: {

                    return FilterActivity.this.getResources().getString(R.string.filter_keyword);
                }
                case 1: {
                    return FilterActivity.this.getResources().getString(R.string.filter_user);
                }
                case 2: {
                    return FilterActivity.this.getResources().getString(R.string.filter_topic);
                }
                case 3: {
                    return FilterActivity.this.getResources().getString(R.string.filter_source);
                }

                default:
                    return "";
            }
        }
    }

    private FilterKeywordFragment getFilterFragment() {
        return ((FilterKeywordFragment) getSupportFragmentManager().findFragmentByTag(FilterKeywordFragment.class.getName()));
    }

    private FilterUserFragment getFilterUserFragment() {
        return ((FilterUserFragment) getSupportFragmentManager().findFragmentByTag(FilterUserFragment.class.getName()));
    }

    private FilterTopicFragment getFilterTopicFragment() {
        return ((FilterTopicFragment) getSupportFragmentManager().findFragmentByTag(FilterTopicFragment.class.getName()));
    }

    private FilterSourceFragment getFilterSourceFragment() {
        return ((FilterSourceFragment) getSupportFragmentManager().findFragmentByTag(FilterSourceFragment.class.getName()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu_filteractivity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            intent = new Intent(this, SettingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.filter_rule) {
            new FilterRuleDialog().show(getSupportFragmentManager(), "");
        }
        return false;
    }

    public static class FilterRuleDialog extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity()).setMessage(getString(R.string.filter_rule_content))
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
        }
    }
}
