package com.union_test.toutiao.activity.nativead;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTBannerAd;
import com.union_test.toutiao.R;
import com.union_test.toutiao.config.TTAdManagerHolder;
import com.union_test.toutiao.utils.TToast;

@SuppressWarnings("unused")
public class BannerActivity extends Activity {

    private TTAdNative mTTAdNative;
    private FrameLayout mBannerContainer;
    private Context mContext;
    private TTAdDislike mTTAdDislike;
    private Button mButtonDownload;
    private Button mButtonLandingPage;

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
        mButtonLandingPage = (Button) findViewById(R.id.btn_banner_landingpage);
        mButtonDownload.setOnClickListener(mClickListener);
        mButtonLandingPage.setOnClickListener(mClickListener);
        //create TTAdNative object which use to request ad，createAdNative(Context context),the context should be an Activity object
        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    protected void onResume() {
        super.onResume();
    }

    private final View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_banner_download) {
                loadBannerAd("945071432");
            } else if (v.getId() == R.id.btn_banner_landingpage) {
                loadBannerAd("945071432");
            }

        }
    };

    private void loadBannerAd(String codeId) {
        //Create a parameter AdSlot for reward ad request type,
        //      refer to the document for meanings of specific parameters
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId) //rit
                .setSupportDeepLink(true)
                .setImageAcceptedSize(600, 257)
                .build();
        //step5:request ad
        mTTAdNative.loadBannerAd(adSlot, new TTAdNative.BannerAdListener() {

            @Override
            public void onError(int code, String message) {
                TToast.show(BannerActivity.this, "load error : " + code + ", " + message);
                mBannerContainer.removeAllViews();
            }

            @Override
            public void onBannerAdLoad(final TTBannerAd ad) {
                if (ad == null) {
                    return;
                }
                View bannerView = ad.getBannerView();
                if (bannerView == null) {
                    return;
                }
                //Set the time interval of the carousel.
                // The value of the interval is between 30s and 120 seconds.
                // If not set, the carousel will not be rotated by default.
                ad.setSlideIntervalTime(30 * 1000);
                mBannerContainer.removeAllViews();
                mBannerContainer.addView(bannerView);
                //set interaction listener
                ad.setBannerInteractionListener(new TTBannerAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
                        TToast.show(mContext, "Ad clicked");
                    }

                    @Override
                    public void onAdShow(View view, int type) {
                        TToast.show(mContext, "Ad showed");
                    }
                });
                //set dislike
                ad.setShowDislikeIcon(new TTAdDislike.DislikeInteractionCallback() {
                    @Override
                    public void onSelected(int position, String value) {
                        TToast.show(mContext, "Click" + value);
                        mBannerContainer.removeAllViews();
                    }

                    @Override
                    public void onCancel() {
                        TToast.show(mContext, "Cancel Click");
                    }
                });

                //dislike dialog，customize the style of dislike icon
                /*mTTAdDislike = ad.getDislikeDialog(new TTAdDislike.DislikeInteractionCallback() {
                        @Override
                        public void onSelected(int position, String value) {
                            //remove ad
                        }

                        @Override
                        public void onCancel() {
                        }
                    });
                if (mTTAdDislike != null) {
                    XXX.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mTTAdDislike.showDislikeDialog();
                        }
                    });
                } */

            }
        });
    }

}
