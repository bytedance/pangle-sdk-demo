package com.union_test.new_api.activity.nativead;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bytedance.sdk.openadsdk.api.nativeAd.PAGImageItem;
import com.bytedance.sdk.openadsdk.api.nativeAd.PAGNativeAd;
import com.bytedance.sdk.openadsdk.api.nativeAd.PAGNativeAdData;
import com.bytedance.sdk.openadsdk.api.nativeAd.PAGNativeAdInteractionListener;
import com.bytedance.sdk.openadsdk.api.nativeAd.PAGNativeAdLoadListener;
import com.bytedance.sdk.openadsdk.api.nativeAd.PAGNativeRequest;
import com.union_test.internationad.R;
import com.union_test.new_api.RitConstants;
import com.union_test.new_api.utils.TToast;
import com.union_test.new_api.view.ILoadMoreListener;
import com.union_test.new_api.view.LoadMoreRecyclerView;
import com.union_test.new_api.view.LoadMoreView;

import java.util.ArrayList;
import java.util.List;

/**
 * Feed ad，in RecyclerView，
 * Specific instructions can refer to FeedListActivity.java
 */
@SuppressWarnings("unused")
public class PAGFeedRecyclerActivity extends Activity {
    private static final String TAG = "FeedRecyclerActivity";

    private static final int AD_POSITION = 3;
    private static final int LIST_ITEM_COUNT = 3;
    private LoadMoreRecyclerView mListView;
    private MyAdapter myAdapter;
    private List<PAGNativeAd> mData;
    private PAGNativeAd mPAGNativeAd;

    private RadioGroup mRadioGroupOri;
    private int mScrollOrientation = RecyclerView.VERTICAL;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_recycler);
        findViewById(R.id.btn_fr_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initListView();
        initRadioGroup();

    }

    @SuppressWarnings("RedundantCast")
    private void initRadioGroup() {
        RadioGroup mRadioGroupManager = (RadioGroup) findViewById(R.id.rg_fra_group);
        mRadioGroupOri = (RadioGroup) findViewById(R.id.rg_fra_group_orientation);


        mRadioGroupManager.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (mListView == null || mData == null || myAdapter == null) {
                    return;
                }

                RecyclerView.LayoutManager manager = null;
                mRadioGroupOri.setVisibility(View.VISIBLE);
                switch (checkedId) {
                    case R.id.rb_fra_linear:
                        manager = new LinearLayoutManager(PAGFeedRecyclerActivity.this, mScrollOrientation, false);
                        break;
                    case R.id.rb_fra_grid:
                        mRadioGroupOri.setVisibility(View.GONE);
                        manager = new GridLayoutManager(PAGFeedRecyclerActivity.this, 2);
                        break;
                    case R.id.rb_fra_staggered:
                        manager = new StaggeredGridLayoutManager(2, mScrollOrientation);
                        break;
                }
                if (manager != null) {
                    mListView.setLayoutManager(manager);
                    mData.clear();
                    myAdapter.notifyDataSetChanged();
                    loadListAd();
                }
            }
        });

        mRadioGroupOri.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (mListView == null || mData == null || myAdapter == null) {
                    return;
                }

                RecyclerView.LayoutManager manager = mListView.getLayoutManager();
                if (manager != null) {
                    if (checkedId == R.id.rb_fra_orientation_v) {
                        mScrollOrientation = RecyclerView.VERTICAL;
                    } else if (checkedId == R.id.rb_fra_orientation_h) {
                        mScrollOrientation = RecyclerView.HORIZONTAL;
                    }

                    if (manager instanceof LinearLayoutManager) {
                        ((LinearLayoutManager) manager).setOrientation(mScrollOrientation);
                    } else if (manager instanceof StaggeredGridLayoutManager) {
                        ((StaggeredGridLayoutManager) manager).setOrientation(mScrollOrientation);
                    }
                    mData.clear();
                    myAdapter.notifyDataSetChanged();
                    loadListAd();
                }
            }
        });
    }

    @SuppressWarnings("RedundantCast")
    private void initListView() {
        mListView = (LoadMoreRecyclerView) findViewById(R.id.my_list);
        mListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mData = new ArrayList<>();
        myAdapter = new MyAdapter(this, mData);
        mListView.setAdapter(myAdapter);
        mListView.setLoadMoreListener(new ILoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadListAd();
            }
        });

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadListAd();
            }
        }, 500);
    }

    /**
     * load feed ads
     */
    private void loadListAd() {
        PAGNativeRequest request = new PAGNativeRequest();
        PAGNativeAd.loadAd(RitConstants.RIT_NATIVE, request, new PAGNativeAdLoadListener() {
            @Override
            public void onError(int code, String message) {
                if (mListView != null) {
                    mListView.setLoadingFinish();
                }
                TToast.show(PAGFeedRecyclerActivity.this, message);
            }

            @Override
            public void onAdLoaded(PAGNativeAd pagNativeAd) {
                if (mListView != null) {
                    mListView.setLoadingFinish();
                }

                if (pagNativeAd == null) {
                    TToast.show(PAGFeedRecyclerActivity.this, "on FeedAdLoaded: ad is null!");
                    return;
                }

                List<PAGNativeAd> ads = new ArrayList<>();
                ads.add(pagNativeAd);


                for (int i = 0; i < LIST_ITEM_COUNT; i++) {
                    mData.add(null);
                }

                int count = mData.size();
                for (PAGNativeAd ad : ads) {
                    int random = (int) (Math.random() * LIST_ITEM_COUNT) + count - LIST_ITEM_COUNT;
                    mData.set(random, pagNativeAd);
                }

                myAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TToast.clearOnDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    private static class MyAdapter extends RecyclerView.Adapter {
        private static final int FOOTER_VIEW_COUNT = 1;

        private static final int ITEM_VIEW_TYPE_LOAD_MORE = -1;
        private static final int ITEM_VIEW_TYPE_NORMAL = 0;
        private static final int ITEM_VIEW_TYPE_AD = 1;

        private final List<PAGNativeAd> mData;
        private final Context mContext;
        private RecyclerView mRecyclerView;
        private final RequestManager mRequestManager;

        public MyAdapter(Context context, List<PAGNativeAd> data) {
            this.mContext = context;
            this.mData = data;
            mRequestManager = Glide.with(mContext);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder holder = null;
            switch (viewType) {
                case ITEM_VIEW_TYPE_LOAD_MORE:
                    return new LoadMoreViewHolder(new LoadMoreView(mContext));
                case ITEM_VIEW_TYPE_AD:
                    return new PangleAdViewHolder(LayoutInflater.from(mContext).inflate(R.layout.new_api_listitem_pangle_feed_ad_view, parent, false));
                default:
                    return new NormalViewHolder(LayoutInflater.from(mContext).inflate(R.layout.listitem_normal, parent, false));
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int count = mData.size();
            PAGNativeAd pagNativeAd;
            if (holder instanceof PangleAdViewHolder) {
                pagNativeAd = mData.get(position);
                if (pagNativeAd != null && pagNativeAd.getNativeAdData() != null) {
                    PAGNativeAdData adData = pagNativeAd.getNativeAdData();
                    PangleAdViewHolder pangleAdViewHolder = (PangleAdViewHolder) holder;
                    ArrayList<View> images = new ArrayList<>();
                    images.add(((PangleAdViewHolder) holder).videoView);
                    bindData(pangleAdViewHolder, images, pagNativeAd, adData);
                    if (pangleAdViewHolder.videoView != null) {
                        View video = adData.getMediaView();
                        if (video != null) {
                            if (video.getParent() == null) {
                                pangleAdViewHolder.videoView.removeAllViews();
                                pangleAdViewHolder.videoView.addView(video);
                            }
                        }
                    }
                }
            } else if (holder instanceof NormalViewHolder) {
                NormalViewHolder normalViewHolder = (NormalViewHolder) holder;
                normalViewHolder.idle.setText("Recycler item " + position);
            } else if (holder instanceof LoadMoreViewHolder) {
                LoadMoreViewHolder loadMoreViewHolder = (LoadMoreViewHolder) holder;
            }

            if (holder instanceof LoadMoreViewHolder) {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            } else if (holder instanceof PangleAdViewHolder) {
                holder.itemView.setBackgroundColor(Color.WHITE);
            } else {
                holder.itemView.setBackgroundColor(getColorRandom());
            }
        }

        private int getColorRandom() {
            int a = Double.valueOf(Math.random() * 255).intValue();
            int r = Double.valueOf(Math.random() * 255).intValue();
            int g = Double.valueOf(Math.random() * 255).intValue();
            int b = Double.valueOf(Math.random() * 255).intValue();
            return Color.argb(a, r, g, b);
        }

        private void addAdLogoView(AdViewHolder adViewHolder, PAGNativeAdData ad) {
            if (adViewHolder == null || ad == null) {
                return;
            }
            ImageView logo = (ImageView) ad.getAdLogoView();
            RelativeLayout layout = adViewHolder.mAdLogoView;
            if (layout == null || logo == null) {
                return;
            }
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            layout.addView(logo, lp);
        }


        private void bindData(final AdViewHolder adViewHolder, List<View> images, final PAGNativeAd ad, final PAGNativeAdData adData) {
            //the views that can be clicked
            addAdLogoView(adViewHolder, ad.getNativeAdData());
            List<View> clickViewList = new ArrayList<>();
            clickViewList.add(adViewHolder.itemView);
            //The views that can trigger the creative action (like download app)
            List<View> creativeViewList = new ArrayList<>();
            creativeViewList.add(adViewHolder.mCreativeButton);
            //notice! This involves advertising billing and must be called correctly. convertView must use ViewGroup.
            ad.registerViewForInteraction((ViewGroup) adViewHolder.itemView, clickViewList, creativeViewList, adViewHolder.mDislike, new PAGNativeAdInteractionListener() {
                @Override
                public void onAdShowed() {
                    if (adData != null) {
                        Log.e(TAG, "ad title:" + adData.getTitle() + ",onAdShowed");
                    }
                }

                @Override
                public void onAdClicked() {
                    if (adData != null) {
                        Log.e(TAG, "ad title:" + adData.getTitle() + ",onAdClicked");
                    }
                }

                /**
                 * click dislike button ，remove ad
                 */
                @Override
                public void onAdDismissed() {
                    if (adData != null) {
                        Log.e(TAG, "ad title:" + adData.getTitle() + ",onAdDismissed");
                    }
                    mData.remove(ad);
                    notifyDataSetChanged();
                }
            });
            if (adViewHolder.mDislike != null) {
                adViewHolder.mDislike.setVisibility(View.VISIBLE);
            }

            if (adData != null) {
                adViewHolder.mTitle.setText(adData.getTitle());
                adViewHolder.mDescription.setText(adData.getDescription());
                PAGImageItem icon = adData.getIcon();
                if (icon != null) {
                    mRequestManager.load(icon.getImageUrl()).into(adViewHolder.mIcon);
                }
                adViewHolder.mCreativeButton.setText(adData.getButtonText());
            }

        }

        @Override
        public int getItemCount() {
            int count = mData == null ? 0 : mData.size();
            return count + FOOTER_VIEW_COUNT;
        }

        @Override
        public int getItemViewType(int position) {
            if (mData != null) {
                int count = mData.size();
                if (position >= count) {
                    return ITEM_VIEW_TYPE_LOAD_MORE;
                } else {
                    PAGNativeAd ad = mData.get(position);
                    if (ad == null) {
                        return ITEM_VIEW_TYPE_NORMAL;
                    } else {
                        return ITEM_VIEW_TYPE_AD;
                    }
                }
            }
            return super.getItemViewType(position);
        }

        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);

            RecyclerView.LayoutManager layout = recyclerView.getLayoutManager();
            if (layout != null && layout instanceof GridLayoutManager) {
                final GridLayoutManager manager = (GridLayoutManager) layout;
                manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        int type = getItemViewType(position);
                        if (type == ITEM_VIEW_TYPE_LOAD_MORE || type == ITEM_VIEW_TYPE_AD) {
                            return manager.getSpanCount();
                        }
                        return 1;
                    }
                });
            }
        }

        @Override
        public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
            //noinspection unchecked
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                int position = holder.getLayoutPosition();
                int type = getItemViewType(position);
                if (type == ITEM_VIEW_TYPE_LOAD_MORE || type == ITEM_VIEW_TYPE_AD) {
                    StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                    p.setFullSpan(true);
                }
            }
        }

        @SuppressWarnings("WeakerAccess")
        private static class PangleAdViewHolder extends AdViewHolder {
            @SuppressWarnings("CanBeFinal")
            FrameLayout videoView;

            @SuppressWarnings("RedundantCast")
            public PangleAdViewHolder(View itemView) {
                super(itemView);

                mDislike = (ImageView) itemView.findViewById(R.id.iv_listitem_dislike);
                mTitle = (TextView) itemView.findViewById(R.id.tv_listitem_ad_title);
                mDescription = (TextView) itemView.findViewById(R.id.tv_listitem_ad_desc);
                videoView = (FrameLayout) itemView.findViewById(R.id.iv_listitem_video);
                mIcon = (ImageView) itemView.findViewById(R.id.iv_listitem_icon);
                mCreativeButton = (Button) itemView.findViewById(R.id.tt_creative_btn);
                mAdLogoView = (RelativeLayout) itemView.findViewById(R.id.tt_ad_logo);
            }
        }


        private static class AdViewHolder extends RecyclerView.ViewHolder {
            ImageView mIcon;
            ImageView mDislike;
            Button mCreativeButton;
            TextView mTitle;
            TextView mDescription;
            RelativeLayout mAdLogoView;

            public AdViewHolder(View itemView) {
                super(itemView);
            }
        }

        private static class NormalViewHolder extends RecyclerView.ViewHolder {
            TextView idle;

            @SuppressWarnings("RedundantCast")
            public NormalViewHolder(View itemView) {
                super(itemView);

                idle = (TextView) itemView.findViewById(R.id.text_idle);

            }
        }

        @SuppressWarnings({"CanBeFinal", "WeakerAccess"})
        private static class LoadMoreViewHolder extends RecyclerView.ViewHolder {
            TextView mTextView;
            ProgressBar mProgressBar;

            @SuppressWarnings("RedundantCast")
            public LoadMoreViewHolder(View itemView) {
                super(itemView);

                itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));

                mTextView = (TextView) itemView.findViewById(R.id.tv_load_more_tip);
                mProgressBar = (ProgressBar) itemView.findViewById(R.id.pb_load_more_progress);
            }
        }
    }

}
