package com.union_test.toutiao.activity.nativead;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.union_test.toutiao.R;
import com.union_test.toutiao.config.TTAdManagerHolder;
import com.union_test.toutiao.utils.TToast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * interstitial ad
 */
public class NativeInteractionActivity extends Activity implements View.OnClickListener {

    private TTAdNative mTTAdNative;
    private Button mShow_InterstitialAd_btn;
    private Button mShow_InterstitialAd_btn_ladingpage;
    private Context mContext;
    private ImageView mAdImageView;
    private ImageView mCloseImageView;
    private Dialog mAdDialog;
    private ViewGroup mRootView;
    private TextView mDislikeView;
    private boolean mIsLoading = false;
    private RequestManager mRequestManager;

    @SuppressLint("SetTextI18n")
    @SuppressWarnings("RedundantCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_ad);
        mContext = this;
        mRequestManager = Glide.with(this);
        //create TTAdNative object which use to request ad，createAdNative(Context context),the context should be an activity object
        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
        mShow_InterstitialAd_btn = (Button) findViewById(R.id.show_ad_dialog);
        mShow_InterstitialAd_btn.setText("show native INTERSTITIAL");
        mShow_InterstitialAd_btn_ladingpage = (Button) findViewById(R.id.show_ad_dialog_landingpage);
        mShow_InterstitialAd_btn_ladingpage.setVisibility(View.GONE);
        mShow_InterstitialAd_btn.setOnClickListener(this);
        mShow_InterstitialAd_btn_ladingpage.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (mIsLoading) {
            return;
        }
        if (v.getId() == R.id.show_ad_dialog) {
            loadInteractionAd("945071429");
        }
    }

    /**
     * load ad
     */
    @SuppressWarnings({"ALL", "SameParameterValue"})
    private void loadInteractionAd(String codeId) {
        mIsLoading = true;
        //AdSlot,notice: the method setNativeAdtype must be invoked
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                //When requesting a native ad,
                // be sure to call this method and set the parameter to
                // TYPE_BANNER or TYPE_INTERACTION_AD
                .setNativeAdType(AdSlot.TYPE_INTERACTION_AD)
                .build();

        //step5:request ad
        mTTAdNative.loadNativeAd(adSlot, new TTAdNative.NativeAdListener() {
            @Override
            public void onError(int code, String message) {
                mIsLoading = false;
                TToast.show(NativeInteractionActivity.this, "load error : " + code + ", " + message);
            }

            @Override
            public void onNativeAdLoad(List<TTNativeAd> ads) {
                mIsLoading = false;
                if (ads.get(0) == null) {
                    return;
                }
                showAd(ads.get(0));
            }
        });
    }

    @SuppressWarnings("RedundantCast")
    private void showAd(TTNativeAd ad) {
        mAdDialog = new Dialog(mContext, R.style.native_insert_dialog);
        mAdDialog.setCancelable(false);
        mAdDialog.setContentView(R.layout.native_insert_ad_layout);
        mRootView = mAdDialog.findViewById(R.id.native_insert_ad_root);
        mAdImageView = (ImageView) mAdDialog.findViewById(R.id.native_insert_ad_img);
        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        int maxWidth = (dm == null) ? 0 : dm.widthPixels;
        int minWidth = maxWidth / 3;
        mAdImageView.setMaxWidth(maxWidth);
        mAdImageView.setMinimumWidth(minWidth);
        //noinspection SuspiciousNameCombination
        mAdImageView.setMinimumHeight(minWidth);
        mCloseImageView = (ImageView) mAdDialog.findViewById(R.id.native_insert_close_icon_img);
        mDislikeView = mAdDialog.findViewById(R.id.native_insert_dislike_text);

        ImageView iv = mAdDialog.findViewById(R.id.native_insert_ad_logo);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            ad.getAdLogo().compress(Bitmap.CompressFormat.PNG, 100, stream);
            mRequestManager
                    .load(stream.toByteArray())
                    .asBitmap()
                    .into(iv);
        }catch (Exception e){

        }finally {
            try {
                stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //close for ad
        bindCloseAction();
        //dislike for ad
        bindDislikeAction(ad);
        //interaction for ad
        bindViewInteraction(ad);
        //load image
        loadAdImage(ad);
    }

    private void bindCloseAction() {
        mCloseImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdDialog.dismiss();
            }
        });
    }

    private void bindDislikeAction(TTNativeAd ad) {
        final TTAdDislike ttAdDislike = ad.getDislikeDialog(this);
        if (ttAdDislike != null) {
            ttAdDislike.setDislikeInteractionCallback(new TTAdDislike.DislikeInteractionCallback() {
                @Override
                public void onSelected(int position, String value) {
                    mAdDialog.dismiss();
                }

                @Override
                public void onCancel() {

                }
            });
        }
        mDislikeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ttAdDislike != null)
                    ttAdDislike.showDislikeDialog();
            }
        });
    }

    private void bindViewInteraction(TTNativeAd ad) {
        //the views that can be clicked
        List<View> clickViewList = new ArrayList<>();
        clickViewList.add(mAdImageView);

        //The views that can trigger the creative action (like download app)
        List<View> creativeViewList = new ArrayList<>();
        creativeViewList.add(mAdImageView);

        //notice! This involves advertising billing and must be called correctly. convertView must use ViewGroup.
        ad.registerViewForInteraction(mRootView, clickViewList, creativeViewList, mDislikeView, new TTNativeAd.AdInteractionListener() {
            @Override
            public void onAdClicked(View view, TTNativeAd ad) {
                if (ad != null) {
                    TToast.show(mContext, "ad" + ad.getTitle() + "clicked");
                }
            }

            @Override
            public void onAdCreativeClick(View view, TTNativeAd ad) {
                if (ad != null) {
                    TToast.show(mContext, "ad" + ad.getTitle() + "button clicked");
                }
            }

            @Override
            public void onAdShow(TTNativeAd ad) {
                if (ad != null) {
                    TToast.show(mContext, "ad" + ad.getTitle() + "showed");
                }
            }
        });
    }

    private void loadAdImage(TTNativeAd ad) {
        if (ad.getImageList() != null && !ad.getImageList().isEmpty()) {
            TTImage image = ad.getImageList().get(0);
            if (image != null && image.isValid()) {
                mRequestManager.load(image.getImageUrl()).into(mAdImageView);
            }
        }

        TTImage image = ad.getImageList().get(0);
        String url = image.getImageUrl();
        mRequestManager.load(url).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable glideDrawable, GlideAnimation<? super GlideDrawable> glideAnimation) {
                if (mAdImageView != null) {
                    mAdImageView.setImageDrawable(glideDrawable);
                    showAd();
                }
            }
        });
    }

    private void showAd() {
        if (this.isFinishing()) {
            return;
        }
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException("should call TTInteractionAd.showInteractionAd in main thread");
        }
        mAdDialog.show();
    }

}
