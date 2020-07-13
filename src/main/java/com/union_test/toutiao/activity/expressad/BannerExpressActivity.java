package com.union_test.toutiao.activity.expressad;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.FilterWord;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.union_test.toutiao.R;
import com.union_test.toutiao.config.TTAdManagerHolder;
import com.union_test.toutiao.dialog.DislikeDialog;
import com.union_test.toutiao.utils.TToast;
import com.union_test.toutiao.view.LoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * created by wuzejian on 2019-12-22
 */
@SuppressWarnings("unused")
public class BannerExpressActivity extends Activity {

    private TTAdNative mTTAdNative;
    private FrameLayout mExpressContainer;
    private Context mContext;
    private TTAdDislike mTTAdDislike;
    private TTNativeExpressAd mTTAd;
    private LoadMoreRecyclerView mListView;
    private List<AdSizeModel> mBannerAdSizeModelList;
    private long startTime = 0;
    private boolean mHasShowDownloadActive = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_native_express_banner);
        mContext = this.getApplicationContext();
        initView();
        initData();
        initRecycleView();
        initTTSDKConfig();

    }

    private void initTTSDKConfig() {
        //step2:create TTAdNative object which use to request ad，createAdNative(Context context),the context should be an Activity object
        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
    }

    private void initRecycleView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        mListView.setLayoutManager(layoutManager);
        AdapterForBannerType adapterForBannerType = new AdapterForBannerType(this, mBannerAdSizeModelList);
        mListView.setAdapter(adapterForBannerType);

    }

    private void initView() {
        mExpressContainer = (FrameLayout) findViewById(R.id.express_container);
        mListView = findViewById(R.id.my_list);
    }

    private void initData() {
        mBannerAdSizeModelList = new ArrayList<>();
        mBannerAdSizeModelList.add(new AdSizeModel("600*90", 600, 0, "945097344"));
        mBannerAdSizeModelList.add(new AdSizeModel("600*150", 600, 0, "945097344"));
        mBannerAdSizeModelList.add(new AdSizeModel("600*260", 600, 0, "945097344"));
        mBannerAdSizeModelList.add(new AdSizeModel("600*300", 600, 0, "945097344"));
        mBannerAdSizeModelList.add(new AdSizeModel("600*400", 600, 0, "945113149"));
        mBannerAdSizeModelList.add(new AdSizeModel("640*100", 640, 0, "945097344"));
        mBannerAdSizeModelList.add(new AdSizeModel("690*388", 690, 0, "945097344"));
        mBannerAdSizeModelList.add(new AdSizeModel("690*500", 690, 0, "945113148"));
    }


    public static class AdapterForBannerType extends RecyclerView.Adapter<AdapterForBannerType.ViewHolder> {
        private List<AdSizeModel> mBannerSizeList;
        private BannerExpressActivity mActivity;

        public AdapterForBannerType(BannerExpressActivity activity, List<AdSizeModel> bannerSize) {
            this.mActivity = activity;
            this.mBannerSizeList = bannerSize;
        }

        @NonNull
        @Override
        public AdapterForBannerType.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.express_banner_list_item, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterForBannerType.ViewHolder viewHolder, int i) {
            final AdSizeModel bannerSize = mBannerSizeList == null ? null : mBannerSizeList.get(i);
            if (bannerSize != null) {
                viewHolder.btnSize.setText(bannerSize.adSizeName);
                viewHolder.btnSize.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //request banner ad
                        mActivity.loadExpressAd(bannerSize.codeId, bannerSize.width, bannerSize.height);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mBannerSizeList != null ? mBannerSizeList.size() : 0;
        }


        public static class ViewHolder extends RecyclerView.ViewHolder {
            private Button btnSize;

            public ViewHolder(View view) {
                super(view);
                btnSize = view.findViewById(R.id.btn_banner_size);
            }

        }
    }

    private void loadExpressAd(String codeId, int expressViewWidth, int expressViewHeight) {
        mExpressContainer.removeAllViews();
        //step3:Create a parameter AdSlot for reward ad request type,
        //      refer to the document for meanings of specific parameters
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId) //rit
                .setSupportDeepLink(true)
                .setAdCount(1) //ad count from 1 to 3
                .setExpressViewAcceptedSize(expressViewWidth,expressViewHeight) //expected ad view size in dp
                .build();
        //step4:request ad，then call render method of TTNativeExpressAd
        mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                TToast.show(BannerExpressActivity.this, "load error : " + code + ", " + message);
                mExpressContainer.removeAllViews();
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0){
                    return;
                }
                mTTAd = ads.get(0);
                bindAdListener(mTTAd);
                startTime = System.currentTimeMillis();
                mTTAd.render();
            }
        });
    }


    private void bindAdListener(TTNativeExpressAd ad) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int type) {
                TToast.show(mContext, "Ad clicked");
            }

            @Override
            public void onAdShow(View view, int type) {
                TToast.show(mContext, "Ad showed");
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Log.e("ExpressView","render fail:"+(System.currentTimeMillis() - startTime));
                TToast.show(mContext, msg+" code:"+code);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                Log.e("ExpressView","render suc:"+(System.currentTimeMillis() - startTime));
                //the width and height of view in dp
                TToast.show(mContext, "Render success");
                mExpressContainer.removeAllViews();
                mExpressContainer.addView(view);
            }
        });
        //dislike
        bindDislike(ad, false);
        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD){
            return;
        }
    }

    /**
     * Set the dislike of the advertisement.
     * Note: It is strongly recommended to set this logic.
     * If dislike processing logic is not set, the dislike area in the template advertisement will not respond to the dislike event.
     *
     * @param ad
     * @param customStyle Whether to customize the style，true:customize the style
     */
    private void bindDislike(TTNativeExpressAd ad, boolean customStyle) {
        if (customStyle) {
            List<FilterWord> words = ad.getFilterWords();
            if (words == null || words.isEmpty()) {
                return;
            }

            final DislikeDialog dislikeDialog = new DislikeDialog(this, words);
            dislikeDialog.setOnDislikeItemClick(new DislikeDialog.OnDislikeItemClick() {
                @Override
                public void onItemClick(FilterWord filterWord) {
                    //remove ad
                    TToast.show(mContext, "click " + filterWord.getName());
                    mExpressContainer.removeAllViews();
                }
            });
            ad.setDislikeDialog(dislikeDialog);
            return;
        }
        //Use the default dislike popup style
        ad.setDislikeCallback(BannerExpressActivity.this, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onSelected(int position, String value) {
                TToast.show(mContext, "click " + value);
                //remove ad
                mExpressContainer.removeAllViews();
            }

            @Override
            public void onCancel() {
                TToast.show(mContext, "Cancel click ");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTTAd != null) {
            mTTAd.destroy();
        }
    }


    public static class AdSizeModel {
        public AdSizeModel(String adSizeName, int width, int height, String codeId) {
            this.adSizeName = adSizeName;
            this.width = width;
            this.height = height;
            this.codeId = codeId;
        }

        public String adSizeName;
        public int width;
        public int height;
        public String codeId;
    }
}
