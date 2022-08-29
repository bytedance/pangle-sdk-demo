package com.union_test.new_api.activity.nativead;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bytedance.sdk.openadsdk.api.open.PAGAppOpenAd;
import com.bytedance.sdk.openadsdk.api.open.PAGAppOpenAdInteractionListener;
import com.bytedance.sdk.openadsdk.api.open.PAGAppOpenAdLoadListener;
import com.bytedance.sdk.openadsdk.api.open.PAGAppOpenRequest;
import com.union_test.internationad.R;
import com.union_test.new_api.PAGAppOpenAdManager;
import com.union_test.new_api.RitConstants;
import com.union_test.new_api.utils.TToast;

public class PAGNativeAppOpenAdActivity extends AppCompatActivity {

    private View mLoadVerticalAd;
    private View mLoadAd;
    private View showAd;
    private PAGAppOpenAdLoadListener mAppOpenAdListener;
    private PAGAppOpenAd ttAppOpenAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_open_ad);
        findViewById(R.id.btn_fsv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mLoadVerticalAd = findViewById(R.id.btn_app_open_ad_vertical_load);
        mLoadAd = findViewById(R.id.btn_app_open_ad_load);
        showAd = findViewById(R.id.btn_app_open_ad_show);
        mAppOpenAdListener = new PAGAppOpenAdLoadListener() {
            @Override
            public void onError(int code, String message) {
                TToast.show(PAGNativeAppOpenAdActivity.this.getApplicationContext(), "code: " + code + " message: " + message+" new");
            }

            @Override
            public void onAdLoaded(PAGAppOpenAd pagAppOpenAd) {
                ttAppOpenAd = pagAppOpenAd;
                TToast.show(PAGNativeAppOpenAdActivity.this.getApplicationContext(), "load success"+" new");
            }
        };

        initClickEvent();
    }

    private void initClickEvent() {
        mLoadAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PAGAppOpenRequest request = new PAGAppOpenRequest();
                request.setTimeout(3000);
                PAGAppOpenAd.loadAd(RitConstants.RIT_OPEN_HORIZONTAL,request,mAppOpenAdListener);
            }
        });

        mLoadVerticalAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PAGAppOpenRequest request = new PAGAppOpenRequest();
                request.setTimeout(3000);
                PAGAppOpenAd.loadAd(RitConstants.RIT_OPEN_VERTICAL,request,mAppOpenAdListener);
            }
        });


        showAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ttAppOpenAd != null){
                    ttAppOpenAd.setAdInteractionListener(new PAGAppOpenAdInteractionListener() {
                        @Override
                        public void onAdShowed() {
                            TToast.show(PAGNativeAppOpenAdActivity.this.getApplicationContext().getApplicationContext(), "onAdShow_new");
                            PAGAppOpenAdManager.setIsShowingAd(true);
                        }

                        @Override
                        public void onAdClicked() {
                            TToast.show(PAGNativeAppOpenAdActivity.this.getApplicationContext(), "onAdClicked_new");
                        }

                        @Override
                        public void onAdDismissed() {
                            TToast.show(PAGNativeAppOpenAdActivity.this.getApplicationContext(), "onAdDismissed_new");
                            PAGAppOpenAdManager.setIsShowingAd(false);
                        }
                    });
                    ttAppOpenAd.show(PAGNativeAppOpenAdActivity.this);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAppOpenAdListener = null;
    }
}