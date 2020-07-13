package com.mopub;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubRewardedVideoListener;
import com.mopub.mobileads.MoPubRewardedVideoManager;
import com.mopub.mobileads.MoPubRewardedVideos;
import com.mopub.mobileads.PangleAdRewardedVideo;
import com.mopub.mobileads.PangleAdapterConfiguration;
import com.union_test.toutiao.utils.UIUtils;
import com.union_test.toutiao.R;
import com.union_test.toutiao.utils.TToast;
import com.union_test.toutiao.utils.UIUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * created by wuzejian on 2019/4/14
 */
@SuppressLint("LongLogTag")
public class MopubRewardedVideoActivity extends Activity implements MoPubRewardedVideoListener {
    private static final String TAG = "MopubRewardedVideoActivity";
    @Nullable
    private Button mShowButton;
    @Nullable
    private String mAdUnitId = "5b658326996743d9b5517c89b7a5c13c";

    private boolean mHasInit = false;


    // private String mCodeId = "901121365";//TTAdConstant.VERTICAL

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mopub_reward_activity);
        Map<String, String> mediatedNetworkConfiguration1 = new HashMap<>();
        mediatedNetworkConfiguration1.put("AppKey", "000000000000000");
        PangleAdapterConfiguration.setMediaExtra("mediaExtra");
        PangleAdapterConfiguration.setRewardAmount(3);
        PangleAdapterConfiguration.setRewardName("gold coin");
        PangleAdapterConfiguration.setUserID("user123");
        SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(mAdUnitId)
                .withMediationSettings()
                .withMediatedNetworkConfiguration(PangleAdapterConfiguration.class.toString(), mediatedNetworkConfiguration1)
                .withLogLevel(MoPubLog.LogLevel.DEBUG)
                .withLegitimateInterestAllowed(true)
                .build();

        // Set location awareness and precision globally for your app:
        MoPub.setLocationAwareness(MoPub.LocationAwareness.TRUNCATED);
        MoPub.setLocationPrecision(4);
        MoPub.initializeSdk(this, sdkConfiguration, initSdkListener());
        MoPubRewardedVideos.setRewardedVideoListener(this);
        findViewById(R.id.loadRewardAd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mHasInit) {
                    UIUtils.logToast(MopubRewardedVideoActivity.this, "init not finish, wait");
                    return;
                }
                MoPubRewardedVideoManager.updateActivity(MopubRewardedVideoActivity.this);// must updateActivity
                MoPubRewardedVideos.loadRewardedVideo(mAdUnitId);
            }
        });

        mShowButton = findViewById(R.id.showRewardAd);
        mShowButton.setEnabled(false);
        mShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoPubRewardedVideos.showRewardedVideo(mAdUnitId);
            }
        });


    }

    private SdkInitializationListener initSdkListener() {
        return new SdkInitializationListener() {
            @Override
            public void onInitializationFinished() {
                mHasInit = true;
                Log.d("MopubRewardedVideoActivity", "onInitializationFinished////");
           /* MoPub SDK initialized.
           Check if you should show the consent dialog here, and make your ad requests. */
            }
        };
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onRewardedVideoLoadSuccess(@NonNull String adUnitId) {
        Log.d(TAG, "onRewardedVideoLoadSuccess.....adUnitId=" + adUnitId);
        if (adUnitId.equals(mAdUnitId)) {
            if (mShowButton != null) {
                TToast.show(MopubRewardedVideoActivity.this, " onRewardedVideoLoadSuccess!");
                mShowButton.setEnabled(true);
            }
        }
        UIUtils.logToast(this, " onRewardedVideoLoadSuccess....");
    }

    @Override
    public void onRewardedVideoLoadFailure(@NonNull String s, @NonNull MoPubErrorCode moPubErrorCode) {
        Log.d(TAG, "onRewardedVideoLoadFailure....." + moPubErrorCode);
        UIUtils.logToast(this, " onRewardedVideoLoadFailure....moPubErrorCode=" + moPubErrorCode);

    }

    @Override
    public void onRewardedVideoStarted(@NonNull String s) {
        mShowButton.setEnabled(false);
        Log.d(TAG, "onRewardedVideoStarted.....");
        UIUtils.logToast(this, " onRewardedVideoStarted....");
    }

    @Override
    public void onRewardedVideoPlaybackError(@NonNull String s, @NonNull MoPubErrorCode moPubErrorCode) {
        Log.d(TAG, "onRewardedVideoPlaybackError.....");
        UIUtils.logToast(this, " onRewardedVideoPlaybackError....");

    }

    @Override
    public void onRewardedVideoClicked(@NonNull String s) {
        Log.d(TAG, "onRewardedVideoClicked.....");
        TToast.show(MopubRewardedVideoActivity.this, " onRewardedVideoClicked!");

    }

    @Override
    public void onRewardedVideoClosed(@NonNull String s) {
        Log.d(TAG, "onRewardedVideoClosed.....");
        UIUtils.logToast(this, " onRewardedVideoClosed....");


    }

    @Override
    public void onRewardedVideoCompleted(@NonNull Set<String> set, @NonNull MoPubReward moPubReward) {
        Log.d(TAG, "onRewardedVideoCompleted.....");
        UIUtils.logToast(this, " onRewardedVideoCompleted....");

    }

    @Override
    public void onDestroy() {
        MoPubRewardedVideos.setRewardedVideoListener(null);
        super.onDestroy();
    }
}
