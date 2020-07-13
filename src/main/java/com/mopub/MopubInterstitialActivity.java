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
import android.widget.EditText;

import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.PangleAdInterstitial;
import com.mopub.mobileads.PangleAdapterConfiguration;
import com.union_test.toutiao.utils.UIUtils;
import com.union_test.toutiao.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * created by wuzejian on 2019/11/29
 */
@SuppressLint("LongLogTag")
public class MopubInterstitialActivity extends Activity implements MoPubInterstitial.InterstitialAdListener, View.OnClickListener {
    private static final String TAG = "MopubBannerActivity";

    @Nullable
    private String mAdUnitId = "1d2299be9f1b476c975f01d8af532aba";
    private EditText mEtWidth;
    private EditText mEtHeight;
    private Button mShowButton;
    private Button mExpressShowBtn;

    private MoPubInterstitial mMoPubInterstitial;
    private boolean mHasInit = false;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mopub_activity_interstitial_express);

        SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(Objects.requireNonNull(mAdUnitId))
                .withLogLevel(MoPubLog.LogLevel.DEBUG)//Log级别
                .build();

        //init MoPub SDK
        MoPub.initializeSdk(this, sdkConfiguration, initSdkListener());
        initView();
    }

    private void initView() {
        findViewById(R.id.btn_size_1_1).setOnClickListener(this);
        findViewById(R.id.btn_size_2_3).setOnClickListener(this);
        findViewById(R.id.btn_size_3_2).setOnClickListener(this);
        mEtHeight = findViewById(R.id.express_height);
        mEtWidth = findViewById(R.id.express_width);
        mShowButton = findViewById(R.id.btn_express_show);
        mExpressShowBtn = findViewById(R.id.btn_express_interstitialShow);

        mShowButton.setEnabled(false);

        float expressViewWidth = 300;
        float expressViewHeight = 450;
        try {
            expressViewWidth = Float.parseFloat(mEtWidth.getText().toString());
            expressViewHeight = Float.parseFloat(mEtHeight.getText().toString());
        } catch (Exception e) {
            expressViewHeight = 0; //高度设置为0,则高度会自适应
        }

        final Map<String, Object> localExtras = new HashMap<>();
        localExtras.put(PangleAdInterstitial.KEY_EXTRA_AD_WIDTH, expressViewWidth);
        localExtras.put(PangleAdInterstitial.KEY_EXTRA_AD_HEIGHT, expressViewHeight);

        findViewById(R.id.btn_native_interstitialLoad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localExtras.put(PangleAdapterConfiguration.AD_PLACEMENT_ID_EXTRA_KEY, "945071429");
                mShowButton.setEnabled(false);
                mAdUnitId = "1d2299be9f1b476c975f01d8af532aba";
                mMoPubInterstitial = new MoPubInterstitial(MopubInterstitialActivity.this, mAdUnitId);
                mMoPubInterstitial.setInterstitialAdListener(MopubInterstitialActivity.this);

                mMoPubInterstitial.setLocalExtras(localExtras);
                mMoPubInterstitial.load();
            }
        });


        mShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMoPubInterstitial != null) {
                    mMoPubInterstitial.show();
                    mShowButton.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_size_1_1:
                loadExpressAd("901121797", 300, 300);
                break;
            case R.id.btn_size_2_3:
                loadExpressAd("901121133", 300, 450);
                break;
            case R.id.btn_size_3_2:
                loadExpressAd("943766446", 450, 300);
                break;
        }
    }

    private void loadExpressAd(String codeId, int expressViewWidth, int expressViewHeight) {
        if (!mHasInit) {
            UIUtils.logToast(MopubInterstitialActivity.this, "init not finish, wait");
            return;
        }
        final Map<String, Object> localExtras = new HashMap<>();
        localExtras.put(PangleAdInterstitial.KEY_EXTRA_AD_WIDTH, expressViewWidth);
        localExtras.put(PangleAdInterstitial.KEY_EXTRA_AD_HEIGHT, expressViewHeight);
        mAdUnitId = "310e61036c9f4fdba1c3ffd4d74cf206";
        localExtras.put(PangleAdapterConfiguration.AD_PLACEMENT_ID_EXTRA_KEY, codeId);
        mExpressShowBtn.setEnabled(false);
        mMoPubInterstitial = new MoPubInterstitial(MopubInterstitialActivity.this, mAdUnitId);
        mMoPubInterstitial.setInterstitialAdListener(MopubInterstitialActivity.this);
        mMoPubInterstitial.setLocalExtras(localExtras);
        mMoPubInterstitial.load();


        mExpressShowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMoPubInterstitial != null) {
                    mMoPubInterstitial.show();
                    mExpressShowBtn.setEnabled(false);
                }
            }
        });
    }


    private SdkInitializationListener initSdkListener() {
        return new SdkInitializationListener() {
            @Override
            public void onInitializationFinished() {
                mHasInit = true;
                Log.d("TiktokAdapter", "onInitializationFinished////");
           /* MoPub SDK initialized.
           Check if you should show the consent dialog here, and make your ad requests. */
            }
        };
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMoPubInterstitial != null) {
            mMoPubInterstitial.destroy();
            mMoPubInterstitial = null;
        }
    }

    @Override
    public void onInterstitialLoaded(MoPubInterstitial interstitial) {
        mShowButton.setEnabled(true);
        mExpressShowBtn.setEnabled(true);
        UIUtils.logToast(MopubInterstitialActivity.this, "Interstitial onInterstitialLoaded.");
    }

    @Override
    public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
        mShowButton.setEnabled(false);
        mExpressShowBtn.setEnabled(false);
        UIUtils.logToast(MopubInterstitialActivity.this, "Interstitial onInterstitialFailed.errorCode=" + errorCode);
    }

    @Override
    public void onInterstitialShown(MoPubInterstitial interstitial) {
        UIUtils.logToast(MopubInterstitialActivity.this, "Interstitial shown.");
    }

    @Override
    public void onInterstitialClicked(MoPubInterstitial interstitial) {
        UIUtils.logToast(MopubInterstitialActivity.this, "Interstitial clicked.");
    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial interstitial) {
        UIUtils.logToast(MopubInterstitialActivity.this, "Interstitial dismissed.");
    }
}
