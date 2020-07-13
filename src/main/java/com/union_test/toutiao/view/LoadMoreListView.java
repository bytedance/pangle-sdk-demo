package com.union_test.toutiao.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Create by hanweiwei on 11/07/2018
 */
@SuppressWarnings("SpellCheckingInspection")
public class LoadMoreListView extends ListView implements AbsListView.OnScrollListener {
    private int mLastVisibleItem, mTotalItemCount;
    private boolean isLoading = false;

    private OnScrollListener mOnScrollListener;
    private ILoadMoreListener mLoadMoreListener;

    public LoadMoreListView(Context context) {
        super(context);
        init(context);
    }

    public LoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadMoreListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        super.setOnScrollListener(this);


        initLoadMoreView(context);
    }

    private void initLoadMoreView(Context context) {
        setFooterDividersEnabled(true);

        LoadMoreView mLoadMoreView = new LoadMoreView(context);
        addFooterView(mLoadMoreView);
    }

    @SuppressWarnings("unused")
    public ILoadMoreListener getLoadMoreListener() {
        return mLoadMoreListener;
    }

    public void setLoadMoreListener(ILoadMoreListener loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
    }

    public void setLoadingFinish() {
        isLoading = false;
    }

    @SuppressWarnings("unused")
    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }

        if (mLastVisibleItem == mTotalItemCount
                && scrollState == SCROLL_STATE_IDLE) {
            if (mLoadMoreListener != null && !isLoading) {
                isLoading = true;
                mLoadMoreListener.onLoadMore();
            }
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
        mLastVisibleItem = firstVisibleItem + visibleItemCount;
        mTotalItemCount = totalItemCount;
    }

}
