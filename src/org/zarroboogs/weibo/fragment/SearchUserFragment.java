
package org.zarroboogs.weibo.fragment;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.bean.AsyncTaskLoaderResult;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.bean.UserListBean;
import org.zarroboogs.weibo.loader.SearchUserLoader;

import java.util.List;

/**
 * User: qii Date: 12-11-10
 */
public class SearchUserFragment extends AbstractUserListFragment {

    private int page = 1;

    public SearchUserFragment() {
        super();
    }

    public void search() {
        pullToRefreshListView.setRefreshing();
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
        String token = GlobalContext.getInstance().getAccessTokenHack();
        String word = ((SearchMainParentFragment) getParentFragment()).getSearchWord();
        page = 1;
        return new SearchUserLoader(getActivity(), token, word, String.valueOf(page));
    }

    @Override
    protected Loader<AsyncTaskLoaderResult<UserListBean>> onCreateOldUserLoader(int id, Bundle args) {
        String token = GlobalContext.getInstance().getAccessTokenHack();
        String word = ((SearchMainParentFragment) getParentFragment()).getSearchWord();
        return new SearchUserLoader(getActivity(), token, word, String.valueOf(page + 1));
    }

}
