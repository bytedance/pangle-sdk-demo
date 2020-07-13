package com.google.ads.mediation.sample.customevent.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;


import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.bytedance.sdk.openadsdk.adapter.MediaView;
import com.bytedance.sdk.openadsdk.adapter.MediationAdapterUtil;
import com.google.ads.mediation.sample.customevent.AdmobAdapterUtil;
import com.google.ads.mediation.sample.customevent.BundleBuilder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAdAssetNames;
import com.google.android.gms.ads.mediation.NativeMediationAdRequest;
import com.google.android.gms.ads.mediation.UnifiedNativeAdMapper;
import com.google.android.gms.ads.mediation.customevent.CustomEventNative;
import com.google.android.gms.ads.mediation.customevent.CustomEventNativeListener;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * created by wuzejian on 2020-06-01
 * pangle feed native ad
 */
public class AdmobFeedAdAdapter implements CustomEventNative {
    private static final String ADAPTER_NAME = "AdmobFeedAdAdapter";
    private static final double SAMPLE_SDK_IMAGE_SCALE = 1.0;
    public static final String KEY_PANGLE_LOGO = "pangle_logo";
    private String mCodeId = "";
    private CustomEventNativeListener mCustomEventNativeListener;
    private Context mContext;
    private NativeAdOptions mNativeAdOptions;

    @Override
    public void requestNativeAd(Context context, CustomEventNativeListener customEventNativeListener, String serverParameter, NativeMediationAdRequest nativeMediationAdRequest, Bundle customEventExtras) {
        this.mContext = context;
        this.mCustomEventNativeListener = customEventNativeListener;

        //obtain ad placement_id from admob server
        mCodeId = AdmobAdapterUtil.getAdPlacementId(serverParameter);

        if (mCodeId == null) {
            mCodeId = customEventExtras.getString(BundleBuilder.KEY_AD_PLACEMENT_ID);
            if (mCodeId == null && customEventNativeListener != null) {
                customEventNativeListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
                return;
            }
        }

        Log.e(ADAPTER_NAME, "AdmobFeedAdAdapter->requestNativeAd.-mCodeId=" + mCodeId);

        TTAdNative ttAdLoader = AdmobAdapterUtil.getPangleAdLoader(context);

        if (customEventExtras != null && customEventExtras.containsKey(BundleBuilder.AD_GDPR)) {
            int gdpr = customEventExtras.getInt(BundleBuilder.AD_GDPR, 1);
            AdmobAdapterUtil.setGdpr(gdpr);
            Log.e(ADAPTER_NAME, "AdmobFeedAdAdapter->feed native ad receive gdpr=" + gdpr);
        }

        mNativeAdOptions = nativeMediationAdRequest.getNativeAdOptions();

        if (!nativeMediationAdRequest.isUnifiedNativeAdRequested()) {
            Log.e(ADAPTER_NAME, "Failed to load ad. Request must be for unified native ads.");
            if (customEventNativeListener != null)
                customEventNativeListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(mCodeId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(640, 320)
                .setAdCount(1) //ad count from 1 to 3
                .build();

        //step5:request ad
        if (ttAdLoader != null) {
            ttAdLoader.loadFeedAd(adSlot, feedAdListener);
        } else {
            if (mCustomEventNativeListener != null) {
                mCustomEventNativeListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            }
            Log.e(ADAPTER_NAME, "ttAdLoader can not be null .");
        }


    }


    private TTAdNative.FeedAdListener feedAdListener = new TTAdNative.FeedAdListener() {
        @Override
        public void onError(int code, String message) {
            Log.e(ADAPTER_NAME, "feedAdListener loaded fail .code=" + code + ",message=" + message);
            if (mCustomEventNativeListener != null) {
                mCustomEventNativeListener.onAdFailedToLoad(AdmobAdapterUtil.getAdmobError(code));
            }
        }

        @Override
        public void onFeedAdLoad(List<TTFeedAd> ads) {
            if (ads == null || ads.size() == 0) {
                if (mCustomEventNativeListener != null) {
                    mCustomEventNativeListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NO_FILL);
                }
                Log.e(ADAPTER_NAME, "feedAdListener loaded success .but ad no fill ");
                return;
            }

            if (mCustomEventNativeListener != null) {
                mCustomEventNativeListener.onAdLoaded(new PangleNativeAd(ads.get(0)));
            }
        }
    };


    class PangleNativeAd extends UnifiedNativeAdMapper {
        private TTFeedAd mPangleAd;

        private PangleNativeAd(TTFeedAd ad) {
            this.mPangleAd = ad;
            setHeadline(mPangleAd.getTitle());
            setBody(mPangleAd.getDescription());
            setCallToAction(mPangleAd.getButtonText());
            setStarRating(Double.valueOf(mPangleAd.getAppScore()));
            setAdvertiser(mPangleAd.getSource());

            if (mPangleAd.getIcon() != null && mPangleAd.getIcon().isValid()) {
                setIcon(new PangleNativeMappedImage(null, Uri.parse(mPangleAd.getIcon().getImageUrl()), SAMPLE_SDK_IMAGE_SCALE));
            }

            if (mPangleAd.getImageList() != null && mPangleAd.getImageList().size() != 0) {
                List<NativeAd.Image> imagesList = new ArrayList<>();
                for (TTImage ttImage : mPangleAd.getImageList()) {
                    if (ttImage.isValid()) {
                        imagesList.add(new PangleNativeMappedImage(null, Uri.parse(ttImage.getImageUrl()),
                                SAMPLE_SDK_IMAGE_SCALE));
                    }
                }
                setImages(imagesList);
            }


//            Bundle extras = new Bundle();
//            extras.putParcelable(KEY_PANGLE_LOGO, mPangleAd.getAdLogo());
//            this.setExtras(extras);

            /**Pangle does its own show event handling and click event handling*/
            setOverrideImpressionRecording(true);
            setOverrideClickHandling(true);


            /** add Native Feed Main View */
            MediaView mediaView = new MediaView(mContext);
            MediationAdapterUtil.addNativeFeedMainView(mContext, ad.getImageMode(), mediaView, ad.getAdView(), ad.getImageList());
            setMediaView(mediaView);


            if (mPangleAd.getImageMode() == TTAdConstant.IMAGE_MODE_VIDEO) {
                setHasVideoContent(true);
                mPangleAd.setVideoAdListener(new TTFeedAd.VideoAdListener() {
                    @Override
                    public void onVideoLoad(TTFeedAd ad) {
                        // Google Mobile Ads SDK doesn't have a matching event. Do nothing.
                    }

                    @Override
                    public void onVideoError(int errorCode, int extraCode) {
                        // Google Mobile Ads SDK doesn't have a matching event. Do nothing.
                    }

                    @Override
                    public void onVideoAdStartPlay(TTFeedAd ad) {
                        // Google Mobile Ads SDK doesn't have a matching event. Do nothing.
                    }

                    @Override
                    public void onVideoAdPaused(TTFeedAd ad) {
                        // Google Mobile Ads SDK doesn't have a matching event. Do nothing.
                    }

                    @Override
                    public void onVideoAdContinuePlay(TTFeedAd ad) {
                        // Google Mobile Ads SDK doesn't have a matching event. Do nothing.
                    }

                    @Override
                    public void onProgressUpdate(long current, long duration) {
                        // Google Mobile Ads SDK doesn't have a matching event. Do nothing.
                    }

                    @Override
                    public void onVideoAdComplete(TTFeedAd ad) {
                        // Google Mobile Ads SDK doesn't have a matching event. Do nothing.
                    }
                });
            }
        }

        @Override
        public void trackViews(View view,
                               Map<String, View> clickableAssetViews,
                               Map<String, View> nonClickableAssetViews) {



            ArrayList<View> assetViews = new ArrayList<>(clickableAssetViews.values());
            View creativeBtn = clickableAssetViews.get(UnifiedNativeAdAssetNames.ASSET_CALL_TO_ACTION);
            ArrayList<View> creativeViews = new ArrayList<>();
            if (creativeBtn != null) {
                creativeViews.add(creativeBtn);
            }

            if (mPangleAd != null) {
                mPangleAd.registerViewForInteraction((ViewGroup) view, assetViews, creativeViews, new TTNativeAd.AdInteractionListener() {

                    @Override
                    public void onAdClicked(View view, TTNativeAd ad) {
                        if (mCustomEventNativeListener != null) {
                            mCustomEventNativeListener.onAdClicked();
                            mCustomEventNativeListener.onAdOpened();
                            mCustomEventNativeListener.onAdLeftApplication();
                        }

                    }

                    @Override
                    public void onAdCreativeClick(View view, TTNativeAd ad) {
                        if (mCustomEventNativeListener != null) {
                            mCustomEventNativeListener.onAdClicked();
                            mCustomEventNativeListener.onAdOpened();
                            mCustomEventNativeListener.onAdLeftApplication();
                        }
                    }

                    @Override
                    public void onAdShow(TTNativeAd ad) {
                        if (mCustomEventNativeListener != null) {
                            mCustomEventNativeListener.onAdImpression();
                        }
                    }
                });
            }


            ViewGroup adView = (ViewGroup) view;

            View overlayView = adView.getChildAt(adView.getChildCount() - 1);
            if (overlayView instanceof FrameLayout) {
                int privacyIconPlacement = NativeAdOptions.ADCHOICES_TOP_RIGHT;
                if (mNativeAdOptions != null) {
                    privacyIconPlacement = mNativeAdOptions.getAdChoicesPlacement();
                }
                final Context context = view.getContext();
                if (context == null) {
                    return;
                }

                ImageView privacyInformationIconImageView = (ImageView) mPangleAd.getAdLogoView();
                privacyInformationIconImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPangleAd.showPrivacyActivity();
                        Log.d(ADAPTER_NAME, "privacyInformationIconImageView--》click");
                    }
                });

                privacyInformationIconImageView.setVisibility(View.VISIBLE);
                ((ViewGroup) overlayView).addView(privacyInformationIconImageView);

                float scale = context.getResources().getDisplayMetrics().density;
                int icon_size_px = (int) (AdmobAdapterUtil.dp2px(mContext, 10) * scale + 0.5);
                FrameLayout.LayoutParams params =
                        new FrameLayout.LayoutParams(icon_size_px, icon_size_px);

                switch (privacyIconPlacement) {
                    case NativeAdOptions.ADCHOICES_TOP_LEFT:
                        params.gravity = Gravity.TOP | Gravity.START;
                        break;
                    case NativeAdOptions.ADCHOICES_BOTTOM_RIGHT:
                        params.gravity = Gravity.BOTTOM | Gravity.END;
                        break;
                    case NativeAdOptions.ADCHOICES_BOTTOM_LEFT:
                        params.gravity = Gravity.BOTTOM | Gravity.START;
                        break;
                    case NativeAdOptions.ADCHOICES_TOP_RIGHT:
                        params.gravity = Gravity.TOP | Gravity.END;
                        break;
                    default:
                        params.gravity = Gravity.TOP | Gravity.END;
                }
                privacyInformationIconImageView.setLayoutParams(params);
                adView.requestLayout();
            }


        }

    }


    public class PangleNativeMappedImage extends NativeAd.Image {

        private final Drawable drawable;
        private final Uri imageUri;
        private final double scale;

        private PangleNativeMappedImage(Drawable drawable, Uri imageUri, double scale) {
            this.drawable = drawable;
            this.imageUri = imageUri;
            this.scale = scale;
        }

        @Override
        public Drawable getDrawable() {
            return drawable;
        }

        @Override
        public Uri getUri() {
            return imageUri;
        }

        @Override
        public double getScale() {
            return scale;
        }

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }
}
