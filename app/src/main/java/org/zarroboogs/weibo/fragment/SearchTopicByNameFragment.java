
package org.zarroboogs.weibo.fragment;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.BrowserWeiboMsgActivity;
import org.zarroboogs.weibo.activity.WriteWeiboWithAppSrcActivity;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.bean.AsyncTaskLoaderResult;
import org.zarroboogs.weibo.bean.data.TopicResultListBean;
import org.zarroboogs.weibo.dao.TopicDao;
import org.zarroboogs.weibo.fragment.base.AbsTimeLineFragment;
import org.zarroboogs.weibo.loader.SearchTopicByNameLoader;
import org.zarroboogs.weibo.support.utils.Utility;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class SearchTopicByNameFragment extends AbsTimeLineFragment<TopicResultListBean> {

    private String q;

    // page 0 and page 1 data is same
    private int page = 1;

    private TopicResultListBean bean = new TopicResultListBean();

    private FollowTopicTask followTopicTask;

    private UnFollowTopicTask unFollowTopicTask;

    @Override
    public TopicResultListBean getDataList() {
        return bean;
    }

    public SearchTopicByNameFragment() {

    }

    public SearchTopicByNameFragment(String q) {
        this.q = q;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Utility.cancelTasks(followTopicTask, unFollowTopicTask);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("q", q);
        outState.putInt("page", page);
        outState.putParcelable(Constants.BEAN, bean);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        switch (getCurrentState(savedInstanceState)) {
            case FIRST_TIME_START:
                getSwipeRefreshLayout().setRefreshing(true);
                loadNewMsg();
                break;
            case SCREEN_ROTATE:
                // nothing
                break;
            case ACTIVITY_DESTROY_AND_CREATE:
                q = savedInstanceState.getString("q");
                page = savedInstanceState.getInt("page");
                getDataList().addNewData((TopicResultListBean) savedInstanceState.getParcelable(Constants.BEAN));
                getAdapter().notifyDataSetChanged();
                break;
        }
    }

    @Override
    protected void onNewMsgLoaderSuccessCallback(TopicResultListBean newValue, Bundle loaderArgs) {
        if (newValue != null && getActivity() != null && newValue.getSize() > 0) {
            getDataList().addNewData(newValue);
            getAdapter().notifyDataSetChanged();
            getListView().setSelectionAfterHeaderView();
            buildActionBatSubtitle();
        }
    }

    @Override
    protected void onOldMsgLoaderSuccessCallback(TopicResultListBean newValue) {
        if (newValue != null && newValue.getSize() > 0) {
            getDataList().addOldData(newValue);
            page++;
            buildActionBatSubtitle();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.actionbar_menu_searchtopicbynamefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_refresh) {
            getSwipeRefreshLayout().setRefreshing(true);
            loadNewMsg();
        }



//		if (itemId == R.id.menu_write) {
//			Intent intent = new Intent(getActivity(), WriteWeiboWithAppSrcActivity.class);
//			intent.putExtra(Constants.TOKEN, BeeboApplication.getInstance().getAccessTokenHack());
//			intent.putExtra(Constants.ACCOUNT, BeeboApplication.getInstance().getAccountBean());
//			intent.putExtra("content", "#" + q + "#");
//			startActivity(intent);
//		} else if (itemId == R.id.menu_refresh) {
//            getSwipeRefreshLayout().setRefreshing(true);
//			loadNewMsg();
//		} else if (itemId == R.id.menu_follow_topic) {
//			if (Utility.isTaskStopped(followTopicTask)) {
//			    followTopicTask = new FollowTopicTask();
//			    followTopicTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
//			}
//		} else if (itemId == R.id.menu_unfollow_topic) {
//			if (Utility.isTaskStopped(unFollowTopicTask)) {
//			    unFollowTopicTask = new UnFollowTopicTask();
//			    unFollowTopicTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
//			}
//		}
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onTimeListViewItemClick(AdapterView parent, View view, int position, long id) {
        startActivity(BrowserWeiboMsgActivity.newIntent(BeeboApplication.getInstance().getAccountBean(), bean.getItemList()
                .get(position), BeeboApplication
                .getInstance().getAccessTokenHack()));
    }

    private void buildActionBatSubtitle() {
        int newSize = bean.getTotal_number();
        String number = bean.getSize() + "/" + newSize;
        // getActivity().getActionBar().setSubtitle(number);
    }

    private class FollowTopicTask extends MyAsyncTask<Void, Boolean, Boolean> {

        WeiboException e;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                return new TopicDao(BeeboApplication.getInstance().getAccessTokenHack()).follow(q);
            } catch (WeiboException e) {
                this.e = e;
                cancel(true);
            }
            return false;
        }

        @Override
        protected void onCancelled(Boolean aBoolean) {
            super.onCancelled(aBoolean);
            if (Utility.isAllNotNull(getActivity(), this.e)) {
                Toast.makeText(getActivity(), e.getError(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (getActivity() == null) {
                return;
            }
            if (aBoolean) {
                Toast.makeText(getActivity(), getString(R.string.follow_topic_successfully), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), getString(R.string.follow_topic_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class UnFollowTopicTask extends MyAsyncTask<Void, Boolean, Boolean> {

        WeiboException e;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                return new TopicDao(BeeboApplication.getInstance().getAccessTokenHack()).destroy(q);
            } catch (WeiboException e) {
                this.e = e;
                cancel(true);
            }
            return false;
        }

        @Override
        protected void onCancelled(Boolean aBoolean) {
            super.onCancelled(aBoolean);
            if (Utility.isAllNotNull(getActivity(), this.e)) {
                Toast.makeText(getActivity(), e.getError(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (getActivity() == null) {
                return;
            }
            if (aBoolean) {
                Toast.makeText(getActivity(), getString(R.string.unfollow_topic_successfully), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), getString(R.string.unfollow_topic_failed), Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected Loader<AsyncTaskLoaderResult<TopicResultListBean>> onCreateNewMsgLoader(int id, Bundle args) {
        String token = BeeboApplication.getInstance().getAccessTokenHack();
        String word = this.q;
        page = 1;
        return new SearchTopicByNameLoader(getActivity(), token, word, String.valueOf(page));
    }

    @Override
    protected Loader<AsyncTaskLoaderResult<TopicResultListBean>> onCreateOldMsgLoader(int id, Bundle args) {
        String token = BeeboApplication.getInstance().getAccessTokenHack();
        String word = this.q;
        return new SearchTopicByNameLoader(getActivity(), token, word, String.valueOf(page + 1));
    }
}
