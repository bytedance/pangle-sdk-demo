package com.mopub.nativeads;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.FilterWord;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTDislikeDialogAbstract;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.mopub.common.DataKeys;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.PangleAdapterConfiguration;
import com.mopub.mobileads.PangleSharedUtil;

import java.util.List;
import java.util.Map;

import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.CLICKED;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.CUSTOM;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.LOAD_ATTEMPTED;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.LOAD_FAILED;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.LOAD_SUCCESS;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.SHOW_SUCCESS;
import static com.mopub.nativeads.NativeErrorCode.NETWORK_NO_FILL;

public class PangleAdNative extends CustomEventNative {
    private static final String ADAPTER_NAME = PangleAdNative.class.getSimpleName();

    public static final String KEY_EXTRA_MEDIA_VIEW_WIDTH = "media_view_width";
    public static final String KEY_EXTRA_MEDIA_VIEW_HEIGHT = "media_view_height";

    private static String mPlacementId = "";
    private Context mContext;
    private CustomEventNativeListener mCustomEventNativeListener;
    private PangleAdapterConfiguration mPangleAdapterConfiguration;

    public PangleAdNative() {
        mPangleAdapterConfiguration = new PangleAdapterConfiguration();
        MoPubLog.log(CUSTOM, ADAPTER_NAME, "PangleAdNative has been create ....");
    }

    @Override
    protected void loadNativeAd(Context context, CustomEventNativeListener customEventNativeListener, Map<String, Object> localExtras, Map<String, String> serverExtras) {
        MoPubLog.log(getAdNetworkId(), CUSTOM, ADAPTER_NAME, "loadNativeAd...... has been create ....");
        this.mContext = context;
        this.mCustomEventNativeListener = customEventNativeListener;

        TTAdManager ttAdManager = null;
        String adm = null;

        if (serverExtras != null) {
            /** Obtain ad placement id from MoPub UI */
            mPlacementId = serverExtras.get(PangleAdapterConfiguration.AD_PLACEMENT_ID_EXTRA_KEY);
            if (TextUtils.isEmpty(mPlacementId)) {
                if (customEventNativeListener != null) {
                    customEventNativeListener.onNativeAdFailed(NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR);
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

            mPangleAdapterConfiguration.setCachedInitializationParameters(context, serverExtras);
        }

        /** default media view ad size */
        int mediaViewWidth = 640;
        int mediaViewHeight = 320;
        if (localExtras != null) {
            if (localExtras.containsKey(KEY_EXTRA_MEDIA_VIEW_WIDTH)) {
                mediaViewWidth = (int) localExtras.get(KEY_EXTRA_MEDIA_VIEW_WIDTH);
            }
            if (localExtras.containsKey(KEY_EXTRA_MEDIA_VIEW_HEIGHT)) {
                mediaViewHeight = (int) localExtras.get(KEY_EXTRA_MEDIA_VIEW_HEIGHT);
            }
        }

        MoPubLog.log(getAdNetworkId(), CUSTOM, ADAPTER_NAME, "extras: mediaViewWidth=" + mediaViewWidth + ", mediaViewHeight=" + mediaViewHeight);
        if (ttAdManager != null) {
            TTAdNative adNative = ttAdManager.createAdNative(mContext);
            AdSlot adSlot = new AdSlot.Builder()
                    .setCodeId(mPlacementId)
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(mediaViewWidth, mediaViewHeight)
                    .setAdCount(1)
                    .withBid(adm)
                    .build();


            /** request ad */
            MoPubLog.log(getAdNetworkId(), LOAD_ATTEMPTED, ADAPTER_NAME);
            adNative.loadFeedAd(adSlot, new TTAdNative.FeedAdListener() {
                @Override
                public void onError(int errorCode, String message) {
                    MoPubLog.log(getAdNetworkId(), CUSTOM, "Loading NativeAd encountered an error: "
                            + mapErrorCode(errorCode).toString() + ",error message:" + message);
                    MoPubLog.log(getAdNetworkId(), LOAD_FAILED, ADAPTER_NAME,
                            mapErrorCode(errorCode).getIntCode(),
                            mapErrorCode(errorCode));
                    if (mCustomEventNativeListener != null) {
                        mCustomEventNativeListener.onNativeAdFailed(mapErrorCode(errorCode));
                    }
                }

                @Override
                public void onFeedAdLoad(List<TTFeedAd> ads) {
                    if (ads != null && ads.size() > 0) {
                        MoPubLog.log(getAdNetworkId(), LOAD_SUCCESS, ADAPTER_NAME);
                        if (mCustomEventNativeListener != null) {
                            for (TTFeedAd ad : ads) {
                                mCustomEventNativeListener.onNativeAdLoaded(new PangolinNativeAd(ad));
                            }
                        }
                    } else {
                        MoPubLog.log(getAdNetworkId(), LOAD_FAILED, ADAPTER_NAME,
                                NETWORK_NO_FILL.getIntCode(), NETWORK_NO_FILL);
                        if (mCustomEventNativeListener != null) {
                            mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_NO_FILL);
                        }
                    }
                }
            });
        } else {
            MoPubLog.log(getAdNetworkId(), CUSTOM, ADAPTER_NAME, "Pangle sdk ttAdManagerLoader can not be created." +
                    " Please make sure to pass the correct app id.");
            if (customEventNativeListener != null) {
                customEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_INVALID_REQUEST);
            }
        }
    }

    private static String getAdNetworkId() {
        return mPlacementId;
    }


    protected static class PangolinNativeAd extends BaseNativeAd implements TTNativeAd.AdInteractionListener {

        private TTFeedAd mTTFeedAd;

        PangolinNativeAd(TTFeedAd ad) {
            this.mTTFeedAd = ad;
        }

        @Override
        public void prepare(View view) {
        }

        @Override
        public void clear(View view) {
        }

        @Override
        public void destroy() {
        }

        @Override
        public void onAdClicked(View view, TTNativeAd ad) {
            MoPubLog.log(getAdNetworkId(), CLICKED, ADAPTER_NAME);
            notifyAdClicked();
        }

        @Override
        public void onAdCreativeClick(View view, TTNativeAd ad) {
            MoPubLog.log(getAdNetworkId(), CLICKED, ADAPTER_NAME);
            notifyAdClicked();
        }

        @Override
        public void onAdShow(TTNativeAd ad) {
            MoPubLog.log(getAdNetworkId(), SHOW_SUCCESS, ADAPTER_NAME);
            notifyAdImpressed();
        }


        public final String getAdvertiserName() {
            if (mTTFeedAd != null) {
                return mTTFeedAd.getSource();
            }
            return null;
        }

        public final String getTitle() {
            if (mTTFeedAd != null) {
                return mTTFeedAd.getTitle();
            }
            return null;
        }

        public final String getDescriptionText() {
            if (mTTFeedAd != null) {
                return mTTFeedAd.getDescription();
            }
            return null;
        }

        public final String getCallToAction() {
            if (mTTFeedAd != null) {
                return mTTFeedAd.getButtonText();
            }
            return null;
        }

        public TTImage getVideoCoverImage() {
            if (mTTFeedAd != null) {
                return mTTFeedAd.getVideoCoverImage();
            }
            return null;
        }

        public TTImage getIcon() {
            if (mTTFeedAd != null) {
                return mTTFeedAd.getIcon();
            }
            return null;
        }

        public void showPrivacyActivity(){
            if (mTTFeedAd != null) {
                mTTFeedAd.showPrivacyActivity();
            }
        }

        public Bitmap getAdLogo() {
            if (mTTFeedAd != null) {
                return mTTFeedAd.getAdLogo();
            }
            return null;
        }

        public List<TTImage> getImageList() {
            if (mTTFeedAd != null) {
                return mTTFeedAd.getImageList();
            }
            return null;
        }

        public int getAppScore() {
            if (mTTFeedAd != null) {
                return mTTFeedAd.getAppScore();
            }
            return -1;
        }

        public int getAppCommentNum() {
            if (mTTFeedAd != null) {
                return mTTFeedAd.getAppCommentNum();
            }
            return -1;
        }

        public int getAppSize() {
            if (mTTFeedAd != null) {
                return mTTFeedAd.getAppSize();
            }
            return -1;
        }

        public int getInteractionType() {
            if (mTTFeedAd != null) {
                return mTTFeedAd.getInteractionType();
            }
            return -1;
        }

        public int getImageMode() {
            if (mTTFeedAd != null) {
                return mTTFeedAd.getImageMode();
            }
            return -1;
        }

        public List<FilterWord> getFilterWords() {
            if (mTTFeedAd != null) {
                return mTTFeedAd.getFilterWords();
            }
            return null;
        }

        public View getAdView() {
            if (mTTFeedAd != null) {
                return mTTFeedAd.getAdView();
            }
            return null;
        }


        public TTAdDislike getDislikeDialog(Activity activity) {
            if (mTTFeedAd != null) {
                return mTTFeedAd.getDislikeDialog(activity);
            }
            return null;
        }

        public TTAdDislike getDislikeDialog(TTDislikeDialogAbstract dialog) {
            if (mTTFeedAd != null) {
                return mTTFeedAd.getDislikeDialog(dialog);
            }
            return null;
        }

        public void registerViewForInteraction(@NonNull ViewGroup container, @NonNull View clickView, TTNativeAd.AdInteractionListener listener) {
            if (mTTFeedAd != null) {
                mTTFeedAd.registerViewForInteraction(container, clickView, listener);
            }
        }


        public void registerViewForInteraction(@NonNull ViewGroup container, @NonNull List<View> clickViews, @Nullable List<View> creativeViews, TTNativeAd.AdInteractionListener listener) {
            if (mTTFeedAd != null) {
                mTTFeedAd.registerViewForInteraction(container, clickViews, creativeViews, listener);
            }
        }


        public void registerViewForInteraction(@NonNull ViewGroup container, @NonNull List<View> clickViews, @Nullable List<View> creativeViews, @Nullable View dislikeView, TTNativeAd.AdInteractionListener listener) {
            if (mTTFeedAd != null) {
                mTTFeedAd.registerViewForInteraction(container, clickViews, creativeViews, dislikeView, listener);
            }
        }


    }

    private static NativeErrorCode mapErrorCode(int error) {
        switch (error) {
            case PangleSharedUtil.CONTENT_TYPE:
            case PangleSharedUtil.REQUEST_PB_ERROR:
                return NativeErrorCode.CONNECTION_ERROR;
            case PangleSharedUtil.NO_AD:
                return NativeErrorCode.NETWORK_NO_FILL;
            case PangleSharedUtil.ADSLOT_EMPTY:
            case PangleSharedUtil.ADSLOT_ID_ERROR:
                return NativeErrorCode.NETWORK_INVALID_REQUEST;
            default:
                return NativeErrorCode.UNSPECIFIED;
        }
    }

}
