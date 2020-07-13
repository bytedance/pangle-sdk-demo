package com.union_test.toutiao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.union_test.toutiao.R;
import com.union_test.toutiao.config.TTAdManagerHolder;
import com.union_test.toutiao.utils.TToast;

/**
 * Created by bytedance on 2018/2/1.
 */

public class FullScreenVideoActivity extends Activity {
    public static final String TAG = "FullScreenVideoActivity";
    private Button mLoadAd;
    private Button mLoadAdVertical;
    private Button mShowAd;
    private TTAdNative mTTAdNative;
    private TTFullScreenVideoAd mttFullVideoAd;
    private String mHorizontalCodeId;
    private String mVerticalCodeId;
    private boolean mIsExpress;
    @SuppressWarnings("RedundantCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_video);
        mLoadAd = (Button) findViewById(R.id.btn_reward_load);
        mLoadAdVertical = (Button) findViewById(R.id.btn_reward_load_vertical);
        mShowAd = (Button) findViewById(R.id.btn_reward_show);
        //step1:Initialize to access the TikTok Ad Network sdk, see the access documentation and the TikTok Ad Network for details
        TTAdManager ttAdManager = TTAdManagerHolder.get();
        //step2:create TTAdNative object which use to request ad
        mTTAdNative = ttAdManager.createAdNative(getApplicationContext());
        getCodeId();
        initClickEvent();
    }

    private void getCodeId() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mHorizontalCodeId = intent.getStringExtra("horizontal_rit");
        mVerticalCodeId = intent.getStringExtra("vertical_rit");
        mIsExpress = intent.getBooleanExtra("isExpress", false);
    }


    private void initClickEvent() {

        mLoadAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAd(mHorizontalCodeId, TTAdConstant.HORIZONTAL);
            }
        });
        mLoadAdVertical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAd(mVerticalCodeId, TTAdConstant.VERTICAL);
            }
        });
        mShowAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mttFullVideoAd != null) {
                    //step6:display ad
//                    mttFullVideoAd.showFullScreenVideoAd(FullScreenVideoActivity.this);

                    //Display the AD and pass display scene parameter
                    mttFullVideoAd.showFullScreenVideoAd(FullScreenVideoActivity.this,TTAdConstant.RitScenes.GAME_GIFT_BONUS,null);
                    mttFullVideoAd = null;
                } else {
                    TToast.show(FullScreenVideoActivity.this, "Please load the ad first !");
                }
            }
        });
    }

    @SuppressWarnings("SameParameterValue")
    private void loadAd(String codeId, int orientation) {
        //step4:Create a parameter AdSlot for reward ad request type,
        //      refer to the document for meanings of specific parameters
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                .setSupportDeepLink(true)
                .isExpressAd(mIsExpress)
                .setImageAcceptedSize(1080, 1920)
                .build();
        //step5:request ad
        mTTAdNative.loadFullScreenVideoAd(adSlot, new TTAdNative.FullScreenVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.e(TAG, "Callback --> onError: " + code + ", " + String.valueOf(message));
                TToast.show(FullScreenVideoActivity.this, message);
            }

            @Override
            public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ad) {
                TToast.show(FullScreenVideoActivity.this, "FullVideoAd loaded");
                mttFullVideoAd = ad;
                mttFullVideoAd.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        Log.d(TAG, "Callback --> FullVideoAd show");
                        TToast.show(FullScreenVideoActivity.this, "FullVideoAd show");
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        Log.d(TAG, "Callback --> FullVideoAd bar click");
                        TToast.show(FullScreenVideoActivity.this, "FullVideoAd bar click");
                    }

                    @Override
                    public void onAdClose() {
                        Log.d(TAG, "Callback --> FullVideoAd close");
                        TToast.show(FullScreenVideoActivity.this, "FullVideoAd close");
                    }

                    @Override
                    public void onVideoComplete() {
                        Log.d(TAG, "Callback --> FullVideoAd complete");
                        TToast.show(FullScreenVideoActivity.this, "FullVideoAd complete");
                    }

                    @Override
                    public void onSkippedVideo() {
                        Log.d(TAG, "Callback --> FullVideoAd skipped");
                        TToast.show(FullScreenVideoActivity.this, "FullVideoAd skipped");

                    }

                });
            }

            @Override
            public void onFullScreenVideoCached() {
                Log.e(TAG, "Callback --> onFullScreenVideoCached");
                TToast.show(FullScreenVideoActivity.this, "FullVideoAd video cached");
            }
        });


    }
}
