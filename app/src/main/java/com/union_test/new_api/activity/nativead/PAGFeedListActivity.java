package com.union_test.new_api.activity.nativead;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bytedance.sdk.openadsdk.api.nativeAd.PAGImageItem;
import com.bytedance.sdk.openadsdk.api.nativeAd.PAGNativeAd;
import com.bytedance.sdk.openadsdk.api.nativeAd.PAGNativeAdData;
import com.bytedance.sdk.openadsdk.api.nativeAd.PAGNativeAdInteractionListener;
import com.bytedance.sdk.openadsdk.api.nativeAd.PAGNativeAdLoadListener;
import com.bytedance.sdk.openadsdk.api.nativeAd.PAGNativeRequest;
import com.bytedance.sdk.openadsdk.api.nativeAd.PAGVideoAdListener;
import com.bytedance.sdk.openadsdk.api.nativeAd.PAGVideoMediaView;
import com.union_test.internationad.R;
import com.union_test.new_api.RitConstants;
import com.union_test.new_api.utils.TToast;
import com.union_test.new_api.view.ILoadMoreListener;
import com.union_test.new_api.view.LoadMoreListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Feed ad,in ListView
 */
@SuppressWarnings("ALL")
public class PAGFeedListActivity extends Activity {
    private static final String TAG = "PAGFeedListActivity";

    private static final int AD_POSITION = 3;
    private static final int LIST_ITEM_COUNT = 10;
    private LoadMoreListView mListView;
    private MyAdapter myAdapter;
    private List<PAGNativeAd> mData;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    // pangle feed ad
    private PAGNativeAd mPAGNativeAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_listview);
        findViewById(R.id.btn_fl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

        PAGNativeRequest request = new PAGNativeRequest();
        PAGNativeAd.loadAd(RitConstants.RIT_NATIVE, request, new PAGNativeAdLoadListener() {
            @Override
            public void onError(int code, String message) {
                if (mListView != null) {
                    mListView.setLoadingFinish();
                }
                TToast.show(PAGFeedListActivity.this, message);
            }

            @Override
            public void onAdLoaded(PAGNativeAd pagNativeAd) {
                if (mListView != null) {
                    mListView.setLoadingFinish();
                }

                if (pagNativeAd == null) {
                    TToast.show(PAGFeedListActivity.this, "on FeedAdLoaded: ad is null!");
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

    @SuppressWarnings("CanBeFinal")
    private static class MyAdapter extends BaseAdapter {

        private static final int ITEM_VIEW_TYPE_NORMAL = 0;
        private static final int ITEM_VIEW_TYPE_AD = 1;
        private int mVideoCount = 0;
        private List<PAGNativeAd> mData;
        private Context mContext;

        public MyAdapter(Context context, List<PAGNativeAd> data) {
            this.mContext = context;
            this.mData = data;
        }

        @Override
        public int getCount() {
            return mData.size(); // for test
        }

        @Override
        public PAGNativeAd getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            PAGNativeAd ad = getItem(position);
            if (ad == null) {
                return ITEM_VIEW_TYPE_NORMAL;
            } else {
                return ITEM_VIEW_TYPE_AD;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PAGNativeAd ad = getItem(position);
            switch (getItemViewType(position)) {
                case ITEM_VIEW_TYPE_AD:
                    return getPangleAdView(convertView, parent, ad);
                default:
                    return getNormalView(convertView, parent, position);
            }
        }


        @SuppressWarnings("RedundantCast")
        private View getPangleAdView(View convertView, ViewGroup parent, @NonNull final PAGNativeAd ad) {
            final VideoAdViewHolder adViewHolder;
            try {
                PAGNativeAdData adData = ad != null ? ad.getNativeAdData() : null;
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.new_api_listitem_pangle_feed_ad_view, parent, false);
                    adViewHolder = new VideoAdViewHolder();
                    adViewHolder.mTitle = (TextView) convertView.findViewById(R.id.tv_listitem_ad_title);
                    adViewHolder.mDescription = (TextView) convertView.findViewById(R.id.tv_listitem_ad_desc);
                    adViewHolder.videoView = (FrameLayout) convertView.findViewById(R.id.iv_listitem_video);
                    adViewHolder.mIcon = (ImageView) convertView.findViewById(R.id.iv_listitem_icon);
                    adViewHolder.mDislike = (ImageView) convertView.findViewById(R.id.iv_listitem_dislike);
                    adViewHolder.mCreativeButton = (Button) convertView.findViewById(R.id.tt_creative_btn);
                    ImageView imageView = (ImageView) adData.getAdLogoView();
                    adViewHolder.mAdLogoView = (RelativeLayout) convertView.findViewById(R.id.tt_ad_logo);
                    addAdLogoView(adViewHolder.mAdLogoView, imageView);
                    convertView.setTag(adViewHolder);
                } else {
                    adViewHolder = (VideoAdViewHolder) convertView.getTag();
                }

                //set data and interaction
                ArrayList<View> images = new ArrayList<>();
                images.add(adViewHolder.videoView);
                bindData(convertView, adViewHolder, images, ad, adData);
                if (adViewHolder.videoView != null) {
                    //get ad view
                    View video = adData.getMediaView();
                    if (video instanceof PAGVideoMediaView) {
                        ((PAGVideoMediaView) video).setVideoAdListener(new PAGVideoAdListener() {
                            @Override
                            public void onVideoAdPlay() {
                                Log.e(TAG, "video onVideoAdPlay");
                            }

                            @Override
                            public void onVideoAdPaused() {
                                Log.e(TAG, "video onVideoAdPaused");
                            }

                            @Override
                            public void onVideoAdComplete() {
                                Log.e(TAG, "video onVideoAdComplete");
                            }

                            @Override
                            public void onVideoError() {
                                Log.e(TAG, "video onVideoError");
                            }
                        });
                    }
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

        private void addAdLogoView(RelativeLayout layout, ImageView logo) {
            if (layout == null || logo == null) {
                return;
            }
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            layout.addView(logo, lp);
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

        private void bindData(View convertView, final AdViewHolder adViewHolder, List<View> images, final PAGNativeAd ad, final PAGNativeAdData adData) {
            //the views that can be clicked
            List<View> clickViewList = new ArrayList<>();
            clickViewList.add(convertView);
            clickViewList.addAll(images);
            //The views that can trigger the creative action (like download app)
            List<View> creativeViewList = new ArrayList<>();
            creativeViewList.add(adViewHolder.mCreativeButton);
            //notice! This involves advertising billing and must be called correctly. convertView must use ViewGroup.
            ad.registerViewForInteraction((ViewGroup) convertView, clickViewList, creativeViewList, adViewHolder.mDislike, new PAGNativeAdInteractionListener() {
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
            adViewHolder.mTitle.setText(adData.getTitle()); //title
            adViewHolder.mDescription.setText(adData.getDescription()); //description
            PAGImageItem icon = adData.getIcon();
            if (icon != null && icon.getImageUrl() != null) {
                Glide.with(mContext).load(icon.getImageUrl()).into(adViewHolder.mIcon);
            }
            Button adCreativeButton = adViewHolder.mCreativeButton;
            adCreativeButton.setText(TextUtils.isEmpty(adData.getButtonText()) ? mContext.getString(R.string.tt_native_banner_download) : adData.getButtonText());
        }

        private static class VideoAdViewHolder extends AdViewHolder {
            FrameLayout videoView;
        }


        private static class AdViewHolder {
            ImageView mIcon;
            ImageView mDislike;
            Button mCreativeButton;
            TextView mTitle;
            TextView mDescription;
            RelativeLayout mAdLogoView;
        }

        private static class NormalViewHolder {
            TextView idle;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TToast.clearOnDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
