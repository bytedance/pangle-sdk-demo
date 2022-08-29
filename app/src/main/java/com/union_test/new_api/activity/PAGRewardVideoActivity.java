package com.union_test.new_api.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bytedance.sdk.openadsdk.api.reward.PAGRewardItem;
import com.bytedance.sdk.openadsdk.api.reward.PAGRewardedAd;
import com.bytedance.sdk.openadsdk.api.reward.PAGRewardedAdInteractionListener;
import com.bytedance.sdk.openadsdk.api.reward.PAGRewardedAdLoadListener;
import com.bytedance.sdk.openadsdk.api.reward.PAGRewardedRequest;
import com.union_test.internationad.R;
import com.union_test.new_api.PAGAppOpenAdManager;
import com.union_test.new_api.RitConstants;
import com.union_test.new_api.utils.TToast;

/**
 * Created by bytedance on 2018/2/1.
 */

public class PAGRewardVideoActivity extends Activity {
    public static final String TAG = "PAGRewardVideoActivity";
    private Button mLoadAd;
    private Button mLoadAdVertical;
    private Button mShowAd;
    private PAGRewardedAd mPAGRewardedAd;
    private String mHorizontalCodeId = RitConstants.RIT_REWARDED_HORIZONTAL;
    private String mVerticalCodeId = RitConstants.RIT_REWARDED_VERTICAL;

    @SuppressWarnings("RedundantCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_video);
        findViewById(R.id.btn_arv_back).setOnClickListener(new View.OnClickListener() {
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
                loadAd( mVerticalCodeId);
            }
        });
        mShowAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPAGRewardedAd != null) {
                    mPAGRewardedAd.setAdInteractionListener(new MyRewardedAdListener(PAGRewardVideoActivity.this.getApplicationContext()));
                    mPAGRewardedAd.show(PAGRewardVideoActivity.this);
                    mPAGRewardedAd = null;
                } else {
                    TToast.show(PAGRewardVideoActivity.this, "Please load the ad first !");
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void loadAd(String codeId) {
        PAGRewardedAd.loadAd(codeId,
                new PAGRewardedRequest(),
                new PAGRewardedAdLoadListener() {
                    @Override
                    public void onError(int code, String message) {
                        Log.e(TAG, "Callback --> onError: " + code + ", " + String.valueOf(message) + "new");
                        TToast.show(PAGRewardVideoActivity.this, message);
                    }

                    @Override
                    public void onAdLoaded(PAGRewardedAd ad) {
                        TToast.show(PAGRewardVideoActivity.this, "rewardVideoAd loaded new");
                        mPAGRewardedAd = ad;
                    }
                });
    }


    public static class MyRewardedAdListener implements PAGRewardedAdInteractionListener {
        Context mContext;

        MyRewardedAdListener(Context context) {
            mContext = context;
        }

        @Override
        public void onAdShowed() {
            Log.d(TAG, "Callback --> rewardVideoAd onAdShowed");
            TToast.show(mContext, "rewardVideoAd onAdShowed");
            PAGAppOpenAdManager.setIsShowingAd(true);
        }

        @Override
        public void onAdClicked() {
            Log.d(TAG, "Callback --> rewardVideoAd onAdClicked");
            TToast.show(mContext, "rewardVideoAd onAdClicked");
        }

        @Override
        public void onAdDismissed() {
            Log.d(TAG, "Callback --> rewardVideoAd onAdDismissed");
            TToast.show(mContext, "rewardVideoAd onAdDismissed");
            PAGAppOpenAdManager.setIsShowingAd(false);
        }

        @Override
        public void onUserEarnedReward(PAGRewardItem item) {
            TToast.show(mContext,  " amount:" + item.getRewardAmount() +
                    " name:" + item.getRewardName());
            String logString = " amount:" + item.getRewardAmount() +
                    " name:" + item.getRewardName();
            Log.d(TAG, "Callback --> " + logString);
            TToast.show(mContext, logString);
        }

        @Override
        public void onUserEarnedRewardFail(int errorCode, String errorMsg) {
            TToast.show(mContext,  " errorCode:" + errorCode + " errorMsg:" + errorMsg);
            String logString =" errorCode:" + errorCode + " errorMsg:" + errorMsg;
            Log.d(TAG, "Callback --> " + logString);
            TToast.show(mContext, logString);
        }
    }
}
