package com.union_test.toutiao.activity.expressad;

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
 * created by wuzejian on 2019-12-19
 */
public class AllExpressAdActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_express_ad);
        bindButton(R.id.express_native_ad, NativeExpressActivity.class);
        bindButton(R.id.express_native_ad_list, NativeExpressListActivity.class);
        bindButton(R.id.express_banner_ad, BannerExpressActivity.class);
        bindButton(R.id.express_interstitial_ad, InteractionExpressActivity.class);
        bindButton(R.id.express_rewarded_video_ad, RewardVideoActivity.class);
        bindButton(R.id.express_full_screen_video_ad, FullScreenVideoActivity.class);
        bindButton(R.id.express_draw_video_ad, DrawNativeExpressVideoActivity.class);

    }

    private void bindButton(@IdRes int id, final Class clz) {
        findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllExpressAdActivity.this, clz);
                //express fullScreenVideoAd rit
                if (v.getId() == R.id.express_full_screen_video_ad) {
                    intent.putExtra("horizontal_rit", "945113167");
                    intent.putExtra("vertical_rit", "945113166");
                    intent.putExtra("isExpress",true);

                }
                //express rewardVideoAd rit
                if (v.getId() == R.id.express_rewarded_video_ad) {
                    intent.putExtra("horizontal_rit", "945113161");
                    intent.putExtra("vertical_rit", "945113160");
                    intent.putExtra("isExpress",true);
                }
                startActivity(intent);
            }
        });
    }
}
