package com.mopub;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.common.logging.MoPubLog;
import com.mopub.nativeads.AdapterHelper;
import com.mopub.nativeads.MediaViewBinder;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.MoPubVideoNativeAdRenderer;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.PangleAdRenderer;
import com.mopub.nativeads.PangleAdViewBinder;
import com.mopub.nativeads.RequestParameters;
import com.mopub.nativeads.ViewBinder;
import com.union_test.toutiao.utils.UIUtils;
import com.union_test.toutiao.R;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * created by wuzejian on 2020/5/12
 */
public class MopubNativeListActivity extends Activity {

    private String mAdUnitId = "b6083b1343594baab31378c0cb36d4ec";
    private MoPubNative mMoPubNative;

    private LinearLayout mAdContainer;
    @Nullable
    private RequestParameters mRequestParameters;


    private Context getActivity() {
        return MopubNativeListActivity.this;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mopub_native_manual_activity);

        mAdContainer = findViewById(R.id.parent_view);

        findViewById(R.id.load_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                updateRequestParameters();

                if (mAdContainer != null) {
                    mAdContainer.removeAllViews();
                }

                if (mMoPubNative != null) {
                    mMoPubNative.makeRequest(mRequestParameters);
                } else {
                    UIUtils.logToast(getActivity(), getName() + " failed to load. MoPubNative instance is null.");
                }
            }


        });
        SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(mAdUnitId)
                .withLogLevel(MoPubLog.LogLevel.DEBUG)//Log级别
                .build();

        //init MoPub SDK
        MoPub.initializeSdk(this, sdkConfiguration, initSdkListener());
    }

    private SdkInitializationListener initSdkListener() {
        return new SdkInitializationListener() {
            @Override
            public void onInitializationFinished() {
//                mHasInit = true;
                Log.d("MopubFullVideoActivity", "onInitializationFinished////");
                loadNativeAd();
            }
        };
    }

    private void loadNativeAd() {
        String adUnitId = mAdUnitId;


        mMoPubNative = new MoPubNative(getActivity(), adUnitId, new MoPubNative.MoPubNativeNetworkListener() {
            @Override
            public void onNativeLoad(NativeAd nativeAd) {


                NativeAd.MoPubNativeEventListener moPubNativeEventListener = new NativeAd.MoPubNativeEventListener() {
                    @Override
                    public void onImpression(View view) {
                        // The ad has registered an impression. You may call any app logic that
                        // depends on having the ad view shown.
                        UIUtils.logToast(getActivity(), getName() + " impressed.");
                    }

                    @Override
                    public void onClick(View view) {
                        UIUtils.logToast(getActivity(), getName() + " clicked.");
                    }
                };


                // In a manual integration, any interval that is at least 2 is acceptable
                final AdapterHelper adapterHelper = new AdapterHelper(getActivity(), 0, 2);
                final View adView;
                adView = adapterHelper.getAdView(null, null, nativeAd, new ViewBinder.Builder(0).build());
                nativeAd.setMoPubNativeEventListener(moPubNativeEventListener);
                if (mAdContainer != null) {
                    mAdContainer.addView(adView);
                } else {
                    UIUtils.logToast(getActivity(), getName() + " failed to show. Ad container is null.");
                }
            }

            @Override
            public void onNativeFail(NativeErrorCode errorCode) {
                UIUtils.logToast(getActivity(), getName() + " failed to load: " + errorCode.toString());
                Log.e("onNativeFail", " failed to load: " + errorCode.toString());
            }
        });

        MoPubStaticNativeAdRenderer moPubStaticNativeAdRenderer = new MoPubStaticNativeAdRenderer(
                new ViewBinder.Builder(R.layout.native_ad_list_item)
                        .titleId(R.id.native_title)
                        .textId(R.id.native_text)
                        .mainImageId(R.id.native_main_image)
                        .iconImageId(R.id.native_icon_image)
                        .callToActionId(R.id.native_cta)
                        .privacyInformationIconImageId(R.id.native_privacy_information_icon_image)
                        .build()
        );

        // Set up a renderer for a video native ad.
        MoPubVideoNativeAdRenderer moPubVideoNativeAdRenderer = new MoPubVideoNativeAdRenderer(
                new MediaViewBinder.Builder(R.layout.video_ad_list_item)
                        .titleId(R.id.native_title)
                        .textId(R.id.native_text)
                        .mediaLayoutId(R.id.native_media_layout)
                        .iconImageId(R.id.native_icon_image)
                        .callToActionId(R.id.native_cta)
                        .privacyInformationIconImageId(R.id.native_privacy_information_icon_image)
                        .build());


        final PangleAdRenderer pangleNatineAdRender = new PangleAdRenderer(
                new PangleAdViewBinder.Builder(R.layout.mopub_listitem_ad_large_image_video)
                        .logoViewId(R.id.tt_ad_logo)
                        .callToActionId(R.id.tt_creative_btn)
                        .decriptionTextId(R.id.tv_listitem_ad_desc)
                        .iconImageId(R.id.iv_listitem_icon)
                        .sourceId(R.id.tv_listitem_ad_source)
                        .titleId(R.id.tv_listitem_ad_title)
                        .mediaViewIdId(R.id.iv_listitem_video)
                        .build());

        // The first renderer that can handle a particular native ad gets used.
        // We are prioritizing network renderers.

        mMoPubNative.registerAdRenderer(pangleNatineAdRender);
        mMoPubNative.registerAdRenderer(moPubStaticNativeAdRenderer);
        mMoPubNative.registerAdRenderer(moPubVideoNativeAdRenderer);
        Map<String, Object> map = new HashMap<>();
        map.put(KEY_EXTRA_AD_UNIT_ID, "945092256");
        mMoPubNative.setLocalExtras(map);
        mMoPubNative.makeRequest(mRequestParameters);
    }

    public static final String KEY_EXTRA_AD_UNIT_ID = "adunit";


    private void updateRequestParameters() {
        final String keywords = "";
        final String userDataKeywords = "";

        // Setting desired assets on your request helps native ad networks and bidders
        // provide higher-quality ads.
        final EnumSet<RequestParameters.NativeAdAsset> desiredAssets = EnumSet.of(
                RequestParameters.NativeAdAsset.TITLE,
                RequestParameters.NativeAdAsset.TEXT,
                RequestParameters.NativeAdAsset.ICON_IMAGE,
                RequestParameters.NativeAdAsset.MAIN_IMAGE,
                RequestParameters.NativeAdAsset.CALL_TO_ACTION_TEXT,
                RequestParameters.NativeAdAsset.STAR_RATING);

        mRequestParameters = new RequestParameters.Builder()
                .keywords(keywords)
                .userDataKeywords(userDataKeywords)
                .desiredAssets(desiredAssets)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mMoPubNative != null) {
            mMoPubNative.destroy();
            mMoPubNative = null;
        }

        if (mAdContainer != null) {
            mAdContainer.removeAllViews();
            mAdContainer = null;
        }
    }

    private String getName() {

        return "pangolin";
    }
}
