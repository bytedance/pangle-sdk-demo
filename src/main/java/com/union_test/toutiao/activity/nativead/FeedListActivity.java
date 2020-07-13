package com.union_test.toutiao.activity.nativead;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.FilterWord;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.union_test.toutiao.R;
import com.union_test.toutiao.config.TTAdManagerHolder;
import com.union_test.toutiao.dialog.DislikeDialog;
import com.union_test.toutiao.utils.TToast;
import com.union_test.toutiao.view.ILoadMoreListener;
import com.union_test.toutiao.view.LoadMoreListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Feed ad,in ListView
 */
@SuppressWarnings("ALL")
public class FeedListActivity extends Activity {
    private static final String TAG = "FeedListActivity";

    private static final int AD_POSITION = 3;
    private static final int LIST_ITEM_COUNT = 30;
    private LoadMoreListView mListView;
    private MyAdapter myAdapter;
    private List<TTFeedAd> mData;

    private TTAdNative mTTAdNative;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_listview);
        TTAdManager ttAdManager = TTAdManagerHolder.get();
        //create TTAdNative object which use to request ad，createAdNative(Context context)
        mTTAdNative = ttAdManager.createAdNative(getApplicationContext());
        initListView();
    }

    @SuppressWarnings("RedundantCast")
    private void initListView() {
        mListView = (LoadMoreListView) findViewById(R.id.my_list);
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
        //Create a parameter AdSlot for reward ad request type,
        //      refer to the document for meanings of specific parameters
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId("945092256")
                .setSupportDeepLink(true)
                .setImageAcceptedSize(640, 320)
                .setAdCount(3) //ad count from 1 to 3
                .build();
        //step5:request ad
        mTTAdNative.loadFeedAd(adSlot, new TTAdNative.FeedAdListener() {
            @Override
            public void onError(int code, String message) {
                if (mListView != null) {
                    mListView.setLoadingFinish();
                }
                TToast.show(FeedListActivity.this, message);
            }

            @Override
            public void onFeedAdLoad(List<TTFeedAd> ads) {
                if (mListView != null) {
                    mListView.setLoadingFinish();
                }

                if (ads == null || ads.isEmpty()) {
                    TToast.show(FeedListActivity.this, "on FeedAdLoaded: ad is null!");
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

    @SuppressWarnings("CanBeFinal")
    private static class MyAdapter extends BaseAdapter {

        private static final int ITEM_VIEW_TYPE_NORMAL = 0;
        private static final int ITEM_VIEW_TYPE_GROUP_PIC_AD = 1;
        private static final int ITEM_VIEW_TYPE_SMALL_PIC_AD = 2;
        private static final int ITEM_VIEW_TYPE_LARGE_PIC_AD = 3;
        private static final int ITEM_VIEW_TYPE_VIDEO = 4;
        private static final int ITEM_VIEW_TYPE_VERTICAL_IMG = 5;//竖版图片

        private int mVideoCount = 0;


        private List<TTFeedAd> mData;
        private Context mContext;

        public MyAdapter(Context context, List<TTFeedAd> data) {
            this.mContext = context;
            this.mData = data;
        }

        @Override
        public int getCount() {
            return mData.size(); // for test
        }

        @Override
        public TTFeedAd getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //ad styles, including large images, small images, group images and videos, judged by ad.getImageMode ()
        @Override
        public int getItemViewType(int position) {
            TTFeedAd ad = getItem(position);
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
            } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_VERTICAL_IMG) {
                return ITEM_VIEW_TYPE_VERTICAL_IMG;
            } else {
                TToast.show(mContext, "Image style error");
                return ITEM_VIEW_TYPE_NORMAL;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 6;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TTFeedAd ad = getItem(position);
            switch (getItemViewType(position)) {
                case ITEM_VIEW_TYPE_SMALL_PIC_AD:
                    return getSmallAdView(convertView, parent, ad);
                case ITEM_VIEW_TYPE_LARGE_PIC_AD:
                    return getLargeAdView(convertView, parent, ad);
                case ITEM_VIEW_TYPE_GROUP_PIC_AD:
                    return getGroupAdView(convertView, parent, ad);
                case ITEM_VIEW_TYPE_VIDEO:
                    return getVideoView(convertView, parent, ad);
                case ITEM_VIEW_TYPE_VERTICAL_IMG:
                    return getVerticalAdView(convertView, parent, ad);
                default:
                    return getNormalView(convertView, parent, position);
            }
        }

        /**
         * @param convertView
         * @param parent
         * @param ad
         * @return
         */
        private View getVerticalAdView(View convertView, ViewGroup parent, @NonNull final TTFeedAd ad) {
            VerticalAdViewHolder adViewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_vertical_pic, parent, false);
                adViewHolder = new VerticalAdViewHolder();
                adViewHolder.mTitle = (TextView) convertView.findViewById(R.id.tv_listitem_ad_title);
                adViewHolder.mSource = (TextView) convertView.findViewById(R.id.tv_listitem_ad_source);
                adViewHolder.mDescription = (TextView) convertView.findViewById(R.id.tv_listitem_ad_desc);
                adViewHolder.mVerticalImage = (ImageView) convertView.findViewById(R.id.iv_listitem_vertical_image);
                adViewHolder.mIcon = (ImageView) convertView.findViewById(R.id.iv_listitem_icon);
                adViewHolder.mDislike = (ImageView) convertView.findViewById(R.id.iv_listitem_dislike);
                adViewHolder.mCreativeButton = (Button) convertView.findViewById(R.id.tt_creative_btn);
                convertView.setTag(adViewHolder);
            } else {
                adViewHolder = (VerticalAdViewHolder) convertView.getTag();
            }
            ArrayList<View> images = new ArrayList<>();
            images.add(adViewHolder.mVerticalImage);
            bindData(convertView, adViewHolder,images, ad);
            if (ad.getImageList() != null && !ad.getImageList().isEmpty()) {
                TTImage image = ad.getImageList().get(0);
                if (image != null && image.isValid()) {
                    Glide.with(mContext).load(image.getImageUrl()).into(adViewHolder.mVerticalImage);
                }
            }

            return convertView;
        }

        @SuppressWarnings("RedundantCast")
        private View getVideoView(View convertView, ViewGroup parent, @NonNull final TTFeedAd ad) {
            final VideoAdViewHolder adViewHolder;
            try {
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_large_video, parent, false);
                    adViewHolder = new VideoAdViewHolder();
                    adViewHolder.mTitle = (TextView) convertView.findViewById(R.id.tv_listitem_ad_title);
                    adViewHolder.mDescription = (TextView) convertView.findViewById(R.id.tv_listitem_ad_desc);
                    adViewHolder.mSource = (TextView) convertView.findViewById(R.id.tv_listitem_ad_source);
                    adViewHolder.videoView = (FrameLayout) convertView.findViewById(R.id.iv_listitem_video);
                    adViewHolder.mIcon = (ImageView) convertView.findViewById(R.id.iv_listitem_icon);
                    adViewHolder.mDislike = (ImageView) convertView.findViewById(R.id.iv_listitem_dislike);
                    adViewHolder.mCreativeButton = (Button) convertView.findViewById(R.id.tt_creative_btn);
                    ImageView imageView = (ImageView) ad.getAdLogoView();
                    adViewHolder.mAdLogoView = (RelativeLayout) convertView.findViewById(R.id.tt_ad_logo);
                    addAdLogoView(adViewHolder.mAdLogoView, imageView);
                    convertView.setTag(adViewHolder);
                } else {
                    adViewHolder = (VideoAdViewHolder) convertView.getTag();
                }

                //set callback（optional）
                ad.setVideoAdListener(new TTFeedAd.VideoAdListener() {
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
                        Log.e("VideoAdListener", "===onProgressUpdate current:" + current + " duration:" + duration);
                    }

                    @Override
                    public void onVideoAdComplete(TTFeedAd ad) {
                        Log.e("VideoAdListener", "===onVideoAdComplete");
                    }
                });
                Log.e("VideoAdListener", "video ad duration:" + ad.getVideoDuration());
                //set data and interaction
                ArrayList<View> images = new ArrayList<>();
                images.add(adViewHolder.videoView);
                bindData(convertView, adViewHolder,images, ad);
                if (adViewHolder.videoView != null) {
                    //get ad view
                    View video = ad.getAdView();
                    if (video != null) {
                        if (video.getParent() == null) {
                            adViewHolder.videoView.removeAllViews();
                            adViewHolder.videoView.addView(video);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }

        @SuppressWarnings("RedundantCast")
        private View getLargeAdView(View convertView, ViewGroup parent, @NonNull final TTFeedAd ad) {
            final LargeAdViewHolder adViewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_large_pic_layout, parent, false);
                adViewHolder = new LargeAdViewHolder();
                adViewHolder.mTitle = (TextView) convertView.findViewById(R.id.tv_listitem_ad_title);
                adViewHolder.mDescription = (TextView) convertView.findViewById(R.id.tv_listitem_ad_desc);
                adViewHolder.mSource = (TextView) convertView.findViewById(R.id.tv_listitem_ad_source);
                adViewHolder.mLargeImage = (ImageView) convertView.findViewById(R.id.iv_listitem_large_image);
                adViewHolder.mIcon = (ImageView) convertView.findViewById(R.id.iv_listitem_icon);
                adViewHolder.mDislike = (ImageView) convertView.findViewById(R.id.iv_listitem_dislike);
                adViewHolder.mCreativeButton = (Button) convertView.findViewById(R.id.tt_creative_btn);
                ImageView imageView = (ImageView) ad.getAdLogoView();
                adViewHolder.mAdLogoView = (RelativeLayout) convertView.findViewById(R.id.tt_ad_logo);
                addAdLogoView(adViewHolder.mAdLogoView, imageView);
                convertView.setTag(adViewHolder);
            } else {
                adViewHolder = (LargeAdViewHolder) convertView.getTag();
            }
            ArrayList<View> images = new ArrayList<>();
            images.add(adViewHolder.mLargeImage);
            bindData(convertView, adViewHolder,images, ad);
            if (ad.getImageList() != null && !ad.getImageList().isEmpty()) {
                TTImage image = ad.getImageList().get(0);
                if (image != null && image.isValid()) {
                    Glide.with(mContext).load(image.getImageUrl()).into(adViewHolder.mLargeImage);
                }
            }
            return convertView;
        }

        private void addAdLogoView(RelativeLayout layout, ImageView logo) {
            if (layout == null || logo == null) {
                return;
            }
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            layout.addView(logo, lp);
        }

        @SuppressWarnings("RedundantCast")
        private View getGroupAdView(View convertView, ViewGroup parent, @NonNull final TTFeedAd ad) {
            GroupAdViewHolder adViewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_group_pic, parent, false);
                adViewHolder = new GroupAdViewHolder();
                adViewHolder.mTitle = (TextView) convertView.findViewById(R.id.tv_listitem_ad_title);
                adViewHolder.mSource = (TextView) convertView.findViewById(R.id.tv_listitem_ad_source);
                adViewHolder.mDescription = (TextView) convertView.findViewById(R.id.tv_listitem_ad_desc);
                adViewHolder.mGroupImage1 = (ImageView) convertView.findViewById(R.id.iv_listitem_image1);
                adViewHolder.mGroupImage2 = (ImageView) convertView.findViewById(R.id.iv_listitem_image2);
                adViewHolder.mGroupImage3 = (ImageView) convertView.findViewById(R.id.iv_listitem_image3);
                adViewHolder.mIcon = (ImageView) convertView.findViewById(R.id.iv_listitem_icon);
                adViewHolder.mDislike = (ImageView) convertView.findViewById(R.id.iv_listitem_dislike);
                adViewHolder.mCreativeButton = (Button) convertView.findViewById(R.id.tt_creative_btn);
                adViewHolder.mAdLogoView = convertView.findViewById(R.id.tt_ad_logo);
                convertView.setTag(adViewHolder);
            } else {
                adViewHolder = (GroupAdViewHolder) convertView.getTag();
            }
            ArrayList<View> images = new ArrayList<>();
            images.add(adViewHolder.mGroupImage1);
            images.add(adViewHolder.mGroupImage2);
            images.add(adViewHolder.mGroupImage3);
            bindData(convertView, adViewHolder,images, ad);
            if (ad.getImageList() != null && ad.getImageList().size() >= 3) {
                TTImage image1 = ad.getImageList().get(0);
                TTImage image2 = ad.getImageList().get(1);
                TTImage image3 = ad.getImageList().get(2);
                if (image1 != null && image1.isValid()) {
                    Glide.with(mContext).load(image1.getImageUrl()).into(adViewHolder.mGroupImage1);
                }
                if (image2 != null && image2.isValid()) {
                    Glide.with(mContext).load(image2.getImageUrl()).into(adViewHolder.mGroupImage2);
                }
                if (image3 != null && image3.isValid()) {
                    Glide.with(mContext).load(image3.getImageUrl()).into(adViewHolder.mGroupImage3);
                }
            }
            return convertView;
        }


        @SuppressWarnings("RedundantCast")
        private View getSmallAdView(View convertView, ViewGroup parent, @NonNull final TTFeedAd ad) {
            SmallAdViewHolder adViewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_small_pic, parent, false);
                adViewHolder = new SmallAdViewHolder();
                adViewHolder.mTitle = (TextView) convertView.findViewById(R.id.tv_listitem_ad_title);
                adViewHolder.mSource = (TextView) convertView.findViewById(R.id.tv_listitem_ad_source);
                adViewHolder.mDescription = (TextView) convertView.findViewById(R.id.tv_listitem_ad_desc);
                adViewHolder.mSmallImage = (ImageView) convertView.findViewById(R.id.iv_listitem_small_image);
                adViewHolder.mIcon = (ImageView) convertView.findViewById(R.id.iv_listitem_icon);
                adViewHolder.mDislike = (ImageView) convertView.findViewById(R.id.iv_listitem_dislike);
                adViewHolder.mCreativeButton = (Button) convertView.findViewById(R.id.tt_creative_btn);
                ImageView imageView = (ImageView) ad.getAdLogoView();
                adViewHolder.mAdLogoView = (RelativeLayout) convertView.findViewById(R.id.tt_ad_logo);
                addAdLogoView(adViewHolder.mAdLogoView, imageView);
                convertView.setTag(adViewHolder);
            } else {
                adViewHolder = (SmallAdViewHolder) convertView.getTag();
            }
            ArrayList<View> images = new ArrayList<>();
            images.add(adViewHolder.mSmallImage);
            bindData(convertView, adViewHolder,images, ad);
            if (ad.getImageList() != null && !ad.getImageList().isEmpty()) {
                TTImage image = ad.getImageList().get(0);
                if (image != null && image.isValid()) {
                    Glide.with(mContext).load(image.getImageUrl()).into(adViewHolder.mSmallImage);
                }
            }

            //dislike
//            final TTAdDislike ttAdDislike = ad.getDislikeDialog();
//            if (ttAdDislike != null) {
//                ttAdDislike.setDislikeInteractionCallback(new TTAdDislike.DislikeInteractionCallback() {
//                    @Override
//                    public void onSelected(int position, String value) {
//
//                    }
//
//                    @Override
//                    public void onCancel() {
//
//                    }
//                });
//                dislikeIcon.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        ttAdDislike.showDislikeDialog();
//                    }
//                });
//            }
            return convertView;
        }

        /**
         * normal list item
         *
         * @param convertView
         * @param parent
         * @param position
         * @return
         */
        @SuppressWarnings("RedundantCast")
        @SuppressLint("SetTextI18n")
        private View getNormalView(View convertView, ViewGroup parent, int position) {
            NormalViewHolder normalViewHolder;
            if (convertView == null) {
                normalViewHolder = new NormalViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_normal, parent, false);
                normalViewHolder.idle = (TextView) convertView.findViewById(R.id.text_idle);
                convertView.setTag(normalViewHolder);
            } else {
                normalViewHolder = (NormalViewHolder) convertView.getTag();
            }
            normalViewHolder.idle.setText("ListView item " + position);
            return convertView;
        }

        private void bindDislikeCustom(View dislike, final TTFeedAd ad) {
            final TTAdDislike ttAdDislike = ad.getDislikeDialog((Activity) mContext);
            if (ttAdDislike != null) {
                ad.getDislikeDialog((Activity) mContext).setDislikeInteractionCallback(new TTAdDislike.DislikeInteractionCallback() {
                    @Override
                    public void onSelected(int position, String value) {
                        mData.remove(ad);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
            dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ttAdDislike != null)
                       ttAdDislike.showDislikeDialog();
                }
            });
//            List<FilterWord> words = ad.getFilterWords();
//            if (words == null || words.isEmpty()) {
//                return;
//            }
//
//            final DislikeDialog dislikeDialog = new DislikeDialog(mContext, words);
//            dislikeDialog.setOnDislikeItemClick(new DislikeDialog.OnDislikeItemClick() {
//                @Override
//                public void onItemClick(FilterWord filterWord) {
//                    //remove
//                    mData.remove(ad);
//                    notifyDataSetChanged();
//                }
//            });
//            final TTAdDislike ttAdDislike = ad.getDislikeDialog(dislikeDialog);
//
//            dislike.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //show dislike
//                    dislikeDialog.show();
//
//                    //or
//                    //ttAdDislike.showDislikeDialog();
//                }
//            });
        }
        private void bindData(View convertView, final AdViewHolder adViewHolder,List<View> images, TTFeedAd ad) {
            //set dislike
            bindDislikeCustom(adViewHolder.mDislike, ad);

            //the views that can be clicked
            List<View> clickViewList = new ArrayList<>();
            clickViewList.add(convertView);
            //The views that can trigger the creative action (like download app)
            List<View> creativeViewList = new ArrayList<>();
            creativeViewList.add(adViewHolder.mCreativeButton);
            //notice! This involves advertising billing and must be called correctly. convertView must use ViewGroup.
            ad.registerViewForInteraction((ViewGroup) convertView, images,clickViewList, creativeViewList,adViewHolder.mDislike, new TTNativeAd.AdInteractionListener() {
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
            adViewHolder.mTitle.setText(ad.getTitle()); //title
            adViewHolder.mDescription.setText(ad.getDescription()); //description
            String adSource = "Ad Source";
            if (mContext != null) {
                adSource = mContext.getString(R.string.tt_ad_source);
            }
            adViewHolder.mSource.setText(ad.getSource() == null ? adSource : ad.getSource());
            TTImage icon = ad.getIcon();
            if (icon != null && icon.isValid()) {
                Glide.with(mContext).load(icon.getImageUrl()).into(adViewHolder.mIcon);
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

        private static class VideoAdViewHolder extends AdViewHolder {
            FrameLayout videoView;
        }

        private static class LargeAdViewHolder extends AdViewHolder {
            ImageView mLargeImage;
        }

        private static class SmallAdViewHolder extends AdViewHolder {
            ImageView mSmallImage;
        }

        private static class VerticalAdViewHolder extends AdViewHolder {
            ImageView mVerticalImage;
        }

        private static class GroupAdViewHolder extends AdViewHolder {
            ImageView mGroupImage1;
            ImageView mGroupImage2;
            ImageView mGroupImage3;
        }

        private static class AdViewHolder {
            ImageView mIcon;
            ImageView mDislike;
            Button mCreativeButton;
            TextView mTitle;
            TextView mDescription;
            TextView mSource;
            RelativeLayout mAdLogoView;
        }

        private static class NormalViewHolder {
            TextView idle;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TToast.reset();
        mHandler.removeCallbacksAndMessages(null);
    }
}
