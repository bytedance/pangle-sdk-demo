package com.union_test.new_api.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialAd;
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialAdInteractionListener;
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialAdLoadListener;
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialRequest;
import com.union_test.internationad.R;
import com.union_test.new_api.PAGAppOpenAdManager;
import com.union_test.new_api.RitConstants;
import com.union_test.new_api.utils.TToast;

/**
 * Created by bytedance on 2018/2/1.
 */

public class PAGInterstitialActivity extends Activity {
    public static final String TAG = "PAGInterstitialActivity";
    private Button mLoadAd;
    private Button mLoadAdVertical;
    private Button mShowAd;
    private String mHorizontalCodeId = RitConstants.RIT_INTER_HORIZONTAL;
    private String mVerticalCodeId = RitConstants.RIT_INTER_VERTICAL;

    private PAGInterstitialAd interstitialAd;
    @SuppressWarnings("RedundantCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_video);
        Log.i(TAG, "PAGInterstitialActivity onCreate: ");
        findViewById(R.id.btn_fsv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mLoadAd = (Button) findViewById(R.id.btn_reward_load);
        mLoadAdVertical = (Button) findViewById(R.id.btn_reward_load_vertical);
        mShowAd = (Button) findViewById(R.id.btn_reward_show);
        initClickEvent();
    }

    private void initClickEvent() {

        mLoadAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAd(mHorizontalCodeId);
            }
        });
        mLoadAdVertical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAd(mVerticalCodeId);
            }
        });
        mShowAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interstitialAd != null) {
                    interstitialAd.setAdInteractionListener(new MyInterstitialAdListener(PAGInterstitialActivity.this));
                    interstitialAd.show(PAGInterstitialActivity.this);
                    interstitialAd = null;

                } else {
                    TToast.show(PAGInterstitialActivity.this, "Please load the ad first !");
                }
            }
        });
    }

    @SuppressWarnings("SameParameterValue")
    private void loadAd(String codeId) {
        PAGInterstitialAd.loadAd(codeId,
                new PAGInterstitialRequest(),
                new PAGInterstitialAdLoadListener() {
                    @Override
                    public void onError(int code, String message) {
                        Log.e(TAG, "Callback --> onError: " + code + ", " + String.valueOf(message));
                        TToast.show(PAGInterstitialActivity.this, message);
                    }

                    @Override
                    public void onAdLoaded(PAGInterstitialAd pagInterstitialAd) {
                        TToast.show(PAGInterstitialActivity.this, "InterstitialAd loaded");
                        interstitialAd = pagInterstitialAd;
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static class MyInterstitialAdListener implements PAGInterstitialAdInteractionListener {
        Context mContext;

        MyInterstitialAdListener(Context context) {
            mContext = context;
        }

        @Override
        public void onAdShowed() {
            Log.d(TAG, "Callback --> onAdShowed");
            TToast.show(mContext, "onAdShowed");
            PAGAppOpenAdManager.setIsShowingAd(true);
        }

        @Override
        public void onAdClicked() {
            Log.d(TAG, "Callback --> onAdClicked");
            TToast.show(mContext, "onAdClicked");
        }

        @Override
        public void onAdDismissed() {
            Log.d(TAG, "Callback --> onAdDismissed");
            TToast.show(mContext, "onAdDismissed");
            PAGAppOpenAdManager.setIsShowingAd(false);
        }
    }
}
