package com.union_test.toutiao.activity.nativead;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.union_test.toutiao.R;
import com.union_test.toutiao.config.TTAdManagerHolder;
import com.union_test.toutiao.utils.TToast;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("EmptyMethod")
public class NativeBannerActivity extends Activity {

    private TTAdNative mTTAdNative;
    private FrameLayout mBannerContainer;
    private Context mContext;
    private Button mButtonDownload;
    private Button mButtonLandingPage;
    private Button mCreativeButton;

    @SuppressLint("SetTextI18n")
    @SuppressWarnings("RedundantCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_banner);
        mContext = this.getApplicationContext();
        mBannerContainer = (FrameLayout) findViewById(R.id.banner_container);
        mButtonDownload = (Button) findViewById(R.id.btn_banner_download);
        mButtonDownload.setText("native banner ad");
        mButtonLandingPage = (Button) findViewById(R.id.btn_banner_landingpage);
        mButtonLandingPage.setVisibility(View.GONE);
        mButtonDownload.setOnClickListener(mClickListener);
        mButtonLandingPage.setOnClickListener(mClickListener);
        //create TTAdNative object which use to request ad，createAdNative(Context context),the context should be an Activity object
        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mCreativeButton != null) {
            mCreativeButton = null;
        }
    }

    private final View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_banner_download) {
                loadBannerAd("945071432");
            }
        }
    };

    @SuppressWarnings({"ALL", "SameParameterValue"})
    private void loadBannerAd(String codeId) {
        //AdSlot,notice: the method setNativeAdtype must be invoked
        final AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(600, 257)
                //When requesting a native ad,
                // be sure to call this method and set the parameter to
                // TYPE_BANNER or TYPE_INTERACTION_AD
                .setNativeAdType(AdSlot.TYPE_BANNER)
                .setAdCount(1)
                .build();

        //request ad
        mTTAdNative.loadNativeAd(adSlot, new TTAdNative.NativeAdListener() {
            @Override
            public void onError(int code, String message) {
                TToast.show(NativeBannerActivity.this, "load error : " + code + ", " + message);
            }

            @Override
            public void onNativeAdLoad(List<TTNativeAd> ads) {
                if (ads.get(0) == null) {
                    return;
                }
                View bannerView = LayoutInflater.from(mContext).inflate(R.layout.native_ad, mBannerContainer, false);
                if (bannerView == null) {
                    return;
                }
                if (mCreativeButton != null) {
                    mCreativeButton = null;
                }
                mBannerContainer.removeAllViews();
                mBannerContainer.addView(bannerView);
                //bind ad data and interaction
                setAdData(bannerView, ads.get(0));
            }
        });
    }

    @SuppressWarnings("RedundantCast")
    private void setAdData(View nativeView, TTNativeAd nativeAd) {
        ((TextView) nativeView.findViewById(R.id.tv_native_ad_title)).setText(nativeAd.getTitle());
        ((TextView) nativeView.findViewById(R.id.tv_native_ad_desc)).setText(nativeAd.getDescription());
        ImageView imgDislike = nativeView.findViewById(R.id.img_native_dislike);
        bindDislikeAction(nativeAd, imgDislike);
        if (nativeAd.getImageList() != null && !nativeAd.getImageList().isEmpty()) {
            TTImage image = nativeAd.getImageList().get(0);
            if (image != null && image.isValid()) {
                ImageView im = nativeView.findViewById(R.id.iv_native_image);
                Glide.with(this).load(image.getImageUrl()).into(im);
            }
        }
        TTImage icon = nativeAd.getIcon();
        if (icon != null && icon.isValid()) {
            ImageView im = nativeView.findViewById(R.id.iv_native_icon);
            Glide.with(this).load(icon.getImageUrl()).into(im);
        }
        mCreativeButton = (Button) nativeView.findViewById(R.id.btn_native_creative);
        switch (nativeAd.getInteractionType()) {
            case TTAdConstant.INTERACTION_TYPE_DOWNLOAD:
                mCreativeButton.setVisibility(View.VISIBLE);
                mCreativeButton.setText(getString(R.string.tt_native_banner_download));
                break;
            case TTAdConstant.INTERACTION_TYPE_DIAL:
                mCreativeButton.setVisibility(View.VISIBLE);
                mCreativeButton.setText(getString(R.string.tt_native_banner_call));
                break;
            case TTAdConstant.INTERACTION_TYPE_LANDING_PAGE:
            case TTAdConstant.INTERACTION_TYPE_BROWSER:
                mCreativeButton.setVisibility(View.VISIBLE);
                mCreativeButton.setText(getString(R.string.tt_native_banner_view));
                break;
            default:
                mCreativeButton.setVisibility(View.GONE);
                TToast.show(mContext, "error");
        }

        //the views that can be clicked
        List<View> clickViewList = new ArrayList<>();
        clickViewList.add(nativeView);

        //The views that can trigger the creative action (like download app)
        List<View> creativeViewList = new ArrayList<>();
        creativeViewList.add(mCreativeButton);

        //notice! This involves advertising billing and must be called correctly. convertView must use ViewGroup.
        nativeAd.registerViewForInteraction((ViewGroup) nativeView, clickViewList, creativeViewList, imgDislike, new TTNativeAd.AdInteractionListener() {
            @Override
            public void onAdClicked(View view, TTNativeAd ad) {
                if (ad != null) {
                    TToast.show(mContext, "ad" + ad.getTitle() + "clicked");
                }
            }

            @Override
            public void onAdCreativeClick(View view, TTNativeAd ad) {
                if (ad != null) {
                    TToast.show(mContext, "ad" + ad.getTitle() + "creative button clicked");
                }
            }

            @Override
            public void onAdShow(TTNativeAd ad) {
                if (ad != null) {
                    TToast.show(mContext, "ad" + ad.getTitle() + "show");
                }
            }
        });

    }

    //set dislike
    private void bindDislikeAction(TTNativeAd ad, View dislikeView) {
        final TTAdDislike ttAdDislike = ad.getDislikeDialog(this);
        if (ttAdDislike != null) {
            ttAdDislike.setDislikeInteractionCallback(new TTAdDislike.DislikeInteractionCallback() {
                @Override
                public void onSelected(int position, String value) {
                    mBannerContainer.removeAllViews();
                }

                @Override
                public void onCancel() {

                }
            });
        }
        dislikeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ttAdDislike != null)
                    ttAdDislike.showDislikeDialog();
            }
        });
    }
}
