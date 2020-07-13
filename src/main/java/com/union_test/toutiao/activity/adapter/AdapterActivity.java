package com.union_test.toutiao.activity.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.union_test.toutiao.R;

/**
 * created by wuzejian on 2019-12-03
 */
public class AdapterActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adapter_activity);

        findViewById(R.id.btn_admob).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdapterActivity.this, AdapterGoogleMainActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_mopub).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdapterActivity.this, MopubAdapterActivity.class);
                startActivity(intent);
            }
        });

    }
}
