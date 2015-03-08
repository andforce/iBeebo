
package org.zarroboogs.weibo.fragment;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.adapter.DMConversationAdapter;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.bean.AsyncTaskLoaderResult;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.bean.data.DMBean;
import org.zarroboogs.weibo.bean.data.DMListBean;
import org.zarroboogs.weibo.dao.SendDMDao;
import org.zarroboogs.weibo.dialogfragment.QuickSendProgressFragment;
import org.zarroboogs.weibo.fragment.base.AbsBaseTimeLineFragment;
import org.zarroboogs.weibo.loader.DMConversationLoader;
import org.zarroboogs.weibo.support.utils.AppConfig;
import org.zarroboogs.weibo.support.utils.SmileyPickerUtility;
import org.zarroboogs.weibo.widget.SmileyPicker;
import org.zarroboogs.weibo.widget.pulltorefresh.PullToRefreshBase;
import org.zarroboogs.weibo.widget.pulltorefresh.PullToRefreshListView;

import com.rengwuxian.materialedittext.MaterialEditText;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;

public class DMConversationListFragment extends AbsBaseTimeLineFragment<DMListBean> {

    private UserBean userBean;

    private int page = 1;

    private DMListBean bean = new DMListBean();

    private MaterialEditText et;

    private SmileyPicker smiley;

    private LinearLayout mContainer;

    private ProgressBar dmProgressBar;

    private Comparator<DMBean> comparator = new Comparator<DMBean>() {
        @Override
        public int compare(DMBean a, DMBean b) {
            long aL = a.getIdLong();
            long bL = b.getIdLong();
            int result = 0;
            if (aL > bL) {
                result = -1;
            } else if (aL < bL) {
                result = 1;
            }
            return result;
        }
    };

    @Override
    public DMListBean getDataList() {
        return bean;
    }

    public static DMConversationListFragment newInstance(UserBean userBean) {
        DMConversationListFragment fragment = new DMConversationListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.USERBEAN, userBean);
        fragment.setArguments(bundle);
        return fragment;
    }

    public DMConversationListFragment() {
        // empty
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.BEAN, bean);
        outState.putParcelable(Constants.USERBEAN, userBean);
        outState.putInt("page", page);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        this.userBean = getArguments().getParcelable(Constants.USERBEAN);

        switch (getCurrentState(savedInstanceState)) {
            case FIRST_TIME_START:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() != null) {
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
                getDataList().addNewData((DMListBean) savedInstanceState.getParcelable(Constants.BEAN));
                userBean = (UserBean) savedInstanceState.getParcelable(Constants.USERBEAN);
                page = savedInstanceState.getInt("page");
                getAdapter().notifyDataSetChanged();
                refreshLayout(bean);
                break;
        }

    }

    @Override
    protected void onTimeListViewItemClick(AdapterView parent, View view, int position, long id) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dmconversationlistfragment_layout, container, false);
        empty = (TextView) view.findViewById(R.id.empty);
        // dirty hack.....in other list, progressbar is used to indicate loading
        // local data; but in this list,
        // use a progressbar to indicate loading new data first time, maybe be
        // refactored at 0.50 version
        progressBar = new ProgressBar(getActivity());
        dmProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.listView);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadOldMsg(null);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadNewMsg();
            }
        });
        getListView().setScrollingCacheEnabled(false);
        getListView().setHeaderDividersEnabled(false);
        getListView().setStackFromBottom(true);

        footerView = inflater.inflate(R.layout.listview_footer_layout, null);
        getListView().addFooterView(footerView);
        dismissFooterView();

        et = (MaterialEditText) view.findViewById(R.id.content);
        view.findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });

        ImageButton emoticon = (ImageButton) view.findViewById(R.id.emoticon);
        emoticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (smiley.isShown()) {
                    hideSmileyPicker(true);
                } else {
                    showSmileyPicker(SmileyPickerUtility.isKeyBoardShow(getActivity()));
                }
            }
        });

        smiley = (SmileyPicker) view.findViewById(R.id.smiley_picker);
        smiley.setEditText(getActivity(), (ViewGroup) view.findViewById(R.id.root_layout), et);
        mContainer = (LinearLayout) view.findViewById(R.id.container);
        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSmileyPicker(true);
            }
        });

        buildListAdapter();
        return view;
    }

    private void showSmileyPicker(boolean showAnimation) {
        this.smiley.show(getActivity(), showAnimation);
        lockContainerHeight(SmileyPickerUtility.getAppContentHeight(getActivity()));

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_menu_dmconversationlistfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
		if (itemId == R.id.menu_clear) {
		}
        return super.onOptionsItemSelected(item);
    }

    public void hideSmileyPicker(boolean showKeyBoard) {
        if (this.smiley.isShown()) {
            if (showKeyBoard) {
                // this time softkeyboard is hidden
                // RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams)
                // this.mContainer.getLayoutParams();
                // localLayoutParams.height = smiley.getTop();
                // localLayoutParams.weight = 0.0F;
                this.smiley.hide(getActivity());

                SmileyPickerUtility.showKeyBoard(et);
                et.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        unlockContainerHeightDelayed();
                    }
                }, 200L);
            } else {
                this.smiley.hide(getActivity());
                unlockContainerHeightDelayed();
            }
        }

    }

    private void lockContainerHeight(int paramInt) {
        // RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams)
        // this.mContainer.getLayoutParams();
        // localLayoutParams.height = paramInt;
        // localLayoutParams.weight = 0.0F;
    }

    public void unlockContainerHeightDelayed() {

        // ((RelativeLayout.LayoutParams) mContainer.getLayoutParams()).height = 0;

    }

    private void send() {

        if (TextUtils.isEmpty(et.getText().toString())) {
            et.setError(getString(R.string.content_cant_be_empty));
            return;
        }

        new QuickCommentTask().executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void buildListAdapter() {
        timeLineAdapter = new DMConversationAdapter(this, getDataList().getItemList(), getListView());
        getListView().setAdapter(timeLineAdapter);
    }

    @Override
    protected void newMsgLoaderSuccessCallback(DMListBean newValue, Bundle loaderArgs) {
        dmProgressBar.setVisibility(View.INVISIBLE);

        if (newValue != null && newValue.getSize() > 0 && getActivity() != null) {
            getDataList().addNewData(newValue);
            Collections.sort(getDataList().getItemList(), comparator);
            getAdapter().notifyDataSetChanged();
            getListView().setSelection(bean.getSize() - 1);
        }

    }

    @Override
    protected void oldMsgLoaderSuccessCallback(DMListBean newValue) {
        if (newValue != null && newValue.getSize() > 0) {
            getDataList().addOldData(newValue);
            Collections.sort(getDataList().getItemList(), comparator);
            getAdapter().notifyDataSetChanged();
            page++;
        }
    }

    private class QuickCommentTask extends AsyncTask<Void, Void, Boolean> {

        WeiboException e;

        QuickSendProgressFragment progressFragment = new QuickSendProgressFragment();

        @Override
        protected void onPreExecute() {
            progressFragment.onCancel(new DialogInterface() {

                @Override
                public void cancel() {
                    QuickCommentTask.this.cancel(true);
                }

                @Override
                public void dismiss() {
                    QuickCommentTask.this.cancel(true);
                }
            });

            progressFragment.show(getFragmentManager(), "");

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            SendDMDao dao = new SendDMDao(GlobalContext.getInstance().getSpecialToken(), userBean.getId(), et.getText()
                    .toString());
            try {
                return dao.send();
            } catch (WeiboException e) {
                this.e = e;
                cancel(true);
                return false;
            }
        }

        @Override
        protected void onCancelled(Boolean commentBean) {
            super.onCancelled(commentBean);
            progressFragment.dismissAllowingStateLoss();
            if (this.e != null) {
                Toast.makeText(getActivity(), e.getError(), Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        protected void onPostExecute(Boolean s) {
            progressFragment.dismissAllowingStateLoss();
            if (s != null) {
                et.setText("");
                loadNewMsg();
            } else {
                Toast.makeText(getActivity(), getString(R.string.send_failed), Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);

        }
    }

    public boolean isSmileyPanelClosed() {
        return !smiley.isShown();
    }

    public void closeSmileyPanel() {
        hideSmileyPicker(false);
    }

    @Override
    public void loadNewMsg() {

        if (bean.getSize() == 0) {
            dmProgressBar.setVisibility(View.VISIBLE);
        }

        getLoaderManager().destroyLoader(MIDDLE_MSG_LOADER_ID);
        getLoaderManager().destroyLoader(OLD_MSG_LOADER_ID);
        dismissFooterView();
        getLoaderManager().restartLoader(NEW_MSG_LOADER_ID, null, msgAsyncTaskLoaderCallback);
    }

    @Override
    protected void loadOldMsg(View view) {
        getLoaderManager().destroyLoader(NEW_MSG_LOADER_ID);
        getPullToRefreshListView().onRefreshComplete();
        getLoaderManager().destroyLoader(MIDDLE_MSG_LOADER_ID);
        getLoaderManager().restartLoader(OLD_MSG_LOADER_ID, null, msgAsyncTaskLoaderCallback);
    }

    @Override
    protected Loader<AsyncTaskLoaderResult<DMListBean>> onCreateNewMsgLoader(int id, Bundle args) {
        String token = GlobalContext.getInstance().getSpecialToken();
        page = 1;
        return new DMConversationLoader(getActivity(), token, userBean.getId(), String.valueOf(page));
    }

    @Override
    protected Loader<AsyncTaskLoaderResult<DMListBean>> onCreateOldMsgLoader(int id, Bundle args) {
        String token = GlobalContext.getInstance().getSpecialToken();
        return new DMConversationLoader(getActivity(), token, userBean.getId(), String.valueOf(page + 1));
    }

}
