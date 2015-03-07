package org.zarroboogs.weibo.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.utils.file.FileLocationMethod;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.activity.BrowserWeiboMsgActivity;
import org.zarroboogs.weibo.adapter.HotWeiboStatusListAdapter;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.bean.MessageBean;
import org.zarroboogs.weibo.bean.MessageListBean;
import org.zarroboogs.weibo.fragment.base.AbsBaseTimeLineFragment;
import org.zarroboogs.weibo.hot.hean.HotWeiboBean;
import org.zarroboogs.weibo.hot.hean.HotWeiboErrorBean;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.asyncdrawable.IWeiciyuanDrawable;
import org.zarroboogs.weibo.support.asyncdrawable.MsgDetailReadWorker;
import org.zarroboogs.weibo.support.asyncdrawable.TimeLineBitmapDownloader;
import org.zarroboogs.weibo.support.gallery.GalleryAnimationActivity;
import org.zarroboogs.weibo.support.lib.AnimationRect;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.widget.WeiboDetailImageView;
import org.zarroboogs.weibo.widget.pulltorefresh.PullToRefreshBase;
import org.zarroboogs.weibo.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener;

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
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;

public class HotWeiboFragmentTravel extends AbsBaseTimeLineFragment<MessageListBean> {

    private MsgDetailReadWorker picTask;
    
    private HotWeiboStatusListAdapter adapter;

    private List<MessageBean> repostList = new ArrayList<MessageBean>();
    
    private MessageListBean mMessageListBean = new MessageListBean();

    private static final int OLD_REPOST_LOADER_ID = 4;

    private View footerView;

    private ActionMode actionMode;

    private boolean canLoadOldRepostData = true;

    private int mPage = 1;
    
    private AsyncHttpClient mAsyncHttoClient = new AsyncHttpClient();

    @Override
    public void onResume() {
        super.onResume();
        getListView().setFastScrollEnabled(SettingUtils.allowFastScroll());
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		loadNewRepostData();
        getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Intent intent = BrowserWeiboMsgActivity.newIntent(GlobalContext.getInstance().getAccountBean(), 
						(MessageBean)adapter.getItem(position - 1), GlobalContext.getInstance().getSpecialToken());
				startActivity(intent);
			}
		});
        
        getPullToRefreshListView().setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				loadNewRepostData();
				getPullToRefreshListView().setRefreshing();
			}
		});
	}
    private void displayPictures(final MessageBean msg, final GridLayout layout, WeiboDetailImageView view,
            boolean refreshPic) {

        if (!msg.isMultiPics()) {
            view.setVisibility(View.VISIBLE);
            if (Utility.isTaskStopped(picTask) && refreshPic) {
                picTask = new MsgDetailReadWorker(view, msg);
                picTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                picTask.setView(view);
            }
        } else {
            layout.setVisibility(View.VISIBLE);

            final int count = msg.getPicCount();
            for (int i = 0; i < count; i++) {
                final IWeiciyuanDrawable pic = (IWeiciyuanDrawable) layout.getChildAt(i);
                pic.setVisibility(View.VISIBLE);

                if (SettingUtils.getEnableBigPic()) {
                    TimeLineBitmapDownloader.getInstance().displayMultiPicture(pic, msg.getHighPicUrls().get(i),
                            FileLocationMethod.picture_large);
                } else {
                    TimeLineBitmapDownloader.getInstance().displayMultiPicture(pic, msg.getMiddlePicUrls().get(i),
                            FileLocationMethod.picture_bmiddle);
                }

                final int finalI = i;
                pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ArrayList<AnimationRect> animationRectArrayList = new ArrayList<AnimationRect>();
                        for (int i = 0; i < count; i++) {
                            final IWeiciyuanDrawable pic = (IWeiciyuanDrawable) layout.getChildAt(i);
                            ImageView imageView = (ImageView) pic;
                            if (imageView.getVisibility() == View.VISIBLE) {
                                AnimationRect rect = AnimationRect.buildFromImageView(imageView);
                                animationRectArrayList.add(rect);
                            }
                        }
                        Intent intent = GalleryAnimationActivity.newIntent(msg, animationRectArrayList, finalI);
                        getActivity().startActivity(intent);
                    }
                });

            }

            if (count < 9) {
                for (int i = count; i < 9; i++) {
                    ImageView pic = (ImageView) layout.getChildAt(i);
                    pic.setVisibility(View.GONE);
                }
            }
        }

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

    private class EmptyHeaderOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
        	loadNewRepostData();
        }
    }


    public void loadNewRepostData() {		
    	getPullToRefreshListView().setRefreshing();
    	
        	mAsyncHttoClient.get(WeiBoURLs.hotWeiboTravel("4u8Kc2373x4U9rFAXPfxc7SC21d", mPage), new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				// TODO Auto-generated method stub
				mPage++;
				String json = new String(responseBody).replaceAll("\"geo\":\"\"", "\"geo\": {}");
				org.zarroboogs.weibo.support.utils.Utility.printLongLog("READ_JSON_DONE", json);
				
				Gson gson = new Gson();
				
				HotWeiboErrorBean error = gson.fromJson(json, HotWeiboErrorBean.class);
				if (error != null && TextUtils.isEmpty(error.getErrmsg())) {
					HotWeiboBean result = gson.fromJson(json, new TypeToken<HotWeiboBean>() {}.getType());
					mMessageListBean.addNewData(result.getMessageListBean());
					List<MessageBean> list = result.getMessageBeans();
					addNewDataAndRememberPosition(list);
				}else {
					Log.d("===========after_READ_JSON_DONE:", "-----------"+ error.getErrmsg());
				}
				
				getPullToRefreshListView().onRefreshComplete();
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				// TODO Auto-generated method stub
				getPullToRefreshListView().onRefreshComplete();
			}
		});
	}

    private void addNewDataAndRememberPosition(final List<MessageBean> newValue) {

        int initSize = getListView().getCount();

        if (getActivity() != null && newValue.size() > 0) {
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
	public MessageListBean getDataList() {
		// TODO Auto-generated method stub
		return mMessageListBean;
	}

	@Override
	protected void onTimeListViewItemClick(AdapterView<?> parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void buildListAdapter() {
		// TODO Auto-generated method stub
        adapter = new HotWeiboStatusListAdapter(this, repostList, getListView(), true, false);
        adapter.setTopTipBar(newMsgTipBar);
        timeLineAdapter = adapter;
        getListView().setAdapter(timeLineAdapter);
	}

	@Override
	protected void newMsgLoaderSuccessCallback(MessageListBean newValue,
			Bundle loaderArgs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void oldMsgLoaderSuccessCallback(MessageListBean newValue) {
		// TODO Auto-generated method stub
		
	}


}
