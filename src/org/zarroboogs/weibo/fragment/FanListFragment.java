
package org.zarroboogs.weibo.fragment;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.bean.AsyncTaskLoaderResult;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.bean.UserListBean;
import org.zarroboogs.weibo.loader.FanUserLoader;
import org.zarroboogs.weibo.ui.actionmenu.MyFanSingleChoiceModeListener;
import org.zarroboogs.weibo.ui.actionmenu.NormalFriendShipSingleChoiceModeListener;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;

/**
 * User: Jiang Qi Date: 12-8-16
 */
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
        getListView().setOnItemLongClickListener(new FanListOnItemLongClickListener());
    }

    @Override
    protected UserBean getCurrentUser() {
        return getArguments().getParcelable(Constants.USERBEAN);
    }

    private class FanListOnItemLongClickListener implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            if (position - 1 < getList().getUsers().size() && position - 1 >= 0) {
                if (actionMode != null) {
                    actionMode.finish();
                    actionMode = null;
                    getListView().setItemChecked(position, true);
                    getAdapter().notifyDataSetChanged();
                    if (getCurrentUser().getId().equals(GlobalContext.getInstance().getCurrentAccountId())) {
                        actionMode = getActivity().startActionMode(
                                new MyFanSingleChoiceModeListener(getListView(), getAdapter(), FanListFragment.this, bean
                                        .getUsers().get(position - 1)));
                    } else {
                        actionMode = getActivity().startActionMode(
                                new NormalFriendShipSingleChoiceModeListener(getListView(), getAdapter(),
                                        FanListFragment.this, bean.getUsers().get(
                                                position - 1)));
                    }
                    return true;
                } else {
                    getListView().setItemChecked(position, true);
                    getAdapter().notifyDataSetChanged();
                    if (getCurrentUser().getId().equals(GlobalContext.getInstance().getCurrentAccountId())) {
                        actionMode = getActivity().startActionMode(
                                new MyFanSingleChoiceModeListener(getListView(), getAdapter(), FanListFragment.this, bean
                                        .getUsers().get(position - 1)));
                    } else {
                        actionMode = getActivity().startActionMode(
                                new NormalFriendShipSingleChoiceModeListener(getListView(), getAdapter(),
                                        FanListFragment.this, bean.getUsers().get(
                                                position - 1)));
                    }
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    protected Loader<AsyncTaskLoaderResult<UserListBean>> onCreateNewUserLoader(int id, Bundle args) {
        String token = GlobalContext.getInstance().getAccessToken();
        String cursor = String.valueOf(0);
        return new FanUserLoader(getActivity(), token, getCurrentUser().getId(), cursor);
    }

    @Override
    protected Loader<AsyncTaskLoaderResult<UserListBean>> onCreateOldUserLoader(int id, Bundle args) {

        if (getList().getUsers().size() > 0 && Integer.valueOf(getList().getNext_cursor()) == 0) {
            return null;
        }

        String token = GlobalContext.getInstance().getAccessToken();
        String cursor = String.valueOf(bean.getNext_cursor());

        return new FanUserLoader(getActivity(), token, getCurrentUser().getId(), cursor);
    }

}
