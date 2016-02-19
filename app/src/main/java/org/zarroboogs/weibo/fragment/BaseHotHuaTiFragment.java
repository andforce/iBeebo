package org.zarroboogs.weibo.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;


public abstract class BaseHotHuaTiFragment extends BaseLoadDataFragment {

    private SharedPreferences mSharedPreference;

    abstract void onGsidLoadSuccess(String gsid);

    abstract void onGsidLoadFailed(String errorStr);

    abstract void onPageSelected();

    abstract public ListView getListView();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mSharedPreference = getActivity().getApplicationContext().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);

    }

    @Override
    void onLoadDataSucess(String json) {
        // TODO Auto-generated method stub

    }

    @Override
    void onLoadDataFailed(String errorStr) {
        // TODO Auto-generated method stub

    }

    @Override
    void onLoadDataStart() {
        // TODO Auto-generated method stub

    }

    public String getGsid() {
        String gsid = mSharedPreference.getString("gsid", "");
        return gsid;
    }

}
