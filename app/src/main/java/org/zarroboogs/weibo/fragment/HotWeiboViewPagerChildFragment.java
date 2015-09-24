package org.zarroboogs.weibo.fragment;

import java.util.ArrayList;
import java.util.List;

import org.zarroboogs.devutils.DevLog;
import org.zarroboogs.msrl.widget.MaterialSwipeRefreshLayout;
import org.zarroboogs.sinaweiboseniorapi.SeniorUrl;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.activity.BrowserWeiboMsgActivity;
import org.zarroboogs.weibo.adapter.HotWeiboStatusListAdapter;
import org.zarroboogs.weibo.bean.MessageBean;
import org.zarroboogs.weibo.bean.MessageListBean;
import org.zarroboogs.weibo.hot.bean.hotweibo.HotWeiboPicInfos;
import org.zarroboogs.weibo.hot.hean.HotWeiboBean;
import org.zarroboogs.weibo.hot.hean.HotWeiboErrorBean;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.widget.TopTipsView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class HotWeiboViewPagerChildFragment extends BaseHotWeiboFragment {

    private HotWeiboStatusListAdapter mAdapter;
    private List<MessageBean> mMessageBeans = new ArrayList<>();
    private int mPage = 1;
    private String mCtg;
    private boolean mIsFirst = true;



    public HotWeiboViewPagerChildFragment() {
        super();
    }

    @SuppressLint("ValidFragment")
    public HotWeiboViewPagerChildFragment(String ctg) {
        super();

        this.mCtg = ctg;
        if (this.mCtg.equals("8999")) {
            this.mCtg = "-1";
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getListView().setFastScrollEnabled(SettingUtils.allowFastScroll());
    }

    @Override
    public void scrollToTop() {
        Utility.stopListViewScrollingAndScrollToTop(getListView());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);

        getListView().setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent intent = BrowserWeiboMsgActivity.newIntent(BeeboApplication.getInstance().getAccountBean(),
                        (MessageBean) mAdapter.getItem(position), BeeboApplication.getInstance().getAccessToken());
                startActivity(intent);
            }
        });

        getSwipeRefreshLayout().setEnableSount(SettingUtils.getEnableSound());
        getSwipeRefreshLayout().setOnRefreshLoadMoreListener(new MaterialSwipeRefreshLayout.OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh() {
                long uid = Long.valueOf(BeeboApplication.getInstance().getAccountBean().getUid());
                String url = SeniorUrl.hotWeiboApi(BeeboApplication.getInstance().getAccountBean().getGsid(), mCtg, mPage, uid);
                DevLog.printLog("HotWeiboFragment_get: ", url);
                loadData(url);
            }

            @Override
            public void onLoadMore() {

            }
        });

        getSwipeRefreshLayout().noMore();

    }

    @Override
    protected void buildListAdapter() {
        // TODO Auto-generated method stub
        mAdapter = new HotWeiboStatusListAdapter(this, mMessageBeans, getListView(), true, false);
        mAdapter.setTopTipBar(newMsgTipBar);
        timeLineAdapter = mAdapter;
        getListView().setAdapter(timeLineAdapter);
    }

    @Override
    void onLoadDataSucess(String json) {
        // TODO Auto-generated method stub
        mPage++;
        String jsonStr = json.replaceAll("\"geo\":\"\"", "\"geo\": {}").replace("},\"mblogid\":", "],\"mblogid\":").replace("\"pic_infos\":{", "\"pic_infos\":[").replaceAll("\"[A-Za-z0-9]{32}\":", "").replace("{\"ad\":{\"url_marked\":true}]", "{\"ad\":{\"url_marked\":true}}");
        Utility.printLongLog("READ_JSON_DONE", jsonStr);

        Gson gson = new Gson();

        HotWeiboErrorBean error = gson.fromJson(jsonStr, HotWeiboErrorBean.class);
        if (error != null && TextUtils.isEmpty(error.getErrmsg())) {
            HotWeiboBean result = gson.fromJson(jsonStr, new TypeToken<HotWeiboBean>() {
            }.getType());


            MessageListBean mslBean = result.getMessageListBean();
            getDataList().addNewData(mslBean);
            List<MessageBean> list = result.getMessageBeans();


            for (MessageBean messageBean : list) {
                List<HotWeiboPicInfos> picInfos = messageBean.getPic_infos();
                DevLog.printLog("getOriginal_pic ", "HotWeiboPicInfos:" + picInfos.size() + "  Original: " + messageBean.getOriginal_pic() + "     Middle: " + messageBean.getBmiddle_pic());
            }
            if (SettingUtils.isReadStyleEqualWeibo()) {
                newMsgTipBar.setValue(mslBean, true);
                mAdapter.addNewData(list);
                mAdapter.notifyDataSetChanged();
            } else {
                addNewDataAndRememberPosition(list, mslBean);
            }

        } else {
            DevLog.printLog("ERROR", error.getErrmsg());
        }

        getSwipeRefreshLayout().setRefreshing(false);
    }

    private void addNewDataAndRememberPosition(final List<MessageBean> newValue, final MessageListBean mb) {

        int initSize = getListView().getCount();

        if (getActivity() != null && newValue.size() > 0) {
            mAdapter.addNewData(newValue);
            int index = getListView().getFirstVisiblePosition();
            mAdapter.notifyDataSetChanged();
            int finalSize = getListView().getCount();
            final int positionAfterRefresh = index + finalSize - initSize + getListView().getHeaderViewsCount();
            // use 1 px to show newMsgTipBar
            Utility.setListViewSelectionFromTop(getListView(), positionAfterRefresh, 1, new Runnable() {

                @Override
                public void run() {
                    newMsgTipBar.setValue(mb, false);
                    newMsgTipBar.setType(TopTipsView.Type.AUTO);
                }
            });

        }

    }

    @Override
    void onLoadDataFailed(String errorStr) {

    }

    @Override
    void onLoadDataStart() {


    }

    @Override
    void onGsidLoadSuccess(String gsid) {
        long uid = Long.valueOf(BeeboApplication.getInstance().getAccountBean().getUid());
        loadData(SeniorUrl.hotWeiboApi(BeeboApplication.getInstance().getAccountBean().getGsid(), mCtg, mPage, uid));
    }

    @Override
    void onGsidLoadFailed(String errorStr) {

    }

    @Override
    void onPageSelected() {
        if (mIsFirst){
            mIsFirst = false;
            DevLog.printLog("onViewPageSelected", "onPageSelected-----");
            long uid = Long.valueOf(BeeboApplication.getInstance().getAccountBean().getUid());
            loadData(SeniorUrl.hotWeiboApi(BeeboApplication.getInstance().getAccountBean().getGsid(), mCtg, mPage, uid));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsFirst = true;
    }
}
