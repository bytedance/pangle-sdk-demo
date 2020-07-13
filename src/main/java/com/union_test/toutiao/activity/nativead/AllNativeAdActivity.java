package com.union_test.toutiao.activity.nativead;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.View;

import com.union_test.toutiao.R;
import com.union_test.toutiao.activity.FullScreenVideoActivity;
import com.union_test.toutiao.activity.RewardVideoActivity;

/**
 * created by wuzejian on 2020-02-13
 */
public class AllNativeAdActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_ad_main);
//        bindButton(R.id.btn_main_banner, BannerActivity.class);
        bindButton(R.id.btn_main_feed_lv, FeedListActivity.class);
        bindButton(R.id.btn_main_feed_rv, FeedRecyclerActivity.class);
//        bindButton(R.id.btn_mian_insert, InteractionActivity.class);
        bindButton(R.id.btn_mian_reward, RewardVideoActivity.class);
        bindButton(R.id.btn_main_full, FullScreenVideoActivity.class);
        bindButton(R.id.btn_main_banner_native, NativeBannerActivity.class);
        bindButton(R.id.btn_main_interstitial_native, NativeInteractionActivity.class);
        bindButton(R.id.btn_main_draw_native, DrawNativeVideoActivity.class);
    }

    private void bindButton(@IdRes int id, final Class clz) {
        findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllNativeAdActivity.this, clz);
                //fullScreenVideoAd rit
                if (v.getId() == R.id.btn_main_full) {
                    intent.putExtra("horizontal_rit","945072452");
                    intent.putExtra("vertical_rit","901121375");
                    intent.putExtra("isExpress",false);
                }
                //rewardVideoAd rit
                if (v.getId() == R.id.btn_mian_reward) {
                    intent.putExtra("horizontal_rit","901121430");
                    intent.putExtra("vertical_rit","901121365");
                    intent.putExtra("isExpress",false);
                }
                startActivity(intent);
            }
        });
    }
}
