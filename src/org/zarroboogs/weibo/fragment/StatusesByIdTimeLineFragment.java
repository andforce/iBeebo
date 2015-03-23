
package org.zarroboogs.weibo.fragment;

import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.BrowserWeiboMsgActivity;
import org.zarroboogs.weibo.bean.AsyncTaskLoaderResult;
import org.zarroboogs.weibo.bean.MessageBean;
import org.zarroboogs.weibo.bean.MessageListBean;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.fragment.base.AbsTimeLineFragment;
import org.zarroboogs.weibo.loader.StatusesByIdLoader;
import org.zarroboogs.weibo.support.utils.AppConfig;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.Loader;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

public class StatusesByIdTimeLineFragment extends AbsTimeLineFragment<MessageListBean> {

    protected UserBean mUserBean;

    protected String mToken;

    private MessageListBean mMessageListBean = new MessageListBean();

    public static StatusesByIdTimeLineFragment newInstance(UserBean userBean, String token) {
        StatusesByIdTimeLineFragment fragment = new StatusesByIdTimeLineFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.USERBEAN, userBean);
        bundle.putString(Constants.TOKEN, token);
        fragment.setArguments(bundle);
        return fragment;
    }

    public StatusesByIdTimeLineFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserBean = getArguments().getParcelable(Constants.USERBEAN);
        mToken = getArguments().getString(Constants.TOKEN);
    }

    @Override
    public MessageListBean getDataList() {
        return mMessageListBean;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        MessageBean msg = (MessageBean) data.getParcelableExtra("msg");
        if (msg != null) {
            for (int i = 0; i < getDataList().getSize(); i++) {
                if (msg.equals(getDataList().getItem(i))) {
                    getDataList().getItem(i).setReposts_count(msg.getReposts_count());
                    getDataList().getItem(i).setComments_count(msg.getComments_count());
                    break;
                }
            }
            getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mUserBean != null && mUserBean.getId() != null
                && mUserBean.getId().equals(GlobalContext.getInstance().getCurrentAccountId())) {
            GlobalContext.getInstance().registerForAccountChangeListener(myProfileInfoChangeListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalContext.getInstance().unRegisterForAccountChangeListener(myProfileInfoChangeListener);
    }

    private GlobalContext.MyProfileInfoChangeListener myProfileInfoChangeListener = new GlobalContext.MyProfileInfoChangeListener() {
        @Override
        public void onChange(UserBean newUserBean) {
            for (MessageBean msg : getDataList().getItemList()) {
                msg.setUser(newUserBean);
            }
            getAdapter().notifyDataSetChanged();
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.BEAN, getDataList());
        outState.putParcelable(Constants.USERBEAN, mUserBean);
        outState.putString(Constants.TOKEN, mToken);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        switch (getCurrentState(savedInstanceState)) {
            case FIRST_TIME_START:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() != null) {
                            getPullToRefreshListView().setRefreshing();
                            loadNewMsg();
                        }

                    }
                }, AppConfig.REFRESH_DELAYED_MILL_SECOND_TIME);
                break;
            case SCREEN_ROTATE:
                // nothing
                refreshLayout(getDataList());
                break;
            case ACTIVITY_DESTROY_AND_CREATE:
                getDataList().replaceData((MessageListBean) savedInstanceState.getParcelable(Constants.BEAN));
                mUserBean = (UserBean) savedInstanceState.getParcelable(Constants.USERBEAN);
                mToken = savedInstanceState.getString(Constants.TOKEN);
                getAdapter().notifyDataSetChanged();
                refreshLayout(getDataList());
                break;
        }

        super.onActivityCreated(savedInstanceState);
    }

    protected void onTimeListViewItemClick(AdapterView parent, View view, int position, long id) {
        startActivityForResult(BrowserWeiboMsgActivity.newIntent(GlobalContext.getInstance().getAccountBean(), getDataList()
                .getItem(position), GlobalContext
                .getInstance().getAccessToken()), 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
		if (itemId == R.id.menu_refresh) {
			getPullToRefreshListView().setRefreshing();
			loadNewMsg();
			return true;
		}
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void newMsgLoaderSuccessCallback(MessageListBean newValue, Bundle loaderArgs) {
        if (getActivity() != null && newValue.getSize() > 0) {
            getDataList().addNewData(newValue);
            getAdapter().notifyDataSetChanged();
            getListView().setSelectionAfterHeaderView();
            getActivity().invalidateOptionsMenu();

        }

    }

    @Override
    protected void oldMsgLoaderSuccessCallback(MessageListBean newValue) {
        if (newValue != null && newValue.getSize() > 1) {
            getDataList().addOldData(newValue);
            getActivity().invalidateOptionsMenu();
        }
    }

    protected Loader<AsyncTaskLoaderResult<MessageListBean>> onCreateNewMsgLoader(int id, Bundle args) {
        String uid = mUserBean.getId();
        String screenName = mUserBean.getScreen_name();
        String sinceId = null;
        if (getDataList().getItemList().size() > 0) {
            sinceId = getDataList().getItemList().get(0).getId();
        }
        return new StatusesByIdLoader(getActivity(), uid, screenName, mToken, sinceId, null);
    }

    protected Loader<AsyncTaskLoaderResult<MessageListBean>> onCreateMiddleMsgLoader(int id, Bundle args,
            String middleBeginId, String middleEndId,
            String middleEndTag, int middlePosition) {
        String uid = mUserBean.getId();
        String screenName = mUserBean.getScreen_name();
        return new StatusesByIdLoader(getActivity(), uid, screenName, mToken, middleBeginId, middleEndId);
    }

    protected Loader<AsyncTaskLoaderResult<MessageListBean>> onCreateOldMsgLoader(int id, Bundle args) {
        String uid = mUserBean.getId();
        String screenName = mUserBean.getScreen_name();
        String maxId = null;

        if (getDataList().getSize() > 0) {
            maxId = getDataList().getItemList().get(getDataList().getSize() - 1).getId();
        }

        return new StatusesByIdLoader(getActivity(), uid, screenName, mToken, null, maxId);
    }
}
