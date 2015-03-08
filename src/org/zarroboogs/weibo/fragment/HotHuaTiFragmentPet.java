package org.zarroboogs.weibo.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.weibo.MyAnimationListener;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.SearchTopicByNameActivity;
import org.zarroboogs.weibo.adapter.HotHuaTiAdapter;
import org.zarroboogs.weibo.fragment.base.BaseStateFragment;
import org.zarroboogs.weibo.hot.bean.huati.HotHuaTi;
import org.zarroboogs.weibo.hot.bean.huati.HotHuaTiCard;
import org.zarroboogs.weibo.hot.bean.huati.HotHuaTiCardGroup;
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

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HotHuaTiFragmentPet extends BaseHotHuaTiFragment {

    private MsgDetailReadWorker picTask;
    
    private ListView listView;

    private HotHuaTiAdapter adapter;

    private List<HotHuaTiCardGroup> repostList = new ArrayList<HotHuaTiCardGroup>();

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
        pullToRefreshListView.setOnScrollListener(listViewOnScrollListener);
        pullToRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				
				if (TextUtils.isEmpty(getGsid())) {
					loadGsid();
				}else {
					loadData(WeiBoURLs.hotHuatiDog(getGsid(), mPage));
				}
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
        adapter = new HotHuaTiAdapter(this.getActivity());
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setHeaderDividersEnabled(false);

		if (TextUtils.isEmpty(getGsid())) {
			loadGsid();
		}else {
			loadData(WeiBoURLs.hotHuatiDog(getGsid(), mPage));
		}
		
//		pullToRefreshListView.setRefreshing();
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				HotHuaTiCardGroup g = (HotHuaTiCardGroup) (adapter).getItem(position - 1);
                Intent intent = new Intent(getActivity(), SearchTopicByNameActivity.class);
                String str = g.getTitle_sub();
                intent.putExtra("q", str.substring(1, str.length() - 1));
                startActivity(intent);
			}
		});
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

    private AbsListView.OnScrollListener listViewOnScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            if (hasActionMode()) {
                int position = getListView().getCheckedItemPosition();
                if (getListView().getFirstVisiblePosition() > position || getListView().getLastVisiblePosition() < position) {
                    clearActionMode();
                }
            }

            if (getListView().getLastVisiblePosition() > 7
                    && getListView().getFirstVisiblePosition() != getListView().getHeaderViewsCount()) {

            	if (getListView().getLastVisiblePosition() > repostList.size() - 3) {
                    loadOldRepostData();
                }
            }
        }
    };

    private void addNewDataAndRememberPosition(final List<HotHuaTiCardGroup> newValue) {

    	Utility.printLongLog("HUATI_", "newValue Size: " + newValue.size());
    	for (HotHuaTiCardGroup hotHuaTiCardGroup : newValue) {
    		Utility.printLongLog("HUATI_", "HuaTi Text: " + hotHuaTiCardGroup.getDesc1());
		}
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
	void onLoadDataSucess(String json) {
		// TODO Auto-generated method stub
		mPage++;
		String jsonStr = json.replaceAll("\"geo\":\"\"", "\"geo\": {}");
		org.zarroboogs.weibo.support.utils.Utility.printLongLog("READ_JSON_DONE-GET_DATE_FROM_NET", json);
		
		HotHuaTi huati = new Gson().fromJson(jsonStr, new TypeToken<HotHuaTi>() {}.getType());
		List<HotHuaTiCard> cards = huati.getCards();
		
		List<HotHuaTiCardGroup> result = new ArrayList<HotHuaTiCardGroup>();
		for (HotHuaTiCard c : cards) {
			List<HotHuaTiCardGroup> group = c.getCard_group();
			if (group != null) {
				result.addAll(group);
			}
		}
		
		addNewDataAndRememberPosition(result);
		
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

	@Override
	void onGsidLoadSuccess(String gsid) {
		// TODO Auto-generated method stub
		loadData(WeiBoURLs.hotHuatiDog(getGsid(), mPage));
	}

	@Override
	void onGsidLoadFailed(String errorStr) {
		// TODO Auto-generated method stub
		
	}

}
