package com.google.ads.mediation.sample.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.ads.mediation.sample.customevent.BundleBuilder;
import com.google.ads.mediation.sample.customevent.adapter.AdmobFullVideoAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.union_test.toutiao.R;
import com.union_test.toutiao.utils.TToast;

/**
 * created by wuzejian on 2019/11/29
 */

@SuppressLint("LongLogTag")
public class AdmobFullVideoActivity extends Activity {
    private static final String TAG = "AdmobFullVideoActivity";
    private Button mShowButton;
    private RewardedVideoAd rewardedVideoAd;
    private Context mContext;
    private String mCodeId = "901121073";
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mopub_reward_activity);
        mShowButton = findViewById(R.id.showRewardAd);
        mShowButton.setEnabled(false);
        mContext = this;
        MobileAds.initialize(mContext, getResources().getString(R.string.admob_app_id));
        mInterstitialAd = new InterstitialAd(mContext);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.admob_full_video_id));


        findViewById(R.id.loadRewardAd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BundleBuilder bundleBuilder = new BundleBuilder();
                bundleBuilder.setCodeId(mCodeId);
                bundleBuilder.setGdpr(1);
                AdRequest adRequest = new AdRequest.Builder().
                        addCustomEventExtrasBundle(AdmobFullVideoAdapter.class, bundleBuilder.build())
                        .build();


                mInterstitialAd.loadAd(adRequest);
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int i) {
                        super.onAdFailedToLoad(i);
                        Log.d(TAG, "onAdFailedToLoad....error=" + i);
                        TToast.show(mContext, "Load fail i=" + i);

                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        mShowButton.setEnabled(true);
                        TToast.show(mContext, "Load success");
                        Log.d(TAG, "....onAdLoaded=onAdLoaded");

                    }

                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        TToast.show(mContext, "Ad closed");

                    }

                    @Override
                    public void onAdOpened() {
                        super.onAdOpened();
                        TToast.show(mContext, "Ad showed");

                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        TToast.show(mContext, "Ad clicked");

                    }

                });
                mInterstitialAd.loadAd(adRequest);

            }
        });


        mShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show();
                    mShowButton.setEnabled(false);
                }
            }
        });


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
