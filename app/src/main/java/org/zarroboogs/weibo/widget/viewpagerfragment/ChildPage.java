package org.zarroboogs.weibo.widget.viewpagerfragment;

import android.support.v4.app.Fragment;

public class ChildPage {
    private String mFragmentTitle;
    private Fragment mFragment;

    public ChildPage(String title, Fragment f) {
        this.mFragmentTitle = title;
        this.mFragment = f;
    }

    public String getmFragmentTitle() {
        return mFragmentTitle;
    }

    public void setmFragmentTitle(String mFragmentTitle) {
        this.mFragmentTitle = mFragmentTitle;
    }

    public Fragment getmFragment() {
        return mFragment;
    }

    public void setmFragment(Fragment mFragment) {
        this.mFragment = mFragment;
    }


}
