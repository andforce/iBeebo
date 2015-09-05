package org.zarroboogs.msrl.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

import org.zarroboogs.msrl.R;

public class MaterialSwipeRefreshLayout extends SwipeRefreshLayout {

    private int mTouchSlop;
    private ListView mListView;
    private OnRefreshLoadMoreListener mOnRefreshLoadMoreListener;
    private View mListViewFooter;
    private boolean isEnableLoadmore = true;

    private int mYDown;
    private int mLastY;

    private boolean isLoading = false;
    private boolean isOnlyPullRefersh = false;
    private boolean isOnlyLoadMore = false;

    public MaterialSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public MaterialSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //set the footer of the ListView with a ProgressBar in it
    public void setFooterView(int layoutId) {
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mListViewFooter = LayoutInflater.from(getContext()).inflate(layoutId, null, false);
        ensureListView();
        mListView.addFooterView(mListViewFooter);
        mListView.setFooterDividersEnabled(false);
    }


    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ensureListView();
    }

    public void noMore() {
        isEnableLoadmore = false;
        if (mListViewFooter != null){
            View view = mListViewFooter.findViewById(R.id.noMoreTips);
            view.setVisibility(VISIBLE);
            View progress = mListViewFooter.findViewById(R.id.loadMoreProgressBar);
            progress.setVisibility(INVISIBLE);
            mListViewFooter.setVisibility(GONE);
        }
    }


    public void enableLoadmoew() {
        isEnableLoadmore = true;
    }

    private void ensureListView() {
        if (mListView == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child instanceof ListView) {
                    mListView = (ListView) child;
                    break;
                }
            }
            setColorSchemeResources(R.color.google_blue,
                    R.color.google_green,
                    R.color.google_red,
                    R.color.google_yellow);
        }
    }

    public void setOnlyPullRefersh(){
        this.isOnlyPullRefersh = true;
    }

    public void setOnlyLoadMore(){
        this.isOnlyLoadMore = true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isOnlyLoadMore){
            return false;
        }else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (isOnlyPullRefersh){
            super.dispatchTouchEvent(event);
            return true;
        }
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mYDown = (int) event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                mLastY = (int) event.getRawY();
                if (mLastY - mYDown > 200) {
//                    playStartSound();
                }
                if (isPullingUp()) {
                    //you can add view or hint here when pulling up the ListView
                }

                break;

            case MotionEvent.ACTION_UP:
                Log.d("MaterialSwipeRefreshLayout","MotionEvent.ACTION_UP" + canLoad());
                if (canLoad()) {
                    loadMore();
                }
                break;
            default:
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    private boolean canLoad() {
        return isBottom() && !isLoading && isPullingUp();
    }

    private boolean isBottom() {
        if (mListView.getCount() > 0) {
            if (mListView.getLastVisiblePosition() == mListView.getAdapter().getCount() - 1 &&
                    mListView.getChildAt(mListView.getChildCount() - 1).getBottom() <= mListView.getHeight()) {
                return true;
            }
        }
        return false;
    }

    private boolean isPullingUp() {
        return (mYDown - mLastY) >= mTouchSlop;
    }

    private void loadMore() {
        if (!isEnableLoadmore) {
            return;
        }
        if (mOnRefreshLoadMoreListener != null) {
            setLoadingMore(true);
            mOnRefreshLoadMoreListener.onLoadMore();
        }
    }

    private void showMaterialProgressBar(){
        if (mListViewFooter != null){
            mListViewFooter.findViewById(R.id.loadMoreProgressBar).setVisibility(VISIBLE);
        }
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        super.setRefreshing(refreshing);
    }

    public void setLoadingMore(boolean loading) {
        isLoading = loading;
        if (loading) {

            if (isRefreshing()){
                return;
            }
            if (mListView.getFooterViewsCount() == 0){
                mListView.addFooterView(mListViewFooter);
            }
            mListView.setSelection(mListView.getAdapter().getCount() - 1);
            showMaterialProgressBar();
//            if (isRefreshing()) {
//                setRefreshing(false);
//            }
//            if (mListView.getFooterViewsCount() == 0) {
//                mListView.addFooterView(mListViewFooter);
//                mListView.setSelection(mListView.getAdapter().getCount() - 1);
//            } else {
//                mListViewFooter.setVisibility(VISIBLE);
//                showMaterialProgressBar();
//                //mListView.addFooterView(mListViewFooter);
//            }
        } else {
//            if (mListView.getAdapter() instanceof HeaderViewListAdapter) {
//                mListView.removeFooterView(mListViewFooter);
//            } else {
//                mListViewFooter.setVisibility(View.GONE);
//            }
            mListView.removeFooterView(mListViewFooter);
            mYDown = 0;
            mLastY = 0;
        }
    }

    public interface OnRefreshLoadMoreListener {
        void onRefresh();

        void onLoadMore();
    }

    public void setOnRefreshLoadMoreListener(OnRefreshLoadMoreListener listener) {
        this.mOnRefreshLoadMoreListener = listener;
        this.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mOnRefreshLoadMoreListener != null) {
                    mOnRefreshLoadMoreListener.onRefresh();
                }
            }
        });
    }
}