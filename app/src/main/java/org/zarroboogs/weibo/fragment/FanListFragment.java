
package org.zarroboogs.weibo.fragment;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.bean.AsyncTaskLoaderResult;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.bean.UserListBean;
import org.zarroboogs.weibo.loader.FanUserLoader;

import android.os.Bundle;
import android.support.v4.content.Loader;

public class FanListFragment extends AbstractFriendsFanListFragment {

    public static FanListFragment newInstance(UserBean userBean) {
        FanListFragment fragment = new FanListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.USERBEAN, userBean);
        fragment.setArguments(bundle);
        return fragment;
    }

    public FanListFragment() {
        super();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected UserBean getCurrentUser() {
        return getArguments().getParcelable(Constants.USERBEAN);
    }


    @Override
    protected Loader<AsyncTaskLoaderResult<UserListBean>> onCreateNewUserLoader(int id, Bundle args) {
        String token = BeeboApplication.getInstance().getAccessTokenHack();
        String cursor = String.valueOf(0);
        return new FanUserLoader(getActivity(), token, getCurrentUser().getId(), cursor);
    }

    @Override
    protected Loader<AsyncTaskLoaderResult<UserListBean>> onCreateOldUserLoader(int id, Bundle args) {

        if (getList().getUsers().size() > 0 && Integer.valueOf(getList().getNext_cursor()) == 0) {
            return null;
        }

        String token = BeeboApplication.getInstance().getAccessTokenHack();
        String cursor = String.valueOf(bean.getNext_cursor());

        return new FanUserLoader(getActivity(), token, getCurrentUser().getId(), cursor);
    }

}
