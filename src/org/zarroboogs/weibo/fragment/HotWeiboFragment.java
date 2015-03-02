package org.zarroboogs.weibo.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.utils.file.FileLocationMethod;
import org.zarroboogs.weibo.MyAnimationListener;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.adapter.HotWeiboAdapter;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.bean.MessageBean;
import org.zarroboogs.weibo.bean.MessageListBean;
import org.zarroboogs.weibo.fragment.base.BaseStateFragment;
import org.zarroboogs.weibo.hot.hean.HotCardBean;
import org.zarroboogs.weibo.hot.hean.HotMblogBean;
import org.zarroboogs.weibo.hot.hean.HotWeiboBean;
import org.zarroboogs.weibo.hot.hean.HotWeiboErrorBean;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.asyncdrawable.IWeiciyuanDrawable;
import org.zarroboogs.weibo.support.asyncdrawable.MsgDetailReadWorker;
import org.zarroboogs.weibo.support.asyncdrawable.TimeLineBitmapDownloader;
import org.zarroboogs.weibo.support.gallery.GalleryAnimationActivity;
import org.zarroboogs.weibo.support.lib.AnimationRect;
import org.zarroboogs.weibo.support.utils.Utility;
import org.zarroboogs.weibo.widget.TopTipsView;
import org.zarroboogs.weibo.widget.WeiboDetailImageView;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HotWeiboFragment extends BaseStateFragment {

    private MsgDetailReadWorker picTask;
    
    private ListView listView;

    private HotWeiboAdapter adapter;

    private List<HotMblogBean> repostList = new ArrayList<HotMblogBean>();

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
				loadNewRepostData();
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
        adapter = new HotWeiboAdapter(this.getActivity());
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setHeaderDividersEnabled(false);

        loadNewRepostData();
        return swipeFrameLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        layout.location.setOnClickListener(locationInfoOnClickListener);
//        view.findViewById(R.id.first).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), UserInfoActivity.class);
//                intent.putExtra(Constants.TOKEN, GlobalContext.getInstance().getSpecialToken());
//                intent.putExtra("user", msg.getUser());
//                startActivity(intent);
//            }
//        });
//        layout.recontent.setOnClickListener(repostContentOnClickListener);
    }

//    public void buildViewData(final boolean refreshPic) {
//        layout.avatar.checkVerified(msg.getUser());
//        if (msg.getUser() != null) {
//            if (TextUtils.isEmpty(msg.getUser().getRemark())) {
//                layout.username.setText(msg.getUser().getScreen_name());
//            } else {
//                layout.username.setText(msg.getUser().getScreen_name() + "(" + msg.getUser().getRemark() + ")");
//            }
//
//            TimeLineBitmapDownloader.getInstance().downloadAvatar(layout.avatar.getImageView(), msg.getUser());
//        }
//        layout.content.setText(msg.getListViewSpannableString());
//        layout.content.setOnTouchListener(new ClickableTextViewMentionLinkOnTouchListener());
//
//        layout.time.setText(msg.getTimeInFormat());
//
//        if (msg.getGeo() != null) {
//            layout.mapView.setVisibility(View.VISIBLE);
//            if (Utility.isTaskStopped(geoTask)) {
//                geoTask = new GetWeiboLocationInfoTask(getActivity(), msg.getGeo(), layout.mapView, layout.location);
//                geoTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
//            }
//        } else {
//            layout.mapView.setVisibility(View.GONE);
//        }
//        if (!TextUtils.isEmpty(msg.getSource())) {
//            layout.source.setText(Html.fromHtml(msg.getSource()).toString());
//        }
//
//        layout.content_pic.setVisibility(View.GONE);
//        layout.content_pic_multi.setVisibility(View.GONE);
//
//        // sina weibo official account can send repost message with picture,
//        // fuck sina weibo
//        if (msg.havePicture() && msg.getRetweeted_status() == null) {
//            displayPictures(msg, layout.content_pic_multi, layout.content_pic, refreshPic);
//        }
//
//        final MessageBean repostMsg = msg.getRetweeted_status();
//
//        layout.repost_layout.setVisibility(repostMsg != null ? View.VISIBLE : View.GONE);
//
//        if (repostMsg != null) {
//            // sina weibo official account can send repost message with picture,
//            // fuck sina weibo
//            layout.content_pic.setVisibility(View.GONE);
//
//            layout.repost_layout.setVisibility(View.VISIBLE);
//            layout.recontent.setVisibility(View.VISIBLE);
//            layout.recontent.setOnTouchListener(new ClickableTextViewMentionLinkOnTouchListener());
//            if (repostMsg.getUser() != null) {
//                layout.recontent.setText(repostMsg.getListViewSpannableString());
//                buildRepostCount();
//            } else {
//                layout.recontent.setText(repostMsg.getListViewSpannableString());
//            }
//
//            layout.repost_pic.setVisibility(View.GONE);
//            layout.repost_pic_multi.setVisibility(View.GONE);
//
//            if (repostMsg.havePicture()) {
//                displayPictures(repostMsg, layout.repost_pic_multi, layout.repost_pic, refreshPic);
//            }
//        }
//
//        ((BrowserWeiboMsgActivity) getActivity()).updateRepostCount(msg.getReposts_count());
//
//    }

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

//    private void buildRepostCount() {
//        MessageBean repostBean = msg.getRetweeted_status();
//
//        if (repostBean.getComments_count() == 0 && repostBean.getReposts_count() == 0) {
//            layout.count_layout.setVisibility(View.GONE);
//            return;
//        } else {
//            layout.count_layout.setVisibility(View.VISIBLE);
//        }
//
//        if (repostBean.getComments_count() > 0) {
//            layout.comment_count.setVisibility(View.VISIBLE);
//            layout.comment_count.setText(String.valueOf(repostBean.getComments_count()));
//        } else {
//            layout.comment_count.setVisibility(View.GONE);
//        }
//
//        if (repostBean.getReposts_count() > 0) {
//            layout.repost_count.setVisibility(View.VISIBLE);
//            layout.repost_count.setText(String.valueOf(repostBean.getReposts_count()));
//        } else {
//            layout.repost_count.setVisibility(View.GONE);
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
//		if (itemId == R.id.menu_refresh) {
//			if (Utility.isTaskStopped(updateMsgTask)) {
//			    updateMsgTask = new UpdateHotWeiboTask(HotWeiboFragment.this, layout.content, layout.recontent,
//			            msg, true);
//			    updateMsgTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
//			}
//			loadNewRepostData();
//		}
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

//    private View.OnClickListener repostContentOnClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//
//            // This condition will satisfy only when it is not an autolinked
//            // text
//            // onClick action
//            boolean isNotLink = layout.recontent.getSelectionStart() == -1 && layout.recontent.getSelectionEnd() == -1;
//            boolean isDeleted = msg.getRetweeted_status() == null || msg.getRetweeted_status().getUser() == null;
//
//            if (isNotLink && !isDeleted) {
//                startActivity(BrowserWeiboMsgActivity.newIntent(GlobalContext.getInstance().getAccountBean(),
//                        msg.getRetweeted_status(), GlobalContext
//                                .getInstance().getSpecialToken()));
//            } else if (isNotLink && isDeleted) {
//                Toast.makeText(getActivity(), getString(R.string.cant_open_deleted_weibo), Toast.LENGTH_SHORT).show();
//            }
//
//        }
//    };

//    private View.OnClickListener locationInfoOnClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if (Utility.isGooglePlaySafe(getActivity())) {
//                GeoBean bean = msg.getGeo();
//                Intent intent = new Intent(getActivity(), AppMapActivity.class);
//                intent.putExtra("lat", bean.getLat());
//                intent.putExtra("lon", bean.getLon());
//                if (!String.valueOf(bean.getLat() + "," + bean.getLon()).equals(layout.location.getText())) {
//                    intent.putExtra("locationStr", layout.location.getText());
//                }
//                startActivity(intent);
//            } else {
//                GeoBean bean = msg.getGeo();
//                String geoUriString = "geo:" + bean.getLat() + "," + bean.getLon() + "?q=" + layout.location.getText();
//                Uri geoUri = Uri.parse(geoUriString);
//                Intent mapCall = new Intent(Intent.ACTION_VIEW, geoUri);
//                if (Utility.isIntentSafe(getActivity(), mapCall)) {
//                    startActivity(mapCall);
//                }
//
//            }
//        }
//    };




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
        	mAsyncHttoClient.get(WeiBoURLs.hotWeiboUrl("4u8Kc2373x4U9rFAXPfxc7SC21d", mPage), new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				// TODO Auto-generated method stub
				mPage++;
				String json = new String(responseBody).replaceAll("\"geo\":\"\"", "\"geo\": {}");
				org.zarroboogs.weibo.support.utils.Utility.printLongLog("READ_JSON_DONE", json);
				
				
			if (true) {
				Gson gson = new Gson();
				
				HotWeiboErrorBean error = gson.fromJson(json, HotWeiboErrorBean.class);
				if (error != null && TextUtils.isEmpty(error.getErrmsg())) {
					HotWeiboBean result = gson.fromJson(json, new TypeToken<HotWeiboBean>() {}.getType());
			    	Log.d("===========after_READ_JSON_DONE:", "-----------"+ result.getCardlistInfo().getDesc());
					List<HotCardBean> cardBeans = result.getCards();
					Log.d("===========after_READ_JSON_DONE:", "-----------" + "Cards Size: " + cardBeans.size());
					
					List<HotMblogBean> hotMblogBeans = new ArrayList<HotMblogBean>();
					for (HotCardBean i : cardBeans) {
						HotMblogBean blog = i.getMblog();
						if (blog != null) {
							hotMblogBeans.add(blog);
						}
					}
			            
					for (HotMblogBean i : hotMblogBeans) {
						Log.d("===========after_READ_JSON_DONE:", i.getUser().getId());
					}
					
					addNewDataAndRememberPosition(hotMblogBeans);
					
				}else {
					Log.d("===========after_READ_JSON_DONE:", "-----------"+ error.getErrmsg());
				}
			}	

		


				pullToRefreshListView.onRefreshComplete();
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				// TODO Auto-generated method stub
				pullToRefreshListView.onRefreshComplete();
			}
		});
	}

    private void addNewDataAndRememberPosition(final List<HotMblogBean> newValue) {

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

}
