package com.google.ads.mediation.sample.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.ads.mediation.sample.customevent.BundleBuilder;
import com.google.ads.mediation.sample.customevent.adapter.AdmobRewardVideoAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.union_test.toutiao.R;


/**
 * created by wuzejian on 2019/11/29
 */
@SuppressLint("LongLogTag")
public class AdmobRewardVideoActivity extends Activity {
    private static final String TAG = "AdmobRewardVideoActivity";
    private Button mShowButton;
    private RewardedAd rewardedVideoAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mopub_reward_activity);
        mShowButton = findViewById(R.id.showRewardAd);
        mShowButton.setEnabled(false);
        MobileAds.initialize(this, getResources().getString(R.string.admob_app_id));

        findViewById(R.id.loadRewardAd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rewardedVideoAd = loadRewardedAd(rewardedVideoAd, getResources().getString(R.string.admob_reward_video_id));
            }
        });

//        ERROR_CODE_INTERNAL_ERROR

        mShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRewardedVideo();
                mShowButton.setEnabled(false);
            }
        });


    }

    private RewardedAd loadRewardedAd(RewardedAd ad, String adUnit) {
        Toast.makeText(this, getString(R.string.tt_toast_start_loading)
                , Toast.LENGTH_SHORT).show();
        Bundle bundle = new BundleBuilder()
                .setGdpr(1)
                .build();
        ad = new RewardedAd(AdmobRewardVideoActivity.this, adUnit);
        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdmobRewardVideoAdapter.class, bundle)
                .build();
        ad.loadAd(adRequest, adLoadCallback);

        return ad;
    }

    private void showRewardedVideo() {
        if (rewardedVideoAd != null && rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.show(AdmobRewardVideoActivity.this, rewardedAdCallback);
        } else {
            Toast.makeText(this, getString(R.string.tt_toast_no_ad)
                    , Toast.LENGTH_SHORT).show();
        }
    }

    private RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
        @Override
        public void onRewardedAdLoaded() {
            mShowButton.setEnabled(true);
            // Ad successfully loaded.
            Toast.makeText(getApplicationContext(),
                    getString(R.string.tt_load_success_text), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "On Rewarded Video Ad Loaded");

            if (rewardedVideoAd.isLoaded()) {
                Log.e(TAG, "RewardedVideo is Loaded" + rewardedVideoAd.getMediationAdapterClassName());
            }

        }

        @Override
        public void onRewardedAdFailedToLoad(int errorCode) {
            // Ad failed to load.
            Toast.makeText(getApplicationContext(), getString(R.string.tt_load_failed_text)
                    + ":" + errorCode, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "On Rewarded Video Ad Failed To Load:" + errorCode);

        }
    };

    private RewardedAdCallback rewardedAdCallback = new RewardedAdCallback() {

        @Override
        public void onRewardedAdOpened() {
            super.onRewardedAdOpened();
            Toast.makeText(getApplicationContext(), getString(R.string.tt_ad_showed_text), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "On Rewarded Video Ad Opened");

        }

        @Override
        public void onRewardedAdClosed() {
            super.onRewardedAdClosed();
            Toast.makeText(getApplicationContext(), getString(R.string.tt_ad_close_text), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "On Rewarded Video Ad Closed");
        }

        @Override
        public void onUserEarnedReward(com.google.android.gms.ads.rewarded.RewardItem rewardItem) {
            super.onUserEarnedReward(rewardItem);
            Toast.makeText(getApplicationContext(), getString(R.string.tt_get_reward)
                    + "：rewardItem=" + rewardItem.getAmount(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "On User Rewarded");
        }

        @Override
        public void onRewardedAdFailedToShow(int i) {
            super.onRewardedAdFailedToShow(i);
            Toast.makeText(getApplicationContext(), getString(R.string.tt_reward_video_show_error)
                    + ",error=" + i, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "On Rewarded Video Ad Failed To Show");
        }
    };

    public void onDestroy() {
        super.onDestroy();
    }
}
