package com.google.ads.mediation.sample.customevent.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.google.android.gms.ads.mediation.Adapter;
import com.google.android.gms.ads.mediation.InitializationCompleteCallback;
import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationConfiguration;
import com.google.android.gms.ads.mediation.MediationRewardedAd;
import com.google.android.gms.ads.mediation.MediationRewardedAdCallback;
import com.google.android.gms.ads.mediation.MediationRewardedAdConfiguration;
import com.google.android.gms.ads.mediation.VersionInfo;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.union_test.toutiao.R;
import com.union_test.toutiao.config.TTAdManagerHolder;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * created by wuzejian on 2019-12-03
 * 激励视频
 */
public class AdmobRewardVideoAdapter extends Adapter implements MediationRewardedAd {

    private static final String ADAPTER_NAME = "AdmobRewardVideoAdapter";

    private MediationAdLoadCallback admobAdLoadCallback;
    private MediationRewardedAdCallback admobRewardedAdCallback;
    private TTRewardVideoAd mttRewardVideoAd;
    private Context context;
    private AtomicBoolean isLoadSuccess = new AtomicBoolean(false);


    @Override
    public void initialize(Context context, InitializationCompleteCallback initializationCompleteCallback, List<MediationConfiguration> list) {

        Log.e(ADAPTER_NAME, "custom event  AdmobRewardVideoAdapter  initialize");
        if (!(context instanceof Activity)) {
            // Context not an Activity context, fail the initialization.
            initializationCompleteCallback.onInitializationFailed("Sample SDK requires an Activity context to initialize");
            return;
        }
        //server config
//        List<String> adUnitIDs = new ArrayList<>();
//        for (MediationConfiguration configuration : list) {
//            if (configuration.getFormat() == AdFormat.REWARDED) {
//                adUnitIDs.add(configuration.getServerParameters().getString(MediationRewardedVideoAdAdapter.CUSTOM_EVENT_SERVER_PARAMETER_FIELD));
//            }
//        }
////        if (adUnitIDs.isEmpty()) {
////            initializationCompleteCallback.onInitializationFailed("Sample SDK requires an Activity context to initialize");
////            return;
////        }
        initializationCompleteCallback.onInitializationSucceeded();
    }

    @Override
    public void loadRewardedAd(MediationRewardedAdConfiguration mediationRewardedAdConfiguration, MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback> mediationAdLoadCallback) {
        Log.e(ADAPTER_NAME, "loadRewardedAd.......");

        this.admobAdLoadCallback = mediationAdLoadCallback;
        this.context = mediationRewardedAdConfiguration.getContext();
        Bundle bundle = mediationRewardedAdConfiguration.getMediationExtras();

        if (context == null) {
            mediationAdLoadCallback.onFailure("Context is null");
            Log.e(ADAPTER_NAME, "Context is null");
            return;
        }
//        //server config
//        Bundle serverparameters = mediationRewardedAdConfiguration.getServerParameters();
//        String placementId = "901121593";
//
//        if (serverparameters != null) {
//            placementId = serverparameters.getString(MediationRewardedVideoAdAdapter.CUSTOM_EVENT_SERVER_PARAMETER_FIELD);
//        }
//
//        Log.e(ADAPTER_NAME, placementId);

//        if (placementId == null || placementId.equals("")) {
//            mediationAdLoadCallback.onFailure("Ad Unit is null");
//            Log.e(ADAPTER_NAME, "AdUnit is null");
//            return;
//        }

        String mCodeId = "901121365";

        TTAdManagerHolder.init(context);
        TTAdManager mTTAdManager = TTAdManagerHolder.get();
        TTAdNative mTTAdNative = mTTAdManager.createAdNative(context.getApplicationContext());

        if (bundle != null) {
            if (bundle.containsKey("gdpr")) {
                int gdpr = bundle.getInt("gdpr", 1);
                mTTAdManager.setGdpr(gdpr);

                Log.e(ADAPTER_NAME, "reward receive gdpr=" + gdpr);

            }
        }

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(mCodeId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .setRewardName("gold coin") //Parameter for rewarded video ad requests, name of the reward
                .setRewardAmount(3)  // The number of rewards in rewarded video ad
                .setUserID("user123")//User ID, a required parameter for rewarded video ads
                .setMediaExtra("media_extra") //optional parameter
                .setOrientation(TTAdConstant.VERTICAL) //Set how you wish the video ad to be displayed, choose from TTAdConstant.HORIZONTAL or TTAdConstant.VERTICAL
                .build();

        //load ad
        mTTAdNative.loadRewardVideoAd(adSlot, mRewardedAdListener);
        Log.e(ADAPTER_NAME, "loadRewardVideoAd.......end.....");
    }

    @Override
    public VersionInfo getVersionInfo() {

        return null;
    }

    @Override
    public VersionInfo getSDKVersionInfo() {

        return null;
    }

    @Override
    public void showAd(Context context) {
        if (mttRewardVideoAd != null && isLoadSuccess.get()) {
            mttRewardVideoAd.showRewardVideoAd((Activity) AdmobRewardVideoAdapter.this.context);
        }
    }

    private TTAdNative.RewardVideoAdListener mRewardedAdListener = new TTAdNative.RewardVideoAdListener() {
        @Override
        public void onError(int i, String msg) {
            isLoadSuccess.set(false);
            Log.e(ADAPTER_NAME, "loadRewardVideoAd.........errorCode =" + i + ",msg=" + msg);
            if (admobAdLoadCallback != null) {
                AdmobRewardVideoAdapter.this.admobAdLoadCallback.onFailure(msg);
            }
        }

        @Override
        public void onRewardVideoAdLoad(TTRewardVideoAd ttRewardVideoAd) {
            isLoadSuccess.set(true);
            Log.e(ADAPTER_NAME, "onRewardVideoAdLoad.........onRewardVideoAdLoad");
            mttRewardVideoAd = ttRewardVideoAd;
            mttRewardVideoAd.setRewardAdInteractionListener(TikTokRewardedInteractiveListener);
            if (admobAdLoadCallback != null) {
                admobRewardedAdCallback = (MediationRewardedAdCallback) admobAdLoadCallback.onSuccess(AdmobRewardVideoAdapter.this);
            }
        }

        @Override
        public void onRewardVideoCached() {

        }
    };

    private TTRewardVideoAd.RewardAdInteractionListener TikTokRewardedInteractiveListener = new TTRewardVideoAd.RewardAdInteractionListener() {
        @Override
        public void onAdShow() {
            if (admobRewardedAdCallback != null) {
                admobRewardedAdCallback.onAdOpened();
                admobRewardedAdCallback.onVideoStart();
            }
        }

        @Override
        public void onAdVideoBarClick() {
            if (admobRewardedAdCallback != null) {
                admobRewardedAdCallback.reportAdClicked();
            }
        }

        @Override
        public void onAdClose() {
            if (admobRewardedAdCallback != null) {
                admobRewardedAdCallback.onAdClosed();
            }
        }

        @Override
        public void onVideoComplete() {
            if (admobRewardedAdCallback != null) {
                admobRewardedAdCallback.onVideoComplete();
            }
        }

        @Override
        public void onVideoError() {
            if (admobRewardedAdCallback != null) {
                admobRewardedAdCallback.onAdFailedToShow("");
            }
        }

        @Override
        //For Test Ad Placement, the b always return false
        public void onRewardVerify(boolean b, int i, String s) {
            if (context != null) {
                Toast.makeText(context, context.getString(R.string.tt_toast_ad_on_rewarded), Toast.LENGTH_SHORT).show();
            }

            if (b) {

                final String rewardType = s;
                final int amount = i;

                RewardItem rewardItem = new RewardItem() {
                    @Override
                    public String getType() {
                        return rewardType;
                    }

                    @Override
                    public int getAmount() {
                        return amount;
                    }
                };
                if (admobRewardedAdCallback != null) {
                    admobRewardedAdCallback.onUserEarnedReward(rewardItem);
                }

            }
        }

        @Override
        public void onSkippedVideo() {

        }
    };
}
