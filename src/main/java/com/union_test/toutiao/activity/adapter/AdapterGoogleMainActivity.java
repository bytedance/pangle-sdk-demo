package com.union_test.toutiao.activity.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.ads.mediation.sample.activity.AdmobBannerActivity;
import com.google.ads.mediation.sample.activity.AdmobFeedAdActivity;
import com.google.ads.mediation.sample.activity.AdmobFullVideoActivity;
import com.google.ads.mediation.sample.activity.AdmobInterstitialActivity;
import com.google.ads.mediation.sample.activity.AdmobRewardVideoActivity;
import com.union_test.toutiao.R;

/**
 * created by wuzejian on 2019-12-03
 */
public class AdapterGoogleMainActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_activity_adapter);

        findViewById(R.id.btn_admob_native).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdapterGoogleMainActivity.this, AdmobFeedAdActivity.class);
                AdapterGoogleMainActivity.this.startActivity(intent);
            }
        });

        findViewById(R.id.btn_admob_reward_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdapterGoogleMainActivity.this, AdmobRewardVideoActivity.class);
                AdapterGoogleMainActivity.this.startActivity(intent);
            }
        });

        findViewById(R.id.btn_admob_full_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdapterGoogleMainActivity.this, AdmobFullVideoActivity.class);
                AdapterGoogleMainActivity.this.startActivity(intent);
            }
        });

        findViewById(R.id.btn_admob_banner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdapterGoogleMainActivity.this, AdmobBannerActivity.class);
                AdapterGoogleMainActivity.this.startActivity(intent);
            }
        });

        findViewById(R.id.btn_admob_interstitial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdapterGoogleMainActivity.this, AdmobInterstitialActivity.class);
                AdapterGoogleMainActivity.this.startActivity(intent);
            }
        });
    }
}
