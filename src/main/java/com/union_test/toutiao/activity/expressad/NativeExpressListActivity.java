package com.union_test.toutiao.activity.expressad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.FilterWord;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.union_test.toutiao.R;
import com.union_test.toutiao.config.TTAdManagerHolder;
import com.union_test.toutiao.dialog.DislikeDialog;
import com.union_test.toutiao.utils.TToast;
import com.union_test.toutiao.view.ILoadMoreListener;
import com.union_test.toutiao.view.LoadMoreListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Feed广告使用示例,使用ListView
 */
@SuppressWarnings("ALL")
public class NativeExpressListActivity extends Activity {
    private static final String TAG = "FeedListActivity";

    private static final int AD_POSITION = 3;
    private static final int LIST_ITEM_COUNT = 30;
    private LoadMoreListView mListView;
    private MyAdapter myAdapter;
    private List<TTNativeExpressAd> mData;
    private EditText mEtWidth;
    private EditText mEtHeight;
    private Button mButtonLoadAd;
    private TTAdNative mTTAdNative;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_express_listview);
        TTAdManager ttAdManager = TTAdManagerHolder.get();
        //create TTAdNative object which use to request ad，createAdNative(Context context)
        mTTAdNative = ttAdManager.createAdNative(getApplicationContext());
        initListView();
    }

    @SuppressWarnings("RedundantCast")
    private void initListView() {
        mEtHeight = (EditText) findViewById(R.id.express_height);
        mEtWidth = (EditText) findViewById(R.id.express_width);
        mButtonLoadAd = (Button) findViewById(R.id.btn_express_load);
        mButtonLoadAd.setOnClickListener(mClickListener);

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

    private final View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_express_load) {
                if (mData != null) {
                    mData.clear();
                    if (myAdapter != null) {
                        myAdapter.notifyDataSetChanged();
                    }
                }
                loadListAd();
            }
        }
    };

    /**
     * 加载feed广告
     */
    private void loadListAd() {
        float expressViewWidth = 350;
        float expressViewHeight = 350;
        try {
            expressViewWidth = Float.parseFloat(mEtWidth.getText().toString());
            expressViewHeight = Float.parseFloat(mEtHeight.getText().toString());
        } catch (Exception e) {
            expressViewHeight = 0; //Set the height to 0, the height will be adaptive
        }
        //Create a parameter AdSlot for reward ad request type,
        //   refer to the document for meanings of specific parameters
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId("945113157")
                .setSupportDeepLink(true)
                .setExpressViewAcceptedSize(expressViewWidth, expressViewHeight) //expected ad view size in dp
                .setAdCount(3) //ad count from 1 to 3
                .build();
        //step5:request ad，then call render method of TTNativeExpressAd
        mTTAdNative.loadNativeExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                if (mListView != null) {
                    mListView.setLoadingFinish();
                }
                TToast.show(NativeExpressListActivity.this, message);
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (mListView != null) {
                    mListView.setLoadingFinish();
                }

                if (ads == null || ads.isEmpty()) {
                    TToast.show(NativeExpressListActivity.this, "on FeedAdLoaded: ad is null!");
                    return;
                }

                for (int i = 0; i < LIST_ITEM_COUNT; i++) {
                    mData.add(null);
                }
                bindAdListener(ads);
            }
        });
    }

    private void bindAdListener(final List<TTNativeExpressAd> ads) {
        final int count = mData.size();
        for (TTNativeExpressAd ad : ads) {
            final TTNativeExpressAd adTmp = ad;
            int random = (int) (Math.random() * LIST_ITEM_COUNT) + count - LIST_ITEM_COUNT;
            mData.set(random, adTmp);
            myAdapter.notifyDataSetChanged();

            adTmp.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
                @Override
                public void onAdClicked(View view, int type) {
                    TToast.show(NativeExpressListActivity.this, "Ad clicked");
                }

                @Override
                public void onAdShow(View view, int type) {
                    TToast.show(NativeExpressListActivity.this, "Ad showed");
                }

                @Override
                public void onRenderFail(View view, String msg, int code) {
                    TToast.show(NativeExpressListActivity.this, msg + " code:" + code);
                }

                @Override
                public void onRenderSuccess(View view, float width, float height) {
                    //the width and height of view in dp
                    TToast.show(NativeExpressListActivity.this, "Render success");
                    myAdapter.notifyDataSetChanged();
                }
            });
            ad.render();

        }

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


        private List<TTNativeExpressAd> mData;
        private Context mContext;

        public MyAdapter(Context context, List<TTNativeExpressAd> data) {
            this.mContext = context;
            this.mData = data;
        }

        @Override
        public int getCount() {
            return mData.size(); // for test
        }

        @Override
        public TTNativeExpressAd getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //ad styles, including large images, small images, group images and videos, judged by ad.getImageMode ()
        @Override
        public int getItemViewType(int position) {
            TTNativeExpressAd ad = getItem(position);
            if (ad == null) {
                return ITEM_VIEW_TYPE_NORMAL;
            } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_SMALL_IMG) {
                return ITEM_VIEW_TYPE_SMALL_PIC_AD;
            } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_LARGE_IMG) {
                return ITEM_VIEW_TYPE_LARGE_PIC_AD;
            } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_GROUP_IMG) {
                return ITEM_VIEW_TYPE_GROUP_PIC_AD;
            } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_VIDEO || ad.getImageMode() == TTAdConstant.IMAGE_MODE_VIDEO_VERTICAL) {
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
            return 5;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TTNativeExpressAd ad = getItem(position);
            switch (getItemViewType(position)) {
                case ITEM_VIEW_TYPE_SMALL_PIC_AD:
                case ITEM_VIEW_TYPE_LARGE_PIC_AD:
                case ITEM_VIEW_TYPE_GROUP_PIC_AD:
                case ITEM_VIEW_TYPE_VERTICAL_IMG:
                case ITEM_VIEW_TYPE_VIDEO:
                    return getVideoView(convertView, parent, ad);
                default:
                    return getNormalView(convertView, parent, position);
            }
        }

        @SuppressWarnings("RedundantCast")
        private View getVideoView(View convertView, ViewGroup parent, @NonNull final TTNativeExpressAd ad) {
            final AdViewHolder adViewHolder;
            try {
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_native_express, parent, false);
                    adViewHolder = new AdViewHolder();
                    adViewHolder.videoView = (FrameLayout) convertView.findViewById(R.id.iv_listitem_express);
                    convertView.setTag(adViewHolder);
                } else {
                    adViewHolder = (AdViewHolder) convertView.getTag();
                }

                //bind data and action
                bindData(convertView, adViewHolder, ad);
                if (adViewHolder.videoView != null) {
                    //get ad view
                    View video = ad.getExpressAdView();
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

        /**
         * Set the dislike of the advertisement.
         * Note: It is strongly recommended to set this logic.
         * If dislike processing logic is not set, the dislike area in the template advertisement will not respond to the dislike event.
         *
         * @param ad
         * @param customStyle Whether to customize the style，true:customize the style
         */
        private void bindDislike(final TTNativeExpressAd ad, boolean customStyle) {
            if (customStyle) {
                List<FilterWord> words = ad.getFilterWords();
                if (words == null || words.isEmpty()) {
                    return;
                }

                final DislikeDialog dislikeDialog = new DislikeDialog(mContext, words);
                dislikeDialog.setOnDislikeItemClick(new DislikeDialog.OnDislikeItemClick() {
                    @Override
                    public void onItemClick(FilterWord filterWord) {
                        //remove ad
                        TToast.show(mContext, "Click " + filterWord.getName());
                        mData.remove(ad);
                        notifyDataSetChanged();
                    }
                });
                ad.setDislikeDialog(dislikeDialog);
                return;
            }
            //Use the default dislike popup style
            ad.setDislikeCallback((Activity) mContext, new TTAdDislike.DislikeInteractionCallback() {
                @Override
                public void onSelected(int position, String value) {
                    TToast.show(mContext, "click " + value);
                    mData.remove(ad);
                    notifyDataSetChanged();
                }

                @Override
                public void onCancel() {
                    TToast.show(mContext, "Cancel click ");
                }
            });
        }

        private void bindData(View convertView, final AdViewHolder adViewHolder, TTNativeExpressAd ad) {
            bindDislike(ad, true);
        }



        private static class AdViewHolder {
            FrameLayout videoView;
        }

        private static class NormalViewHolder {
            TextView idle;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mData != null) {
            for (TTNativeExpressAd ad : mData) {
                if (ad != null) {
                    ad.destroy();
                }
            }
        }
        mHandler.removeCallbacksAndMessages(null);
    }
}
