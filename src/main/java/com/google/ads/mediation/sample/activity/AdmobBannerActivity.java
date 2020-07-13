package com.google.ads.mediation.sample.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.ads.mediation.sample.customevent.BundleBuilder;
import com.google.ads.mediation.sample.customevent.adapter.AdmobExpressBannerAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.union_test.toutiao.R;
import com.union_test.toutiao.utils.TToast;


/**
 * created by wuzejian on 2019/11/29
 */
@SuppressLint("LongLogTag")
public class AdmobBannerActivity extends Activity {
    private static final String TAG = "AdmobBannerActivity";
    private AdView adView;
    private EditText mEtWidth;
    private EditText mEtHeight;
    private String mCodeId = "901121246";//穿山甲代码位id

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_activity_banner_express);
        initView();

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this);

    }

    private void initView() {
        mEtHeight = findViewById(R.id.express_height);
        mEtWidth = findViewById(R.id.express_width);

        findViewById(R.id.btn_express_load).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float expressViewWidth = 350;
                float expressViewHeight = 280;
                try {
                    expressViewWidth = Float.parseFloat(mEtWidth.getText().toString());
                    expressViewHeight = Float.parseFloat(mEtHeight.getText().toString());
                } catch (Exception e) {
                }

                if (expressViewWidth == 0) expressViewWidth = 300;
                if (expressViewHeight == 0) expressViewHeight = 240;


                BundleBuilder bundleBuilder = new BundleBuilder();
                bundleBuilder.setHeight((int) expressViewHeight);
                bundleBuilder.setWidth((int) expressViewWidth);
                bundleBuilder.setCodeId(mCodeId);
                bundleBuilder.setGdpr(1);

                adView = (AdView) findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder()
                        .addCustomEventExtrasBundle(AdmobExpressBannerAdapter.class, bundleBuilder.build())
                        .build();
                adView.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        Log.d(TAG, "Ad closed！");
                        TToast.show(AdmobBannerActivity.this, "banner close...");
                    }

                    @Override
                    public void onAdFailedToLoad(int i) {
                        super.onAdFailedToLoad(i);
                        Log.d(TAG, "Load fail！ errorCode=" + i);
                        TToast.show(AdmobBannerActivity.this, "onAdFailedToLoad close...");

                    }

                    @Override
                    public void onAdOpened() {
                        super.onAdOpened();
                        Log.d(TAG, "Ad showed！");

                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        Log.d(TAG, "Load success！");

                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        Log.d(TAG, "Ad clicked！");
                    }
                });
                adView.loadAd(adRequest);

            }
        });

    }


    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}
