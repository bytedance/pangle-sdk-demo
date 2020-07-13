package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.view.View;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.bytedance.sdk.openadsdk.adapter.PangleAdInterstitialActivity;
import com.mopub.common.DataKeys;
import com.mopub.common.logging.MoPubLog;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.mopub.common.logging.MoPubLog.AdLogEvent.DID_DISAPPEAR;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.CLICKED;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.CUSTOM;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.LOAD_ATTEMPTED;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.LOAD_FAILED;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.LOAD_SUCCESS;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.SHOW_ATTEMPTED;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.SHOW_FAILED;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.SHOW_SUCCESS;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.WILL_LEAVE_APPLICATION;

public class PangleAdInterstitial extends CustomEventInterstitial {
    private static final String ADAPTER_NAME = PangleAdInterstitial.class.getSimpleName();
    public static final String KEY_EXTRA_AD_WIDTH = "ad_width";
    public static final String KEY_EXTRA_AD_HEIGHT = "ad_height";


    /**
     * Pangle network Interstitial ad unit ID.
     */
    private static String mPlacementId;
    private Context mContext;
    private float mAdWidth = 0;
    private float mAdHeight = 0;
    private PangleAdapterConfiguration mPangleAdapterConfiguration;
    private PangleAdInterstitialFullVideoLoader mFullVideoLoader;
    private CustomEventInterstitial.CustomEventInterstitialListener customEventInterstitialListener;

    public PangleAdInterstitial() {
        mPangleAdapterConfiguration = new PangleAdapterConfiguration();
        MoPubLog.log(getAdNetworkId(), CUSTOM, ADAPTER_NAME, "PangleAdInterstitial has been create ....");
    }

    @Override
    protected void loadInterstitial(
            final Context context,
            final CustomEventInterstitialListener customEventInterstitialListener,
            final Map<String, Object> localExtras,
            final Map<String, String> serverExtras) {
        this.mContext = context;
        this.customEventInterstitialListener = customEventInterstitialListener;
        int mOrientation = mContext.getResources().getConfiguration().orientation;
        mPangleAdapterConfiguration.setCachedInitializationParameters(context, serverExtras);
        setAutomaticImpressionAndClickTracking(false);

        String adm = null;

        TTAdManager ttAdManager = null;
        TTAdNative ttAdNative = null;


        if (serverExtras != null) {
            /** Obtain ad placement id from MoPub UI */
            mPlacementId = serverExtras.get(PangleAdapterConfiguration.AD_PLACEMENT_ID_EXTRA_KEY);
            if (TextUtils.isEmpty(mPlacementId)) {
                if (customEventInterstitialListener != null) {
                    customEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
                    MoPubLog.log(getAdNetworkId(), CUSTOM, ADAPTER_NAME,
                            "Invalid Pangle placement ID. Failing ad request. " +
                                    "Ensure the ad placement id is valid on the MoPub dashboard.");
                }
                return;
            }
            adm = serverExtras.get(DataKeys.ADM_KEY);

            /** Init Pangle SDK if fail to initialize in the adapterConfiguration */
            String appId = serverExtras.get(PangleAdapterConfiguration.APP_ID_EXTRA_KEY);
            PangleAdapterConfiguration.pangleSdkInit(context, appId);
            ttAdManager = PangleAdapterConfiguration.getPangleSdkManager();
            ttAdNative = ttAdManager.createAdNative(context.getApplicationContext());
            mPangleAdapterConfiguration.setCachedInitializationParameters(context, serverExtras);

        }

        MoPubLog.log(getAdNetworkId(), CUSTOM, ADAPTER_NAME, "adWidth =" + mAdWidth + "，adHeight=" + mAdHeight + ",placementId=" + mPlacementId);

        if (ttAdManager == null) {
            if (customEventInterstitialListener != null) {
                customEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_INVALID_STATE);
            }
            MoPubLog.log(getAdNetworkId(), LOAD_FAILED, ADAPTER_NAME,
                    MoPubErrorCode.NETWORK_INVALID_STATE.getIntCode(),
                    MoPubErrorCode.NETWORK_INVALID_STATE);
            return;
        }

        AdSlot.Builder adSlotBuilder = new AdSlot.Builder()
                .setCodeId(mPlacementId)
                .setSupportDeepLink(true)
                .setAdCount(1)
                .setImageAcceptedSize(1080, 1920)
                .withBid(adm);
        MoPubLog.log(getAdNetworkId(), LOAD_ATTEMPTED, ADAPTER_NAME, "Loading Pangle FullVideoAd ad");

        /**  request FullVideoAd */
        mFullVideoLoader = new PangleAdInterstitialFullVideoLoader(mContext, customEventInterstitialListener);
        mFullVideoLoader.loadAdFullVideoListener(adSlotBuilder.build(), ttAdNative);

    }


    private void checkSize(boolean isExpressAd) {
        if (isExpressAd) {
            if (mAdWidth <= 0) {
                mAdWidth = 300;
                mAdWidth = 450;
            }
            if (mAdHeight < 0) {
                mAdHeight = 0;
            }
        } else {
            if (mAdWidth <= 0 || mAdHeight <= 0) {
                mAdWidth = 600;
                mAdHeight = 900;
            }
        }
    }


    @Override
    protected void showInterstitial() {
        MoPubLog.log(getAdNetworkId(), SHOW_ATTEMPTED, ADAPTER_NAME);
        boolean hasShow = false;
        if (mFullVideoLoader != null && mContext instanceof Activity) {
            mFullVideoLoader.showFullVideo((Activity) mContext);
            hasShow = true;
        }


        if (!hasShow && customEventInterstitialListener != null) {
            MoPubLog.log(getAdNetworkId(), SHOW_FAILED, ADAPTER_NAME);
            customEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.UNSPECIFIED);
        }
    }

    private static String getAdNetworkId() {
        return mPlacementId;
    }

    @Override
    protected void onInvalidate() {
        if (mFullVideoLoader != null) {
            mFullVideoLoader.destroy();
        }
    }

    /**
     * Pangle full-video ad
     */
    public static class PangleAdInterstitialFullVideoLoader {
        private Context mContext;
        private CustomEventInterstitial.CustomEventInterstitialListener mFullVideoListener;
        private boolean mIsLoaded;
        private TTFullScreenVideoAd mTTFullScreenVideoAd;

        PangleAdInterstitialFullVideoLoader(Context context, CustomEventInterstitial.CustomEventInterstitialListener fullVideoListener) {
            this.mContext = context;
            this.mFullVideoListener = fullVideoListener;
        }

        void loadAdFullVideoListener(AdSlot adSlot, TTAdNative ttAdNative) {
            if (ttAdNative == null || mContext == null || adSlot == null || TextUtils.isEmpty(adSlot.getCodeId())) {
                if (mFullVideoListener != null) {
                    mFullVideoListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
                }
                return;
            }
            ttAdNative.loadFullScreenVideoAd(adSlot, mLoadFullVideoAdListener);
        }

        void showFullVideo(Activity activity) {
            if (mTTFullScreenVideoAd != null && mIsLoaded) {
                mTTFullScreenVideoAd.showFullScreenVideoAd(activity);
            }
        }

        public void destroy() {
            mContext = null;
            mFullVideoListener = null;
            mTTFullScreenVideoAd = null;
            mLoadFullVideoAdListener = null;
            mFullScreenVideoAdInteractionListener = null;
        }


        private TTAdNative.FullScreenVideoAdListener mLoadFullVideoAdListener = new TTAdNative.FullScreenVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                MoPubLog.log(getAdNetworkId(), LOAD_FAILED, ADAPTER_NAME, "Loading Full Video creative encountered an error: " + PangleSharedUtil.mapErrorCode(code).toString() + ",error message:" + message);
                if (mFullVideoListener != null) {
                    mFullVideoListener.onInterstitialFailed(PangleSharedUtil.mapErrorCode(code));
                }
            }

            @Override
            public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ad) {
                if (ad != null) {
                    mIsLoaded = true;
                    mTTFullScreenVideoAd = ad;
                    mTTFullScreenVideoAd.setFullScreenVideoAdInteractionListener(mFullScreenVideoAdInteractionListener);
                    if (mFullVideoListener != null) {
                        mFullVideoListener.onInterstitialLoaded();
                    }
                    MoPubLog.log(getAdNetworkId(), LOAD_SUCCESS, ADAPTER_NAME);

                } else {
                    if (mFullVideoListener != null) {
                        mFullVideoListener.onInterstitialFailed(PangleSharedUtil.mapErrorCode(PangleSharedUtil.NO_AD));
                    }
                    MoPubLog.log(getAdNetworkId(), SHOW_FAILED, ADAPTER_NAME);
                }
            }

            @Override
            public void onFullScreenVideoCached() {
                MoPubLog.log(getAdNetworkId(), CUSTOM, ADAPTER_NAME, " Pangle onFullScreenVideoCached invoke !");
            }
        };

        private TTFullScreenVideoAd.FullScreenVideoAdInteractionListener mFullScreenVideoAdInteractionListener = new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {

            @Override
            public void onAdShow() {
                MoPubLog.log(getAdNetworkId(), SHOW_SUCCESS, ADAPTER_NAME);
                if (mFullVideoListener != null) {
                    mFullVideoListener.onInterstitialShown();
                    mFullVideoListener.onInterstitialImpression();
                }
            }

            @Override
            public void onAdVideoBarClick() {
                MoPubLog.log(getAdNetworkId(), CLICKED, ADAPTER_NAME);
                if (mFullVideoListener != null) {
                    mFullVideoListener.onInterstitialClicked();
                }
            }

            @Override
            public void onAdClose() {
                MoPubLog.log(getAdNetworkId(), DID_DISAPPEAR, ADAPTER_NAME);
                if (mFullVideoListener != null) {
                    mFullVideoListener.onInterstitialDismissed();
                }
            }

            @Override
            public void onVideoComplete() {
                MoPubLog.log(getAdNetworkId(), CUSTOM, ADAPTER_NAME, "Pangle FullScreenVideoAd onVideoComplete...");
            }

            @Override
            public void onSkippedVideo() {
                MoPubLog.log(getAdNetworkId(), CUSTOM, ADAPTER_NAME, "Pangle FullScreenVideoAd onSkippedVideo...");
            }
        };

    }
}
