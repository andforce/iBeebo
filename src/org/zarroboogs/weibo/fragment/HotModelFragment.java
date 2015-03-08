package org.zarroboogs.weibo.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.weibo.MyAnimationListener;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.HotModelActivity;
import org.zarroboogs.weibo.adapter.HotHuaTiAdapter;
import org.zarroboogs.weibo.adapter.HotModelAdapter;
import org.zarroboogs.weibo.adapter.HotModelAdapter.OnModelDetailonClickListener;
import org.zarroboogs.weibo.fragment.base.BaseStateFragment;
import org.zarroboogs.weibo.hot.bean.model.HotModel;
import org.zarroboogs.weibo.hot.bean.model.HotModelCards;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.asyncdrawable.MsgDetailReadWorker;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.widget.pulltorefresh.PullToRefreshBase;
import org.zarroboogs.weibo.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import org.zarroboogs.weibo.widget.pulltorefresh.PullToRefreshListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HotModelFragment extends BaseHotFragment implements OnModelDetailonClickListener {

    private MsgDetailReadWorker picTask;
    
    private ListView listView;

    private HotModelAdapter adapter;


    private static final int OLD_REPOST_LOADER_ID = 4;

    private View footerView;

    private ActionMode actionMode;

    private boolean canLoadOldRepostData = true;

    private int mPage = 1;
    
    private AsyncHttpClient mAsyncHttoClient = new AsyncHttpClient();

    private PullToRefreshListView pullToRefreshListView;
    @Override
    public void onResume() {
        super.onResume();
        getListView().setFastScrollEnabled(SettingUtils.allowFastScroll());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout swipeFrameLayout = (RelativeLayout) inflater.inflate(R.layout.hotweibo_fragment_layout,container, false);
        
        pullToRefreshListView = (PullToRefreshListView) swipeFrameLayout.findViewById(R.id.pullToFreshView);

        pullToRefreshListView.setOnLastItemVisibleListener(onLastItemVisibleListener);
//        pullToRefreshListView.setOnScrollListener(listViewOnScrollListener);
        pullToRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				loadData(WeiBoURLs.hotModel("4u8Kc2373x4U9rFAXPfxc7SC21d", mPage));
				refreshView.setRefreshing();
			}
        	
		});
        listView = pullToRefreshListView.getRefreshableView();


        footerView = inflater.inflate(R.layout.listview_footer_layout, null);
        listView.addFooterView(footerView);
        dismissFooterView();

//        repostTab.setOnClickListener(new RepostTabOnClickListener());

        listView.setFooterDividersEnabled(false);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        // listView.setOnItemLongClickListener(commentOnItemLongClickListener);

//        initView(swipeFrameLayout, savedInstanceState);
        adapter = new HotModelAdapter(this.getActivity());
        adapter.setOnModelDetailonClickListener(this);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setHeaderDividersEnabled(false);

		loadData(WeiBoURLs.hotModel("4u8Kc2373x4U9rFAXPfxc7SC21d", mPage));
		pullToRefreshListView.setRefreshing();
		
        return swipeFrameLayout;
    }

    protected void showFooterView() {
        View view = footerView.findViewById(R.id.loading_progressbar);
        view.setVisibility(View.VISIBLE);
        view.setScaleX(1.0f);
        view.setScaleY(1.0f);
        view.setAlpha(1.0f);
        footerView.findViewById(R.id.laod_failed).setVisibility(View.GONE);
    }

    protected void dismissFooterView() {
        final View progressbar = footerView.findViewById(R.id.loading_progressbar);
        progressbar.animate().scaleX(0).scaleY(0).alpha(0.5f).setDuration(300)
                .setListener(new MyAnimationListener(new Runnable() {
                    @Override
                    public void run() {
                        progressbar.setVisibility(View.GONE);
                    }
                }));
        footerView.findViewById(R.id.laod_failed).setVisibility(View.GONE);
    }

    protected void showErrorFooterView() {
        View view = footerView.findViewById(R.id.loading_progressbar);
        view.setVisibility(View.GONE);
        TextView tv = ((TextView) footerView.findViewById(R.id.laod_failed));
        tv.setVisibility(View.VISIBLE);
    }

    public void clearActionMode() {
        if (actionMode != null) {

            actionMode.finish();
            actionMode = null;
        }
        if (getListView() != null && getListView().getCheckedItemCount() > 0) {
            getListView().clearChoices();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private ListView getListView() {
        return listView;
    }

    public void setActionMode(ActionMode mActionMode) {
        this.actionMode = mActionMode;
    }

    public boolean hasActionMode() {
        return actionMode != null;
    }

    private boolean resetActionMode() {
        if (actionMode != null) {
            getListView().clearChoices();
            actionMode.finish();
            actionMode = null;
            return true;
        } else {
            return false;
        }
    }






    private PullToRefreshBase.OnLastItemVisibleListener onLastItemVisibleListener = new PullToRefreshBase.OnLastItemVisibleListener() {
        @Override
        public void onLastItemVisible() {
//        	if (msg.getReposts_count() > 0 && repostList.size() > 0) {
//                loadOldRepostData();
//            }
        }
    };


    private class EmptyHeaderOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
        	loadData(WeiBoURLs.hotModel("4u8Kc2373x4U9rFAXPfxc7SC21d", mPage));
        }
    }



    private void addNewDataAndRememberPosition(final List<HotModelCards> newValue) {

        int initSize = getListView().getCount();

        if (getActivity() != null) {
            adapter.addNewData(newValue);
            int index = getListView().getFirstVisiblePosition();
            adapter.notifyDataSetChanged();
            int finalSize = getListView().getCount();
            final int positionAfterRefresh = index + finalSize - initSize + getListView().getHeaderViewsCount();
            // use 1 px to show newMsgTipBar
            Utility.setListViewSelectionFromTop(getListView(), positionAfterRefresh, 1, new Runnable() {

                @Override
                public void run() {

                }
            });

        }

    }
    

    public void loadOldRepostData() {
        if (getLoaderManager().getLoader(OLD_REPOST_LOADER_ID) != null || !canLoadOldRepostData) {
            return;
        }
        showFooterView();

    }

	@Override
	public void onModelDetailClick(HotModelCards cards) {
		// TODO Auto-generated method stub
		((HotModelActivity)getActivity()).switch2DetailFragemt(cards.getExtparam());
	}

	@Override
	void onLoadDataSucess(String json) {
		// TODO Auto-generated method stub
		mPage++;
		org.zarroboogs.weibo.support.utils.Utility.printLongLog("READ_JSON_DONE-GET_DATE_FROM_NET", json);

		HotModel hotModel = new Gson().fromJson(json, HotModel.class);
		
		addNewDataAndRememberPosition(hotModel.getCards());
		
		pullToRefreshListView.onRefreshComplete();
	}

	@Override
	void onLoadDataFailed(String errorStr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void onLoadDataStart() {
		// TODO Auto-generated method stub
		
	}

}
