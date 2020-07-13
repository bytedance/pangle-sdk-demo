package com.google.ads.mediation.sample.customevent.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.google.ads.mediation.sample.customevent.AdmobAdapterUtil;
import com.google.ads.mediation.sample.customevent.BundleBuilder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventBanner;
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener;
import com.union_test.toutiao.config.TTAdManagerHolder;
import com.union_test.toutiao.utils.TToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * created by wuzejian on 2019-12-02
 */
public class AdmobExpressBannerAdapter implements CustomEventBanner {
    private static final String ADAPTER_NAME = "BannerAdapterForGoogle";


    private String mCodeId = "";

    private TTNativeExpressAd mTTNativeExpressAd;

    private CustomEventBannerListener mCustomEventBannerListener;

    private Context mContext;


    /**
     * @param context
     * @param listener
     * @param serverParameter    服务器附加信息
     * @param size
     * @param mediationAdRequest 包含一些常用的定位信息，可供广告定位时使用
     * @param customEventExtras
     */
    @Override
    public void requestBannerAd(Context context,
                                CustomEventBannerListener listener,
                                String serverParameter,
                                AdSize size,
                                MediationAdRequest mediationAdRequest,
                                Bundle customEventExtras) {


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

        mContext = context;
        this.mCustomEventBannerListener = listener;
        TTAdNative ttAdLoader = AdmobAdapterUtil.getPangleAdLoader(context);

        if (customEventExtras != null && customEventExtras.containsKey(BundleBuilder.AD_GDPR)) {
            int gdpr = customEventExtras.getInt(BundleBuilder.AD_GDPR, 1);
            AdmobAdapterUtil.setGdpr(gdpr);
            Log.e(ADAPTER_NAME, "banner receive gdpr=" + gdpr);
        }

        int expressViewWidth = 0;
        int expressViewHeight = 0;

        if (customEventExtras != null) {
            int[] bannerAdSizeAdapterSafely = AdmobAdapterUtil.getBannerAdSizeAdapterSafely(customEventExtras, BundleBuilder.AD_WIDTH, BundleBuilder.AD_HEIGHT);
            expressViewWidth = bannerAdSizeAdapterSafely[0];
            expressViewHeight = bannerAdSizeAdapterSafely[1];
        }
        Log.d(ADAPTER_NAME, " requestBannerAd.mCodeId =" + mCodeId + ",expressViewWidth=" + expressViewWidth + ",expressViewHeight=" + expressViewHeight);


        // Assumes that the serverParameter is the AdUnit for the Sample Network.
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(mCodeId) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(expressViewWidth, expressViewHeight) //期望模板广告view的size,单位dp
                .setImageAcceptedSize(expressViewWidth, expressViewHeight)
                .build();

        //step5:请求广告，对请求回调的广告作渲染处理
        if (ttAdLoader != null) {
            ttAdLoader.loadBannerExpressAd(adSlot, mTTBannerNativeExpressAdListener);
        } else {
            if (listener != null) {
                listener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            }
        }
        Log.d(ADAPTER_NAME, "loadBannerExpressAd.....");
    }

    @Override
    public void onDestroy() {
        if (mTTNativeExpressAd != null) {
            mTTNativeExpressAd.destroy();
        }
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onResume() {
    }

    /**
     * banner ad listener
     */
    private TTAdNative.NativeExpressAdListener mTTBannerNativeExpressAdListener = new TTAdNative.NativeExpressAdListener() {
        @SuppressLint("LongLogTag")
        @Override
        public void onError(int code, String message) {
            Log.e(ADAPTER_NAME, " onBannerFailed.-code=" + code + "," + message);
            if (mCustomEventBannerListener != null) {
                mCustomEventBannerListener.onAdFailedToLoad(code);
            }
        }

        @Override
        public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
            Log.e(ADAPTER_NAME, " onNativeExpressAdLoad.-code=");

            if (ads == null || ads.size() == 0) {
                return;
            }
            mTTNativeExpressAd = ads.get(0);
            mTTNativeExpressAd.setSlideIntervalTime(30 * 1000);
            mTTNativeExpressAd.setExpressInteractionListener(mExpressAdInteractionListener);
            bindDislike(mTTNativeExpressAd);
            mTTNativeExpressAd.render();
        }
    };

    private void bindDislike(TTNativeExpressAd ad) {
        //dislike function, maybe you can use custom dialog, please refer to the access document by yourself
        if (mContext instanceof Activity) {
            ad.setDislikeCallback((Activity) mContext, new TTAdDislike.DislikeInteractionCallback() {
                @Override
                public void onSelected(int position, String value) {
                    TToast.show(mContext, "click " + value);
                }

                @Override
                public void onCancel() {
                    TToast.show(mContext, "Cancel click ");
                }
            });
        }
    }

    /**
     * banner render listener
     */
    private TTNativeExpressAd.ExpressAdInteractionListener mExpressAdInteractionListener = new TTNativeExpressAd.ExpressAdInteractionListener() {
        @Override
        public void onAdClicked(View view, int type) {
            if (mCustomEventBannerListener != null) {
                mCustomEventBannerListener.onAdClicked();
            }
        }

        @Override
        public void onAdShow(View view, int type) {
            if (mCustomEventBannerListener != null) {
                mCustomEventBannerListener.onAdOpened();
            }
        }

        @Override
        public void onRenderFail(View view, String msg, int code) {
            Log.e(ADAPTER_NAME, " onBannerFailed.-code=" + code + "," + msg);
            if (mCustomEventBannerListener != null) {
                mCustomEventBannerListener.onAdFailedToLoad(code);
            }
        }

        @Override
        public void onRenderSuccess(View view, float width, float height) {
            Log.e(ADAPTER_NAME, " onRenderSuccess.-code=");
            if (mCustomEventBannerListener != null) {
                //render success add view to google view
                mCustomEventBannerListener.onAdLoaded(view);
            }

        }
    };
}
