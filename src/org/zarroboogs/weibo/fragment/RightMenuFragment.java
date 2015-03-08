package org.zarroboogs.weibo.fragment;

import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.MainTimeLineActivity;
import org.zarroboogs.weibo.adapter.FriendsTimeLineListNavAdapter;
import org.zarroboogs.weibo.bean.GroupBean;
import org.zarroboogs.weibo.bean.GroupListBean;
import org.zarroboogs.weibo.db.task.GroupDBTask;
import org.zarroboogs.weibo.support.utils.AppEventAction;
import org.zarroboogs.weibo.widget.pulltorefresh.PullToRefreshBase;
import org.zarroboogs.weibo.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import org.zarroboogs.weibo.widget.pulltorefresh.PullToRefreshListView;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

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
import java.util.List;

public class RightMenuFragment extends BaseLoadDataFragment {

	private boolean firstStart = true;

	public static final String SWITCH_GROUP_KEY = "switch_group";
	private PullToRefreshListView mPullToRefreshListView;
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
		if (GlobalContext.getInstance().getGroup() != null) {
			list = GlobalContext.getInstance().getGroup().getLists();
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
			name.add(b.getName());
		}

		String[] valueArray = name.toArray(new String[name.size()]);
		return valueArray;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.right_slidingdrawer_contents, container, false);

		mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.rightGroupListView);
		mPullToRefreshListView.setAdapter(mBaseAdapter);
		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub

				loadGroup();
			}

		});
		mPullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				((MainTimeLineActivity) getActivity()).closeRightDrawer();

				mBaseAdapter.setSelectId(position - 1);
				Intent mIntent = new Intent(AppEventAction.SWITCH_WEIBO_GROUP_BROADCAST);
				mIntent.putExtra(SWITCH_GROUP_KEY, position - 1);
				LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(mIntent);
			}

		});
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		if (GlobalContext.getInstance().getGroup() == null || GlobalContext.getInstance().getGroup().getLists().size() == 0) {
			loadGroup();
		}
	}

	private void loadGroup() {
		RequestParams requestParams = new RequestParams();
		requestParams.add("access_token", GlobalContext.getInstance().getAccountBean().getAccess_token());
		loadData(WeiBoURLs.FRIENDSGROUP_INFO, requestParams);
	}
	
	@Override
	void onLoadDataSucess(String json) {
		// TODO Auto-generated method stub
		Log.d("FETCH_GROUP ", "onLoadDataSucess");

		GroupListBean groupListBean = new Gson().fromJson(json, GroupListBean.class);

		List<GroupBean> groupBeans = groupListBean.getLists();
		if (groupBeans != null) {
			GroupDBTask.update(groupListBean, GlobalContext.getInstance().getCurrentAccountId());
			GlobalContext.getInstance().setGroup(groupListBean);
			for (GroupBean groupBean : groupBeans) {
				Log.d("FETCH_GROUP ", "" + groupBean.getName());
			}
			String[] newGroup = buildListNavData(groupBeans);
			mBaseAdapter.refresh(newGroup);
		}
		mPullToRefreshListView.onRefreshComplete();

	}

	@Override
	void onLoadDataFailed(String errorStr) {
		// TODO Auto-generated method stub
		Log.d("FETCH_GROUP ", "onLoadDataFailed");
		mPullToRefreshListView.onRefreshComplete();
	}

	@Override
	void onLoadDataStart() {
		// TODO Auto-generated method stub
		Log.d("FETCH_GROUP ", "onLoadDataStart");
		mPullToRefreshListView.setRefreshing();
	}
}
