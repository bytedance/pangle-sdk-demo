package com.union_test.toutiao.activity.expressad;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.union_test.toutiao.R;
import com.union_test.toutiao.config.TTAdManagerHolder;
import com.union_test.toutiao.utils.TToast;

import java.util.List;

@SuppressWarnings("unused")
public class InteractionExpressActivity extends Activity implements View.OnClickListener {

    private TTAdNative mTTAdNative;
    private Context mContext;
    private TTAdDislike mTTAdDislike;
    private TTNativeExpressAd mTTAd;
    private long startTime = 0;
    private boolean mHasShowDownloadActive = false;

    @SuppressWarnings("RedundantCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_native_express_intersitial);
        mContext = this.getApplicationContext();
        initView();
        initTTSDKConfig();
    }


    private void initView() {
        findViewById(R.id.btn_size_1_1).setOnClickListener(this);
        findViewById(R.id.btn_size_2_3).setOnClickListener(this);
        findViewById(R.id.btn_size_3_2).setOnClickListener(this);
    }

    private void initTTSDKConfig() {
        //create TTAdNative object which use to request ad，createAdNative(Context context),the context should be an Activity object
        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_size_1_1:
                loadExpressAd("945113151", 300, 0);
                break;
            case R.id.btn_size_2_3:
                loadExpressAd("945113153", 300, 0);
                break;
            case R.id.btn_size_3_2:
                loadExpressAd("945113152", 450, 0);
                break;
        }
    }

    private void loadExpressAd(String codeId, int expressViewWidth, int expressViewHeight) {
        //Create a parameter AdSlot for reward ad request type,
        //      refer to the document for meanings of specific parameters
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId) //rit
                .setSupportDeepLink(true)
                .setAdCount(1) //ad count from 1 to 3
                .setExpressViewAcceptedSize(expressViewWidth,expressViewHeight) //expected ad view size in dp
                .build();
        //request ad，then call render method of TTNativeExpressAd
        mTTAdNative.loadInteractionExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                TToast.show(InteractionExpressActivity.this, "load error : " + code + ", " + message);
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0){
                    return;
                }
                mTTAd = ads.get(0);
                bindAdListener(mTTAd);
                startTime = System.currentTimeMillis();
                mTTAd.render();
            }
        });
    }


    private void bindAdListener(TTNativeExpressAd ad) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.AdInteractionListener() {
            @Override
            public void onAdDismiss() {
                TToast.show(mContext, "Ad closed");
            }

            @Override
            public void onAdClicked(View view, int type) {
                TToast.show(mContext, "Ad clicked");
            }

            @Override
            public void onAdShow(View view, int type) {
                TToast.show(mContext, "Ad showed");
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Log.e("ExpressView","render fail:"+(System.currentTimeMillis() - startTime));
                TToast.show(mContext, msg+" code:"+code);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                Log.e("ExpressView","render suc:"+(System.currentTimeMillis() - startTime));
                //the width and height of view in dp
                TToast.show(mContext, "Render success");
                mTTAd.showInteractionExpressAd(InteractionExpressActivity.this);

            }
        });

        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD){
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTTAd != null) {
            mTTAd.destroy();
        }
    }


}
