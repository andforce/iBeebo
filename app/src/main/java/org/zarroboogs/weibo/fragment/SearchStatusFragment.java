
package org.zarroboogs.weibo.fragment;

import org.zarroboogs.msrl.widget.MaterialSwipeRefreshLayout;
import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.BrowserWeiboMsgActivity;
import org.zarroboogs.weibo.bean.AsyncTaskLoaderResult;
import org.zarroboogs.weibo.bean.data.SearchStatusListBean;
import org.zarroboogs.weibo.fragment.base.AbsTimeLineFragment;
import org.zarroboogs.weibo.loader.SearchStatusLoader;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.utils.ViewUtility;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;

public class SearchStatusFragment extends AbsTimeLineFragment<SearchStatusListBean> {

    private int page = 1;
    private String searchKey = "";

    private SearchStatusListBean bean = new SearchStatusListBean();

    @Override
    public SearchStatusListBean getDataList() {
        return bean;
    }

    public SearchStatusFragment() {

    }

    public void search(String searchKey) {
    	this.searchKey = searchKey;
//        mPullToRefreshListView.setRefreshing();
        getSwipeRefreshLayout().setRefreshing(true);
        loadNewMsg();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.BEAN, bean);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && bean.getItemList().size() == 0) {
            clearAndReplaceValue((SearchStatusListBean) savedInstanceState.getParcelable(Constants.BEAN));
            timeLineAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialSwipeRefreshLayout materialSwipeRefreshLayout = ViewUtility.findViewById(view, R.id.refresh_layout);
        materialSwipeRefreshLayout.setEnableSount(SettingUtils.getEnableSound());
        materialSwipeRefreshLayout.noMore();
    }

    protected void onTimeListViewItemClick(AdapterView parent, View view, int position, long id) {
        startActivity(BrowserWeiboMsgActivity.newIntent(BeeboApplication.getInstance().getAccountBean(),
                bean.getItem(position), BeeboApplication.getInstance()
                        .getAccessTokenHack()));
    }

    @Override
    protected Loader<AsyncTaskLoaderResult<SearchStatusListBean>> onCreateNewMsgLoader(int id, Bundle args) {
        String token = BeeboApplication.getInstance().getAccessTokenHack();
        String word = searchKey;
        page = 1;
        return new SearchStatusLoader(getActivity(), token, word, String.valueOf(page));
    }

    @Override
    protected Loader<AsyncTaskLoaderResult<SearchStatusListBean>> onCreateOldMsgLoader(int id, Bundle args) {
        String token = BeeboApplication.getInstance().getAccessTokenHack();
        String word = searchKey;
        return new SearchStatusLoader(getActivity(), token, word, String.valueOf(page + 1));
    }

    @Override
    protected void onNewMsgLoaderSuccessCallback(SearchStatusListBean newValue, Bundle loaderArgs) {
        if (newValue != null && getActivity() != null && newValue.getSize() > 0) {
            getDataList().addNewData(newValue);
            getAdapter().notifyDataSetChanged();
            getListView().setSelectionAfterHeaderView();
            getActivity().invalidateOptionsMenu();
        }

    }

    @Override
    protected void onOldMsgLoaderSuccessCallback(SearchStatusListBean newValue) {

        if (newValue != null && newValue.getSize() > 0) {
            getDataList().addOldData(newValue);
            getAdapter().notifyDataSetChanged();
            getActivity().invalidateOptionsMenu();
            page++;
        }
    }
}
