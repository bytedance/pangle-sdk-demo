package com.union_test.new_api.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.bytedance.sdk.openadsdk.api.init.PAGSdk;
import com.union_test.internationad.R;
import com.union_test.new_api.AdManagerHolder;
import com.union_test.new_api.activity.nativead.PAGFeedListActivity;
import com.union_test.new_api.activity.nativead.PAGFeedRecyclerActivity;
import com.union_test.new_api.activity.expressad.PAGBannerActivity;
import com.union_test.new_api.activity.nativead.PAGNativeAppOpenAdActivity;
import com.union_test.new_api.activity.testtools.AllTestToolActivity;


public class PAGMainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_main2);

        bindButton(R.id.btn_main_feed_lv, PAGFeedListActivity.class);
        bindButton(R.id.btn_main_feed_rv, PAGFeedRecyclerActivity.class);

        bindButton(R.id.btn_banner_ad, PAGBannerActivity.class);

        bindButton(R.id.btn_mian_reward, PAGRewardVideoActivity.class);
        bindButton(R.id.btn_main_full, PAGInterstitialActivity.class);
        bindButton(R.id.btn_main_app_open_ad, PAGNativeAppOpenAdActivity.class);



        bindButton(R.id.btn_api_example, ApiExampleActivity.class);
        bindButton(R.id.btn_all_test_tool, AllTestToolActivity.class);


        TextView tvVersion = findViewById(R.id.tv_version);
        String ver = getString(R.string.main_sdk_version_tip, PAGSdk.getSDKVersion());
        tvVersion.setText(ver);

    }

    private void bindButton(@IdRes int id, final Class clz) {
        findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PAGMainActivity.this, clz);
                startActivity(intent);
            }
        });
    }


}
