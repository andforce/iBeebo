
package org.zarroboogs.weibo.fragment.base;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Toast;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.IRemoveItem;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.adapter.TimeLineStatusListAdapter;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.bean.MessageBean;
import org.zarroboogs.weibo.bean.data.DataListItem;
import org.zarroboogs.weibo.dao.DestroyStatusDao;
import org.zarroboogs.weibo.support.utils.Utility;

import java.util.ArrayList;

public abstract class AbsTimeLineFragment<T extends DataListItem<MessageBean, ?>> extends AbsBaseTimeLineFragment<T>
        implements IRemoveItem {

    private RemoveTask removeTask;

    protected void showNewMsgToastMessage(DataListItem<MessageBean, ?> newValue) {
        if (newValue != null && getActivity() != null) {
            if (newValue.getSize() == 0) {
                Toast.makeText(getActivity(), getString(R.string.no_new_message), Toast.LENGTH_SHORT).show();
            } else if (newValue.getSize() > 0) {
                Toast.makeText(getActivity(),
                        getString(R.string.total) + newValue.getSize() + getString(R.string.new_messages),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void clearAndReplaceValue(DataListItem<MessageBean, ?> value) {
        getDataList().getItemList().clear();
        getDataList().getItemList().addAll(value.getItemList());
        getDataList().setTotal_number(value.getTotal_number());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * fix android bug,long press a item in the first tab's listview, rotate screen, the item
     * background is still blue(it is checked), but if you test on other tabs' lstview, the item is
     * not checked
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void buildListAdapter() {
        timeLineAdapter = new TimeLineStatusListAdapter(this, (ArrayList<MessageBean>) getDataList().getItemList());
        getListView().setAdapter(timeLineAdapter);
    }

    @Override
    public void removeItem(int position) {
        if (removeTask == null || removeTask.getStatus() == MyAsyncTask.Status.FINISHED) {
            removeTask = new RemoveTask(BeeboApplication.getInstance().getAccessToken(), getDataList().getItemList().get(position)
                    .getId(), position);
            removeTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void removeCancel() {
    }

    class RemoveTask extends MyAsyncTask<Void, Void, Boolean> {

        String token;

        String id;

        int positon;

        WeiboException e;

        public RemoveTask(String token, String id, int positon) {
            this.token = token;
            this.id = id;
            this.positon = positon;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            DestroyStatusDao dao = new DestroyStatusDao(token, id);
            try {
                return dao.destroy();
            } catch (WeiboException e) {
                this.e = e;
                cancel(true);
                return false;
            }
        }

        @Override
        protected void onCancelled(Boolean aBoolean) {
            super.onCancelled(aBoolean);
            if (Utility.isAllNotNull(this.e, getActivity())) {
                Toast.makeText(getActivity(), e.getError(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                //((StatusListAdapter) timeLineAdapter).removeItem(positon);
            }
        }
    }
}
