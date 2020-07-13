package com.mopub;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubRewardedVideos;
import com.mopub.mobileads.PangleAdapterConfiguration;
import com.union_test.toutiao.utils.UIUtils;
import com.union_test.toutiao.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * created by wuzejian on 2019/4/14
 */
@SuppressLint("LongLogTag")
public class MopubFullVideoActivity extends Activity implements MoPubInterstitial.InterstitialAdListener {
    private static final String TAG = "MopubFullVideoActivity";
    @Nullable
    private Button mShowButton;
    @Nullable
    private String mAdUnitId = "6a3af25ca406481bba3822af771bad7b";

    private boolean mHasInit = false;

    private MoPubInterstitial mMoPubInterstitial;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mopub_reward_activity);
        SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(Objects.requireNonNull(mAdUnitId))
                .withLogLevel(MoPubLog.LogLevel.DEBUG)//Log级别
                .build();

        //init MoPub SDK
        MoPub.initializeSdk(this, sdkConfiguration, initSdkListener());

        final Map<String, Object> localExtras = new HashMap<>();
        localExtras.put(PangleAdapterConfiguration.AD_PLACEMENT_ID_EXTRA_KEY, "901121375");

        findViewById(R.id.loadRewardAd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mHasInit) {
                    UIUtils.logToast(MopubFullVideoActivity.this, "init not finish, wait");
                    return;
                }
                if (mMoPubInterstitial == null) {
                    mMoPubInterstitial = new MoPubInterstitial(MopubFullVideoActivity.this, mAdUnitId);
                    mMoPubInterstitial.setInterstitialAdListener(MopubFullVideoActivity.this);
                }
                mMoPubInterstitial.setLocalExtras(localExtras);
                mMoPubInterstitial.load();
                Log.d(TAG, "onClick loadFullVideo");
            }
        });

        mShowButton = findViewById(R.id.showRewardAd);
        mShowButton.setEnabled(false);
        mShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMoPubInterstitial != null) {
                    mMoPubInterstitial.show();
                }
            }
        });


    }

    private SdkInitializationListener initSdkListener() {
        return new SdkInitializationListener() {
            @Override
            public void onInitializationFinished() {
                mHasInit = true;
                Log.d("MopubFullVideoActivity", "onInitializationFinished////");
           /* MoPub SDK initialized.
           Check if you should show the consent dialog here, and make your ad requests. */
            }
        };
    }

    @Override
    public void onDestroy() {
        MoPubRewardedVideos.setRewardedVideoListener(null);
        super.onDestroy();
    }

    @Override
    public void onInterstitialLoaded(MoPubInterstitial moPubInterstitial) {
        mShowButton.setEnabled(true);
        Log.d(TAG, "loadFullVideo->.....onInterstitialLoaded-....");
        UIUtils.logToast(this, "Interstitial load success.");

    }

    @Override
    public void onInterstitialFailed(MoPubInterstitial moPubInterstitial, MoPubErrorCode moPubErrorCode) {
        mShowButton.setEnabled(false);
        Log.d(TAG, "loadFullVideo->.....onInterstitialFailed=" + moPubErrorCode);
        UIUtils.logToast(this, "Interstitial load fail.moPubErrorCode="+moPubErrorCode);

    }

    @Override
    public void onInterstitialShown(MoPubInterstitial moPubInterstitial) {
        Log.d(TAG, "loadFullVideo->.....show");
        UIUtils.logToast(this, "Interstitial show ");
    }

    @Override
    public void onInterstitialClicked(MoPubInterstitial moPubInterstitial) {
        Log.d(TAG, "loadFullVideo->.....onInterstitialClicked");
        UIUtils.logToast(this, "Interstitial onInterstitialClicked ");
    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial moPubInterstitial) {
        Log.d(TAG, "loadFullVideo->onInterstitialDismissed.....");
        UIUtils.logToast(this, "Interstitial close ");
    }
}
