package org.zarroboogs.weibo.fragment;

import org.zarroboogs.msrl.widget.MaterialSwipeRefreshLayout;
import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.weibo.BeeboApplication;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.MainTimeLineActivity;
import org.zarroboogs.weibo.adapter.FriendsTimeLineListNavAdapter;
import org.zarroboogs.weibo.bean.GroupBean;
import org.zarroboogs.weibo.bean.GroupListBean;
import org.zarroboogs.weibo.db.task.GroupDBTask;
import org.zarroboogs.weibo.support.utils.AppEventAction;
import org.zarroboogs.weibo.support.utils.ViewUtility;


import com.google.gson.Gson;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RightMenuFragment extends BaseLoadDataFragment {

	private boolean firstStart = true;

	public static final String SWITCH_GROUP_KEY = "switch_group";

    private MaterialSwipeRefreshLayout mSwitchRefreshLayout;
	private FriendsTimeLineListNavAdapter mBaseAdapter;

	public static RightMenuFragment newInstance() {
		RightMenuFragment fragment = new RightMenuFragment();
		fragment.setArguments(new Bundle());
		return fragment;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("firstStart", firstStart);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			firstStart = savedInstanceState.getBoolean("firstStart");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		List<GroupBean> list = new ArrayList<GroupBean>();
		if (BeeboApplication.getInstance().getGroup() != null) {
			list = BeeboApplication.getInstance().getGroup().getLists();
		} else {
			list = new ArrayList<GroupBean>();
		}

		mBaseAdapter = new FriendsTimeLineListNavAdapter(getActivity(), buildListNavData(list));
	}

	private String[] buildListNavData(List<GroupBean> list) {
		List<String> name = new ArrayList<String>();

		name.add(getString(R.string.all_people));
		name.add(getString(R.string.bilateral));

		for (GroupBean b : list) {
            if (b.getMember_count() != 0) {
                name.add(b.getName());
            }
		}

		String[] valueArray = name.toArray(new String[name.size()]);
		return valueArray;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.right_slidingdrawer_contents, container, false);

        mSwitchRefreshLayout = ViewUtility.findViewById(view,R.id.rightMenuGroupSRL);
		mSwitchRefreshLayout.setOnlyPullRefersh();
        ListView mPullToRefreshListView  = ViewUtility.findViewById(view,R.id.rightGroupListView);
		mPullToRefreshListView.setAdapter(mBaseAdapter);
		mSwitchRefreshLayout.setOnRefreshLoadMoreListener(new MaterialSwipeRefreshLayout.OnRefreshLoadMoreListener() {
			@Override
			public void onRefresh() {
				loadGroup();
			}

			@Override
			public void onLoadMore() {

			}
		});

		mPullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				((MainTimeLineActivity) getActivity()).closeRightDrawer();
				mBaseAdapter.setSelectId(position);
				Intent mIntent = new Intent(AppEventAction.SWITCH_WEIBO_GROUP_BROADCAST);
				mIntent.putExtra(SWITCH_GROUP_KEY, position);
				LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(mIntent);
			}

		});
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		if (BeeboApplication.getInstance().getGroup() == null || BeeboApplication.getInstance().getGroup().getLists().size() == 0) {
			loadGroup();
		}
	}

	private void loadGroup() {
		Map<String, String> requestParams = new HashMap<>();
		requestParams.put("access_token", BeeboApplication.getInstance().getAccountBean().getAccess_token_hack());
		loadData(WeiBoURLs.FRIENDSGROUP_INFO, requestParams);
	}
	
	@Override
	void onLoadDataSucess(String json) {
		// TODO Auto-generated method stub
		Log.d("FETCH_GROUP ", "onLoadDataSucess");

		GroupListBean groupListBean = new Gson().fromJson(json, GroupListBean.class);

		List<GroupBean> groupBeans = groupListBean.getLists();
		if (groupBeans != null) {
			GroupDBTask.update(groupListBean, BeeboApplication.getInstance().getCurrentAccountId());
			BeeboApplication.getInstance().setGroup(groupListBean);
			for (GroupBean groupBean : groupBeans) {
				Log.d("FETCH_GROUP ", "" + groupBean.getName());
			}
			String[] newGroup = buildListNavData(groupBeans);
			mBaseAdapter.refresh(newGroup);
		}
        mSwitchRefreshLayout.setRefreshing(false);

	}

	@Override
	void onLoadDataFailed(String errorStr) {
		// TODO Auto-generated method stub
		Log.d("FETCH_GROUP ", "onLoadDataFailed");
        mSwitchRefreshLayout.setRefreshing(false);
	}

	@Override
	void onLoadDataStart() {
		// TODO Auto-generated method stub
		Log.d("FETCH_GROUP ", "onLoadDataStart");
        mSwitchRefreshLayout.setRefreshing(true);
	}
}
