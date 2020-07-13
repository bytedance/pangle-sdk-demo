package com.union_test.toutiao.activity.testtools;

import com.union_test.toutiao.R;
import com.union_test.toutiao.activity.nativead.AllNativeAdActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;

public class AllTestToolActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_test_tool);
        bindButton(R.id.btn_playable, PlayableToolActivity.class);
        bindButton(R.id.btn_ip_port, IpPortToolActivity.class);
        bindButton(R.id.btn_gps, GpsToolActivity.class);
    }

    private void bindButton(@IdRes int id, final Class clz) {
        findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllTestToolActivity.this, clz);
                startActivity(intent);
            }
        });
    }
}
