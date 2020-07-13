package com.mopub.mobileads;

import android.app.Activity;
import android.text.TextUtils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.mopub.common.DataKeys;
import com.mopub.common.LifecycleListener;
import com.mopub.common.MoPubReward;
import com.mopub.common.logging.MoPubLog;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.CLICKED;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.CUSTOM;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.DID_DISAPPEAR;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.LOAD_ATTEMPTED;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.LOAD_FAILED;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.LOAD_SUCCESS;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.SHOULD_REWARD;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.SHOW_ATTEMPTED;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.SHOW_FAILED;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.SHOW_SUCCESS;

public class PangleAdRewardedVideo extends CustomEventRewardedVideo {
    private static final String ADAPTER_NAME = PangleAdRewardedVideo.class.getSimpleName();

    /**
     * Flag to determine whether or not the adapter has been initialized.
     */
    private static AtomicBoolean mIsSDKInitialized;
    private static String mPlacementId;

    /**
     * Flag to determine whether or not the Pangle Rewarded Video Ad instance has loaded.
     */
    private boolean mIsLoaded;

    private PangleAdapterConfiguration mPangleAdapterConfiguration;
    private WeakReference<Activity> mWeakActivity;
    private TTRewardVideoAd mTTRewardVideoAd;


    public PangleAdRewardedVideo() {
        mIsSDKInitialized = new AtomicBoolean(false);
        mPangleAdapterConfiguration = new PangleAdapterConfiguration();
    }

    @Override
    public boolean hasVideoAvailable() {
        return mTTRewardVideoAd != null && mIsLoaded;
    }


    @Nullable
    @Override
    protected LifecycleListener getLifecycleListener() {
        return null;
    }

    @Override
    protected boolean checkAndInitializeSdk(@NonNull Activity launcherActivity, @NonNull Map<String, Object> localExtras, @NonNull Map<String, String> serverExtras) throws Exception {
        if (!mIsSDKInitialized.get()) {
            if (serverExtras != null) {
                String appId = serverExtras.get(PangleAdapterConfiguration.APP_ID_EXTRA_KEY);

                if (TextUtils.isEmpty(appId)) {
                    MoPubRewardedVideoManager.onRewardedVideoLoadFailure(PangleAdRewardedVideo.class, getAdNetworkId(), MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
                    MoPubLog.log(getAdNetworkId(), CUSTOM, ADAPTER_NAME,
                            "Invalid Pangle app ID. Failing pangle sdk init. " +
                                    "Ensure the ad placement id is valid on the MoPub dashboard.");
                    return false;
                }
                PangleAdapterConfiguration.pangleSdkInit(launcherActivity, appId);
                mPangleAdapterConfiguration.setCachedInitializationParameters(launcherActivity, serverExtras);
                mIsSDKInitialized.set(true);
            }

            return true;
        }

        return false;
    }

    @Override
    protected void loadWithSdkInitialized(@NonNull Activity activity, @NonNull Map<String, Object> localExtras, @NonNull Map<String, String> serverExtras) throws Exception {
        MoPubLog.log(getAdNetworkId(), CUSTOM, ADAPTER_NAME, "loadWithSdkInitialized method execute .....");
        mWeakActivity = new WeakReference<>(activity);

        TTAdManager adManager = PangleAdapterConfiguration.getPangleSdkManager();
        final TTAdNative adInstance = adManager.createAdNative(activity.getApplicationContext());

        /** obtain adunit from server by mopub */
        mPlacementId = serverExtras.get(PangleAdapterConfiguration.AD_PLACEMENT_ID_EXTRA_KEY);

        if (TextUtils.isEmpty(mPlacementId)) {
            MoPubRewardedVideoManager.onRewardedVideoLoadFailure(PangleAdRewardedVideo.class, getAdNetworkId(), MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            MoPubLog.log(getAdNetworkId(), CUSTOM, ADAPTER_NAME,
                    "Invalid Pangle placement ID. Failing ad request. " +
                            "Ensure the ad placement id is valid on the MoPub dashboard.");
            return;
        }

        /** Create a parameter AdSlot for reward ad request type,
         refer to the document for meanings of specific parameters */
        final String adm = serverExtras.get(DataKeys.ADM_KEY);

        /** create AdSlot and set request parameters */
        final AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(getAdNetworkId())
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .setRewardName(PangleAdapterConfiguration.getRewardName())
                .setRewardAmount(PangleAdapterConfiguration.getRewardAmount())
                .setUserID(PangleAdapterConfiguration.getUserID())
                .setMediaExtra(PangleAdapterConfiguration.getMediaExtra())
                .withBid(adm)
                .build();


        /**load ad */
        MoPubLog.log(getAdNetworkId(), LOAD_ATTEMPTED, ADAPTER_NAME);
        adInstance.loadRewardVideoAd(adSlot, mLoadRewardVideoAdListener);
    }

    @Override
    protected void showVideo() {
        if (hasVideoAvailable() && mWeakActivity != null && mWeakActivity.get() != null) {
            mTTRewardVideoAd.setRewardAdInteractionListener(mRewardAdInteractionListener);
            mTTRewardVideoAd.showRewardVideoAd(mWeakActivity.get());
            MoPubLog.log(getAdNetworkId(), SHOW_ATTEMPTED, ADAPTER_NAME);
        } else {
            MoPubLog.log(SHOW_FAILED, ADAPTER_NAME,
                    MoPubErrorCode.NETWORK_NO_FILL.getIntCode(),
                    MoPubErrorCode.NETWORK_NO_FILL);
            MoPubLog.log(getAdNetworkId(), CUSTOM, ADAPTER_NAME, "Failed to show an Pangle rewarded video." +
                    " A video might not have been loaded");

            MoPubRewardedVideoManager.onRewardedVideoPlaybackError(
                    PangleAdRewardedVideo.class,
                    getAdNetworkId(), MoPubErrorCode.VIDEO_PLAYBACK_ERROR);
        }
    }

    @NonNull
    @Override
    protected String getAdNetworkId() {
        return mPlacementId;
    }

    @Override
    protected void onInvalidate() {
        if (mTTRewardVideoAd != null) {
            MoPubLog.log(getAdNetworkId(), CUSTOM, ADAPTER_NAME, "Performing cleanup tasks...");
            mTTRewardVideoAd = null;
        }
    }

    private TTAdNative.RewardVideoAdListener mLoadRewardVideoAdListener = new TTAdNative.RewardVideoAdListener() {

        @Override
        public void onError(int code, String message) {
            MoPubLog.log(getAdNetworkId(), LOAD_FAILED, ADAPTER_NAME, "Loading Rewarded Video creative encountered an error: " + mapErrorCode(code).toString() + ",error message:" + message);
            MoPubRewardedVideoManager.onRewardedVideoLoadFailure(PangleAdRewardedVideo.class, getAdNetworkId(), mapErrorCode(code));
            MoPubLog.log(getAdNetworkId(), LOAD_FAILED, ADAPTER_NAME, "Loading Rewarded Video creative encountered an error: " + mapErrorCode(code).toString() + ",error message:" + message);
        }

        @Override
        public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
            MoPubLog.log(getAdNetworkId(), CUSTOM, ADAPTER_NAME, "onRewardVideoAdLoad method execute ......ad = " + ad);
            if (ad != null) {
                mIsLoaded = true;
                mTTRewardVideoAd = ad;
                MoPubLog.log(getAdNetworkId(), LOAD_SUCCESS, ADAPTER_NAME);
                MoPubRewardedVideoManager.onRewardedVideoLoadSuccess(
                        PangleAdRewardedVideo.class,
                        getAdNetworkId());
            } else {
                MoPubRewardedVideoManager.onRewardedVideoLoadFailure(PangleAdRewardedVideo.class, getAdNetworkId(), MoPubErrorCode.NETWORK_NO_FILL);
                MoPubLog.log(getAdNetworkId(), LOAD_FAILED, ADAPTER_NAME, " RewardVideoAd is null !");
            }
        }

        @Override
        public void onRewardVideoCached() {
            MoPubLog.log(getAdNetworkId(), CUSTOM, ADAPTER_NAME, "Pangle RewardVideoAd onRewardVideoCached...");
        }
    };

    private TTRewardVideoAd.RewardAdInteractionListener mRewardAdInteractionListener = new TTRewardVideoAd.RewardAdInteractionListener() {
        @Override
        public void onAdShow() {
            MoPubLog.log(getAdNetworkId(), SHOW_SUCCESS, ADAPTER_NAME);
            MoPubRewardedVideoManager.onRewardedVideoStarted(PangleAdRewardedVideo.class, getAdNetworkId());
        }

        @Override
        public void onAdVideoBarClick() {
            MoPubLog.log(getAdNetworkId(), CLICKED, ADAPTER_NAME);
            MoPubRewardedVideoManager.onRewardedVideoClicked(PangleAdRewardedVideo.class, getAdNetworkId());
        }

        @Override
        public void onAdClose() {
            MoPubLog.log(getAdNetworkId(), DID_DISAPPEAR, ADAPTER_NAME);
            MoPubLog.log(getAdNetworkId(), CUSTOM, ADAPTER_NAME, "Pangle RewardVideoAd onAdClose...");
            MoPubRewardedVideoManager.onRewardedVideoClosed(PangleAdRewardedVideo.class, getAdNetworkId());
        }

        @Override
        public void onVideoComplete() {
            MoPubLog.log(getAdNetworkId(), CUSTOM, ADAPTER_NAME, "Pangle RewardVideoAd onVideoComplete...");
        }

        @Override
        public void onVideoError() {
            MoPubLog.log(getAdNetworkId(), CUSTOM, ADAPTER_NAME, "Pangle RewardVideoAd onVideoError...");
            MoPubRewardedVideoManager.onRewardedVideoPlaybackError(PangleAdRewardedVideo.class, getAdNetworkId(), MoPubErrorCode.UNSPECIFIED);
        }

        @Override
        public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
            MoPubLog.log(getAdNetworkId(), CUSTOM, ADAPTER_NAME, "Pangle TTRewardVideoAd onRewardVerify...rewardVerify："
                    + rewardVerify + "，rewardAmount=" + rewardAmount + "，rewardName=" + rewardName);
            MoPubLog.log(getAdNetworkId(), SHOULD_REWARD, ADAPTER_NAME, rewardAmount, rewardName);
            MoPubRewardedVideoManager.onRewardedVideoCompleted(
                    PangleAdRewardedVideo.class,
                    getAdNetworkId(),
                    MoPubReward.success(rewardName, rewardAmount));
        }

        @Override
        public void onSkippedVideo() {
            MoPubLog.log(getAdNetworkId(), CUSTOM, ADAPTER_NAME, "Pangle TTRewardVideoAd skipped video...");
        }
    };

    private static MoPubErrorCode mapErrorCode(int error) {
        switch (error) {
            case PangleSharedUtil.CONTENT_TYPE:
            case PangleSharedUtil.REQUEST_PB_ERROR:
                return MoPubErrorCode.NO_CONNECTION;
            case PangleSharedUtil.NO_AD:
                return MoPubErrorCode.NETWORK_NO_FILL;
            case PangleSharedUtil.ADSLOT_EMPTY:
            case PangleSharedUtil.ADSLOT_ID_ERROR:
                return MoPubErrorCode.MISSING_AD_UNIT_ID;
            default:
                return MoPubErrorCode.UNSPECIFIED;
        }
    }
}
