package com.union_test.new_api.activity.expressad;

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

import com.bytedance.sdk.openadsdk.api.banner.PAGBannerAd;
import com.bytedance.sdk.openadsdk.api.banner.PAGBannerAdInteractionListener;
import com.bytedance.sdk.openadsdk.api.banner.PAGBannerAdLoadListener;
import com.bytedance.sdk.openadsdk.api.banner.PAGBannerRequest;
import com.bytedance.sdk.openadsdk.api.banner.PAGBannerSize;
import com.union_test.internationad.R;
import com.union_test.new_api.RitConstants;
import com.union_test.new_api.utils.TToast;
import com.union_test.new_api.view.LoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luojun on 2022/5/7
 * Usage:
 * Doc
 */
public class PAGBannerActivity extends Activity {
    private FrameLayout mExpressContainer;
    private Context mContext;
    private PAGBannerAd mPAGBannerAd;
    private LoadMoreRecyclerView mListView;
    private List<AdSizeModel> mBannerAdSizeModelList;
    private long startTime = 0;
    private boolean mHasShowDownloadActive = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_banner);
        findViewById(R.id.btn_eb_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mContext = this.getApplicationContext();
        initView();
        initData();
        initRecycleView();

    }

    private void initRecycleView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
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
        mBannerAdSizeModelList.add(new AdSizeModel("320*50", PAGBannerSize.BANNER_W_320_H_50, RitConstants.RIT_BANNER_320X50));
        mBannerAdSizeModelList.add(new AdSizeModel("300*250", PAGBannerSize.BANNER_W_300_H_250, RitConstants.RIT_BANNER_300X250));
        mBannerAdSizeModelList.add(new AdSizeModel("728*90", PAGBannerSize.BANNER_W_728_H_90, RitConstants.RIT_BANNER_300X250));
    }


    public static class AdapterForBannerType extends RecyclerView.Adapter<AdapterForBannerType.ViewHolder> {
        private List<AdSizeModel> mBannerSizeList;
        private PAGBannerActivity mActivity;

        public AdapterForBannerType(PAGBannerActivity activity, List<AdSizeModel> bannerSize) {
            this.mActivity = activity;
            this.mBannerSizeList = bannerSize;
        }

        @NonNull
        @Override
        public AdapterForBannerType.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.express_banner_list_item, viewGroup, false);
            return new AdapterForBannerType.ViewHolder(view);
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
                        mActivity.loadExpressAd(bannerSize.codeId, bannerSize.bannerSize);
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

    private void loadExpressAd(String codeId, PAGBannerSize bannerSize) {
        mExpressContainer.removeAllViews();
        //step1:Create a parameter PAGBannerRequest for banner ad request type,
        //      refer to the document for meanings of specific parameters
        PAGBannerRequest bannerRequest = new PAGBannerRequest(bannerSize);

        //step2:request ad
        PAGBannerAd.loadAd(codeId, bannerRequest, new PAGBannerAdLoadListener() {
            @Override
            public void onError(int code, String message) {
                TToast.show(PAGBannerActivity.this, "load error : " + code + ", " + message);
                mExpressContainer.removeAllViews();
            }

            @Override
            public void onAdLoaded(PAGBannerAd bannerAd) {
                if (bannerAd == null) {
                    return;
                }
                mPAGBannerAd = bannerAd;
                TToast.show(mContext, "Load Ad Success");
                bindAdListener(mPAGBannerAd);
                if (mPAGBannerAd != null) {
                    //step3:add ad view to view container
                    mExpressContainer.addView(mPAGBannerAd.getBannerView());
                }
                startTime = System.currentTimeMillis();
                TToast.show(mContext, "load success!");

            }
        });
    }


    private void bindAdListener(PAGBannerAd ad) {
        ad.setAdInteractionListener(new PAGBannerAdInteractionListener() {
            @Override
            public void onAdShowed() {
                TToast.show(mContext, "Ad showed");
            }

            @Override
            public void onAdClicked() {
                TToast.show(mContext, "Ad clicked");
            }

            @Override
            public void onAdDismissed() {
                Log.e("ExpressView", "Ad dismissed" + (System.currentTimeMillis() - startTime));
                TToast.show(mContext, "Ad dismissed");
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPAGBannerAd != null) {
            mPAGBannerAd.destroy();
        }
    }


    public static class AdSizeModel {
        public AdSizeModel(String adSizeName, PAGBannerSize bannerSize, String codeId) {
            this.adSizeName = adSizeName;
            this.bannerSize = bannerSize;
            this.codeId = codeId;
        }

        public String adSizeName;
        public PAGBannerSize bannerSize;
        public String codeId;
    }
}
