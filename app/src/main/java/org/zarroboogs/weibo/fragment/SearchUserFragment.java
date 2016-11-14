
package org.zarroboogs.weibo.fragment;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import org.zarroboogs.msrl.widget.MaterialSwipeRefreshLayout;
import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.bean.AsyncTaskLoaderResult;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.bean.UserListBean;
import org.zarroboogs.weibo.loader.SearchUserLoader;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.utils.ViewUtility;

import java.util.List;

public class SearchUserFragment extends AbstractUserListFragment {

    private int page = 1;
    private String searchKey = "";

    public SearchUserFragment() {
        super();
    }

    public void search(String searchKey) {
        this.searchKey = searchKey;

//        pullToRefreshListView.setRefreshing();
        mSwipeRefreshLayout.setRefreshing(true);
        loadNewMsg();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // don't need refresh menu

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // getBaseToolbar().setVisibility(View.GONE);
        MaterialSwipeRefreshLayout materialSwipeRefreshLayout = ViewUtility.findViewById(view, R.id.userListSRL);
        materialSwipeRefreshLayout.setEnableSount(SettingUtils.getEnableSound());
        materialSwipeRefreshLayout.noMore();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            clearAndReplaceValue((UserListBean) savedInstanceState.getParcelable(Constants.BEAN));
            getAdapter().notifyDataSetChanged();
        }
        refreshLayout(bean);
    }

    @Override
    protected void oldUserLoaderSuccessCallback(UserListBean newValue) {
        if (newValue != null && newValue.getUsers().size() > 0) {
            List<UserBean> list = newValue.getUsers();
            getList().getUsers().addAll(list);
            page++;
        }
    }

    @Override
    protected Loader<AsyncTaskLoaderResult<UserListBean>> onCreateNewUserLoader(int id, Bundle args) {
        String token = BeeboApplication.getInstance().getAccessTokenHack();
        String word = searchKey;
        page = 1;
        return new SearchUserLoader(getActivity(), token, word, String.valueOf(page));
    }

    @Override
    protected Loader<AsyncTaskLoaderResult<UserListBean>> onCreateOldUserLoader(int id, Bundle args) {
        String token = BeeboApplication.getInstance().getAccessTokenHack();
        String word = searchKey;
        return new SearchUserLoader(getActivity(), token, word, String.valueOf(page + 1));
    }

}
