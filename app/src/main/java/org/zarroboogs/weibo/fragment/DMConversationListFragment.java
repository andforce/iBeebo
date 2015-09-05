
package org.zarroboogs.weibo.fragment;

import org.zarroboogs.keyboardlayout.KeyboardRelativeLayout;
import org.zarroboogs.keyboardlayout.OnKeyboardStateChangeListener;
import org.zarroboogs.keyboardlayout.smilepicker.SmileyPicker;
import org.zarroboogs.msrl.widget.MaterialSwipeRefreshLayout;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.BeeboApplication;
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
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.utils.AppConfig;
import org.zarroboogs.weibo.support.utils.SmileyPickerUtility;
import org.zarroboogs.weibo.support.utils.ViewUtility;

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
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;

public class DMConversationListFragment extends AbsBaseTimeLineFragment<DMListBean> {

    private UserBean userBean;

    private int page = 1;

    private DMListBean bean = new DMListBean();

    private ListView mPullToRefreshListView;

    private MaterialEditText et;

    private SmileyPicker smiley;

    private LinearLayout mContainer;

    private MaterialSwipeRefreshLayout mSwipeRefreshLayout;

    private KeyboardRelativeLayout mRootKeyBoardLayout;

    private boolean isSmileClicked = false;

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
                break;
            case ACTIVITY_DESTROY_AND_CREATE:
                getDataList().addNewData((DMListBean) savedInstanceState.getParcelable(Constants.BEAN));
                userBean = savedInstanceState.getParcelable(Constants.USERBEAN);
                page = savedInstanceState.getInt("page");
                getAdapter().notifyDataSetChanged();
                break;
        }

    }

    @Override
    protected void onTimeListViewItemClick(AdapterView parent, View view, int position, long id) {

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //
        empty = (TextView) view.findViewById(R.id.empty);
        // dirty hack.....in other list, progressbar is used to indicate loading
        // local data; but in this list,
        // use a progressbar to indicate loading new data first time, maybe be
        // refactored at 0.50 version
        mSwipeRefreshLayout = ViewUtility.findViewById(view, R.id.dmConversationSRL);
        mSwipeRefreshLayout.setEnableSount(SettingUtils.getEnableSound());
        mSwipeRefreshLayout.setOnlyPullRefersh();

        mPullToRefreshListView = ViewUtility.findViewById(view,R.id.listView);
//        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

        mRootKeyBoardLayout = ViewUtility.findViewById(view, R.id.root_layout);

        mSwipeRefreshLayout.setOnRefreshLoadMoreListener(new MaterialSwipeRefreshLayout.OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh() {
                loadOldMsg(null);
            }

            @Override
            public void onLoadMore() {
                loadOldMsg(null);
            }
        });

        mPullToRefreshListView.setScrollingCacheEnabled(false);
        mPullToRefreshListView.setHeaderDividersEnabled(false);
        mPullToRefreshListView.setStackFromBottom(true);

        dismissFooterView();

        et = (MaterialEditText) view.findViewById(R.id.content);
        view.findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });

        smiley = (SmileyPicker) view.findViewById(R.id.smiley_picker);
        smiley.setEditText(et);
        mContainer = (LinearLayout) view.findViewById(R.id.container);
        ImageButton emoticon = (ImageButton) view.findViewById(R.id.emoticon);

        emoticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isSmileClicked = true;
                if (mRootKeyBoardLayout.getKeyBoardHelper().isKeyboardShow()) {

                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mContainer.getLayoutParams();
                    params.height = mContainer.getHeight();
                    mContainer.requestLayout();

                    mRootKeyBoardLayout.getKeyBoardHelper().hideKeyboard();

                } else {
                    mRootKeyBoardLayout.getKeyBoardHelper().showKeyboard(et);
                }
            }
        });

        mRootKeyBoardLayout.setOnKeyboardStateListener(new OnKeyboardStateChangeListener() {
            @Override
            public void onKeyBoardShow(int height) {

                if (isSmileClicked){
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mContainer.getLayoutParams();
                    params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
                    mContainer.requestLayout();
                }
            }

            @Override
            public void onKeyBoardHide() {
                if (isSmileClicked){
                    showViewWithAnim(smiley);
                }
            }
        });

        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSmileyPicker(true);
            }
        });

        buildListAdapter();
    }

    private void showViewWithAnim(View view) {
        smiley.setVisibility(View.VISIBLE);

        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
        animation.setDuration(150);
//        animation.setFillAfter(true);

        view.startAnimation(animation);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.dmconversationlistfragment_layout, container, false);

        return view;
    }

    private void showSmileyPicker(boolean showAnimation) {
//        this.smiley.show(getActivity(), showAnimation);
        lockContainerHeight(SmileyPickerUtility.getAppContentHeight(getActivity()));

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
//                this.smiley.hide(getActivity());

                SmileyPickerUtility.showKeyBoard(et);
                et.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        unlockContainerHeightDelayed();
                    }
                }, 200L);
            } else {
//                this.smiley.hide(getActivity());
                unlockContainerHeightDelayed();
            }
        }

    }

    public boolean isSmileShown(){
        return smiley.isShown();
    }
    public void removeSmile(){
        removeViewWithAnim(smiley);
    }

    private void removeViewWithAnim(View view){
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF,0, Animation.RELATIVE_TO_SELF, 1);
        animation.setDuration(200);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mContainer.getLayoutParams();
                params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
                mContainer.setLayoutParams(params);
                smiley.setVisibility(View.GONE);
                smiley.requestLayout();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);

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
        timeLineAdapter = new DMConversationAdapter(this, getDataList().getItemList(), mPullToRefreshListView);
        mPullToRefreshListView.setAdapter(timeLineAdapter);
    }

    @Override
    protected void onNewMsgLoaderSuccessCallback(DMListBean newValue, Bundle loaderArgs) {
//        dmProgressBar.setVisibility(View.INVISIBLE);

        if (newValue != null && newValue.getSize() > 0 && getActivity() != null) {
            getDataList().addNewData(newValue);
            Collections.sort(getDataList().getItemList(), comparator);
            getAdapter().notifyDataSetChanged();
            mPullToRefreshListView.setSelection(bean.getSize() - 1);
        }

    }

    @Override
    protected void onOldMsgLoaderSuccessCallback(DMListBean newValue) {
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
            SendDMDao dao = new SendDMDao(BeeboApplication.getInstance().getAccessTokenHack(), userBean.getId(), et.getText().toString());
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
//            dmProgressBar.setVisibility(View.VISIBLE);
        }

        getLoaderManager().destroyLoader(OLD_MSG_LOADER_ID);
        dismissFooterView();
        getLoaderManager().restartLoader(NEW_MSG_LOADER_ID, null, msgAsyncTaskLoaderCallback);
    }

    @Override
    protected void loadOldMsg(View view) {
        getLoaderManager().destroyLoader(NEW_MSG_LOADER_ID);
        mSwipeRefreshLayout.setRefreshing(false);
        getLoaderManager().restartLoader(OLD_MSG_LOADER_ID, null, msgAsyncTaskLoaderCallback);
    }

    @Override
    protected Loader<AsyncTaskLoaderResult<DMListBean>> onCreateNewMsgLoader(int id, Bundle args) {
        String token = BeeboApplication.getInstance().getAccessTokenHack();
        page = 1;
        return new DMConversationLoader(getActivity(), token, userBean.getId(), String.valueOf(page));
    }

    @Override
    protected Loader<AsyncTaskLoaderResult<DMListBean>> onCreateOldMsgLoader(int id, Bundle args) {
        String token = BeeboApplication.getInstance().getAccessTokenHack();
        return new DMConversationLoader(getActivity(), token, userBean.getId(), String.valueOf(page + 1));
    }

}
