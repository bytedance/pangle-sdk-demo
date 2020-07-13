package com.union_test.toutiao.activity.nativead;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTInteractionAd;
import com.union_test.toutiao.R;
import com.union_test.toutiao.config.TTAdManagerHolder;
import com.union_test.toutiao.utils.TToast;

import static com.bytedance.sdk.openadsdk.AdSlot.Builder;

/**
 * 插屏广告Activity示例
 */
public class InteractionActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "InteractionActivity";

    private TTAdNative mTTAdNative;
    private Button mShow_InterstitialAd_btn;
    private Button mShow_InterstitialAd_btn_ladingpage;
    private Context mContext;

    @SuppressWarnings("RedundantCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_ad);
        mContext = this.getBaseContext();
        //create TTAdNative object which use to request ad，createAdNative(Context context),the context should be an activity object
        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
        mShow_InterstitialAd_btn = (Button) findViewById(R.id.show_ad_dialog);
        mShow_InterstitialAd_btn_ladingpage = (Button) findViewById(R.id.show_ad_dialog_landingpage);
        mShow_InterstitialAd_btn.setOnClickListener(this);
        mShow_InterstitialAd_btn_ladingpage.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.show_ad_dialog_landingpage) {
            loadInteractionAd("945071429");
        } else if (v.getId() == R.id.show_ad_dialog) {
            loadInteractionAd("945071429");
        }
    }

    /**
     * load ad
     */
    private void loadInteractionAd(String codeId) {
        //Create a parameter AdSlot for reward ad request type,
        //      refer to the document for meanings of specific parameters
        AdSlot adSlot = new Builder()
                .setCodeId(codeId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(600, 600) //Pass in the same size according to the size selected by the advertising platform
                .build();
        //step5:request
        mTTAdNative.loadInteractionAd(adSlot, new TTAdNative.InteractionAdListener() {
            @Override
            public void onError(int code, String message) {
                TToast.show(getApplicationContext(), "code: " + code + "  message: " + message);
            }

            @Override
            public void onInteractionAdLoad(TTInteractionAd ttInteractionAd) {
                TToast.show(getApplicationContext(), "type:  " + ttInteractionAd.getInteractionType());
                ttInteractionAd.setAdInteractionListener(new TTInteractionAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked() {
                        Log.d(TAG, "onAdClicked");
                        TToast.show(mContext, "Ad clicked");
                    }

                    @Override
                    public void onAdShow() {
                        Log.d(TAG, "onAdShow");
                        TToast.show(mContext, "Ad showed");
                    }

                    @Override
                    public void onAdDismiss() {
                        Log.d(TAG, "onAdDismiss");
                        TToast.show(mContext, "Ad closed");
                    }
                });
                //show ad
                ttInteractionAd.showInteractionAd(InteractionActivity.this);
            }
        });
    }
}
