package com.union_test.toutiao.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.text.InputType;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.union_test.toutiao.R;
import com.union_test.toutiao.activity.adapter.AdapterActivity;
import com.union_test.toutiao.activity.expressad.AllExpressAdActivity;
import com.union_test.toutiao.activity.nativead.AllNativeAdActivity;
import com.union_test.toutiao.activity.testtools.AllTestToolActivity;
import com.union_test.toutiao.config.TTAdManagerHolder;
import com.union_test.toutiao.utils.TToast;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_main);

        TextView tvVersion = findViewById(R.id.tv_version);
        String ver = getString(R.string.main_sdk_version_tip, TTAdManagerHolder.get().getSDKVersion());
        tvVersion.setText(ver);
        bindButton(R.id.btn_adapter, AdapterActivity.class);
        bindButton(R.id.btn_all_native_ad, AllNativeAdActivity.class);
        bindButton(R.id.btn_express_ad, AllExpressAdActivity.class);
        bindButton(R.id.btn_all_test_tool, AllTestToolActivity.class);
        findViewById(R.id.btn_privacy_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TTAdManagerHolder.get().showPrivacyProtection();
            }
        });

        findViewById(R.id.btn_get_gdrp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GDRP value if need : 0 close GDRP Privacy protection ，1: open GDRP Privacy protection , see class : TTAdConstant
                TToast.show(MainActivity.this, "gdrp value : " + TTAdManagerHolder.get().getGdpr(), Toast.LENGTH_LONG);
            }
        });
        findViewById(R.id.btn_set_gdrp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set GDPR value，the value must be 0/1。see class: TTAdConstant

                final EditText et = new EditText(MainActivity.this);
                et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                et.setTextColor(Color.RED);
                et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                et.requestFocus();
                new AlertDialog.Builder(MainActivity.this).setTitle("Input GDPR Value")
                        .setView(et)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String input = et.getText().toString();
                                try {
                                    int val = Integer.valueOf(input);
                                    if (val == TTAdConstant.TT_OPEN_GDRP || val == TTAdConstant.TT_CLOSE_GDRP) {
                                        TTAdManagerHolder.get().setGdpr(val);
                                        TToast.show(MainActivity.this, "set gdpr: " + val, Toast.LENGTH_LONG);
                                        return;
                                    }
                                } catch (Throwable ignore) {
                                }
                                TToast.show(MainActivity.this, "set gdpr error", Toast.LENGTH_LONG);
                            }
                        }).setNegativeButton("cancel", null).show();
            }
        });
    }

    private void bindButton(@IdRes int id, final Class clz) {
        findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, clz));
            }
        });
    }

}
