package com.union_test.new_api.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.api.init.PAGConfig;
import com.bytedance.sdk.openadsdk.api.init.PAGSdk;
import com.union_test.internationad.R;
import com.union_test.new_api.AdManagerHolder;
import com.union_test.new_api.sp.PangleSpUtils;
import com.union_test.new_api.utils.TToast;

/**
 * @author create by wuzejian on 2021/4/30.
 */
public class ApiExampleActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_eample);
        findViewById(R.id.btn_aae_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        findViewById(R.id.btn_get_gdpr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GDRP value if need : 0 close GDRP Privacy protection ，1: open GDRP Privacy protection , see class : TTAdConstant
                TToast.show(ApiExampleActivity.this, "GDPR value : " + TTAdSdk.getGdpr() + "  Coppa value : " + TTAdSdk.getCoppa(), Toast.LENGTH_LONG);

            }
        });

        findViewById(R.id.btn_set_gdrp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set GDPR value，the value must be 0/1。see class: TTAdConstant
                final EditText et = new EditText(ApiExampleActivity.this);
                et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                et.setTextColor(Color.RED);
                et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                et.requestFocus();
                new AlertDialog.Builder(ApiExampleActivity.this).setTitle("Input GDPR Value")
                        .setView(et)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String input = et.getText().toString();
                                try {
                                    int val = Integer.valueOf(input);
                                    if (val == TTAdConstant.TT_OPEN_GDRP || val == TTAdConstant.TT_CLOSE_GDRP) {
                                        TTAdSdk.setGdpr(val);
                                        PangleSpUtils.getInstance().setGdpr(val);
                                        TToast.show(ApiExampleActivity.this, "set gdpr: " + val, Toast.LENGTH_LONG);
                                        return;
                                    }
                                } catch (Throwable ignore) {
                                }
                                TToast.show(ApiExampleActivity.this, "set gdpr error", Toast.LENGTH_LONG);
                            }
                        }).setNegativeButton("cancel", null).show();
            }
        });


        findViewById(R.id.btn_set_coppa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //set GDPR value，the value must be 0/1。see class: TTAdConstant
                final EditText et = new EditText(ApiExampleActivity.this);
                et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                et.setTextColor(Color.RED);
                et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                et.requestFocus();
                new AlertDialog.Builder(ApiExampleActivity.this).setTitle("Input Coppa Value")
                        .setView(et)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String input = et.getText().toString();
                                try {
                                    int val = Integer.valueOf(input);
                                    if (val == TTAdConstant.TT_OPEN_GDRP || val == TTAdConstant.TT_CLOSE_GDRP) {
                                        TTAdSdk.setCoppa(val);
                                        PangleSpUtils.getInstance().setCoppa(val);
                                        TToast.show(ApiExampleActivity.this, "set coppa: " + val, Toast.LENGTH_LONG);
                                        return;
                                    }
                                } catch (Throwable ignore) {
                                }
                                TToast.show(ApiExampleActivity.this, "set coppa error", Toast.LENGTH_LONG);
                            }
                        }).setNegativeButton("cancel", null).show();
            }
        });

        findViewById(R.id.getBiddingToken).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String biddingToken = PAGSdk.getBiddingToken("945509744");
                int tokenSize = biddingToken.getBytes().length;
                Log.v("GetBiddingToken", biddingToken);
                Log.v("GetBiddingToken", "bidding token size:" + tokenSize + "bytes");
                TToast.show(ApiExampleActivity.this, "bidding token size : " + tokenSize);
            }
        });

        findViewById(R.id.btn_set_ccpa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText et = new EditText(ApiExampleActivity.this);
                et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                et.setTextColor(Color.RED);
                et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                et.requestFocus();
                new AlertDialog.Builder(ApiExampleActivity.this).setTitle("Input CCPA Value")
                        .setView(et)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String input = et.getText().toString();
                                try {
                                    int val = Integer.valueOf(input);
                                    if (val == TTAdConstant.TT_OPEN_CCPA || val == TTAdConstant.TT_CLOSE_CCPA) {
                                        TTAdSdk.setCCPA(val);
                                        PangleSpUtils.getInstance().setCcpa(val);
                                        TToast.show(ApiExampleActivity.this, "set CCPA: " + val, Toast.LENGTH_LONG);
                                        return;
                                    }
                                } catch (Throwable ignore) {
                                }
                                TToast.show(ApiExampleActivity.this, "set CCPA error", Toast.LENGTH_LONG);
                            }
                        }).setNegativeButton("cancel", null).show();
            }
        });

        findViewById(R.id.btn_get_ccpa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ccpa = PAGConfig.getDoNotSell();
                TToast.show(ApiExampleActivity.this, "ccpa : " + ccpa);
            }
        });
    }
}
