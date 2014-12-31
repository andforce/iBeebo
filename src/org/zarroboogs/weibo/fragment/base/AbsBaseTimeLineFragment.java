package org.zarroboogs.weibo.fragment.base;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.utils.Constants;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.MyAnimationListener;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.activity.MainTimeLineActivity;
import org.zarroboogs.weibo.activity.WeiboMainActivity;
import org.zarroboogs.weibo.activity.WriteWeiboActivity;
import org.zarroboogs.weibo.adapter.AbstractAppListAdapter;
import org.zarroboogs.weibo.bean.AsyncTaskLoaderResult;
import org.zarroboogs.weibo.bean.data.DataItem;
import org.zarroboogs.weibo.bean.data.DataListItem;
import org.zarroboogs.weibo.loader.AbstractAsyncNetRequestTaskLoader;
import org.zarroboogs.weibo.loader.DummyLoader;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.asyncdrawable.TimeLineBitmapDownloader;
import org.zarroboogs.weibo.support.lib.LongClickableLinkMovementMethod;
import org.zarroboogs.weibo.support.utils.BundleArgsConstants;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.support.utils.ViewUtility;
import org.zarroboogs.weibo.widget.ListViewMiddleMsgLoadingView;
import org.zarroboogs.weibo.widget.TopTipsView;
import org.zarroboogs.weibo.widget.VelocityListView;
import org.zarroboogs.weibo.widget.pulltorefresh.PullToRefreshBase;
import org.zarroboogs.weibo.widget.pulltorefresh.PullToRefreshListView;
import org.zarroboogs.weibo.widget.pulltorefresh.SoundPullEventListener;

import com.melnykov.fab.FloatingActionButton;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * User: qii Date: 12-8-27 weiciyuan has two kinds of methods to send/receive
 * network request/response asynchronously, one is setRetainInstance(true) +
 * AsyncTask, the other is AsyncTaskLoader Because nested fragment(parent
 * fragment has a viewpager, viewpager has many children fragments, these
 * children fragments are called nested fragment) can't use
 * setRetainInstance(true), at this moment you have to use AsyncTaskLoader to
 * solve Android configuration change(for example: change screen orientation,
 * change system language)
 */
public abstract class AbsBaseTimeLineFragment<T extends DataListItem<?, ?>> extends BaseStateFragment {

	protected PullToRefreshListView mPullToRefreshListView;

	protected TextView empty;

	protected ProgressBar progressBar;

	protected TopTipsView newMsgTipBar;

	protected BaseAdapter timeLineAdapter;
	
	public FloatingActionButton mFab;

	protected View footerView;

	protected static final int DB_CACHE_LOADER_ID = 0;

	protected static final int NEW_MSG_LOADER_ID = 1;

	protected static final int MIDDLE_MSG_LOADER_ID = 2;

	protected static final int OLD_MSG_LOADER_ID = 3;

	protected ActionMode actionMode;

	protected int savedCurrentLoadingMsgViewPositon = NO_SAVED_CURRENT_LOADING_MSG_VIEW_POSITION;

	public static final int NO_SAVED_CURRENT_LOADING_MSG_VIEW_POSITION = -1;

	private int listViewScrollState = -1;

	private boolean canLoadOldData = true;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.listview_layout, container, false);
		buildLayout(inflater, view);
		return view;
	}

	protected void buildLayout(LayoutInflater inflater, View view) {
		empty = ViewUtility.findViewById(view, R.id.empty);
		progressBar = ViewUtility.findViewById(view, R.id.progressbar);
		progressBar.setVisibility(View.GONE);
		mPullToRefreshListView = ViewUtility.findViewById(view, R.id.listView);
		newMsgTipBar = ViewUtility.findViewById(view, R.id.tv_unread_new_message_count_tip_bar);

		getListView().setHeaderDividersEnabled(false);
		getListView().setScrollingCacheEnabled(false);

		footerView = inflater.inflate(R.layout.listview_footer_layout, null);
		getListView().addFooterView(footerView);
		dismissFooterView();
		mFab = ViewUtility.findViewById(view, R.id.absTimeLineFab);
		
		mFab.attachToListView(getListView());
		
		mFab.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Class<?> clzz = null;
				if (SettingUtils.isDebug()) {
					clzz = WriteWeiboActivity.class;
				} else {
					clzz = WeiboMainActivity.class;
				}
				Intent intent = new Intent(getActivity(), clzz);
				intent.putExtra(BundleArgsConstants.ACCOUNT_EXTRA, GlobalContext.getInstance().getAccountBean());
				intent.putExtra(Constants.TOKEN, GlobalContext.getInstance().getSpecialToken());
				intent.putExtra(Constants.ACCOUNT, GlobalContext.getInstance().getAccountBean());
				startActivity(intent);
			}
		});
	}
	

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mPullToRefreshListView.setOnRefreshListener(listViewOnRefreshListener);
		mPullToRefreshListView.setOnLastItemVisibleListener(listViewOnLastItemVisibleListener);
		mPullToRefreshListView.setOnScrollListener(listViewOnScrollListener);
		mPullToRefreshListView.setOnItemClickListener(listViewOnItemClickListener);
		mPullToRefreshListView.setOnPullEventListener(getPullEventListener());
		buildListAdapter();
		if (savedInstanceState != null) {
			savedCurrentLoadingMsgViewPositon = savedInstanceState.getInt("savedCurrentLoadingMsgViewPositon", NO_SAVED_CURRENT_LOADING_MSG_VIEW_POSITION);
		}
		if (savedCurrentLoadingMsgViewPositon != NO_SAVED_CURRENT_LOADING_MSG_VIEW_POSITION && timeLineAdapter instanceof AbstractAppListAdapter) {
			((AbstractAppListAdapter<?>) timeLineAdapter).setSavedMiddleLoadingViewPosition(savedCurrentLoadingMsgViewPositon);
		}
	}

	   public int getListViewScrollState() {
	        return listViewScrollState;
	    }

	    public PullToRefreshListView getPullToRefreshListView() {
	        return mPullToRefreshListView;
	    }

	    public ListView getListView() {
	        return mPullToRefreshListView.getRefreshableView();
	    }
	    

	    public BaseAdapter getAdapter() {
	        return timeLineAdapter;
	    }

	    public TopTipsView getNewMsgTipBar() {
	        return newMsgTipBar;
	    }

	    protected void refreshLayout(T bean) {
	        if (bean != null && bean.getSize() > 0) {
	            // empty.setVisibility(View.INVISIBLE);
	            progressBar.setVisibility(View.INVISIBLE);
	            // listView.setVisibility(View.VISIBLE);
	        } else if (bean == null || bean.getSize() == 0) {
	            // empty.setVisibility(View.VISIBLE);
	            progressBar.setVisibility(View.INVISIBLE);
	            // listView.setVisibility(View.VISIBLE);
	        } else if (bean.getSize() == bean.getTotal_number()) {
	            // empty.setVisibility(View.INVISIBLE);
	            progressBar.setVisibility(View.INVISIBLE);
	            // listView.setVisibility(View.VISIBLE);
	        }
	    }

	    public abstract T getList();

	    protected abstract void onTimeListViewItemClick(AdapterView<?> parent, View view, int position, long id);

	    public void loadNewMsg() {
	        canLoadOldData = true;
	        getLoaderManager().destroyLoader(MIDDLE_MSG_LOADER_ID);
	        getLoaderManager().destroyLoader(OLD_MSG_LOADER_ID);
	        dismissFooterView();
	        getLoaderManager().restartLoader(NEW_MSG_LOADER_ID, null, msgAsyncTaskLoaderCallback);
	    }

	    protected void loadOldMsg(View view) {

	        if (getLoaderManager().getLoader(OLD_MSG_LOADER_ID) != null || !canLoadOldData) {
	            return;
	        }

	        getLoaderManager().destroyLoader(NEW_MSG_LOADER_ID);
	        getPullToRefreshListView().onRefreshComplete();
	        getLoaderManager().destroyLoader(MIDDLE_MSG_LOADER_ID);
	        getLoaderManager().restartLoader(OLD_MSG_LOADER_ID, null, msgAsyncTaskLoaderCallback);
	    }

	    public void loadMiddleMsg(String beginId, String endId, int position) {
	        getLoaderManager().destroyLoader(NEW_MSG_LOADER_ID);
	        getLoaderManager().destroyLoader(OLD_MSG_LOADER_ID);
	        getPullToRefreshListView().onRefreshComplete();
	        dismissFooterView();

	        Bundle bundle = new Bundle();
	        bundle.putString("beginId", beginId);
	        bundle.putString("endId", endId);
	        bundle.putInt("position", position);
	        VelocityListView velocityListView = (VelocityListView) getListView();
	        bundle.putBoolean("towardsBottom", velocityListView.getTowardsOrientation() == VelocityListView.TOWARDS_BOTTOM);
	        getLoaderManager().restartLoader(MIDDLE_MSG_LOADER_ID, bundle, msgAsyncTaskLoaderCallback);

	    }

	    @Override
	    public void onSaveInstanceState(Bundle outState) {
	        super.onSaveInstanceState(outState);
	        outState.putInt("savedCurrentLoadingMsgViewPositon", savedCurrentLoadingMsgViewPositon);
	    }
	    
	private PullToRefreshBase.OnLastItemVisibleListener listViewOnLastItemVisibleListener = new PullToRefreshBase.OnLastItemVisibleListener() {
		@Override
		public void onLastItemVisible() {
			if (getActivity() == null) {
				return;
			}

			if (getLoaderManager().getLoader(OLD_MSG_LOADER_ID) != null) {
				return;
			}

			loadOldMsg(null);
		}
	};

	private PullToRefreshBase.OnRefreshListener<ListView> listViewOnRefreshListener = new PullToRefreshBase.OnRefreshListener<ListView>() {
		@Override
		public void onRefresh(PullToRefreshBase<ListView> refreshView) {
			if (getActivity() == null) {
				return;
			}

			if (getLoaderManager().getLoader(NEW_MSG_LOADER_ID) != null) {
				return;
			}

			loadNewMsg();
		}
	};

	private SoundPullEventListener<ListView> getPullEventListener() {
		SoundPullEventListener<ListView> listener = new SoundPullEventListener<ListView>(getActivity());
		if (SettingUtils.getEnableSound()) {
			listener.addSoundEvent(PullToRefreshBase.State.RELEASE_TO_REFRESH, R.raw.psst1);
			// listener.addSoundEvent(PullToRefreshBase.State.GIVE_UP,
			// R.raw.psst2);
			listener.addSoundEvent(PullToRefreshBase.State.RESET, R.raw.pop);
		}
		return listener;
	}

	private AdapterView.OnItemClickListener listViewOnItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (resetActionMode()) {
				return;
			}

			getListView().clearChoices();
			int headerViewsCount = getListView().getHeaderViewsCount();
			if (isPositionBetweenHeaderViewAndFooterView(position)) {
				int indexInDataSource = position - headerViewsCount;
				DataItem msg = getList().getItem(indexInDataSource);
				if (!isNullFlag(msg)) {
					onTimeListViewItemClick(parent, view, indexInDataSource, id);
				} else {
					String beginId = getList().getItem(indexInDataSource + 1).getId();
					String endId = getList().getItem(indexInDataSource - 1).getId();
					ListViewMiddleMsgLoadingView loadingView = (ListViewMiddleMsgLoadingView) view;
					if (!((ListViewMiddleMsgLoadingView) view).isLoading() && savedCurrentLoadingMsgViewPositon == NO_SAVED_CURRENT_LOADING_MSG_VIEW_POSITION) {
						loadingView.load();
						loadMiddleMsg(beginId, endId, indexInDataSource);
						savedCurrentLoadingMsgViewPositon = indexInDataSource + headerViewsCount;
						if (timeLineAdapter instanceof AbstractAppListAdapter) {
							((AbstractAppListAdapter<?>) timeLineAdapter).setSavedMiddleLoadingViewPosition(savedCurrentLoadingMsgViewPositon);
						}
					}
				}

			} else if (isLastItem(position)) {
				loadOldMsg(view);
			}
		}

		boolean isPositionBetweenHeaderViewAndFooterView(int position) {
			return position - getListView().getHeaderViewsCount() < getList().getSize() && position - getListView().getHeaderViewsCount() >= 0;
		}

		boolean resetActionMode() {
			if (actionMode != null) {
				getListView().clearChoices();
				actionMode.finish();
				actionMode = null;
				return true;
			} else {
				return false;
			}
		}

		boolean isNullFlag(DataItem msg) {
			return msg == null;
		}

		boolean isLastItem(int position) {
			return position - 1 >= getList().getSize();
		}
	};

	private AbsListView.OnScrollListener listViewOnScrollListener = new AbsListView.OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			listViewScrollState = scrollState;
			switch (scrollState) {
			case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
				if (!enableRefreshTime) {
					enableRefreshTime = true;
					getAdapter().notifyDataSetChanged();
				}
				onListViewScrollStop();
				LongClickableLinkMovementMethod.getInstance().setLongClickable(true);
				TimeLineBitmapDownloader.getInstance().setPauseDownloadWork(false);
				TimeLineBitmapDownloader.getInstance().setPauseReadWork(false);

				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
				enableRefreshTime = false;
				LongClickableLinkMovementMethod.getInstance().setLongClickable(false);
				TimeLineBitmapDownloader.getInstance().setPauseDownloadWork(true);
				TimeLineBitmapDownloader.getInstance().setPauseReadWork(true);
				onListViewScrollStateFling();
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				enableRefreshTime = true;
				LongClickableLinkMovementMethod.getInstance().setLongClickable(false);
				TimeLineBitmapDownloader.getInstance().setPauseDownloadWork(true);
				onListViewScrollStateTouchScroll();
				break;
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			onListViewScroll();
		}
	};

	@Override
	public void onPause() {
		super.onPause();
		TimeLineBitmapDownloader.getInstance().setPauseDownloadWork(false);
		TimeLineBitmapDownloader.getInstance().setPauseReadWork(false);

		MobclickAgent.onPageEnd(this.getClass().getName());
	}

	protected void onListViewScrollStop() {

	}

	protected void onListViewScrollStateTouchScroll() {

	}

	protected void onListViewScrollStateFling() {

	}

	protected void onListViewScroll() {

		if (hasActionMode()) {
			int position = getListView().getCheckedItemPosition();
			if (getListView().getFirstVisiblePosition() > position || getListView().getLastVisiblePosition() < position) {
				clearActionMode();
			}
		}

		if (allowLoadOldMsgBeforeReachListBottom() && getListView().getLastVisiblePosition() > 7
				&& getListView().getLastVisiblePosition() > getList().getSize() - 3
				&& getListView().getFirstVisiblePosition() != getListView().getHeaderViewsCount()) {
			loadOldMsg(null);
		}
	}

	protected boolean allowLoadOldMsgBeforeReachListBottom() {
		return true;
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
		progressbar.animate().scaleX(0).scaleY(0).alpha(0.5f).setDuration(300).setListener(new MyAnimationListener(new Runnable() {
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
		if (mPullToRefreshListView != null && getListView().getCheckedItemCount() > 0) {
			getListView().clearChoices();
			if (getAdapter() != null) {
				getAdapter().notifyDataSetChanged();
			}
		}
	}

	public boolean clearActionModeIfOpen() {
		boolean flag = false;
		if (actionMode != null) {
			actionMode.finish();
			actionMode = null;
			flag = true;
		}
		if (mPullToRefreshListView != null && getListView().getCheckedItemCount() > 0) {
			getListView().clearChoices();
			if (getAdapter() != null) {
				getAdapter().notifyDataSetChanged();
			}
		}
		return flag;
	}

	protected abstract void buildListAdapter();

	protected boolean allowRefresh() {
		boolean isNewMsgLoaderLoading = getLoaderManager().getLoader(NEW_MSG_LOADER_ID) != null;
		return getPullToRefreshListView().getVisibility() == View.VISIBLE && !isNewMsgLoaderLoading;
	}

	@Override
	public void onResume() {
		super.onResume();
		getListView().setFastScrollEnabled(SettingUtils.allowFastScroll());
		getAdapter().notifyDataSetChanged();
		
		MobclickAgent.onPageStart(this.getClass().getName());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Loader<T> loader = getLoaderManager().getLoader(NEW_MSG_LOADER_ID);
		if (loader != null) {
			getLoaderManager().initLoader(NEW_MSG_LOADER_ID, null, msgAsyncTaskLoaderCallback);
		}
		loader = getLoaderManager().getLoader(MIDDLE_MSG_LOADER_ID);
		if (loader != null) {
			getLoaderManager().initLoader(MIDDLE_MSG_LOADER_ID, null, msgAsyncTaskLoaderCallback);
		}
		loader = getLoaderManager().getLoader(OLD_MSG_LOADER_ID);
		if (loader != null) {
			getLoaderManager().initLoader(OLD_MSG_LOADER_ID, null, msgAsyncTaskLoaderCallback);
		}
		
		if (getActivity() instanceof MainTimeLineActivity) {
			mFab.setVisibility(View.VISIBLE);
		}else {
			if (mFab != null) {
				mFab.setVisibility(View.GONE);
			}
		}
	}

	public void setActionMode(ActionMode mActionMode) {
		this.actionMode = mActionMode;
	}

	public boolean hasActionMode() {
		return actionMode != null;
	}

	protected void showListView() {
		progressBar.setVisibility(View.INVISIBLE);
	}

	private volatile boolean enableRefreshTime = true;

	public boolean isListViewFling() {
		return !enableRefreshTime;
	}

	protected abstract void newMsgLoaderSuccessCallback(T newValue, Bundle loaderArgs);

	protected abstract void oldMsgLoaderSuccessCallback(T newValue);

	protected void middleMsgLoaderSuccessCallback(int position, T newValue, boolean towardsBottom) {

		if (newValue == null) {
			return;
		}

		if (newValue.getSize() == 0 || newValue.getSize() == 1) {
			getList().getItemList().remove(position);
			getAdapter().notifyDataSetChanged();
			return;
		}
	}

	protected void newMsgLoaderFailedCallback(WeiboException exception) {

	}

	protected void oldMsgLoaderFailedCallback(WeiboException exception) {

	}

	private Loader<AsyncTaskLoaderResult<T>> createNewMsgLoader(int id, Bundle args) {
		Loader<AsyncTaskLoaderResult<T>> loader = onCreateNewMsgLoader(id, args);
		if (loader == null) {
			loader = new DummyLoader<T>(getActivity());
		}
		if (loader instanceof AbstractAsyncNetRequestTaskLoader) {
			((AbstractAsyncNetRequestTaskLoader<T>) loader).setArgs(args);
		}
		return loader;
	}

	private Loader<AsyncTaskLoaderResult<T>> createMiddleMsgLoader(int id, Bundle args, String middleBeginId, String middleEndId, String middleEndTag,
			int middlePosition) {
		Loader<AsyncTaskLoaderResult<T>> loader = onCreateMiddleMsgLoader(id, args, middleBeginId, middleEndId, middleEndTag, middlePosition);
		if (loader == null) {
			loader = new DummyLoader<T>(getActivity());
		}
		return loader;
	}

	private Loader<AsyncTaskLoaderResult<T>> createOldMsgLoader(int id, Bundle args) {
		Loader<AsyncTaskLoaderResult<T>> loader = onCreateOldMsgLoader(id, args);
		if (loader == null) {
			loader = new DummyLoader<T>(getActivity());
		}
		return loader;
	}

	protected Loader<AsyncTaskLoaderResult<T>> onCreateNewMsgLoader(int id, Bundle args) {
		return null;
	}

	protected Loader<AsyncTaskLoaderResult<T>> onCreateMiddleMsgLoader(int id, Bundle args, String middleBeginId, String middleEndId, String middleEndTag,
			int middlePosition) {
		return null;
	}

	protected Loader<AsyncTaskLoaderResult<T>> onCreateOldMsgLoader(int id, Bundle args) {
		return null;
	}

	protected LoaderManager.LoaderCallbacks<AsyncTaskLoaderResult<T>> msgAsyncTaskLoaderCallback = new LoaderManager.LoaderCallbacks<AsyncTaskLoaderResult<T>>() {

		private String middleBeginId = "";

		private String middleEndId = "";

		private int middlePosition = -1;

		private boolean towardsBottom = false;

		@Override
		public Loader<AsyncTaskLoaderResult<T>> onCreateLoader(int id, Bundle args) {
			clearActionMode();
			showListView();
			switch (id) {
			case NEW_MSG_LOADER_ID:
				if (args == null || args.getBoolean(BundleArgsConstants.SCROLL_TO_TOP)) {
					Utility.stopListViewScrollingAndScrollToTop(getListView());
				}
				return createNewMsgLoader(id, args);
			case MIDDLE_MSG_LOADER_ID:
				middleBeginId = args.getString("beginId");
				middleEndId = args.getString("endId");
				middlePosition = args.getInt("position");
				towardsBottom = args.getBoolean("towardsBottom");
				return createMiddleMsgLoader(id, args, middleBeginId, middleEndId, null, middlePosition);
			case OLD_MSG_LOADER_ID:
				showFooterView();
				return createOldMsgLoader(id, args);
			}

			return null;
		}

		@Override
		public void onLoadFinished(Loader<AsyncTaskLoaderResult<T>> loader, AsyncTaskLoaderResult<T> result) {

			T data = result != null ? result.data : null;
			WeiboException exception = result != null ? result.exception : null;
			Bundle args = result != null ? result.args : null;

			switch (loader.getId()) {
			case NEW_MSG_LOADER_ID:
				getPullToRefreshListView().onRefreshComplete();
				refreshLayout(getList());
				if (Utility.isAllNotNull(exception)) {
					newMsgTipBar.setError(exception.getError());
					newMsgLoaderFailedCallback(exception);
				} else {
					newMsgLoaderSuccessCallback(data, args);
				}
				break;
			case MIDDLE_MSG_LOADER_ID:
				if (exception != null) {
					View view = Utility.getListViewItemViewFromPosition(getListView(), savedCurrentLoadingMsgViewPositon);
					ListViewMiddleMsgLoadingView loadingView = (ListViewMiddleMsgLoadingView) view;
					if (loadingView != null) {
						loadingView.setErrorMessage(exception.getError());
					}
				} else {
					middleMsgLoaderSuccessCallback(middlePosition, data, towardsBottom);
					// getAdapter().notifyDataSetChanged();
				}
				savedCurrentLoadingMsgViewPositon = NO_SAVED_CURRENT_LOADING_MSG_VIEW_POSITION;
				if (timeLineAdapter instanceof AbstractAppListAdapter) {
					((AbstractAppListAdapter<?>) timeLineAdapter).setSavedMiddleLoadingViewPosition(savedCurrentLoadingMsgViewPositon);
				}
				break;
			case OLD_MSG_LOADER_ID:
				refreshLayout(getList());

				if (exception != null) {
					showErrorFooterView();
					oldMsgLoaderFailedCallback(exception);
				} else if (data != null) {
					canLoadOldData = data.getSize() > 1;
					oldMsgLoaderSuccessCallback(data);
					getAdapter().notifyDataSetChanged();
					dismissFooterView();
				} else {
					canLoadOldData = false;
					dismissFooterView();
				}
				break;
			}
			getLoaderManager().destroyLoader(loader.getId());
		}

		@Override
		public void onLoaderReset(Loader<AsyncTaskLoaderResult<T>> loader) {

		}
	};

}
