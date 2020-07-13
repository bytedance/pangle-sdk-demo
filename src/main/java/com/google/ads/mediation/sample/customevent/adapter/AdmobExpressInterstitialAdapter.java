package com.google.ads.mediation.sample.customevent.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.google.ads.mediation.sample.customevent.AdmobAdapterUtil;
import com.google.ads.mediation.sample.customevent.BundleBuilder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitial;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitialListener;
import com.union_test.toutiao.config.TTAdManagerHolder;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * created by wuzejian on 2020-05-28
 */
public class AdmobExpressInterstitialAdapter implements CustomEventInterstitial {

    private static final String ADAPTER_NAME = "AdapterForGoogle";
    private String mCodeId = "";
    private CustomEventInterstitialListener mCustomEventInterstitialListener;
    private TTNativeExpressAd mTTInterstitialExpressAd;
    private Activity mActivity;
    private AtomicBoolean isRenderLoaded = new AtomicBoolean(false);

    @Override
    public void requestInterstitialAd(Context context,
                                      CustomEventInterstitialListener listener,
                                      String serverParameter,
                                      MediationAdRequest mediationAdRequest,
                                      Bundle customEventExtras) {
        if (!(context instanceof Activity)) {
            Log.e(ADAPTER_NAME, " AdmobExpressInterstitialAdapter , context must be  Activity !!!!!");
            if (listener != null) {
                listener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            }
        }

        mActivity = (Activity) context;
        mCustomEventInterstitialListener = listener;

        //obtain ad placement_id from admob server
        mCodeId = AdmobAdapterUtil.getAdPlacementId(serverParameter);

        if (mCodeId == null) {
            mCodeId = customEventExtras.getString(BundleBuilder.KEY_AD_PLACEMENT_ID);
            if (mCodeId == null && listener != null) {
                listener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
                return;
            }
        }

        Log.e(ADAPTER_NAME, "requestBannerAd.-mCodeId=" + mCodeId);

        TTAdNative ttAdLoader = AdmobAdapterUtil.getPangleAdLoader(context);

        if (customEventExtras != null && customEventExtras.containsKey(BundleBuilder.AD_GDPR)) {
            int gdpr = customEventExtras.getInt(BundleBuilder.AD_GDPR, 1);
            AdmobAdapterUtil.setGdpr(gdpr);
            Log.e(ADAPTER_NAME, "requestInterstitialAd receive gdpr=" + gdpr);
        }

        int expressViewWidth = 0;
        int expressViewHeight = 0;

        if (customEventExtras != null) {
            int[] adSizeSafely = AdmobAdapterUtil.getAdSizeSafely(customEventExtras, BundleBuilder.AD_WIDTH, BundleBuilder.AD_HEIGHT);
            expressViewWidth = adSizeSafely[0];
            expressViewHeight = adSizeSafely[1];
            Log.e(ADAPTER_NAME, " requestInterstitialAd.mCodeId =" + mCodeId + ",expressViewWidth=" + expressViewWidth + ",expressViewHeight=" + expressViewHeight);
        }

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(mCodeId)
                .setSupportDeepLink(true)
                .setAdCount(1) //request ad count
                .setExpressViewAcceptedSize(expressViewWidth, expressViewHeight)
                .build();

        //request ad
        if (ttAdLoader != null) {
            ttAdLoader.loadInteractionExpressAd(adSlot, mInterstitialAdExpressAdListener);
        } else {
            if (listener != null) {
                listener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            }
            Log.e(ADAPTER_NAME, "requestInterstitialAd.-pangle sdk - ttAdLoader can not be null !!!!");
        }

    }

    @Override
    public void showInterstitial() {
        if (mTTInterstitialExpressAd != null && isRenderLoaded.get() && mActivity != null) {
            mTTInterstitialExpressAd.showInteractionExpressAd(mActivity);
        }
    }

    @Override
    public void onDestroy() {
        if (mTTInterstitialExpressAd != null) {
            mTTInterstitialExpressAd.destroy();
        }
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onResume() {
    }


    /**
     * pangolin ad load listener
     */
    private TTAdNative.NativeExpressAdListener mInterstitialAdExpressAdListener = new TTAdNative.NativeExpressAdListener() {
        @Override
        public void onError(int code, String message) {
            if (mCustomEventInterstitialListener != null) {
                mCustomEventInterstitialListener.onAdFailedToLoad(code);
            }
        }

        @Override
        public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
            if (ads == null || ads.size() == 0) {
                return;
            }
            mTTInterstitialExpressAd = ads.get(0);
            mTTInterstitialExpressAd.setSlideIntervalTime(30 * 1000);
            mTTInterstitialExpressAd.setExpressInteractionListener(mInterstitialExpressAdInteractionListener);
            mTTInterstitialExpressAd.render();
        }
    };

    /**
     * render listener
     */
    private TTNativeExpressAd.AdInteractionListener mInterstitialExpressAdInteractionListener = new TTNativeExpressAd.AdInteractionListener() {
        @Override
        public void onAdDismiss() {
            if (mCustomEventInterstitialListener != null) {
                mCustomEventInterstitialListener.onAdClosed();
            }
        }

        @Override
        public void onAdClicked(View view, int type) {
            if (mCustomEventInterstitialListener != null) {
                mCustomEventInterstitialListener.onAdClicked();
            }
        }

        @Override
        public void onAdShow(View view, int type) {
            if (mCustomEventInterstitialListener != null) {
                mCustomEventInterstitialListener.onAdOpened();
            }
        }

        @Override
        public void onRenderFail(View view, String msg, int code) {
            if (mCustomEventInterstitialListener != null) {
                mCustomEventInterstitialListener.onAdFailedToLoad(code);
            }
        }

        @Override
        public void onRenderSuccess(View view, float width, float height) {
            //返回view的宽高 单位 dp
            isRenderLoaded.set(true);
            if (mCustomEventInterstitialListener != null) {
                mCustomEventInterstitialListener.onAdLoaded();
            }
        }
    };
}
