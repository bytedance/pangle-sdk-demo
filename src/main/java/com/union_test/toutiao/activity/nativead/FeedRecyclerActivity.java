package com.union_test.toutiao.activity.nativead;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.union_test.toutiao.R;
import com.union_test.toutiao.config.TTAdManagerHolder;
import com.union_test.toutiao.utils.TToast;
import com.union_test.toutiao.view.ILoadMoreListener;
import com.union_test.toutiao.view.LoadMoreRecyclerView;
import com.union_test.toutiao.view.LoadMoreView;

import java.util.ArrayList;
import java.util.List;

/**
 * Feed ad，in RecyclerView，
 * Specific instructions can refer to FeedListActivity.java
 */
@SuppressWarnings("unused")
public class FeedRecyclerActivity extends Activity {
    private static final String TAG = "FeedRecyclerActivity";

    private static final int AD_POSITION = 3;
    private static final int LIST_ITEM_COUNT = 30;
    private LoadMoreRecyclerView mListView;
    private MyAdapter myAdapter;
    private List<TTFeedAd> mData;

    private TTAdNative mTTAdNative;
    private RadioGroup mRadioGroupManager;
    private RadioGroup mRadioGroupOri;
    private int mScrollOrientation = RecyclerView.VERTICAL;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_recycler);
        TTAdManager ttAdManager = TTAdManagerHolder.get();
        mTTAdNative = ttAdManager.createAdNative(getApplicationContext());
        initListView();
        initRadioGroup();

    }

    @SuppressWarnings("RedundantCast")
    private void initRadioGroup() {
        mRadioGroupManager = (RadioGroup) findViewById(R.id.rg_fra_group);
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
                        manager = new LinearLayoutManager(FeedRecyclerActivity.this, mScrollOrientation, false);
                        break;
                    case R.id.rb_fra_grid:
                        mRadioGroupOri.setVisibility(View.GONE);
                        manager = new GridLayoutManager(FeedRecyclerActivity.this, 2);
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
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId("945092256")
                .setSupportDeepLink(true)
                .setImageAcceptedSize(640, 320)
                .setAdCount(3)
                .build();
        //loadFeedAd
        mTTAdNative.loadFeedAd(adSlot, new TTAdNative.FeedAdListener() {
            @Override
            public void onError(int code, String message) {
                if (mListView != null) {
                    mListView.setLoadingFinish();
                }
                TToast.show(FeedRecyclerActivity.this, message);
            }

            @Override
            public void onFeedAdLoad(List<TTFeedAd> ads) {
                if (mListView != null) {
                    mListView.setLoadingFinish();
                }
                if (ads == null || ads.isEmpty()) {
                    TToast.show(FeedRecyclerActivity.this, "on FeedAdLoaded: ad is null!");
                    return;
                }

                for (int i = 0; i < LIST_ITEM_COUNT; i++) {
                    mData.add(null);
                }

                int count = mData.size();
                for (TTFeedAd ad : ads) {
                    int random = (int) (Math.random() * LIST_ITEM_COUNT) + count - LIST_ITEM_COUNT;
                    mData.set(random, ad);
                }


                myAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TToast.reset();
        mHandler.removeCallbacksAndMessages(null);
    }

    private static class MyAdapter extends RecyclerView.Adapter {
        private static final int FOOTER_VIEW_COUNT = 1;

        private static final int ITEM_VIEW_TYPE_LOAD_MORE = -1;
        private static final int ITEM_VIEW_TYPE_NORMAL = 0;
        private static final int ITEM_VIEW_TYPE_GROUP_PIC_AD = 1;
        private static final int ITEM_VIEW_TYPE_SMALL_PIC_AD = 2;
        private static final int ITEM_VIEW_TYPE_LARGE_PIC_AD = 3;
        private static final int ITEM_VIEW_TYPE_VIDEO = 4;
        private static final int ITEM_VIEW_TYPE_VERTICAL_PIC_AD = 5;

        private List<TTFeedAd> mData;
        private Context mContext;
        private RecyclerView mRecyclerView;
        private RequestManager mRequestManager;

        public MyAdapter(Context context, List<TTFeedAd> data) {
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
                case ITEM_VIEW_TYPE_SMALL_PIC_AD:
                    return new SmallAdViewHolder(LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_small_pic, parent, false));
                case ITEM_VIEW_TYPE_LARGE_PIC_AD:
                    return new LargeAdViewHolder(LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_large_pic_layout, parent, false));
                case ITEM_VIEW_TYPE_VERTICAL_PIC_AD:
                    return new VerticalAdViewHolder(LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_vertical_pic, parent, false));
                case ITEM_VIEW_TYPE_GROUP_PIC_AD:
                    return new GroupAdViewHolder(LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_group_pic, parent, false));
                case ITEM_VIEW_TYPE_VIDEO:
                    return new VideoAdViewHolder(LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_large_video, parent, false));
                default:
                    return new NormalViewHolder(LayoutInflater.from(mContext).inflate(R.layout.listitem_normal, parent, false));
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int count = mData.size();
            TTFeedAd ttFeedAd;
            if (holder instanceof SmallAdViewHolder) {
                ttFeedAd = mData.get(position);
                SmallAdViewHolder smallAdViewHolder = (SmallAdViewHolder) holder;
                ArrayList<View> images = new ArrayList<>();
                images.add(((SmallAdViewHolder) holder).mSmallImage);
                bindData(smallAdViewHolder, images,ttFeedAd);
                if (ttFeedAd.getImageList() != null && !ttFeedAd.getImageList().isEmpty()) {
                    TTImage image = ttFeedAd.getImageList().get(0);
                    if (image != null && image.isValid()) {
                        mRequestManager.load(image.getImageUrl()).into(smallAdViewHolder.mSmallImage);
                    }
                }

            } else if (holder instanceof LargeAdViewHolder) {
                ttFeedAd = mData.get(position);
                LargeAdViewHolder largeAdViewHolder = (LargeAdViewHolder) holder;
                ArrayList<View> images = new ArrayList<>();
                images.add(((LargeAdViewHolder) holder).mLargeImage);
                bindData(largeAdViewHolder,images, ttFeedAd);
                if (ttFeedAd.getImageList() != null && !ttFeedAd.getImageList().isEmpty()) {
                    TTImage image = ttFeedAd.getImageList().get(0);
                    if (image != null && image.isValid()) {
                        mRequestManager.load(image.getImageUrl()).into(largeAdViewHolder.mLargeImage);
                    }
                }

            } else if (holder instanceof VerticalAdViewHolder) {
                ttFeedAd = mData.get(position);
                VerticalAdViewHolder verticalAdViewHolder = (VerticalAdViewHolder) holder;
                ArrayList<View> images = new ArrayList<>();
                images.add((((VerticalAdViewHolder) holder).mVerticalImage));
                bindData(verticalAdViewHolder, images,ttFeedAd);
                if (ttFeedAd.getImageList() != null && !ttFeedAd.getImageList().isEmpty()) {
                    TTImage image = ttFeedAd.getImageList().get(0);
                    if (image != null && image.isValid()) {
                        mRequestManager.load(image.getImageUrl()).into(verticalAdViewHolder.mVerticalImage);
                    }
                }
            } else if (holder instanceof GroupAdViewHolder) {
                ttFeedAd = mData.get(position);
                GroupAdViewHolder groupAdViewHolder = (GroupAdViewHolder) holder;
                ArrayList<View> images = new ArrayList<>();
                images.add(((GroupAdViewHolder) holder).mGroupImage1);
                images.add(((GroupAdViewHolder) holder).mGroupImage2);
                images.add(((GroupAdViewHolder) holder).mGroupImage3);
                bindData(groupAdViewHolder, images,ttFeedAd);
                if (ttFeedAd.getImageList() != null && ttFeedAd.getImageList().size() >= 3) {
                    TTImage image1 = ttFeedAd.getImageList().get(0);
                    TTImage image2 = ttFeedAd.getImageList().get(1);
                    TTImage image3 = ttFeedAd.getImageList().get(2);
                    if (image1 != null && image1.isValid()) {
                        mRequestManager.load(image1.getImageUrl()).into(groupAdViewHolder.mGroupImage1);
                    }
                    if (image2 != null && image2.isValid()) {
                        mRequestManager.load(image2.getImageUrl()).into(groupAdViewHolder.mGroupImage2);
                    }
                    if (image3 != null && image3.isValid()) {
                        mRequestManager.load(image3.getImageUrl()).into(groupAdViewHolder.mGroupImage3);
                    }
                }

            } else if (holder instanceof VideoAdViewHolder) {
                ttFeedAd = mData.get(position);
                VideoAdViewHolder videoAdViewHolder = (VideoAdViewHolder) holder;
                ArrayList<View> images = new ArrayList<>();
                images.add(((VideoAdViewHolder) holder).videoView);
                bindData(videoAdViewHolder, images, ttFeedAd);
                ttFeedAd.setVideoAdListener(new TTFeedAd.VideoAdListener() {
                    @Override
                    public void onVideoLoad(TTFeedAd ad) {

                    }

                    @Override
                    public void onVideoError(int errorCode, int extraCode) {

                    }

                    @Override
                    public void onVideoAdStartPlay(TTFeedAd ad) {

                    }

                    @Override
                    public void onVideoAdPaused(TTFeedAd ad) {

                    }

                    @Override
                    public void onVideoAdContinuePlay(TTFeedAd ad) {

                    }

                    @Override
                    public void onProgressUpdate(long current, long duration) {

                    }

                    @Override
                    public void onVideoAdComplete(TTFeedAd ad) {

                    }
                });
                if (videoAdViewHolder.videoView != null) {
                    View video = ttFeedAd.getAdView();
                    if (video != null) {
                        if (video.getParent() == null) {
                            videoAdViewHolder.videoView.removeAllViews();
                            videoAdViewHolder.videoView.addView(video);
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
            } else if (holder instanceof SmallAdViewHolder ||
                    holder instanceof VideoAdViewHolder ||
                    holder instanceof LargeAdViewHolder ||
                    holder instanceof GroupAdViewHolder ||
                    holder instanceof VerticalAdViewHolder) {
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

        private void addAdLogoView(AdViewHolder adViewHolder, TTFeedAd ad) {
            if (adViewHolder == null || ad == null) {
                return;
            }
            ImageView logo = (ImageView) ad.getAdLogoView();
            RelativeLayout layout = adViewHolder.mAdLogoView;
            if (layout == null || logo == null) {
                return;
            }
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            layout.addView(logo,lp);
        }
        private void bindData(final AdViewHolder adViewHolder,List<View> images, final TTFeedAd ad) {
            //the views that can be clicked
            addAdLogoView(adViewHolder, ad);
            List<View> clickViewList = new ArrayList<>();
            clickViewList.add(adViewHolder.itemView);
            //The views that can trigger the creative action (like download app)
            List<View> creativeViewList = new ArrayList<>();
            creativeViewList.add(adViewHolder.mCreativeButton);
            //notice! This involves advertising billing and must be called correctly. convertView must use ViewGroup.
            ad.registerViewForInteraction((ViewGroup) adViewHolder.itemView, images,clickViewList, creativeViewList,adViewHolder.mDislike, new TTNativeAd.AdInteractionListener() {
                @Override
                public void onAdClicked(View view, TTNativeAd ad) {
                    if (ad != null) {
                        TToast.show(mContext, "ad" + ad.getTitle() + "clicked");
                    }
                }

                @Override
                public void onAdCreativeClick(View view, TTNativeAd ad) {
                    if (ad != null) {
                        TToast.show(mContext, "ad" + ad.getTitle() + "button clicked");
                    }
                }

                @Override
                public void onAdShow(TTNativeAd ad) {
                    if (ad != null) {
                        TToast.show(mContext, "ad" + ad.getTitle() + "showed");
                    }
                }
            });
            if (adViewHolder.mDislike != null) {
                adViewHolder.mDislike.setVisibility(View.VISIBLE);
                adViewHolder.mDislike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mContext instanceof Activity) {
                            ad.getDislikeDialog((Activity) mContext).showDislikeDialog();
                        }
                    }
                });
            }
            adViewHolder.mTitle.setText(ad.getTitle());
            adViewHolder.mDescription.setText(ad.getDescription());
            String adSource = "Ad Source";
            if (mContext != null) {
                adSource = mContext.getString(R.string.tt_ad_source);
            }
            adViewHolder.mSource.setText(ad.getSource() == null ? adSource : ad.getSource());
            TTImage icon = ad.getIcon();
            if (icon != null && icon.isValid()) {
                mRequestManager.load(icon.getImageUrl()).into(adViewHolder.mIcon);
            }
            Button adCreativeButton = adViewHolder.mCreativeButton;
            switch (ad.getInteractionType()) {
                case TTAdConstant.INTERACTION_TYPE_DOWNLOAD:
                    adCreativeButton.setVisibility(View.VISIBLE);
                    String downloadBtnDesc = "Download";
                    if (mContext != null) {
                        downloadBtnDesc = mContext.getString(R.string.tt_native_banner_download);
                    }
                    adCreativeButton.setText(downloadBtnDesc);
                    break;
                case TTAdConstant.INTERACTION_TYPE_DIAL:
                    adCreativeButton.setVisibility(View.VISIBLE);
                    String callBtnDesc = "call";
                    if (mContext != null) {
                        callBtnDesc = mContext.getString(R.string.tt_native_banner_call);
                    }
                    adCreativeButton.setText(callBtnDesc);
                    break;
                case TTAdConstant.INTERACTION_TYPE_LANDING_PAGE:
                case TTAdConstant.INTERACTION_TYPE_BROWSER:
//                    adCreativeButton.setVisibility(View.GONE);
                    adCreativeButton.setVisibility(View.VISIBLE);
                    String btnDesc = "view";
                    if (mContext != null) {
                        btnDesc = mContext.getString(R.string.tt_native_banner_view);
                    }
                    adCreativeButton.setText(btnDesc);
                    break;
                default:
                    adCreativeButton.setVisibility(View.GONE);
                    TToast.show(mContext, "Interaction type error");
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
                    TTFeedAd ad = mData.get(position);
                    if (ad == null) {
                        return ITEM_VIEW_TYPE_NORMAL;
                    } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_SMALL_IMG) {
                        return ITEM_VIEW_TYPE_SMALL_PIC_AD;
                    } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_LARGE_IMG) {
                        return ITEM_VIEW_TYPE_LARGE_PIC_AD;
                    } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_GROUP_IMG) {
                        return ITEM_VIEW_TYPE_GROUP_PIC_AD;
                    } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_VIDEO) {
                        return ITEM_VIEW_TYPE_VIDEO;
                    } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_VERTICAL_IMG) {//竖版图片
                        return ITEM_VIEW_TYPE_VERTICAL_PIC_AD;
                    } else {
                        TToast.show(mContext, "Image style error");
                        return ITEM_VIEW_TYPE_NORMAL;
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
                        if (type == ITEM_VIEW_TYPE_LOAD_MORE || type == ITEM_VIEW_TYPE_VIDEO) {
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
                if (type == ITEM_VIEW_TYPE_LOAD_MORE || type == ITEM_VIEW_TYPE_VIDEO) {
                    StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                    p.setFullSpan(true);
                }
            }
        }

        @SuppressWarnings("WeakerAccess")
        private static class VideoAdViewHolder extends AdViewHolder {
            @SuppressWarnings("CanBeFinal")
            FrameLayout videoView;

            @SuppressWarnings("RedundantCast")
            public VideoAdViewHolder(View itemView) {
                super(itemView);

                mDislike = (ImageView) itemView.findViewById(R.id.iv_listitem_dislike);
                mTitle = (TextView) itemView.findViewById(R.id.tv_listitem_ad_title);
                mDescription = (TextView) itemView.findViewById(R.id.tv_listitem_ad_desc);
                mSource = (TextView) itemView.findViewById(R.id.tv_listitem_ad_source);
                videoView = (FrameLayout) itemView.findViewById(R.id.iv_listitem_video);
                mIcon = (ImageView) itemView.findViewById(R.id.iv_listitem_icon);
                mCreativeButton = (Button) itemView.findViewById(R.id.tt_creative_btn);
                mAdLogoView = (RelativeLayout) itemView.findViewById(R.id.tt_ad_logo);
            }
        }

        private static class LargeAdViewHolder extends AdViewHolder {
            ImageView mLargeImage;

            @SuppressWarnings("RedundantCast")
            public LargeAdViewHolder(View itemView) {
                super(itemView);

                mDislike = (ImageView) itemView.findViewById(R.id.iv_listitem_dislike);
                mTitle = (TextView) itemView.findViewById(R.id.tv_listitem_ad_title);
                mDescription = (TextView) itemView.findViewById(R.id.tv_listitem_ad_desc);
                mSource = (TextView) itemView.findViewById(R.id.tv_listitem_ad_source);
                mLargeImage = (ImageView) itemView.findViewById(R.id.iv_listitem_large_image);
                mIcon = (ImageView) itemView.findViewById(R.id.iv_listitem_icon);
                mCreativeButton = (Button) itemView.findViewById(R.id.tt_creative_btn);
                mAdLogoView = (RelativeLayout) itemView.findViewById(R.id.tt_ad_logo);
            }
        }

        private static class SmallAdViewHolder extends AdViewHolder {
            ImageView mSmallImage;

            @SuppressWarnings("RedundantCast")
            public SmallAdViewHolder(View itemView) {
                super(itemView);

                mDislike = (ImageView) itemView.findViewById(R.id.iv_listitem_dislike);
                mTitle = (TextView) itemView.findViewById(R.id.tv_listitem_ad_title);
                mSource = (TextView) itemView.findViewById(R.id.tv_listitem_ad_source);
                mDescription = (TextView) itemView.findViewById(R.id.tv_listitem_ad_desc);
                mSmallImage = (ImageView) itemView.findViewById(R.id.iv_listitem_small_image);
                mIcon = (ImageView) itemView.findViewById(R.id.iv_listitem_icon);
                mCreativeButton = (Button) itemView.findViewById(R.id.tt_creative_btn);
                mAdLogoView = (RelativeLayout) itemView.findViewById(R.id.tt_ad_logo);
            }
        }

        private static class VerticalAdViewHolder extends AdViewHolder {
            ImageView mVerticalImage;

            @SuppressWarnings("RedundantCast")
            public VerticalAdViewHolder(View itemView) {
                super(itemView);

                mDislike = (ImageView) itemView.findViewById(R.id.iv_listitem_dislike);
                mTitle = (TextView) itemView.findViewById(R.id.tv_listitem_ad_title);
                mSource = (TextView) itemView.findViewById(R.id.tv_listitem_ad_source);
                mDescription = (TextView) itemView.findViewById(R.id.tv_listitem_ad_desc);
                mVerticalImage = (ImageView) itemView.findViewById(R.id.iv_listitem_vertical_image);
                mIcon = (ImageView) itemView.findViewById(R.id.iv_listitem_icon);
                mCreativeButton = (Button) itemView.findViewById(R.id.tt_creative_btn);
                mAdLogoView = (RelativeLayout) itemView.findViewById(R.id.tt_ad_logo);
            }
        }


        @SuppressWarnings("CanBeFinal")
        private static class GroupAdViewHolder extends AdViewHolder {
            ImageView mGroupImage1;
            ImageView mGroupImage2;
            ImageView mGroupImage3;

            @SuppressWarnings("RedundantCast")
            public GroupAdViewHolder(View itemView) {
                super(itemView);

                mDislike = (ImageView) itemView.findViewById(R.id.iv_listitem_dislike);
                mTitle = (TextView) itemView.findViewById(R.id.tv_listitem_ad_title);
                mSource = (TextView) itemView.findViewById(R.id.tv_listitem_ad_source);
                mDescription = (TextView) itemView.findViewById(R.id.tv_listitem_ad_desc);
                mGroupImage1 = (ImageView) itemView.findViewById(R.id.iv_listitem_image1);
                mGroupImage2 = (ImageView) itemView.findViewById(R.id.iv_listitem_image2);
                mGroupImage3 = (ImageView) itemView.findViewById(R.id.iv_listitem_image3);
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
            TextView mSource;
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
